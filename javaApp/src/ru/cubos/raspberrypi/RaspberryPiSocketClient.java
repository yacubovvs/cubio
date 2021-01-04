package ru.cubos.raspberrypi;

import ru.cubos.Connector;
import ru.cubos.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import static ru.cubos.Protocol.PinLevels.*;
import static ru.cubos.Protocol.PinModes.*;
import static ru.cubos.raspberrypi.SingleboardSocketServer.clientBufferSize_max;

public class RaspberryPiSocketClient implements Connector {

    private static Socket clientSocket;
    private static InputStream in;
    private static OutputStream out;
    private int port;
    private String addr;
    private List<byte[]> messagesToSend = new ArrayList<>();
    private Reader reader;
    private Writer writer;

    public static void main(String[] args) {
        RaspberryPiSocketClient socketClient = new RaspberryPiSocketClient("10.0.0.154", 8000);
        socketClient.run();
    }

    void run(){
        pinMode(2, INPUT);
        //addMessage("o 2");

        while (true){
            //digitalWrite(2, 0);
            digitalRead(2);
            delay(1000);
        }

    }

    public void addMessage(String message){
        message.trim();
        message += "\n";
        messagesToSend.add(message.getBytes());

        if(writer==null){
            writer = new Writer();
            writer.start();
        }
    }

    public void disconnect(){
        try {
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public RaspberryPiSocketClient(final String addr, final int port){

        try {
            //clientSocket = new Socket(addr, port);
            clientSocket = new Socket();

            InetAddress addr_obj = InetAddress.getByName(addr);
            clientSocket.connect(new InetSocketAddress( addr_obj, port));
            clientSocket.setReceiveBufferSize(clientBufferSize_max);

            this.addr = addr;
            this.port = port;

            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();

            reader = new Reader();
            writer = new Writer();

            reader.start();
            writer.start();

            // Sending screen params

        } catch (IOException e) {
            e.printStackTrace();
            //System.out.println("Error starting socket client");
            onError(Protocol.Error.CONNECT_ERROR, "Error starting socket client");
            return;
        }

    }

    List<Integer> digitalReadArray = new ArrayList<>();

    @Override
    public boolean digitalRead(int pin) {
        addMessage(Protocol._0_DIGITAL_READ + " " + pin);
        //readWaitArray.add()
        return false;
    }

    @Override
    public int analogRead(int pin) {
        return 0;
    }

    @Override
    public void digitalWrite(int pin, Protocol.PinLevels pinLevel) {
        addMessage(Protocol._1_DIGITAL_WRITE + " " + pin + " " + (pinLevel== HIGH?1:0));
    }

    @Override
    public void setPinInterrupt(int pin) {

    }

    @Override
    public void clearPinInterrupt(int pin) {

    }

    @Override
    public void reset() {

    }

    @Override
    public void analogWrite(int pin, int pinLevel) {

    }

    @Override
    public void pinMode(int pin, Protocol.PinModes pinMode) {
        switch (pinMode){
            case OUTPUT:
                addMessage(Protocol._2_SET_PIN_MODE_OUTPUT + " " + pin);
                break;
            case INPUT:
                addMessage(Protocol._0_SET_PIN_MODE_INPUT + " " + pin);
                break;
            case INPUT_PULLUP:
                addMessage(Protocol._1_SET_PIN_MODE_INPUT_PULLUP + " " + pin);
                break;
        }
    }

    @Override
    public void onError(Exception e, String description) {

    }

    @Override
    public void onError(Protocol.Error e, String description) {

    }

    @Override
    public void digitalInterruptReply(int pin, int value, long time) {

    }

    @Override
    public long millis() {
        return 0;
    }

    private class Reader extends Thread {

        @Override
        public void run() {

            while (true) {
                int count;
                //byte bytes[] = new byte[clientBufferSize];
                byte bytes[] = new byte[256];

                try {
                    while ((count = in.read(bytes)) > 0) {
                        System.out.println("Read client: " + bytes.toString());
                    }
                }catch(SocketException e){
                    if(clientSocket.isClosed()) {
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

            }
        }
    }


    public class Writer extends Thread {

        @Override
        public void run() {
            while (messagesToSend.size()>0) {

                try {
                    byte data[] = messagesToSend.get(0);
                    out.write(data);
                    out.flush();
                    messagesToSend.remove(data);

                } catch (IOException e) {}

            }

            writer = null;
        }
    }
}

