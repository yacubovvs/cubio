package ru.cubos.examples.arduino;

import jssc.SerialPortException;
import ru.cubos.Protocol;
import ru.cubos.arduino.ArduinoSerialConnector;
import ru.cubos.modules.CounterModule;
import ru.cubos.modules.Module;
import ru.cubos.raspberrypi.RaspberryPiSocketClient;

import static ru.cubos.Protocol.PinLevels.HIGH;
import static ru.cubos.Protocol.PinLevels.LOW;
import static ru.cubos.Protocol.PinModes.*;

public class ArduinoSocketExample {
    public static void main(String[] args){

        CounterModule counterModule = new CounterModule() {
            @Override
            public void onCounterInterrupt(int pin, int counterNumber, long millis) {
                System.out.println("Counter interrupt. Pin: " + pin + ", number: " + counterNumber + ", time " + millis);
            }
        };

        RaspberryPiSocketClient socketClient = new RaspberryPiSocketClient(){
            @Override
            public void onConnect() {

                int PIN = 11;

                pinMode(PIN, INPUT_PULLUP);
                //counterModule.resetCounter();
                counterModule.setCounter(PIN, 10, 0);
                //setPinInterrupt(PIN);

            }

            @Override
            public void digitalInterruptReply(int pin, Protocol.PinLevels value, long time) {
                System.out.println("Interrupt " + pin + " " + value + " " + time);
            }

        };


        counterModule.setConnector(socketClient);
        socketClient.addModule(counterModule);
        socketClient.connect("10.0.0.193", 8888);
        //socketClient.connect("192.168.1.33", 8888);


    }
}
