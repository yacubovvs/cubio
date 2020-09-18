/*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* *                                                                     * *
* *                            CUBIO Protocol                           * *
* *                                                                     * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/

#define _BOARD_STARTED                        0xB0
#define _BOARD_RESET                          0xB1

#define _0_SET_PIN_MODE_INPUT                 0xE0
#define _1_SET_PIN_MODE_INPUT_PULLUP          0xE1
#define _2_SET_PIN_MODE_OUTPUT                0xE2
#define _3_SET_PIN_INTERRUPT                  0xE3
#define _4_CLEAR_PIN_INTERRUPT                0xE4


#define _0_DIGITAL_READ                       0xF2
#define _1_DIGITAL_WRITE                      0xF3
#define _2_ANALOG_READ                        0xF4
#define _3_ANALOG_WRITE                       0xF5
#define _4_PIN_INTERRUPT                      0xF6

#define _0_ERROR_UNKNOWN_COMMAND              0x10


#define digitalInteruptsTimeout     10
#define digitalInterruptsLength     21    // D0-D13 + A0-A7
boolean digitalInterrupt[digitalInterruptsLength*2]; 

byte current_command_position = 0;
long command_summ = 0;

int current_byte;

void setup(){
  for(byte i=0; i<digitalInterruptsLength; i++){
    digitalInterrupt[i*2] = false;
    digitalInterrupt[i*2+1] = false;
  }
  
  Serial.begin(115200);
  sendMessage(_BOARD_STARTED);
}

long lastCheckDigitalInterrupt;
void checkInerrupts(){
  if(abs(millis() - lastCheckDigitalInterrupt)>digitalInteruptsTimeout){
    lastCheckDigitalInterrupt = millis();

    for(byte i=0; i<digitalInterruptsLength; i++){
      if(digitalInterrupt[i*2]==true){
        boolean digitalValue = digitalRead(i);
        if(digitalValue!=digitalInterrupt[i*2 + 1]){
          digitalInterrupt[i*2 + 1] = digitalValue;
          sendMessage((byte)_4_PIN_INTERRUPT);
          sendMessage((byte)i);
          sendMessage((byte)digitalValue);       
        } 
      }
    }  
  }
}

void sendMessage(byte message[]){
  for(int i=0; i<sizeof(message)/sizeof(message[0]); i++){
    Serial.print((char)message[i]);
  }
}

void sendMessage(byte message){
  Serial.print((char)message);
}


boolean isSerialAvailable(){
  return Serial.available() > 0;
}


byte serialRead(){
  while (!isSerialAvailable()){
    checkInerrupts();
  }
  return Serial.read();
}

void(* resetFunc) (void) = 0;

void loop() {
  //Serial.print("Test");
  for(int i=0; i<255; i++) Serial.print((char)i);
  while(true){}
  /*
  pinMode(12, INPUT_PULLUP);
  //for(byte i=0; i<128; i++) sendMessage((char)i);\

  while(true){
    if(!digitalRead(12)) {
      for(byte i=0; i<128; i++) sendMessage((char)i);
      delay(200);  
    }
     
  }*/
  return;
  byte pin, value;
  byte currentCommand = serialRead();
  switch(currentCommand){
    case _BOARD_RESET:
      resetFunc();
      break;
      
    case _0_SET_PIN_MODE_INPUT:
      pinMode(serialRead(), INPUT);
      break;
      
    case _1_SET_PIN_MODE_INPUT_PULLUP:
      pinMode(serialRead(), INPUT_PULLUP);
      break;
      
    case _2_SET_PIN_MODE_OUTPUT:
      pin = serialRead();
      pinMode(pin, OUTPUT);
      break;
      
    case _3_SET_PIN_INTERRUPT:
      pin = serialRead();
      digitalInterrupt[pin*2] = true;
      digitalInterrupt[pin*2+1] = digitalRead(pin);
      break;
      
    case _4_CLEAR_PIN_INTERRUPT:
      pin = serialRead();
      digitalInterrupt[pin*2] = false;
      break;
      
    case _0_DIGITAL_READ:
      pin = serialRead();
      sendMessage((byte)_0_DIGITAL_READ);
      sendMessage((byte)pin);
      if(digitalRead(pin)) sendMessage(0x01);
      else sendMessage((byte)0x00);
      break;
      
    case _1_DIGITAL_WRITE:
      pin = serialRead();
      value = serialRead();
      digitalWrite(pin, value);
      break;
    
    case _2_ANALOG_READ:
      pin = serialRead();
      int value = analogRead(pin);
      sendMessage((byte)_2_ANALOG_READ);
      sendMessage((byte)pin);

      sendMessage(value&0x000000FF);
      sendMessage((value&0x0000FF00)>>8);
      break;
    
    case _3_ANALOG_WRITE:
      pin = serialRead();
      value = serialRead();
      analogWrite(pin, value);
      break;
    
    default:
      sendMessage(_0_ERROR_UNKNOWN_COMMAND);
      sendMessage(currentCommand);
      break;
      
  }
}
