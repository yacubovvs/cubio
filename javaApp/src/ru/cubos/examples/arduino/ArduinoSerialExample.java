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
                delay(1000);
                System.out.println("On board start");

                clearPinInterrupt(2);
                pinMode(2, INPUT_PULLUP);
                //setPinInterrupt(2);

                //pinMode(2, INPUT_PULLUP);
                //setPinInterrupt(2);

                counterModule.setCounter(2, 1000, 0);
            }

            @Override
            public void digitalInterruptReply(int pin, int value, long time){
                System.out.println("OnInterrupt: pin: " + pin + ", value: " + value + ", time: " + time);
            }

        };

        arduinoSerialConnector.setPort("COM13");
        //arduinoSerialConnector.setPort("/dev/cu.usbserial-1410");
        counterModule.setConnector(arduinoSerialConnector);
        arduinoSerialConnector.addModule(counterModule);
        arduinoSerialConnector.connect();

        arduinoSerialConnector.onBoardStart();


    }
}
