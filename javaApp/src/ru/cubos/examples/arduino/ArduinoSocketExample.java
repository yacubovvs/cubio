package ru.cubos.examples.arduino;

import jssc.SerialPortException;
import ru.cubos.Protocol;
import ru.cubos.arduino.ArduinoSerialConnector;
import ru.cubos.raspberrypi.RaspberryPiSocketClient;

import static ru.cubos.Protocol.PinModes.INPUT;
import static ru.cubos.Protocol.PinModes.OUTPUT;

public class ArduinoSocketExample {
    public static void main(String[] args) throws SerialPortException {
        RaspberryPiSocketClient socketClient = new RaspberryPiSocketClient("10.0.0.183", 8888){
            @Override
            public void onConnect() {
                //setPinInterrupt(2);
                pinMode(2, OUTPUT);

                while(true){
                    digitalWrite(2, 0);
                    delay(1000);
                    digitalWrite(2, 1);
                    delay(1000);
                }

            }

            @Override
            public void digitalInterruptReply(int pin, Protocol.PinLevels value, long time) {
                System.out.println("Interrupt " + pin + " " + value + " " + time);
            }

        };

    }
}
