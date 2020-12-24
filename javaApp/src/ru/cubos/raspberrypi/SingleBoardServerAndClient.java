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
        socketClient.addMessage("test");
        socketClient.addMessage("test2");
        socketClient.addMessage("test3");
        socketClient.addMessage("test4");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        socketClient.disconnect();

    }
}
