package ru.cubos.examples;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import ru.cubos.LightSwitch;
import ru.cubos.SingleboardSocketServer;
import ru.cubos.connectors.SingleBoardSocketConnector;

import static ru.cubos.Protocol.*;

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


        SingleBoardSocketConnector socketConnector = new SingleBoardSocketConnector("10.0.0.153", 4);
        socketConnector.addMessage("Message".getBytes());

    }
}
