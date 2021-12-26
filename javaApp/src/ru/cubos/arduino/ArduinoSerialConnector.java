package ru.cubos.arduino;

import ru.cubos.arduino.SerialConnector;

import static ru.cubos.Protocol.PinModes.*;

public class ArduinoSerialConnector extends SerialConnector {

    @Override
    public void onBoardStart(){
    }

    @Override
    public void digitalInterruptReply(int pin, int value, long time){
        System.out.println("Digital interrupt pin " + pin + " - " + value + " on time " + time);
    }

}
