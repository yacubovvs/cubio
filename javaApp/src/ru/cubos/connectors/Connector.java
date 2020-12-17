package ru.cubos.connectors;

import ru.cubos.Protocol;

import static ru.cubos.Protocol.*;

public interface Connector {

    void digitalReadReply(int pin, int value);
    void analogReadReply(int pin, int value);
    default void delay(long val){
        if(val<=0) return;
        try {
            Thread.sleep(val);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    default void digitalWrite(int pin,  int pinLevel){
        if(pinLevel==0) digitalWrite(pin, Protocol.PinLevels.LOW);
        else digitalWrite(pin, Protocol.PinLevels.HIGH);
    };
    default void digitalWrite(int pin,  boolean pinLevel){
        if(pinLevel) digitalWrite(pin, Protocol.PinLevels.HIGH);
        else digitalWrite(pin, Protocol.PinLevels.LOW);
    };
    boolean digitalRead(int pin);
    int analogRead(int pin);
    void digitalWrite(int pin,  PinLevels pinLevel);
    void setPinInterrupt(int pin);
    void clearPinInterrupt(int pin);
    void reset();
    void analogWrite(int pin,  int pinLevel);
    void pinMode(int pin, PinModes pinMode);
    void onError(Exception e, String description);
    void onError(Protocol.Error e, String description);
}
