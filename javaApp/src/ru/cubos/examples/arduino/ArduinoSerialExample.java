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
                clearPinInterrupt(7);
                //clearPinInterrupt(6);
                //clearPinInterrupt(5);
                //clearPinInterrupt(4);

                pinMode(2, INPUT);
                pinMode(7, INPUT);
                pinMode(6, INPUT);
                pinMode(4, INPUT);

                pinMode(3, OUTPUT);
                //pinMode(6, INPUT);
                //pinMode(5, INPUT);
                //pinMode(4, INPUT);



                counterModule.setCounter(2, 300, 0);

                setPinInterrupt(7);
                setPinInterrupt(6);
                //setPinInterrupt(5);
                setPinInterrupt(4);

                while(true){
                    delay(1000);
                    digitalWrite(3, HIGH);
                    delay(1000);
                    digitalWrite(3, LOW);
                }
            }

            @Override
            public void digitalInterruptReply(int pin, int value, long time){
                System.out.println("OnInterrupt: pin: " + pin + ", value: " + value + ", time: " + time);
            }

        };

        //arduinoSerialConnector.setPort("COM13");
        arduinoSerialConnector.setPort("/dev/cu.usbserial-1410");
        counterModule.setConnector(arduinoSerialConnector);
        arduinoSerialConnector.addModule(counterModule);
        arduinoSerialConnector.connect();

        //arduinoSerialConnector.onBoardStart();


    }
}
