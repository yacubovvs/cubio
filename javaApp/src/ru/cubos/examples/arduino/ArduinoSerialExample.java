package ru.cubos.examples.arduino;

import jssc.SerialPortException;
import ru.cubos.Protocol;
import ru.cubos.arduino.ArduinoSerialConnector;

import static ru.cubos.Protocol.PinLevels.*;
import static ru.cubos.Protocol.PinModes.*;

public class ArduinoSerialExample {
    public static void main(String[] args) throws SerialPortException {
        ArduinoSerialConnector arduinoSerialConnector = new ArduinoSerialConnector(){

            @Override
            public void onBoardStart(){
                System.out.println("On board start");


                pinMode(2, INPUT_PULLUP);

                //setPinInterrupt(7);
                //pinMode(2, INPUT);
                //setPinInterrupt(2);

                while(true){
                    if(digitalRead(2)==HIGH){
                        System.out.println("ON!");
                    }else{
                        System.out.println("OFF!");
                    }

                    delay(1000);
                }

                /*pinMode(2, INPUT);
                pinMode(3, INPUT_PULLUP);
                pinMode(4, INPUT_PULLUP);
                pinMode(5, INPUT_PULLUP);
                pinMode(6, INPUT_PULLUP);
                pinMode(7, INPUT_PULLUP);

                pinMode(8, INPUT_PULLUP);
                pinMode(9, INPUT_PULLUP);
                pinMode(10, INPUT_PULLUP);

                setPinInterrupt(2);
                setPinInterrupt(3);
                setPinInterrupt(4);
                setPinInterrupt(5);
                setPinInterrupt(6);
                setPinInterrupt(7);

                setPinInterrupt(8);
                setPinInterrupt(9);
                setPinInterrupt(10);

                while(true) {
                    if(digitalRead(2)==HIGH){
                        System.out.println("ON");
                    }else{
                        System.out.println("OFF");
                    }

                    delay(500);
                }
                    */
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

            int encoder_count = 0;
            long lastTime = 0;
            @Override
            public void digitalInterruptReply(int pin, int value, long time){

                /*
                if(pin==ENCODER_PIN){
                    if(lastTime<=0){
                        lastTime = time;
                        return;
                    }
                    encoder_count = 0;
                    System.out.println(time - lastTime);
                    lastTime = time;
                }*/
            }

        };
        arduinoSerialConnector.setPort("COM13");
        arduinoSerialConnector.connect();
        arduinoSerialConnector.onBoardStart();

    }
}
