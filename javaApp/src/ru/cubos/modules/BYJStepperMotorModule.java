package ru.cubos.modules;

import ru.cubos.Connector;
import ru.cubos.Decoder;

public abstract class BYJStepperMotorModule extends Module {

    final static String _MODULE_28DYJ_STEPPER_ADD_MOTOR         = "MODULE_28DYJ_STEPPER_a";
    final static String _MODULE_28DYJ_STEPPER_SET_STEPS         = "MODULE_28DYJ_STEPPER_s";
    final static String _MODULE_28DYJ_STEPPER_SET_MAX_STEPS     = "MODULE_28DYJ_STEPPER_S";
    final static String _MODULE_28DYJ_STEPPER_IS_MOVING         = "MODULE_28DYJ_STEPPER_m";
    final static String _MODULE_28DYJ_STEPPER_SET_STEP_DELAY    = "MODULE_28DYJ_STEPPER_d";
    final static String _MODULE_28DYJ_STEPPER_POWEROFF          = "MODULE_28DYJ_STEPPER_p";

    public BYJStepperMotorModule() {}
    public BYJStepperMotorModule(Connector connector) {
        super(connector);
    }

    public void addMotor(int motor, int pin1, int pin2, int pin3, int pin4){
        connector.write(_MODULE_28DYJ_STEPPER_ADD_MOTOR + " " + motor + " " + pin1 + " " + pin2 + " " + pin3 + " " + pin4 + " ");
    }

    public void setMaxSteps(int motor, int steps){
        connector.write(_MODULE_28DYJ_STEPPER_SET_MAX_STEPS + " " + motor + " " + steps + " ");
    }

    public void setStepDelay(int motor, int delay){
        connector.write(_MODULE_28DYJ_STEPPER_SET_STEP_DELAY + " " + motor + " " + delay + " ");
    }

    public void moveToStep(int motor, int step, boolean direction){
        connector.write(_MODULE_28DYJ_STEPPER_SET_STEPS + " " + motor + " " + step + " " + (direction?1:0) + " ");
    }

    public void powerOff(int motor){
        connector.write(_MODULE_28DYJ_STEPPER_POWEROFF + " " + motor + " ");
    }

    public void isMoving(int motor){
        connector.write(_MODULE_28DYJ_STEPPER_IS_MOVING + " " + motor + " ");
    }

    @Override
    public boolean decode(String string, Decoder decoder) {
        switch (string){
            case _MODULE_28DYJ_STEPPER_IS_MOVING:
                int motor = decoder.readInt();
                boolean isMoving = decoder.readInt()==1;
                onMotorMovingResponse(motor, isMoving);
                return true;
        }
        return false;
    }

    protected abstract void onMotorMovingResponse(int motor, boolean isMoving);
}
