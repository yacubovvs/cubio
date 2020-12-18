package ru.cubos.arduino;

import jssc.SerialPortException;

import static ru.cubos.Protocol.PinModes.*;

public class ArduinoSerialExample {
    public static void main(String[] args) throws SerialPortException {
        LightSwitch lightSwitch = new LightSwitch(){
            @Override
            protected void onBoardStart(){
                System.out.println("On board start");
                pinMode(6, OUTPUT);
                pinMode(5, OUTPUT);
                pinMode(3, OUTPUT);
                //digitalWrite(3, HIGH);
                //reset();

                pinMode(11, INPUT_PULLUP);
                pinMode(12, INPUT_PULLUP);
                pinMode(10, INPUT);
                setPinInterrupt(10);
                setPinInterrupt(11);
                setPinInterrupt(12);

                while(true) {
                    digitalWrite(6, 1);
                    delay(1000);
                    digitalWrite(6, 0);
                    digitalWrite(5, 1);
                    delay(1000);
                    digitalWrite(5, 1);
                    digitalWrite(3, 1);
                    delay(1000);
                    digitalWrite(3, 0);
                    digitalWrite(6, 0);
                    delay(1000);
                }
            }
        };
        lightSwitch.setPort("COM13");
        lightSwitch.connect();
    }
}
