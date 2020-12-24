package ru.cubos;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ru.cubos.Protocol.PinLevels.*;
import static ru.cubos.Protocol.PinModes.*;

public class PiScetcher implements Connector {
    static final int INTERRUPT_DAEMON_DELAY_MS = 10;
    HashMap<Integer, Boolean> interruptDigitalPinsValues = new HashMap<>();
    Thread interruptDaemon = null;


    public  PiScetcher(){ }

    void createInterruptDaemon(){
        interruptDaemon = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    // Checking pins interrupt
                    for(Map.Entry<Integer, Boolean> entry : interruptDigitalPinsValues.entrySet()) {
                        Integer pin = entry.getKey();
                        Boolean value = entry.getValue();
                        boolean currentValue = digitalRead(pin);

                        if(currentValue!=value.booleanValue()){
                            interruptDigitalPinsValues.put((Integer)pin, currentValue);
                            digitalInterruptReply(pin, (currentValue?1:0));
                        }
                    }
                    delay(INTERRUPT_DAEMON_DELAY_MS);
                }
            }
        });

        interruptDaemon.start();
    }

    public static void main(String[] args) {
        PiScetcher piScetcher = new PiScetcher();
        piScetcher.test();
    }

    public void test(){

        pinMode(20, INPUT);
        pinMode(26, INPUT);
        pinMode(16, INPUT);
        pinMode(19, INPUT);

        pinMode(23, OUTPUT);
        pinMode(25, OUTPUT);
        pinMode(24, OUTPUT);
        pinMode(27, OUTPUT);

        delay(500);

        setPinInterrupt(20);
        setPinInterrupt(26);
        setPinInterrupt(16);
        setPinInterrupt(19);


        /*
        while(true) {
            if (digitalRead(20)) {
                digitalWrite(23, HIGH);
            } else {
                digitalWrite(23, LOW);
            }

            if (digitalRead(26)) {
                digitalWrite(25, HIGH);
            } else {
                digitalWrite(25, LOW);
            }

            if (digitalRead(16)) {
                digitalWrite(24, HIGH);
            } else {
                digitalWrite(24, LOW);
            }

            if (digitalRead(19)) {
                digitalWrite(27, HIGH);
            } else {
                digitalWrite(27, LOW);
            }
        }*/

        //delay(100);

            /*
            digitalWrite(23, HIGH);
            delay(100);
            digitalWrite(24, HIGH);
            delay(100);
            digitalWrite(25, HIGH);
            delay(100);
            digitalWrite(27, HIGH);
            delay(100);
            digitalWrite(23, LOW);
            delay(100);
            digitalWrite(24, LOW);
            delay(100);
            digitalWrite(25, LOW);
            delay(100);
            digitalWrite(27, LOW);
            delay(100);
            }
            */

        //delay(10);

    }

    @Override
    public boolean digitalRead(int pin) {
        if(readDataFromFile("/sys/class/gpio/gpio" + pin + "/value").equals("1")) return true;
        else return false;
    }

    @Override
    public int analogRead(int pin) {
        onWarning("Analog read is not supported in single board computers like RaspberryPI or OrangePI");
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
        interruptDigitalPinsValues.put(pin, digitalRead(pin));
        if(interruptDaemon==null) createInterruptDaemon();
    }

    @Override
    public void clearPinInterrupt(int pin) {
        interruptDigitalPinsValues.remove((Integer)pin);
    }

    @Override
    public void reset() {

    }

    @Override
    public void analogWrite(int pin, int pinLevel) {
    }

    public void onWarning(String warning){
        System.out.println("Warning: " + warning);
    };

    @Override
    public void pinMode(int pin, Protocol.PinModes pinMode) {
        try {
            writeDataToFile("/sys/class/gpio/export", "" + pin);

        } catch (IOException e) {
            onWarning("Pin already activated");
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

    @Override
    public void digitalInterruptReply(int pin, int value) {
        System.out.println("Interrupt on pin " + pin + " with level " + value);
    }


    public void writeDataToFile(String path, String value) throws IOException {
        FileWriter writer = new FileWriter(path, false);
        writer.write(value);
        writer.flush();
        writer.close();
    }

    public String readDataFromFile(String path){
        try {
            FileReader reader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String result = bufferedReader.readLine();
            bufferedReader.close();
            reader.close();
            return result.trim();
        } catch (FileNotFoundException e) {
            onError(e, "Pin file not exist");
            return "";
        } catch (IOException e) {
            onError(e, "Cant read pin file");
            return "";
        }
    }
}
