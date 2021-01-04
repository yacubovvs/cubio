package ru.cubos.examples.arduino;

import jssc.SerialPortException;
import ru.cubos.arduino.LightSwitch;

import static ru.cubos.Protocol.PinModes.*;

public class ArduinoSerialExample {
    public static void main(String[] args) throws SerialPortException {
        LightSwitch lightSwitch = new LightSwitch(){
            @Override
            public void onBoardStart(){
                System.out.println("On board start");


                pinMode(2, INPUT_PULLUP);
                pinMode(3, INPUT_PULLUP);
                pinMode(4, INPUT_PULLUP);
                pinMode(5, INPUT_PULLUP);
                pinMode(6, INPUT_PULLUP);
                pinMode(7, INPUT_PULLUP);

                setPinInterrupt(2);
                setPinInterrupt(3);
                setPinInterrupt(4);
                setPinInterrupt(5);
                setPinInterrupt(6);
                setPinInterrupt(7);


                /*
                while(true) {
                    digitalWrite(11, 0);
                    digitalWrite(10, 1);
                    delay(1000);
                    digitalWrite(11, 1);
                    digitalWrite(10, 1);
                    delay(1000);
                    digitalWrite(10, 0);
                    digitalWrite(11, 1);
                    delay(1000);
                    digitalWrite(10, 0);
                    digitalWrite(11, 0);
                    delay(1000);
                }*/
            }

            @Override
            public void digitalInterruptReply(int pin, int value, long time){
                System.out.println(" - Digital interrupt pin " + pin + " - " + value + " on time " + time);
            }

        };
        //lightSwitch.setPort("COM13");
        lightSwitch.setPort("/dev/ttyUSB0");
        lightSwitch.connect();
    }
}
