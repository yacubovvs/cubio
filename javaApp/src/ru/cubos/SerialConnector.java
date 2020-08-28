package ru.cubos;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.util.HashMap;

import static ru.cubos.Protocol.*;

public class SerialConnector extends Decoder{

    HashMap<Integer, Integer> resultWaiter = new HashMap<>();

    @Override
    protected void digitaReadReply(byte pin, byte value){
        //System.out.println("digitaReadReply " + pin + " - " + value);
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

    boolean isStarted = false;
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

    private class PortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0){
                try {
                    byte data[] = serialPort.readBytes(event.getEventValue());
                    SerialConnector.this.decode(data);
                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
        }
    }

    void delay(int val){
        if(val<=0) return;
        try {
            Thread.sleep(val);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void digitalWrite(int pin,  int pinLevel){
        if(pinLevel>=1) digitalWrite(pin, PinLevels.HIGH);
        else digitalWrite(pin, PinLevels.LOW);
    }

    void digitalWrite(int pin,  boolean pinLevel){
        if(pinLevel) digitalWrite(pin, PinLevels.HIGH);
        else digitalWrite(pin, PinLevels.LOW);
    }

    boolean digitalRead(int pin){
        byte data[] = new byte[]{
                _0_DIGITAL_READ,
                (byte)pin
        };

        resultWaiter.remove(pin);

        try {
            serialPort.writeBytes(data);
        } catch (SerialPortException e) {
            e.printStackTrace();
            onConnectError(e);
        }

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

    void digitalWrite(int pin,  PinLevels pinLevel){
        byte level;
        switch (pinLevel){
            case HIGH:
                level = 0x01;
                break;
            case LOW:
            default:
                level = 0x00;
                break;
        }

        byte data[] = new byte[]{
                _1_DIGITAL_WRITE,
                (byte)pin,
                level,
        };

        try {
            serialPort.writeBytes(data);
        } catch (SerialPortException e) {
            e.printStackTrace();
            onConnectError(e);
        }
    }

    void analogWrite(int pin,  int pinLevel){
        byte data[] = new byte[]{
                _3_ANALOG_WRITE,
                (byte)pin,
                (byte)pinLevel,
        };

        try {
            serialPort.writeBytes(data);
        } catch (SerialPortException e) {
            e.printStackTrace();
            onConnectError(e);
        }
    }

    void pinMode(int pin, PinModes pinMode){
        byte command;
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

        byte data[] = new byte[]{
            command,
            (byte)pin,
        };

        try {
            serialPort.writeBytes(data);
        } catch (SerialPortException e) {
            e.printStackTrace();
            onConnectError(e);
            onError(Error.CONNECT_ERROR);
        }
    }

    void onConnectError(Exception e){

    }

    void onError(Error error){

    }

    public enum Error{
        CONNECT_ERROR,
        NO_READ_ANSWER
    }

}
