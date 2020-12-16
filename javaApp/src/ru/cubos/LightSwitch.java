package ru.cubos;

import ru.cubos.connectors.SerialConnector;

import static ru.cubos.Protocol.PinModes.*;

public class LightSwitch extends SerialConnector {

    @Override
    protected void onBoardStart(){
        System.out.println("On board start");
        pinMode(6, OUTPUT);
        pinMode(5, OUTPUT);
        pinMode(3, OUTPUT);
        //digitalWrite(3, HIGH);
        //reset();

        pinMode(11, INPUT_PULLUP);
        pinMode(12, INPUT_PULLUP);
        pinMode(10, INPUT);
        setPinInterrupt(10);
        setPinInterrupt(11);
        setPinInterrupt(12);

        while(true) {
            digitalWrite(6, 1);
            delay(1000);
            digitalWrite(6, 0);
            digitalWrite(5, 1);
            delay(1000);
            digitalWrite(5, 1);
            digitalWrite(3, 1);
            delay(1000);
            digitalWrite(3, 0);
            digitalWrite(6, 0);
            delay(1000);
        }


        /*
        pinMode(3, OUTPUT);
        pinMode(5, OUTPUT);
        pinMode(6, OUTPUT);

        digitalWrite(3, 0);
        digitalWrite(5, 0);
        digitalWrite(6, 0);

        pinMode(11, INPUT_PULLUP);
        pinMode(12, INPUT_PULLUP);
        pinMode(10, INPUT);

        setPinInterrupt(11);
        setPinInterrupt(10);
        setPinInterrupt(12);


        delay(2000);




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
    protected void digitalInterruptReply(int pin, int value){
        System.out.println("Digital interrupt pin " + pin + " - " + value);
    }

}
