package ru.cubos.examples.arduino;

import jssc.SerialPortException;
import ru.cubos.arduino.LightSwitch;

import static ru.cubos.Protocol.PinModes.*;

public class ArduinoSerialExample {
    public static void main(String[] args) throws SerialPortException {
        LightSwitch lightSwitch = new LightSwitch(){
            @Override
            protected void onBoardStart(){
                System.out.println("On board start");
                pinMode(10, OUTPUT);
                pinMode(11, OUTPUT);

                //digitalWrite(3, HIGH);
                //reset();

                pinMode(9, INPUT_PULLUP);
                pinMode(8, INPUT_PULLUP);

                setPinInterrupt(9);
                setPinInterrupt(8);


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
                }
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
