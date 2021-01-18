package ru.cubos.arduino;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import ru.cubos.Connector;
import ru.cubos.Decoder;
import ru.cubos.Protocol;

import java.util.HashMap;

import static ru.cubos.Protocol.*;
import static ru.cubos.Protocol.PinLevels.*;

public class SerialConnector extends Decoder implements Connector {

    HashMap<Integer, Integer> resultWaiter = new HashMap<>();

    public SerialConnector(){
        super();
    }

    @Override
    public void digitalReadReply(int pin, int value){
        resultWaiter.put((int)pin, (int)value);
    }

    @Override
    public void analogReadReply(int pin, int value){
        resultWaiter.put((int)pin, (int)value);
    }

    static SerialPort serialPort;

    public void setPort(String serialPortName){
        serialPort = new SerialPort(serialPortName);
    }

    public void connect() throws SerialPortException {
        serialPort.openPort();
        serialPort.setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        //serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
        //SerialPort.MASK_RXCHAR
        serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
    }

    public void disconnect() throws SerialPortException {
        serialPort.closePort();
    }

    private class PortReader implements SerialPortEventListener {

        synchronized public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0){
                try {
                    byte data[] = serialPort.readBytes(event.getEventValue());
                    for(int i=0; i<data.length; i++){
                        receivedByteList.add(data[i]);
                    }
                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
        }
    }

    public void digitalWrite(int pin,  int pinLevel){
        if(pinLevel>=1) digitalWrite(pin, PinLevels.HIGH);
        else digitalWrite(pin, LOW);
    }

    public void digitalWrite(int pin,  boolean pinLevel){
        if(pinLevel) digitalWrite(pin, PinLevels.HIGH);
        else digitalWrite(pin, LOW);
    }

    public void write(String s){
        System.out.println("To write " + s);
        try {
            serialPort.writeString(s + " ");
            //System.out.print(s + " ");
        } catch (SerialPortException e) {
            e.printStackTrace();
            onError(e, "Serial port send error");
        }
    }

    public void write(int i){
        write("" + i);
    }

    public PinLevels digitalRead(int pin){
        write(_0_DIGITAL_READ);
        write(pin);

        resultWaiter.remove(pin);

        long timer = System.currentTimeMillis();
        final int timeout = 100;
        while(true){
            if(resultWaiter.get(pin)!=null){
                int result = resultWaiter.get(pin);
                resultWaiter.remove(pin);
                if(result<1)return LOW;
                else return HIGH;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(System.currentTimeMillis() - timer>timeout){
                onError(Protocol.Error.NO_READ_ANSWER, "No read answer");
                return NO_ANSWER;
            }
        }
    }

    public int analogRead(int pin){
        write(_2_ANALOG_READ);
        write(pin);

        resultWaiter.remove(pin);

        long timer = System.currentTimeMillis();
        final int timeout = 100;
        while(true){
            if(resultWaiter.get(pin)!=null){
                int result = resultWaiter.get(pin);
                resultWaiter.remove(pin);
                return result;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(System.currentTimeMillis() - timer>timeout){
                onError(Protocol.Error.NO_READ_ANSWER, "No read answer");
                return -1;
            }
        }
    }

    @Override
    public void digitalWrite(int pin, PinLevels pinLevel){
        int level;
        switch (pinLevel){
            case HIGH:
                level = 0x01;
                break;
            case LOW:
            default:
                level = 0x00;
                break;
        }

        write(_1_DIGITAL_WRITE);
        write(pin);
        write(level);
    }

    public void setPinInterrupt(int pin){
        write(_3_SET_PIN_INTERRUPT);
        write(pin);
    }

    public void clearPinInterrupt(int pin){
        write(_4_CLEAR_PIN_INTERRUPT);
        write(pin);
    }

    public void reset(){
        write(_BOARD_RESET);
    }

    public void analogWrite(int pin,  int pinLevel){
        write(_3_ANALOG_WRITE);
        write(pin);
        write(pinLevel);
    }

    public void pinMode(int pin, Protocol.PinModes pinMode){
        String command;
        switch (pinMode){
            case INPUT:
                command = _0_SET_PIN_MODE_INPUT;
                break;
            case INPUT_PULLUP:
                command = _1_SET_PIN_MODE_INPUT_PULLUP;
                break;
            case OUTPUT:
            default:
                command = _2_SET_PIN_MODE_OUTPUT;
                break;
        }

        write(command);
        write(pin);
    }

    public void onError(Exception e, String description){
        System.out.println("On connect error " + e);
    }

    public void onError(Protocol.Error e, String description){
        System.out.println("Error " + e);
    }

    @Override
    public long millis() {
        return 0;
    }

    @Override
    public void digitalInterruptReply(int pin, int value, long time) {

    }

    @Override
    public void digitalInterruptReply(int pin, PinLevels value, long time) {

    }

}
