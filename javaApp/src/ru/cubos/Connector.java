package ru.cubos;

import static ru.cubos.Protocol.*;

public interface Connector {
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
    PinLevels digitalRead(int pin);
    int analogRead(int pin);
    void digitalWrite(int pin,  PinLevels pinLevel);
    void setPinInterrupt(int pin);
    void clearPinInterrupt(int pin);
    void reset();
    void analogWrite(int pin,  int pinLevel);
    void pinMode(int pin, PinModes pinMode);
    void onError(Exception e, String description);
    void onError(Protocol.Error e, String description);
    void digitalInterruptReply(int pin, int value, long time);

    void digitalInterruptReply(int pin, PinLevels value, long time);

    void write(String string);

    long millis();
}
