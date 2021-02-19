package ru.cubos.examples.arduino;

import jssc.SerialPortException;
import ru.cubos.Protocol;
import ru.cubos.arduino.ArduinoSerialConnector;
import ru.cubos.modules.CounterModule;

import static ru.cubos.Protocol.PinLevels.*;
import static ru.cubos.Protocol.PinModes.*;

public class ArduinoSerialExample {
    static final int TICKS_IN_ENCODER_COUNTER = 600;
    static int millisDelay_test = 0;

    public static void main(String[] args) throws SerialPortException {
        CounterModule counterModule = new CounterModule() {
            @Override
            public void onCounterInterrupt(int pin, int counterNumber, long millis) {
                //System.out.println("Counter interrupt. Pin: " + pin + ", number: " + counterNumber + ", time " + millis);

                //double speed = 128.349 * TICKS_IN_ENCODER_COUNTER / (millis * 300.0);
                double speed = 128.349 * TICKS_IN_ENCODER_COUNTER / (millis * 600.0);
                millisDelay_test = (int)(8.0/100.0/speed*1000.0) + 300;
                System.out.printf("Angle speed " + speed + " mps\n");
                System.out.printf("Rejecter " + millisDelay_test + " ms\n");
            }
        };
        ArduinoSerialConnector arduinoSerialConnector = new ArduinoSerialConnector(){

            @Override
            public void onBoardStart(){
                //clearPinInterrupt(7);
                //clearPinInterrupt(6);
                //clearPinInterrupt(5);
                //clearPinInterrupt(4);

                pinMode(2, INPUT);
                pinMode(6, INPUT);
                pinMode(7, INPUT);
                pinMode(8, INPUT);

                pinMode(4, OUTPUT);
                //pinMode(6, INPUT);
                //pinMode(5, INPUT);
                //pinMode(4, INPUT);



                counterModule.setCounter(2, TICKS_IN_ENCODER_COUNTER, 0);

                //setPinInterrupt(2);
                setPinInterrupt(6);
                setPinInterrupt(7);
                setPinInterrupt(8);

                /*
                while(true){
                    delay(1000);
                    digitalWrite(3, HIGH);
                    delay(1000);
                    digitalWrite(3, LOW);
                }*/

                digitalWrite(4, 1);
            }

            @Override
            public void digitalInterruptReply(int pin, int value, long time){
                System.out.println("OnInterrupt: pin: " + pin + ", value: " + value + ", time: " + time);


                if(pin==7 && value == 1){
                    (new Thread(new Runnable() {
                        @Override
                        public void run() {
                            delay(millisDelay_test);
                            digitalWrite(4, 0);
                            delay(250);
                            digitalWrite(4, 1);
                        }
                    })).start();
                }
            }

        };

        //arduinoSerialConnector.setPort("COM24");
        arduinoSerialConnector.setPort("/dev/cu.usbserial-1410");
        counterModule.setConnector(arduinoSerialConnector);
        arduinoSerialConnector.addModule(counterModule);
        arduinoSerialConnector.connect();

        //arduinoSerialConnector.onBoardStart();


    }
}
