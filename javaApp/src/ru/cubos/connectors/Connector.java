package ru.cubos.connectors;

import static ru.cubos.Protocol.*;

public interface Connector {

    void digitalReadReply(int pin, int value);
    void analogReadReply(int pin, int value);
    void delay(int val);
    void digitalWrite(int pin,  int pinLevel);
    void digitalWrite(int pin,  boolean pinLevel);
    boolean digitalRead(int pin);
    int analogRead(int pin);
    void digitalWrite(int pin,  PinLevels pinLevel);
    void setPinInterrupt(int pin);
    void clearPinInterrupt(int pin);
    void reset();
    void analogWrite(int pin,  int pinLevel);
    void pinMode(int pin, PinModes pinMode);
    void onConnectError(Exception e);
}
