package ru.cubos;

import java.util.ArrayList;
import java.util.List;

import static ru.cubos.Protocol.*;
import static ru.cubos.Protocol.PinLevels.*;

public class Decoder {

    protected List<Byte> receivedByteList = new ArrayList<>();

    public Decoder(){
        Thread decoder = new Thread(() -> {
            while(true) {
                String s = readString();

                if(s!=null){
                    // Decoder
                    if(s.equals(_BOARD_STARTED)){
                        Thread thread = new Thread(() -> onBoardStart());
                        thread.start();
                    }else if(s.equals(_0_ERROR_UNKNOWN_COMMAND)){
                        String value = readString();
                        onErrorUnknownCommandOnBoard(value);
                    }else if(s.equals(_0_DIGITAL_READ)){
                        int pin = readInt();
                        int value = readInt();
                        digitalReadReply(pin, value);
                    }else if(s.equals(_2_ANALOG_READ)) {
                        int pin = readInt();
                        int value = readInt();
                        analogReadReply(pin, value);
                    }else if(s.equals(_4_PIN_INTERRUPT)) {
                        int pin = readInt();
                        int value = readInt();
                        long time = readLong();
                        digitalInterruptReply(pin, value, time);
                    }else{
                        decode_unknownOperation(s);
                        //break;
                        continue;

                    }


                }else{
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        decoder.start();
    }

    int readInt(){
        try {
            int value = Integer.parseInt(readString());
            return value;
        }catch (Exception ex){
            return -1;
        }
    }

    long readLong(){
        try {
            String parseString = readString();
            long value = Long.parseLong(parseString);
            return value;
        }catch (Exception ex){
            return -1;
        }
    }


    String readString(){
        String s = "";
        while(true) {
            if (receivedByteList.size() > 0) {
                Byte b = receivedByteList.get(0);
                if(b==null){
                    receivedByteList.remove(0);
                    continue;
                };
                char c = (char) (b.intValue());
                receivedByteList.remove(0);
                if(c==' ' || c=='\n'){
                    //System.out.println(s);
                    return s;
                }
                s += c;
            }else{

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
        }
    }

    protected void decode_unknownOperation(String s){
        System.out.println("decode_unknownOperation" + s);
    }

    protected void onErrorUnknownCommandOnBoard(String value){
        System.out.println("onErrorUnknowCommandOnBoard " + value);
    }

    protected void onBoardStart(){
        System.out.println("onBoardStart");
    }

    protected void analogReadReply(int pin, int value){
        System.out.println("analogReadReply " + pin + " - " + value);
    }

    protected void digitalReadReply(int pin, int value){
        System.out.println("digitaReadReply " + pin + " - " + value);
    }

    protected void digitalInterruptReply(int pin, int value, long time){
        digitalInterruptReply(pin, (value==0?LOW:HIGH), time);
    }

    protected void digitalInterruptReply(int pin, Protocol.PinLevels value, long time){
        return;
    }
}
