/*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* *                                                                     * *
* *                            CUBIO Protocol                           * *
* *                                                                     * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/

#define _BOARD_STARTED                        "s"
#define _BOARD_RESET                          "t"

#define _0_SET_PIN_MODE_INPUT                 "i"
#define _1_SET_PIN_MODE_INPUT_PULLUP          "p"
#define _2_SET_PIN_MODE_OUTPUT                "o"
#define _3_SET_PIN_INTERRUPT                  "r"
#define _4_CLEAR_PIN_INTERRUPT                "c"


#define _0_DIGITAL_READ                       "r"
#define _1_DIGITAL_WRITE                      "w"
#define _2_ANALOG_READ                        "R"
#define _3_ANALOG_WRITE                       "W"
#define _4_PIN_INTERRUPT                      "I"

#define _0_ERROR_UNKNOWN_COMMAND              "u"


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
  Serial.println(_BOARD_STARTED);
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
          //sendMessage((byte)_4_PIN_INTERRUPT);
          //sendMessage((byte)i);
          //sendMessage((byte)digitalValue);       
        } 
      }
    }  
  }
}

void write(String string){
  Serial.print(string + " ");
}

String readString(){
  String command = "";
  char charReadValue = 0;
  while(true){
     if(Serial.available()){
        charReadValue = Serial.read();
        if(charReadValue==32) break;
        command += (char)charReadValue;
        //Serial.print((int)charReadValue);
     }else{
       checkInerrupts();
     }

     
  }
  checkInerrupts();
  return command;
}

void(* resetFunc) (void) = 0;

void loop() {
  String command = readString();
  //Serial.println(command);
  //byte pin, value;
  //String currentCommand = readString();

  if(command==_BOARD_RESET){
      resetFunc();
  }else if(command==_0_SET_PIN_MODE_INPUT){
      Serial.println("_0_SET_PIN_MODE_INPUT");
      //pinMode(serialRead(), INPUT);
  }else if(command==_1_SET_PIN_MODE_INPUT_PULLUP){
      Serial.println("_1_SET_PIN_MODE_INPUT_PULLUP");
      //pinMode(serialRead(), INPUT_PULLUP);
  }else if(command==_2_SET_PIN_MODE_OUTPUT){
      Serial.println("_2_SET_PIN_MODE_OUTPUT");
      //pin = serialRead();
      //pinMode(pin, OUTPUT);
  }else if(command==_3_SET_PIN_INTERRUPT){
      Serial.println("_3_SET_PIN_INTERRUPT");
      //pin = serialRead();
      //digitalInterrupt[pin*2] = true;
      //digitalInterrupt[pin*2+1] = digitalRead(pin);
  }else if(command==_4_CLEAR_PIN_INTERRUPT){
      Serial.println("_4_CLEAR_PIN_INTERRUPT");
      //pin = serialRead();
      //digitalInterrupt[pin*2] = false;
  }else if(command==_0_DIGITAL_READ){
      Serial.println("_0_DIGITAL_READ");
      //pin = serialRead();
      //sendMessage((byte)_0_DIGITAL_READ);
      //sendMessage((byte)pin);
      //if(digitalRead(pin)) sendMessage(0x01);
      //else sendMessage((byte)0x00);
  }else if(command==_1_DIGITAL_WRITE){
      Serial.println("_1_DIGITAL_WRITE");
      //pin = serialRead();
      //value = serialRead();
      //digitalWrite(pin, value);
  }else if(command==_2_ANALOG_READ){
      Serial.println("_2_ANALOG_READ");
      //pin = serialRead();
      //int value = analogRead(pin);
      //sendMessage((byte)_2_ANALOG_READ);
      //sendMessage((byte)pin);

      //sendMessage(value&0x000000FF);
      //sendMessage((value&0x0000FF00)>>8);
  }else if(command==_3_ANALOG_WRITE){
      Serial.println("_3_ANALOG_WRITE");
      //pin = serialRead();
      //value = serialRead();
      //analogWrite(pin, value);
  }else{
      write(_0_ERROR_UNKNOWN_COMMAND);
      write(command);
  }
}
