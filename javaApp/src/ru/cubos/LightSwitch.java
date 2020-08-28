package ru.cubos;

import jssc.SerialPortException;

import static ru.cubos.Protocol._3_ANALOG_WRITE;
import static ru.cubos.SerialConnector.PinLevels.*;
import static ru.cubos.SerialConnector.PinModes.*;

public class LightSwitch extends SerialConnector {

    @Override
    protected void onBoardStart(){
        System.out.println("On board start");
        pinMode(3, OUTPUT);
        pinMode(5, OUTPUT);
        pinMode(6, OUTPUT);

        analogWrite(3, 2);
        analogWrite(5, 16);
        analogWrite(6, 255);

        //analogWrite(3, 0);
        /*
        pinMode(3, OUTPUT);
        pinMode(5, OUTPUT);
        pinMode(6, OUTPUT);

        pinMode(12, INPUT_PULLUP);
        pinMode(11, INPUT_PULLUP);
        pinMode(10, INPUT);

        while(true) {
            if(!digitalRead(12)) digitalWrite(6, HIGH);
            else digitalWrite(6, LOW);

            if(!digitalRead(11)) digitalWrite(5, HIGH);
            else digitalWrite(5, LOW);

            if(digitalRead(10)) digitalWrite(3, HIGH);
            else digitalWrite(3, LOW);

            delay(10);
        }
        */


    }

    @Override
    void onError(Error error){
        System.out.println("Error: " + error.name());
    }

}
