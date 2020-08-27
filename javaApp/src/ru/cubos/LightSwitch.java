package ru.cubos;

import jssc.SerialPortException;

import static ru.cubos.Protocol.*;

public class LightSwitch extends SerialConnector {

    @Override
    protected void onBoardStart(){
        System.out.println("OnBoardStart");

        byte data[] = new byte[]{
                _2_SET_PIN_MODE_OUTPUT,
                0x03, // D3

                _1_DIGITAL_WRITE,
                0x03, // D3
                0x01, // HIGH
        };

        try {
            serialPort.writeBytes(data);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

}
