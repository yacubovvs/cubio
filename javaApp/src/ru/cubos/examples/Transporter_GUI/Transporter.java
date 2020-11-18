package ru.cubos.examples.Transporter_GUI;

import jssc.SerialPortException;
import ru.cubos.LightSwitch;
import ru.cubos.SerialConnector;

import javax.swing.*;

import static ru.cubos.SerialConnector.PinModes.*;
import static ru.cubos.SerialConnector.PinLevels.*;

public class Transporter extends JFrame{
    private JLabel sensor1Result;
    private JLabel sensor2Result;
    private JPanel mainpanel;

    public static void main(String[] args) {
        new Transporter();
    }

    public Transporter(){
        SerialConnector serialConnector = new SerialConnector() {
            @Override
            protected void decode_unknownOperation(String s) {
                super.decode_unknownOperation(s);
            }

            @Override
            protected void onErrorUnknownCommandOnBoard(String value) {
                super.onErrorUnknownCommandOnBoard(value);
            }

            @Override
            protected void onBoardStart() {
                super.onBoardStart();

                pinMode(8, INPUT_PULLUP);
                pinMode(9, INPUT_PULLUP);

                setPinInterrupt(8);
                setPinInterrupt(9);
            }

            @Override
            protected void digitalInterruptReply(int pin, int value) {
                super.digitalInterruptReply(pin, value);

                if(pin == 8){
                    if(value==1) sensor1Result.setText("OFF");
                    else sensor1Result.setText("ON");
                }

                if(pin == 9){
                    if(value==1) sensor2Result.setText("OFF");
                    else sensor2Result.setText("ON");
                }
            }
        };
        //lightSwitch.setPort("COM11");
        //lightSwitch.setPort("//dev//cu.usbserial-1420");
        //lightSwitch.setPort("//dev//cu.usbserial-1420");

        serialConnector.setPort("COM5");

        try {
            serialConnector.connect();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }

        setContentPane(mainpanel);
        setSize(600,300);
        setVisible(true);

    }
}
