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
                    onBoardStart();
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
}
