package ru.cubos;

import ru.cubos.modules.Module;

import java.util.ArrayList;
import java.util.List;

import static ru.cubos.Protocol.*;
import static ru.cubos.Protocol.PinLevels.*;

public class Decoder {

    public List<Module> moduleList = new ArrayList<>();
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

                        boolean isModuleCommand = false;
                        for(Module module: moduleList){
                            if(module.decode(s, this)){
                                isModuleCommand = true;
                                break;
                            }
                        }

                        if(!isModuleCommand) decode_unknownOperation(s);

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

    public void addModule(Module module){
        moduleList.add(module);
    }

    public int readInt(){
        try {
            int value = Integer.parseInt(readString());
            return value;
        }catch (Exception ex){
            return -1;
        }
    }

    public long readLong(){
        try {
            String parseString = readString();
            long value = Long.parseLong(parseString);
            return value;
        }catch (Exception ex){
            return -1;
        }
    }


    public String readString(){
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
                    s = s.trim();
                    if(s.length()>0) return s;
                }else{
                    s += c;
                }

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
