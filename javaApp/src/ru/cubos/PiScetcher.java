package ru.cubos;

import ru.cubos.connectors.Connector;
import ru.cubos.connectors.SingleBoardSocketConnector;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.FileWriter;
import java.io.IOException;
import ru.cubos.Protocol.PinLevels.*;

import static ru.cubos.Protocol.PinLevels.*;
import static ru.cubos.Protocol.PinModes.*;

public class PiScetcher implements Connector {

    public static void main(String[] args) {
        PiScetcher piScetcher = new PiScetcher();
        piScetcher.test();
    }

    public void test(){
        pinMode(23, OUTPUT);
        pinMode(25, OUTPUT);
        pinMode(24, OUTPUT);
        pinMode(27, OUTPUT);
        while(true){
            digitalWrite(23, HIGH);
            delay(1000);
            digitalWrite(24, HIGH);
            delay(1000);
            digitalWrite(25, HIGH);
            delay(1000);
            digitalWrite(27, HIGH);
            delay(1000);
            digitalWrite(23, LOW);
            delay(1000);
            digitalWrite(24, LOW);
            delay(1000);
            digitalWrite(25, LOW);
            delay(1000);
            digitalWrite(27, LOW);
            delay(1000);
        }


    }

    @Override
    public void digitalReadReply(int pin, int value) {

    }

    @Override
    public void analogReadReply(int pin, int value) {

    }

    @Override
    public boolean digitalRead(int pin) {
        return false;
    }

    @Override
    public int analogRead(int pin) {
        return 0;
    }

    @Override
    public void digitalWrite(int pin, Protocol.PinLevels pinLevel) {
        try {
            if(pinLevel==HIGH) writeDataToFile("/sys/class/gpio/gpio" + pin + "/value", "" + 1);
            else writeDataToFile("/sys/class/gpio/gpio" + pin + "/value", "" + 0);
        } catch (IOException e) {
            onError(e, "digitalRead");
        }
    }

    @Override
    public void setPinInterrupt(int pin) {

    }

    @Override
    public void clearPinInterrupt(int pin) {

    }

    @Override
    public void reset() {

    }

    @Override
    public void analogWrite(int pin, int pinLevel) {

    }

    @Override
    public void pinMode(int pin, Protocol.PinModes pinMode) {
        try {
            writeDataToFile("/sys/class/gpio/export", "" + pin);

        } catch (IOException e) {
            //onError(new Exception(), "Enabling pin warning");
        }

        try {
            switch (pinMode){
                case OUTPUT:
                    writeDataToFile("/sys/class/gpio/gpio" + pin + "/direction", "out");
                    break;
                case INPUT_PULLUP:
                    System.out.println("INPUT_PULLUP is not supported in Single Boards CubIO");
                case INPUT:
                    writeDataToFile("/sys/class/gpio/gpio" + pin + "/direction", "in");
                    break;
            }

        } catch (IOException e) {
            onError(e, "setting pinMode");
        }
    }

    @Override
    public void onError(Exception e, String description) {
        if(description.length()!=0) System.out.println("Error: " + description);
        e.printStackTrace();
    }

    @Override
    public void onError(Protocol.Error e, String description) {
        System.out.println("Error: " + e.toString());
        if(description.length()!=0) System.out.println("Error: " + description);
    }


    public void writeDataToFile(String path, String value) throws IOException {
        FileWriter writer = new FileWriter(path, false);
        writer.write(value);
        writer.flush();
        writer.close();
    }
}
