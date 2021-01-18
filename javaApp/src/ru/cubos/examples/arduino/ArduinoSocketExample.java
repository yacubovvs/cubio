package ru.cubos.examples.arduino;

import jssc.SerialPortException;
import ru.cubos.Protocol;
import ru.cubos.arduino.ArduinoSerialConnector;
import ru.cubos.modules.CounterModule;
import ru.cubos.modules.Module;
import ru.cubos.raspberrypi.RaspberryPiSocketClient;

import static ru.cubos.Protocol.PinLevels.HIGH;
import static ru.cubos.Protocol.PinLevels.LOW;
import static ru.cubos.Protocol.PinModes.INPUT;
import static ru.cubos.Protocol.PinModes.OUTPUT;

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

                pinMode(2, INPUT);
                counterModule.setCounter(2, 10, 0);
                //setPinInterrupt(2);
                //counterModule.setCounter(2, 10, 0);

                delay(2000);
                counterModule.clearCounter(0);
                clearPinInterrupt(2);
                //counterModule.resetCounter();



            }

            @Override
            public void digitalInterruptReply(int pin, Protocol.PinLevels value, long time) {
                System.out.println("Interrupt " + pin + " " + value + " " + time);
            }

        };


        counterModule.setConnector(socketClient);
        socketClient.addModule(counterModule);
        //socketClient.connect("10.0.0.183", 8888);
        socketClient.connect("192.168.1.33", 8888);


    }
}
