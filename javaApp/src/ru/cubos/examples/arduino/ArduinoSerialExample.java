package ru.cubos.examples.arduino;

import jssc.SerialPortException;
import ru.cubos.Protocol;
import ru.cubos.arduino.ArduinoSerialConnector;
import ru.cubos.modules.CounterModule;

import static ru.cubos.Protocol.PinLevels.*;
import static ru.cubos.Protocol.PinModes.*;

public class ArduinoSerialExample {
    public static void main(String[] args) throws SerialPortException {
        CounterModule counterModule = new CounterModule() {
            @Override
            public void onCounterInterrupt(int pin, int counterNumber, long millis) {
                System.out.println("Counter interrupt. Pin: " + pin + ", number: " + counterNumber + ", time " + millis);
            }
        };
        ArduinoSerialConnector arduinoSerialConnector = new ArduinoSerialConnector(){

            @Override
            public void onBoardStart(){
                System.out.println("On board start");
                delay(5000);
                System.out.println("Run");


                pinMode(2, INPUT);
                /*
                while(true){
                    delay(1000);
                    System.out.println(digitalRead(2));
                }*/

                setPinInterrupt(2);
                //counterModule.setCounter(8, 400, 0);
                //delay(3000);
                //counterModule.clearCounter(0);
                //counterModule.resetCounter();
            }

            @Override
            public void digitalInterruptReply(int pin, int value, long time){
                return;
            }

        };

        arduinoSerialConnector.setPort("COM15");
        counterModule.setConnector(arduinoSerialConnector);
        arduinoSerialConnector.addModule(counterModule);
        arduinoSerialConnector.connect();
        arduinoSerialConnector.onBoardStart();


    }
}
