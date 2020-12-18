package ru.cubos.arduino;

import ru.cubos.arduino.SerialConnector;

import static ru.cubos.Protocol.PinModes.*;

public class LightSwitch extends SerialConnector {

    @Override
    protected void onBoardStart(){



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
    public void digitalInterruptReply(int pin, int value){
        System.out.println("Digital interrupt pin " + pin + " - " + value);
    }

}
