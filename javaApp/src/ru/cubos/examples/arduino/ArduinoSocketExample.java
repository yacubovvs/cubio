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
