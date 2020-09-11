package ru.cubos;

import static ru.cubos.Protocol.*;

public class Decoder {

    byte current_command_tree[] = new byte[8];
    byte current_command_position = 0;
    long command_summ = 0;

    public boolean decode(byte data[]){
        byte pin, value, value2;

        for(int i=0; i<data.length; i++) {
            switch (data[i]) {
                case _BOARD_STARTED:
                    Thread thread = new Thread(() -> onBoardStart());
                    thread.start();
                    break;
                case _0_DIGITAL_READ:
                    if(data.length-i<2) return false;
                    pin = data[i+1];
                    value = data[i+2];
                    digitalReadReply(pin, value);
                    i+=2;
                    break;
                case _2_ANALOG_READ:
                    if(data.length-i<3) return false;
                    pin = data[i+1];
                    value = data[i+2];
                    value2 = data[i+3];

                    analogReadReply(pin, (Byte.toUnsignedInt(value2)<<8) + Byte.toUnsignedInt(value));
                    i+=3;
                    break;
                case _4_PIN_INTERRUPT:
                    if(data.length-i<2) return false;
                    pin = data[i+1];
                    value = data[i+2];
                    digitalInterruptReply(pin, value);
                    i+=2;
                    break;
                case _0_ERROR_UNKNOWN_COMMAND:
                    if(data.length-i<1) return false;
                    value = data[i+1];
                    onErrorUnknownCommandOnBoard(value);
                    i+=2;
                    break;
                default:
                    decode_unknownOperation();
                    break;
            }
        }

        return true;
    }

    protected void decode_unknownOperation(){
        System.out.println("decode_unknownOperation");
    }

    protected void onErrorUnknownCommandOnBoard(byte value){
        System.out.println("onErrorUnknowCommandOnBoard " + (int)value);
    }

    protected void onBoardStart(){
        System.out.println("onBoardStart");
    }

    protected void analogReadReply(byte pin, int value){
        System.out.println("analogReadReply " + pin + " - " + value);
    }

    protected void digitalReadReply(byte pin, byte value){
        System.out.println("digitaReadReply " + pin + " - " + value);
    }

    protected void digitalInterruptReply(byte pin, byte value){
        System.out.println("digitaInterruptReply " + pin + " - " + value);
    }
}
