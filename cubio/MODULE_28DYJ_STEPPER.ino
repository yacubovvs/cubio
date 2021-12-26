#ifndef MODULE_28DYJ_STEPPER_MOTORS_COUNT
    #define MODULE_28DYJ_STEPPER_MOTORS_COUNT 1
#endif


uint8_t stepers_pins[] = {0,0,0,0};
bool motorEnable[] = {false};
uint32_t lastStep_time[] = {0};
uint32_t step_delay[] = {10000};
uint8_t currentStep_position[] = {0};
uint32_t currentStep[] = {0};
uint32_t moveToStep[] = {0};
uint32_t maxSteps[] = {2000};
bool direction[] = {false};

void setup_MODULE_28DYJ_STEPPER(){

}

void loop_MODULE_28DYJ_STEPPER(){
    for(uint8_t motor=0; motor<MODULE_28DYJ_STEPPER_MOTORS_COUNT; motor++){
        if(motorEnable[motor] && currentStep[motor]!=moveToStep[motor] && (micros() - lastStep_time[motor]>step_delay[motor])){
            if(direction[motor]){
                MODULE_28DYJ_STEPPER_setMotorPosition( 0, MODULE_28DYJ_STEPPER_getMotorPosition(0) + 1);
                currentStep[motor] ++;
                if(currentStep[motor]>maxSteps[motor]) currentStep[motor] = 0;
                lastStep_time[motor] = micros();
                //Serial.println(currentStep[motor]);
            }else{
                MODULE_28DYJ_STEPPER_setMotorPosition( 0, 8 + MODULE_28DYJ_STEPPER_getMotorPosition(0) - 1);
                if(currentStep[motor]==0) currentStep[motor] = maxSteps[motor];
                currentStep[motor] --;
                lastStep_time[motor] = micros();
                //Serial.println(currentStep[motor]);
            }
        }
    }

}

void MODULE_28DYJ_STEPPER_setDirection(uint8_t motor, bool dir){
    direction[motor] = dir;
}

void MODULE_28DYJ_STEPPER_poweroff(uint8_t motor){
    digitalWrite(stepers_pins[motor*4 + 0], 0);
    digitalWrite(stepers_pins[motor*4 + 1], 0);
    digitalWrite(stepers_pins[motor*4 + 2], 0);
    digitalWrite(stepers_pins[motor*4 + 3], 0);
}

bool MODULE_28DYJ_STEPPER_isMoving(uint8_t motor){
    return direction[motor];
}

void MODULE_28DYJ_STEPPER_setMoveToSteps(uint8_t motor, uint32_t steps){
    steps = steps%maxSteps[motor];
    moveToStep[motor] = steps;
}

void MODULE_28DYJ_STEPPER_setMaxSteps(uint8_t motor, uint32_t steps){
    maxSteps[motor] = steps;
}

void MODULE_28DYJ_STEPPER_addMotor(uint8_t motor, uint8_t pin1, uint8_t pin2, uint8_t pin3, uint8_t pin4){
    motorEnable[motor] = true;
    stepers_pins[motor*4 + 0] = pin1;
    stepers_pins[motor*4 + 1] = pin2;
    stepers_pins[motor*4 + 2] = pin3;
    stepers_pins[motor*4 + 3] = pin4;

    pinMode(pin1, OUTPUT);
    pinMode(pin2, OUTPUT);
    pinMode(pin3, OUTPUT);
    pinMode(pin4, OUTPUT);

    digitalWrite(pin1, 0);
    digitalWrite(pin2, 0);
    digitalWrite(pin3, 0);
    digitalWrite(pin4, 0);
}


void MODULE_28DYJ_STEPPER_setStepperDelay(uint8_t motor, uint32_t stepDelay){
    step_delay[motor] = stepDelay;
}

uint8_t MODULE_28DYJ_STEPPER_getPin(uint8_t motor, byte uint8_t){
    return stepers_pins[motor];
}

uint8_t MODULE_28DYJ_STEPPER_getMotorPosition(uint8_t motor){
    return currentStep_position[motor];
}

void MODULE_28DYJ_STEPPER_setMotorPosition(uint8_t motor, uint8_t position){
    uint8_t pin1 = stepers_pins[motor*4 + 0];
    uint8_t pin2 = stepers_pins[motor*4 + 1];
    uint8_t pin3 = stepers_pins[motor*4 + 2];
    uint8_t pin4 = stepers_pins[motor*4 + 3];

    currentStep_position[motor] = position%8;

    switch (currentStep_position[motor]){
        case 0:
            digitalWrite(pin1, 1);
            digitalWrite(pin2, 0);
            digitalWrite(pin3, 0);
            digitalWrite(pin4, 0);
            break;
        case 1:
            digitalWrite(pin1, 1);
            digitalWrite(pin2, 1);
            digitalWrite(pin3, 0);
            digitalWrite(pin4, 0);
            break;
        case 2:
            digitalWrite(pin1, 0);
            digitalWrite(pin2, 1);
            digitalWrite(pin3, 0);
            digitalWrite(pin4, 0);
            break;
        case 3:
            digitalWrite(pin1, 0);
            digitalWrite(pin2, 1);
            digitalWrite(pin3, 1);
            digitalWrite(pin4, 0);
            break;
        case 4:
            digitalWrite(pin1, 0);
            digitalWrite(pin2, 0);
            digitalWrite(pin3, 1);
            digitalWrite(pin4, 0);
            break;
        case 5:
            digitalWrite(pin1, 0);
            digitalWrite(pin2, 0);
            digitalWrite(pin3, 1);
            digitalWrite(pin4, 1);
            break;
        case 6:
            digitalWrite(pin1, 0);
            digitalWrite(pin2, 0);
            digitalWrite(pin3, 0);
            digitalWrite(pin4, 1);
            break;
        case 7:
            digitalWrite(pin1, 1);
            digitalWrite(pin2, 0);
            digitalWrite(pin3, 0);
            digitalWrite(pin4, 1);
            break;
    }
}