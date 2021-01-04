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
        int pin;
        int value;

        switch(command){
            case _BOARD_RESET:
                reset();
                break;
            case _0_SET_PIN_MODE_INPUT:
                pin = readInt();
                pinMode(pin, INPUT);
                break;
            case _1_SET_PIN_MODE_INPUT_PULLUP:
                pin = readInt();
                pinMode(pin, INPUT_PULLUP);
                break;
            case _2_SET_PIN_MODE_OUTPUT:
                pin = readInt();
                pinMode(pin, OUTPUT);
                break;
            case _3_SET_PIN_INTERRUPT:
                pin = readInt();
                setPinInterrupt(pin);
                break;
            case _4_CLEAR_PIN_INTERRUPT:
                pin = readInt();
                clearPinInterrupt(pin);
                break;
            case _0_DIGITAL_READ:
                pin = readInt();
                write(_0_DIGITAL_READ);
                write(pin);
                write((digitalRead(pin) == true ? 1 : 0));
                break;
            case _1_DIGITAL_WRITE:
                pin = readInt();
                value = readInt();
                digitalWrite(pin, value);
                break;
            case _2_ANALOG_READ:
                pin = readInt();
                value = analogRead(pin);
                write(_2_ANALOG_READ);
                write(pin);
                write(value);
                break;
            case _3_ANALOG_WRITE:
                pin = readInt();
                value = readInt();
                analogWrite(pin, value);
                break;
            default:
                write(_0_ERROR_UNKNOWN_COMMAND);
                write(command);
                break;
        }
    }

    void write(String string){
        System.out.println("Write to client string " + string);
    }

    void write(Number number){
        System.out.println("Write to client number " + number);
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
