package ru.cubos.raspberrypi;

import ru.cubos.PiScetcher;

public class ServerSocketDataDecoder extends PiScetcher {
    String decoderMessage;
    void decodeString(String message){
        decoderMessage = message;
        String command = readMessageString();
        System.out.print("Command found: " + command + " ");

        long parametr1 = readMessageLong();
        int parametr2 = readMessageInt();
        int parametr3 = readMessageInt();
        System.out.print(" parametr1: " + parametr1);
        System.out.print(" parametr2: " + parametr2);
        System.out.println(" parametr3: " + parametr3);
    }

    String readMessageString(){
        int position = decoderMessage.indexOf(' ');
        if (position==-1) position = decoderMessage.length();

        String command = decoderMessage.substring(0,position);
        if(position+1<decoderMessage.length()) decoderMessage = decoderMessage.substring(position+1, decoderMessage.length());
        return command;
    }

    int readMessageInt(){
        int position = decoderMessage.indexOf(' ');
        if (position==-1) position = decoderMessage.length();

        int parametr;
        try {
            parametr = Integer.parseInt(decoderMessage.substring(0, position));
        }catch (Exception e){
            return 0;
        }
        if(position+1<decoderMessage.length()) decoderMessage = decoderMessage.substring(position+1, decoderMessage.length());
        return parametr;
    }

    long readMessageLong(){
        int position = decoderMessage.indexOf(' ');
        if (position==-1) position = decoderMessage.length();

        long parametr;
        try {
            parametr = Long.parseLong(decoderMessage.substring(0, position));
        }catch (Exception e){
            return 0;
        }
        if(position+1<decoderMessage.length()) decoderMessage = decoderMessage.substring(position+1, decoderMessage.length());
        return parametr;
    }
}
