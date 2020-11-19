package ru.cubos;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.util.HashMap;

import static ru.cubos.Protocol.*;

abstract public class SerialConnector extends Decoder{

    HashMap<Integer, Integer> resultWaiter = new HashMap<>();

    public SerialConnector(){
        super();
    }

    @Override
    protected void digitalReadReply(int pin, int value){
        resultWaiter.put((int)pin, (int)value);
    }

    @Override
    protected void analogReadReply(int pin, int value){
        resultWaiter.put((int)pin, (int)value);
    }

    public enum PinModes{
        OUTPUT,
        INPUT,
        INPUT_PULLUP
    }

    public enum PinLevels{
        HIGH,
        LOW
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

    protected void delay(int val){
        if(val<=0) return;
        try {
            Thread.sleep(val);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void digitalWrite(int pin,  int pinLevel){
        if(pinLevel>=1) digitalWrite(pin, PinLevels.HIGH);
        else digitalWrite(pin, PinLevels.LOW);
    }

    protected void digitalWrite(int pin,  boolean pinLevel){
        if(pinLevel) digitalWrite(pin, PinLevels.HIGH);
        else digitalWrite(pin, PinLevels.LOW);
    }

    protected void write(String s){
        try {
            serialPort.writeString(s + " ");
            //System.out.print(s + " ");
        } catch (SerialPortException e) {
            e.printStackTrace();
            onConnectError(e);
        }
    }

    protected void write(int i){
        write("" + i);
    }

    protected boolean digitalRead(int pin){
        write(_0_DIGITAL_READ);
        write(pin);

        resultWaiter.remove(pin);

        long timer = System.currentTimeMillis();
        final int timeout = 100;
        while(true){
            if(resultWaiter.get(pin)!=null){
                int result = resultWaiter.get(pin);
                resultWaiter.remove(pin);
                if(result<1)return false;
                else return true;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(System.currentTimeMillis() - timer>timeout){
                onError(Error.NO_READ_ANSWER);
                return false;
            }
        }
    }

    protected int analogRead(int pin){
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
                onError(Error.NO_READ_ANSWER);
                return -1;
            }
        }
    }

    protected void digitalWrite(int pin,  PinLevels pinLevel){
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

    protected void setPinInterrupt(int pin){
        write(_3_SET_PIN_INTERRUPT);
        write(pin);
    }

    protected void clearPinInterrupt(int pin){
        write(_4_CLEAR_PIN_INTERRUPT);
        write(pin);
    }

    protected void reset(){
        write(_BOARD_RESET);
    }

    protected void analogWrite(int pin,  int pinLevel){
        write(_3_ANALOG_WRITE);
        write(pin);
        write(pinLevel);
    }

    protected void pinMode(int pin, PinModes pinMode){
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

    void onConnectError(Exception e){
        System.out.println("On connect error " + e);
    }

    void onError(Error e){
        System.out.println("Error " + e);
    }

    public enum Error{
        CONNECT_ERROR,
        NO_READ_ANSWER
    }

}
