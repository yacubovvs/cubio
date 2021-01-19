/*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* *                                                                     * *
* *                           CUBIO PWM MODULE                          * *
* *                                                                     * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/

#ifdef MODULE_PWM_PCA9685
  #include <Wire.h>
  #include <Adafruit_PWMServoDriver.h>

  #define MODULE_PWM_PCA9685_STATUS_NOT_INITED     0x00
  #define MODULE_PWM_PCA9685_STATUS_INITED         0x01
  #define MODULE_PWM_PCA9685_STATUS_INIT_ERROR     0x02

  byte module_pwm_status = MODULE_PWM_PCA9685_STATUS_NOT_INITED;
  
  // default I2C address 0x40 
  Adafruit_PWMServoDriver pwm = Adafruit_PWMServoDriver();
  
  #define SERVOMIN  50 // This is the 'minimum' pulse length count (out of 4096)
  #define SERVOMAX  600 // This is the 'maximum' pulse length count (out of 4096)
  #define USMIN  600 // This is the rounded 'minimum' microsecond length based on the minimum pulse of 150
  #define USMAX  2400 // This is the rounded 'maximum' microsecond length based on the maximum pulse of 600
  #define SERVO_FREQ 50 // Analog servos run at ~50 Hz updates
  
  void setup_MODULE_PWM_PCA9685(){
    pwm.begin();
    pwm.setOscillatorFrequency(27000000);
    pwm.setPWMFreq(SERVO_FREQ);  // Analog servos run at ~50 Hz updates
  
    delay(10);
    module_pwm_status = MODULE_PWM_PCA9685_STATUS_INITED;
  }

  long pwm_data[] = {
  //OLD_PWM       NEW_PWM       PWM_IN_SECOND   LAST_TIME_CHANGE
    0,            0,            0,              0,            //  0
    0,            0,            0,              0,            //  1
    0,            0,            0,              0,            //  2
    0,            0,            0,              0,            //  3
    0,            0,            0,              0,            //  4
    0,            0,            0,              0,            //  5
    0,            0,            0,              0,            //  6
    0,            0,            0,              0,            //  7
    0,            0,            0,              0,            //  8
    0,            0,            0,              0,            //  9
    0,            0,            0,              0,            //  10
    0,            0,            0,              0,            //  10
    0,            0,            0,              0,            //  11
    0,            0,            0,              0,            //  12
    0,            0,            0,              0,            //  13
    0,            0,            0,              0,            //  14
    0,            0,            0,              0,            //  15
  };

  void setStartPWM_MODULE_PWM_PCA9685(byte NUM, int PWM){
    if(PWM<0) PWM = 0;
    if(PWM>1000) PWM = 1000;
    pwm_data[NUM*4 + 0] = PWM;
    pwm_data[NUM*4 + 1] = PWM;
    pwm_data[NUM*4 + 3] = millis();
  }

  void setPWM_MODULE_PWM_PCA9685(byte NUM, int NEW_PWM, int PWN_IN_SECONDS){
    if(NEW_PWM<0) NEW_PWM = 0;
    if(NEW_PWM>1000) NEW_PWM = 1000;
    pwm_data[NUM*4 + 1] = NEW_PWM;
    pwm_data[NUM*4 + 2] = PWN_IN_SECONDS;
    pwm_data[NUM*4 + 3] = millis();
  }

  void loop_MODULE_PWM_PCA9685(){
    
    for(byte num=0; num<16; num++){
      if(pwm_data[num*4]!=pwm_data[num*4 + 1]){

        long old_pwn          = pwm_data[num*4];
        long new_pwn          = pwm_data[num*4 + 1];
        long pwm_in_seconds   = pwm_data[num*4 + 2];
        long lastTimeChange   = pwm_data[num*4 + 3];
        long iterationPWM     = (millis() - lastTimeChange)*pwm_in_seconds/1000;
        
        if( iterationPWM > 0 ){
          if(abs((int)old_pwn - (int)new_pwn)<iterationPWM){
            old_pwn = new_pwn;;
          }else if(old_pwn>new_pwn){
            old_pwn -= iterationPWM;
          }else{
            old_pwn += iterationPWM;
          }

          uint16_t valueToPWM = map(old_pwn, 0, 1000, SERVOMIN, SERVOMAX);
          pwm.setPWM(num, 0, valueToPWM);
          
          pwm_data[num*4] = old_pwn;
          pwm_data[num*4 + 3] = millis();
          
        }
      }
    }
  }

  byte getStatus_MODULE_PWM_PCA9685(){
    return module_pwm_status;  
  }

#endif

//m_pshw_p 0 0 500 
//m_pshw_p 0 830 500 
