package ru.cubos.raspberrypi;

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

        RaspberryPiSocketClient socketClient = new RaspberryPiSocketClient("127.0.0.1", 8000);
        socketClient.addMessage("test 1000 500 40");
        socketClient.addMessage("test2 1001 501 41");


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        socketClient.disconnect();

    }
}
