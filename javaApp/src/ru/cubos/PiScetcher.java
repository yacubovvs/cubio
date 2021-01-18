package ru.cubos;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static ru.cubos.Protocol.PinLevels.*;
import static ru.cubos.Protocol.PinModes.*;

public class PiScetcher implements Connector {
    static final int INTERRUPT_DAEMON_DELAY_MS = 10;
    HashMap<Integer, Protocol.PinLevels> interruptDigitalPinsValues = new HashMap<>();
    Thread interruptDaemon = null;

    long appStartMillis;

    public  PiScetcher(){
        appStartMillis = System.currentTimeMillis();
    }

    void createInterruptDaemon(){
        interruptDaemon = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    long current_time = millis();
                    // Checking pins interrupt
                    for(Map.Entry<Integer, Protocol.PinLevels> entry : interruptDigitalPinsValues.entrySet()) {
                        Integer pin = entry.getKey();
                        Protocol.PinLevels value = entry.getValue();
                        Protocol.PinLevels currentValue = digitalRead(pin);

                        if(currentValue!=value){
                            interruptDigitalPinsValues.put((Integer)pin, currentValue);
                            digitalInterruptReply(pin, (currentValue==HIGH?1:0), current_time);
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

        setPinInterrupt(20);
        setPinInterrupt(26);
        setPinInterrupt(16);
        setPinInterrupt(19);

    }

    @Override
    public Protocol.PinLevels digitalRead(int pin) {
        if(readDataFromFile("/sys/class/gpio/gpio" + pin + "/value").equals("1")) return HIGH;
        else return LOW;
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
            onError(e, "digitalWrite");
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
    public void digitalInterruptReply(int pin, int value, long time) {
        digitalInterruptReply(pin, (value==0?LOW:HIGH), time);
    }

    @Override
    public void digitalInterruptReply(int pin, Protocol.PinLevels value, long time){
        return;
    }

    @Override
    public void write(String string) {
        System.out.println("No protocol write enabled in PiScetcher");
    }

    @Override
    public long millis() {
        return System.currentTimeMillis() - appStartMillis;
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
