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

#define _0_SET_PIN_MODE_INPUT                 0xE0
#define _1_SET_PIN_MODE_INPUT_PULLUP          0xE1
#define _2_SET_PIN_MODE_OUTPUT                0xE2

#define _0_DIGITAL_READ                       0xF2
#define _1_DIGITAL_WRITE                      0xF3
#define _2_ANALOG_READ                        0xF4
#define _3_ANALOG_WRITE                       0xF5

#define _0_ERROR_UNKNOWN_COMMAND              0x10


byte current_command_tree[8];
byte current_command_position = 0;
long command_summ = 0;

int current_byte;

void setup(){
  Serial.begin(115200);
  sendMessage(_BOARD_STARTED);
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
  while (!isSerialAvailable()){}
  return Serial.read();
}


void loop() {
  switch(serialRead()){
    case _0_SET_PIN_MODE_INPUT):
      pinMode(serialRead(), INPUT);
      break;
    case _1_SET_PIN_MODE_INPUT_PULLUP:
      pinMode(serialRead(), INPUT_PULLUP);
      break;
    case _2_SET_PIN_MODE_OUTPUT:
      pinMode(serialRead(), OUTPUT);
      break;

    case _0_DIGITAL_READ:
      break;
    case _1_DIGITAL_WRITE:
      digitalWrite(serialRead(), serialRead());
      break;
    
    case _2_ANALOG_READ:
      break;
    
    case _3_ANALOG_WRITE:
      break;
    
    default:
      sendMessage(_0_ERROR_UNKNOWN_COMMAND);
      break:
      
  }
}
