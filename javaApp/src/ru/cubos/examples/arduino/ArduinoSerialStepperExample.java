package ru.cubos.examples.arduino;

import jssc.SerialPortException;
import ru.cubos.arduino.ArduinoSerialConnector;
import ru.cubos.modules.BYJStepperMotorModule;
import ru.cubos.modules.CounterModule;

public class ArduinoSerialStepperExample {

    static BYJStepperMotorModule motorModule;

    public static void main(String[] args) throws SerialPortException {

        ArduinoSerialConnector arduinoSerialConnector = new ArduinoSerialConnector(){

            @Override
            public void onBoardStart(){
                motorModule = new BYJStepperMotorModule(this) {
                    @Override
                    protected void onMotorMovingResponse(int motor, boolean isMoving) {
                        if(isMoving) System.out.println("Motor " + motor + " is moving");
                        else System.out.println("Motor " + motor + " is not moving");
                    }
                };

                motorModule.setConnector(this);
                this.addModule(motorModule);

                motorModule.addMotor(0, 2, 3, 4, 5);
                motorModule.setStepDelay(0, 1600);
                motorModule.setMaxSteps(0, 4096);

                /*
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(true) {
                            motorModule.isMoving(0);
                            delay(300);
                        }
                    }
                });
                thread.start();
                */

                while(true){
                    motorModule.moveToStep(0, 4096/4, true);
                    delay(2000);
                    motorModule.powerOff(0);
                    delay(1000);
                    motorModule.moveToStep(0, 0, false);
                    delay(2000);
                    motorModule.powerOff(0);
                    delay(1000);
                }


            }

            @Override
            public void digitalInterruptReply(int pin, int value, long time){
            }

        };

        //arduinoSerialConnector.setPort("COM24");
        //arduinoSerialConnector.setPort("/dev/cu.usbserial-1410");
        arduinoSerialConnector.setPort("/dev/ttyUSB0");
        arduinoSerialConnector.connect();

        //arduinoSerialConnector.onBoardStart();


    }
}
