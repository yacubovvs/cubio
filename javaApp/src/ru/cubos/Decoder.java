package ru.cubos;

import static ru.cubos.Protocol.*;

public class Decoder {

    byte current_command_tree[] = new byte[8];
    byte current_command_position = 0;
    long command_summ = 0;

    public boolean decode(byte data[]){

        for(int i=0; i<data.length; i++) {
            switch (data[i]) {
                case _BOARD_STARTED:
                    Thread thread = new Thread(() -> onBoardStart());
                    thread.start();
                    break;
                case _0_DIGITAL_READ:
                    if(data.length-i<2) return false;
                    byte pin = data[i+1];
                    byte value = data[i+2];
                    digitaReadReply(pin, value);
                    i+=2;
                    break;
                case _0_ERROR_UNKNOWN_COMMAND:
                    onErrorUnknowCommandOnBoard();
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

    protected void onErrorUnknowCommandOnBoard(){
        System.out.println("onErrorUnknowCommandOnBoard");
    }

    protected void onBoardStart(){
        System.out.println("onBoardStart");
    }

    protected void digitaReadReply(byte pin, byte value){
        System.out.println("digitaReadReply " + pin + " - " + value);
    }
}
