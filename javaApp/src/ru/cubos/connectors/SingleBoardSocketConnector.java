package ru.cubos.connectors;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static ru.cubos.SingleboardSocketServer.clientBufferSize_max;

public class SingleBoardSocketConnector{

    private static Socket clientSocket;
    private static InputStream in;
    private static OutputStream out;
    private int port;
    private String addr;
    private List<byte[]> messagesToSend = new ArrayList<>();
    private Reader reader;
    private Writer writer;

    public void addMessage(byte[] message){
        messagesToSend.add(message);

        if(writer==null){
            writer = new Writer();
            writer.start();
        }
    }



    public SingleBoardSocketConnector(final String addr, final int port){

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
            System.out.println("Error starting socket client");
            return;
        }

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
