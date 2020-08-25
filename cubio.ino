/*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* *                                                                     * *
* *                            CUBIO Protocol                           * *
* *                                                                     * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/

#define _START_FINISH_BYTE      0xA0
#define _START_MESSAGE          0xA1
#define _FINISH_MESSAGE         0xA2

#define _DATA_SIZE              0xA3

#define _0_PIN_COMMAND          0x01
#define _0_1_SET_MODE           0x01
#define _0_2_DIGITAL_READ       0x02
#define _0_3_DIGITAL_WRITE      0x03
#define _0_4_ANALOG_READ        0x04
#define _0_5_ANALOG_WRITE       0x05


/*
*********************************************************
                   SERIAL PORT MESSAGE                   
*********************************************************

_START_FINISH_BYTE
_START_FINISH_BYTE
_START_FINISH_BYTE
_START_MESSAGE
_DATA_SIZE      
0x01            // Length of message in next 1 byte
0xFF            // Length of message 255 bytes

-- data --

_START_FINISH_BYTE
_START_FINISH_BYTE
_FINISH_MESSAGE
0xFF            // HASH amount byte #1
0x01            // HASH amount byte #2
_FINISH_MESSAGE

*/

byte current_command_tree[8];
byte current_command_position = 0;
long command_summ = 0;

int current_byte;

void setup(){
  Serial.begin(115200);
}

void loop() {
  if (Serial.available() > 0) {  //если есть доступные данные
    current_command_tree[current_command_position] = Serial.read();
    
    switch(current_command_position){
      case 0:
        switch (current_command_tree[current_command_position]){
          case _START_FINISH_BYTE:
            Serial.println("_START_FINISH_BYTE");
            return;
          case _START_MESSAGE:
            Serial.println("_START_MESSAGE");
            return;
          case _FINISH_MESSAGE:
            Serial.println("_FINISH_MESSAGE");
            return;
          case _DATA_SIZE:
            Serial.println("_DATA_SIZE");
            return;
        }
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      default:
        return;
    }
  }
}
