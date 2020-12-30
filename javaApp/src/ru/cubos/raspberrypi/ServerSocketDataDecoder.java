package ru.cubos.raspberrypi;

import ru.cubos.PiScetcher;

import static ru.cubos.Protocol.*;
import static ru.cubos.Protocol.PinModes.*;

public class ServerSocketDataDecoder extends PiScetcher {
    String decoderMessage;
    void decodeString(String message){
        decoderMessage = message;
        /*
        String command = readCommang();
        System.out.print("Command found: " + command + " ");

        long parametr1 = readLong();
        int parametr2 = readInt();
        int parametr3 = readInt();
        System.out.print(" parametr1: " + parametr1);
        System.out.print(" parametr2: " + parametr2);
        System.out.println(" parametr3: " + parametr3);
        */

        String command = readCommand();

        if(command==_BOARD_RESET){
            reset();
        }else if(command==_0_SET_PIN_MODE_INPUT){
            pinMode(readInt(), INPUT);
        }else if(command==_1_SET_PIN_MODE_INPUT_PULLUP){
            pinMode(readInt(), INPUT_PULLUP);
        }else if(command==_2_SET_PIN_MODE_OUTPUT){
            pinMode(readInt(), OUTPUT);
        }else if(command==_3_SET_PIN_INTERRUPT){
            setPinInterrupt(readInt());
        }else if(command==_4_CLEAR_PIN_INTERRUPT){
            clearPinInterrupt(readInt());
        }else if(command==_0_DIGITAL_READ){
            int pin = readInt();
            write(_0_DIGITAL_READ);
            write(pin);
            write((digitalRead(pin)==true?1:0));
        }else if(command==_1_DIGITAL_WRITE){
            int pin = readInt();
            int value = readInt();
            digitalWrite(pin, value);
        }else if(command==_2_ANALOG_READ){
            int pin = readInt();
            int value = analogRead(pin);
            write(_2_ANALOG_READ);
            write(pin);
            write(value);
        }else if(command==_3_ANALOG_WRITE){
            int pin = readInt();
            int value = readInt();
            analogWrite(pin, value);
        }else{
            write(_0_ERROR_UNKNOWN_COMMAND);
            write(command);
        }
    }

    void write(String string){
        System.out.println("Write to client " + string);
    }

    void write(Number number){
        System.out.println("Write to client " + number);
    }



    String readCommand(){
        int position = decoderMessage.indexOf(' ');
        if (position==-1) position = decoderMessage.length();

        String command = decoderMessage.substring(0,position);
        if(position+1<decoderMessage.length()) decoderMessage = decoderMessage.substring(position+1, decoderMessage.length());
        return command;
    }

    int readInt(){
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

    long readLong(){
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
