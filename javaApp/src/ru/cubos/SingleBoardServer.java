package ru.cubos;

import ru.cubos.raspberrypi.RaspberryPiSocketConnector;

public class SingleBoardServer {
    public static void main(String[] args) {

        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SingleboardSocketServer socketServer = new SingleboardSocketServer(4);
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


        RaspberryPiSocketConnector socketConnector = new RaspberryPiSocketConnector("10.0.0.153", 4);
        socketConnector.addMessage("Message".getBytes());

    }
}
