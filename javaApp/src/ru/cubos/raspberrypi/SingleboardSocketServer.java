package ru.cubos.raspberrypi;

import ru.cubos.PiScetcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SingleboardSocketServer extends ServerSocketDataDecoder {

    private Socket clientSocket; //сокет для общения
    private ServerSocket socketServer; // серверсокет
    private InputStream in; // поток чтения из сокета
    private OutputStream out; // поток записи в сокет
    private int port;
    public List<byte[]> messagesToSend = new ArrayList<>();

    private Reader reader;
    private Writer writer;


    public static int serverBufferSize = 128 * 1024;
    public static int clientBufferSize = serverBufferSize;

    public static int serverBufferSize_max = serverBufferSize;
    public static int clientBufferSize_max = serverBufferSize;

    public SingleboardSocketServer(int port){
        this.port = port;
    }

    public void addMessage(byte[] message){
        messagesToSend.add(message);
        messagesToSend.add("\n".getBytes());

        if(writer==null){
            writer = new Writer();
            writer.start();
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

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                this.start();
            }
        } catch (IOException e) {
            System.err.println(e);
        }

        //}
    }

    String totalIncomeMessage = "";
    private class Reader extends Thread {
        @Override
        public void run() {
            int count;
            //byte bytes[] = new byte[16 * 1024 * 1024];
            byte bytes[] = new byte[serverBufferSize];

            try {
                while ((count = in.read(bytes)) > 0) {
                    //System.out.println("Read server: " + bytes.toString());
                    String inString = (new String(bytes, StandardCharsets.UTF_8)).substring(0, count);
                    totalIncomeMessage += inString;
                    //System.out.println("Read server: " + inString);
                    executeCommands();
                }
            } catch (IOException e) {
                e.printStackTrace();
                //return;
            }

            executeCommands();

        }

        void executeCommands(){
            while(totalIncomeMessage.indexOf('\n')!=-1){
                int stringPosition = totalIncomeMessage.indexOf('\n');
                String parseMessage = totalIncomeMessage.substring(0, stringPosition);
                //System.out.println("Parse message: " + parseMessage);
                decodeString(parseMessage);
                totalIncomeMessage = totalIncomeMessage.substring(stringPosition + 1, totalIncomeMessage.length());
            }
        }
    }

    @Override
    void write(String string){
        addMessage(string);
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

