package ru.cubos.raspberrypi;

import ru.cubos.Protocol;

import static ru.cubos.Protocol.PinLevels.*;
import static ru.cubos.Protocol.PinModes.*;

public class SingleBoardServerAndClient {
    public static void main(String[] args) {

        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SingleboardSocketServer socketServer = new SingleboardSocketServer(8000);
                socketServer.start();
                System.out.println("...server started");
            }
        });
        serverThread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        RaspberryPiSocketClient socketClient = new RaspberryPiSocketClient("127.0.0.1", 8000){
            @Override
            public void onConnect() {

                pinMode(2, OUTPUT);
                pinMode(3, OUTPUT);
                pinMode(4, OUTPUT);
                pinMode(17, OUTPUT);

                pinMode(27, INPUT);
                pinMode(22, INPUT);
                pinMode(10, INPUT);
                pinMode(9, INPUT);

                setPinInterrupt(27);
                setPinInterrupt(22);
                setPinInterrupt(10);
                setPinInterrupt(9);
                /*
                while(true) {
                    if(digitalRead(27)==HIGH) digitalWrite(2, true);
                    else if(digitalRead(27)==LOW) digitalWrite(2, false);

                    if(digitalRead(22)==HIGH) digitalWrite(3, true);
                    else if(digitalRead(22)==LOW) digitalWrite(3, false);

                    if(digitalRead(10)==HIGH) digitalWrite(4, true);
                    else if(digitalRead(10)==LOW) digitalWrite(4, false);

                    if(digitalRead(9)==HIGH) digitalWrite(17, true);
                    else if(digitalRead(9)==LOW) digitalWrite(17, false);

                    delay(100);

                }*/

            }

            @Override
            public void digitalInterruptReply(int pin, Protocol.PinLevels value, long time) {
                System.out.println(time);
                switch (pin){
                    case 27:
                        digitalWrite(2, value);
                        break;
                    case 22:
                        digitalWrite(3, value);
                        break;
                    case 10:
                        digitalWrite(4, value);
                        break;
                    case 9:
                        digitalWrite(17, value);
                        break;
                }
            }

        };




        /*


            if(socketClient.digitalRead(2)){
                System.out.println("2 pin in on");
            }else{
                System.out.println("2 pin in off");
            }
            socketClient.delay(100);
        }
        */


        /*
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        socketClient.disconnect();
        */

    }
}
