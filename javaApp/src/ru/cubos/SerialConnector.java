package ru.cubos;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;


public class SerialConnector extends Decoder{
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

}
