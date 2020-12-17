package ru.cubos;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SingleboardSocketServer {

    private Socket clientSocket; //сокет для общения
    private ServerSocket socketServer; // серверсокет
    private InputStream in; // поток чтения из сокета
    private OutputStream out; // поток записи в сокет
    private int port;
    public List<byte[]> messagesToSend = new ArrayList<>();

    private Reader reader;
    private Writer writer;

    public static int clientBufferSize = 2 * 1024 * 1024;
    public static int serverBufferSize = 1024 * 1024;

    public static int clientBufferSize_max = 2 * 1024 * 1024;
    public static int serverBufferSize_max = 1024 * 1024;

    public SingleboardSocketServer(int port){
        this.port = port;
    }

    public void addMessage(byte[] message){
        messagesToSend.add(message);

        if(writer==null){
            writer = new Writer();
            writer.start();
        }
    }

    public void start(){
        //while (true) {
        try {
            try {
                //socketServer = new java.net.ServerSocket(port);
                socketServer = new java.net.ServerSocket();
                socketServer.setReceiveBufferSize(serverBufferSize_max);
                socketServer.bind(new InetSocketAddress( (InetAddress) null, port));
                System.out.println("Socket server started at port " + port);

                clientSocket = socketServer.accept();

                try {
                    String dataString = "";

                    in = clientSocket.getInputStream();
                    out = clientSocket.getOutputStream();

                    reader = new Reader();
                    writer = new Writer();

                    reader.start();
                    writer.start();
                } catch (Exception e) {
                    System.out.println("Server error #1");
                } finally {
                    //clientSocket.close();
                    //in.close();
                    //out.close();
                }
            } catch (Exception e) {

                System.out.println("Server error #2");
            } finally {
                System.out.println("Server closed!");
                if(socketServer!=null)socketServer.close();
            }
        } catch (IOException e) {
            System.err.println(e);
        }

        //}
    }

    private class Reader extends Thread {
        @Override
        public void run() {
            int count;
            //byte bytes[] = new byte[16 * 1024 * 1024];
            byte bytes[] = new byte[serverBufferSize];

            try {
                while ((count = in.read(bytes)) > 0) {
                    System.out.println("Read server: " + bytes.toString());

                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public class Writer extends Thread {

        @Override
        public void run() {
            while (messagesToSend.size()>0) {

                if(out==null){
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

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

