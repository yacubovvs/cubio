package ru.cubos.examples.arduino;

import jssc.SerialPortException;
import ru.cubos.Protocol;
import ru.cubos.arduino.ArduinoSerialConnector;
import ru.cubos.raspberrypi.RaspberryPiSocketClient;

import static ru.cubos.Protocol.PinLevels.HIGH;
import static ru.cubos.Protocol.PinLevels.LOW;
import static ru.cubos.Protocol.PinModes.INPUT;
import static ru.cubos.Protocol.PinModes.OUTPUT;

public class ArduinoSocketExample {
    public static void main(String[] args) throws SerialPortException {
        RaspberryPiSocketClient socketClient = new RaspberryPiSocketClient("10.0.0.183", 8888){
            @Override
            public void onConnect() {

                pinMode(2, INPUT);
                setPinInterrupt(2);
                /*
                while(true){
                    if(digitalRead(2)==HIGH){
                        System.out.println("ON!");
                    }else{
                        System.out.println("OFF!");
                    }

                    delay(1000);
                }*/

                /*
                pinMode(2, OUTPUT);
                while(true) {
                    delay(1000);
                    digitalWrite(2, HIGH);
                    delay(1000);
                    digitalWrite(2, LOW);
                }*/



            }

            @Override
            public void digitalInterruptReply(int pin, Protocol.PinLevels value, long time) {
                System.out.println("Interrupt " + pin + " " + value + " " + time);
            }

        };

    }
}
