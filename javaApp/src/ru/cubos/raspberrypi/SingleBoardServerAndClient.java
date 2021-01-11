package ru.cubos.raspberrypi;

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
            void onConnect() {
                pinMode(2, INPUT);
                //setPinInterrupt(2);
                //while(true) {
                    if (digitalRead(2)) {
                        System.out.println("2 pin in on");
                    } else {
                        System.out.println("2 pin in off");
                    }
                //}
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
