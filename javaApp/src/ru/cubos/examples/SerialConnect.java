package ru.cubos.examples;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import ru.cubos.LightSwitch;

import static ru.cubos.Protocol.*;

public class SerialConnect {
    public static void main(String[] args) {
        //serialPort = new SerialPort("COM11");
        //serialPort = new SerialPort("//dev//cu.usbserial-1420");
        LightSwitch lightSwitch = new LightSwitch();
        //lightSwitch.setPort("COM11");
        //lightSwitch.setPort("//dev//cu.usbserial-1420");
        //lightSwitch.setPort("//dev//cu.usbserial-1420");

        lightSwitch.setPort("COM13");

        try {
            lightSwitch.connect();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

    }
}
