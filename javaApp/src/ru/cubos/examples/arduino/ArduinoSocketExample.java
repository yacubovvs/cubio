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
        RaspberryPiSocketClient socketClient = new RaspberryPiSocketClient(){
            @Override
            public void onConnect() {

                pinMode(2, INPUT);
                setPinInterrupt(2);

            }

            @Override
            public void digitalInterruptReply(int pin, Protocol.PinLevels value, long time) {
                System.out.println("Interrupt " + pin + " " + value + " " + time);
            }

        };

        CounterModule counterModule = new CounterModule(socketClient) {
            @Override
            public void onCounterInterrupt(int pin, int counterNumber, long millis) {

            }
        };

        socketClient.moduleList.add(counterModule);
        socketClient.connect("10.0.0.183", 8888);


    }
}
