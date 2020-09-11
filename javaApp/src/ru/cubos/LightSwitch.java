package ru.cubos;

import jssc.SerialPortException;

import static ru.cubos.Protocol._3_ANALOG_WRITE;
import static ru.cubos.SerialConnector.PinLevels.*;
import static ru.cubos.SerialConnector.PinModes.*;

public class LightSwitch extends SerialConnector {

    @Override
    protected void onBoardStart(){
        System.out.println("On board start");
        pinMode(14, INPUT);
        while(true) {
            System.out.println("Analog value: " + analogRead(14));
            analogRead(14);
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

        while(true) {
            digitalWrite(6, 1);
            delay(1000);
            digitalWrite(6, 0);
            digitalWrite(5, 1);
            delay(1000);
            digitalWrite(5, 0);
            digitalWrite(3, 1);
            delay(1000);
            digitalWrite(3, 0);
        }
         */


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

    @Override
    protected void digitalInterruptReply(byte pin, byte value){
        System.out.println("Digital interrupt pin " + pin + " - " + value);
    }

}
