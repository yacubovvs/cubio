package ru.cubos.raspberrypi;

import ru.cubos.Connector;
import ru.cubos.Decoder;
import ru.cubos.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static ru.cubos.Protocol.Error.*;
import static ru.cubos.Protocol.PinLevels.*;
import static ru.cubos.Protocol.*;
import static ru.cubos.raspberrypi.SingleboardSocketServer.*;

public abstract class RaspberryPiSocketClient extends Decoder implements Connector {

    private static Socket clientSocket;
    private static InputStream in;
    private static OutputStream out;
    private int port;
    private String addr;
    private List<byte[]> messagesToSend = new ArrayList<>();
    private Reader reader;
    private Writer writer;
    final int timeout = 1000;

    HashMap<Integer, Integer> resultWaiter = new HashMap<>();

    public static void main(String[] args) {
        RaspberryPiSocketClient socketClient = new RaspberryPiSocketClient(){
            @Override
            public void onConnect() {

            }
        };

        socketClient.connect("10.0.0.154", 8000);

    }

    public abstract void onConnect();

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


    long appStartMillis;

    public RaspberryPiSocketClient(){

    }

    public void connect(final String addr, final int port){

        appStartMillis = System.currentTimeMillis();

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

            onConnect();

        } catch (IOException e) {
            e.printStackTrace();
            //System.out.println("Error starting socket client");
            onError(CONNECT_ERROR, "Error starting socket client");
            return;
        }

    }


    @Override
    public PinLevels digitalRead(int pin) {
        addMessage(_0_DIGITAL_READ + " " + pin);

        resultWaiter.remove(pin);

        long timer = System.currentTimeMillis();
        while(true){
            if(resultWaiter.get(pin)!=null){
                int result = resultWaiter.get(pin);
                resultWaiter.remove(pin);
                if(result<1)return LOW;
                else return HIGH;
            }

            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(System.currentTimeMillis() - timer>timeout){
                onError(NO_READ_ANSWER, "No read answer");
                return NO_ANSWER;
            }
        }
    }

    @Override
    public int analogRead(int pin) {
        addMessage(_2_ANALOG_READ + " " + pin);

        resultWaiter.remove(pin);

        long timer = System.currentTimeMillis();

        while(true){
            if(resultWaiter.get(pin)!=null){
                int result = resultWaiter.get(pin);
                resultWaiter.remove(pin);
                return result;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(System.currentTimeMillis() - timer>timeout){
                onError(NO_READ_ANSWER, "No read answer");
                return -1;
            }
        }
    }

    @Override
    public void digitalWrite(int pin, Protocol.PinLevels pinLevel) {
        addMessage(_1_DIGITAL_WRITE + " " + pin + " " + (pinLevel== HIGH?1:0));
    }

    @Override
    public void setPinInterrupt(int pin) {
        addMessage(_3_SET_PIN_INTERRUPT + " " + pin);
    }

    @Override
    public void clearPinInterrupt(int pin) {
        addMessage(_4_CLEAR_PIN_INTERRUPT + " " + pin);
    }

    @Override
    public void reset() {
        addMessage(_BOARD_RESET);
    }

    @Override
    public void analogWrite(int pin, int pinLevel) {
        addMessage(_3_ANALOG_WRITE + " " + pin + " " + pinLevel);
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
        System.out.println("On Error " + description);
        System.out.println(e);
        return;
    }

    @Override
    public void onError(Protocol.Error e, String description) {
        System.out.println("On Error " + description);
        System.out.println(e);
        return;
    }

    @Override
    public void digitalReadReply(int pin, int value){
        resultWaiter.put((int)pin, (int)value);
    }

    @Override
    public void analogReadReply(int pin, int value){
        resultWaiter.put((int)pin, (int)value);
    }

    @Override
    public void digitalInterruptReply(int pin, int value, long time) {
        digitalInterruptReply(pin, (value==0?LOW:HIGH), time);
    }

    @Override
    public void digitalInterruptReply(int pin, PinLevels value, long time) {

    }

    @Override
    public long millis() {
        return System.currentTimeMillis() - appStartMillis;
    }

    String totalIncomeMessage = "";
    private class Reader extends Thread {

        @Override
        public void run() {

            int count;
            //byte bytes[] = new byte[16 * 1024 * 1024];
            byte bytes[] = new byte[clientBufferSize];

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
    }

    void executeCommands(){
        while(totalIncomeMessage.indexOf('\n')!=-1){
            int stringPosition = totalIncomeMessage.indexOf('\n');
            String parseMessage = totalIncomeMessage.substring(0, stringPosition);
            //System.out.println("Parse message: " + parseMessage);
            //decodeString(parseMessage);
            for(int i=0; i<totalIncomeMessage.getBytes().length; i++){
                receivedByteList.add(totalIncomeMessage.getBytes()[i]);
            }

            totalIncomeMessage = totalIncomeMessage.substring(stringPosition + 1, totalIncomeMessage.length());
        }
    }

    @Override
    public void write(String string) {
        messagesToSend.add(string.trim().getBytes());

        if(writer==null){
            writer = new Writer();
            writer.start();
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

