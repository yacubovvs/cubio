/*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* *                                                                     * *
* *                              SETTINGS                               * *
* *                                                                     * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/



#define WIFI_CONNECT

#define WIFI_CONNECT_SSID       "DIR-615"
#define WIFI_CONNECT_PASSWORD   "tsdurovo6200"

#ifdef WIFI_CONNECT
  #include <ESP8266WiFi.h>
  #define WIFI_CONNECT_SERVER_PORT 8888
  WiFiServer server(WIFI_CONNECT_SERVER_PORT);
#endif

#define SERIAL_LOG
//#define SERIAL_CONNECT
#define SERIAL_BOUNDRATE 115200



/*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* *                                                                     * *
* *                               MODULES                               * *
* *                                                                     * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/

//#define MODULE_PWM_PCA9685

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
#define _3_SET_PIN_INTERRUPT                  "R"
#define _4_CLEAR_PIN_INTERRUPT                "c"


#define _0_DIGITAL_READ                       "r"
#define _1_DIGITAL_WRITE                      "w"
#define _2_ANALOG_READ                        "A"
#define _3_ANALOG_WRITE                       "W"
#define _4_PIN_INTERRUPT                      "I"

#define _0_ERROR_UNKNOWN_COMMAND              "u"

//#define digitalInteruptsTimeout_enable
#define digitalInteruptsTimeout     10
#define digitalInterruptsLength     21    // D0-D13 + A0-A7

#ifdef MODULE_PWM_PCA9685
  #define _MODULE_PWM_PCA9685_STATUS      "m_PCA9685_s"
  #define _MODULE_PWM_PCA9685_STEPPWM     "m_PCA9685_p"
  #define _MODULE_PWM_PCA9685_STARTVALUE  "m_PCA9685_t"
#endif

boolean digitalInterrupt[digitalInterruptsLength*2]; 

byte current_command_position = 0;
long command_summ = 0;

int current_byte;

void setup(){
  for(byte i=0; i<digitalInterruptsLength; i++){
    digitalInterrupt[i*2] = false;
    digitalInterrupt[i*2+1] = false;
  }

  #ifdef MODULE_PWM_PCA9685
    setup_MODULE_PWM_PCA9685();
  #endif
  
  Serial.begin(SERIAL_BOUNDRATE);

  #ifdef WIFI_CONNECT
    const char* ssid = WIFI_CONNECT_SSID;
    const char* password = WIFI_CONNECT_PASSWORD;

    WiFi.begin(ssid, password);
    log("Connecting to ");
    log(WIFI_CONNECT_SSID);
    log("\n");
    while (WiFi.status() != WL_CONNECTED){
      log(".");
      delay(1000);
    }
    log("\nConnected ");
    log(WiFi.localIP().toString());
    log("\n");

    server.begin();
    server.setNoDelay(true);

    log("Server started\n");
    
  #endif
  
  write(_BOARD_STARTED);
}

int getPin(int pin){
  #ifdef ESP8266
    switch(pin){
      case 0:
        return D0;
      case 1:
        return D1;
      case 2:
        return D2;
      case 3:
        return D3;
      case 4:
        return D4;
      case 5:
        return D5;
      case 6:
        return D6;
      case 7:
        return D7;
      case 8:
        return D8;
      case 9:
        return D9;
      case 10:
        return D10;
      case 11:
        return D11;
      case 12:
        return D12;
      case 13:
        return D13;
      case 14:
        return D14;
      case 15:
        return D15;
      case 16:
        return A0;
    }
  #else
    return pin;
  #endif
}

void log(String string){
  #ifdef SERIAL_LOG
    Serial.print(string + " ");
  #endif  
}

void write(String string){
  #ifdef SERIAL_CONNECT
    Serial.print(string + " ");
  #endif
}

void write(long string){
  #ifdef SERIAL_CONNECT
    Serial.print(string);
    Serial.print(" ");
  #endif
}

void write(int string){
  #ifdef SERIAL_CONNECT
    Serial.print(string);
    Serial.print(" ");
  #endif
}

long lastCheckDigitalInterrupt;
void checkDaemons(){
  WdtLoop();
  
  // Interupts
  #ifdef digitalInteruptsTimeout_enable
  if(abs(millis() - lastCheckDigitalInterrupt)>digitalInteruptsTimeout){
  #endif
    lastCheckDigitalInterrupt = millis();

    for(byte i=0; i<digitalInterruptsLength; i++){
      if(digitalInterrupt[i*2]==true){
        boolean digitalValue = digitalRead(getPin(i));
        if(digitalValue!=digitalInterrupt[i*2 + 1]){
          digitalInterrupt[i*2 + 1] = digitalValue;
          write(_4_PIN_INTERRUPT);
          write((int)i);
          write((int)digitalValue);       
          write((long)millis());
          Serial.flush();
        } 
      }
    }  
  #ifdef digitalInteruptsTimeout_enable
  }
  #endif

  // Modules
  #ifdef MODULE_PWM_PCA9685
    loop_MODULE_PWM_PCA9685();
  #endif
  
}

#ifdef WIFI_CONNECT
  WiFiClient client;
#endif

String readWord(){
  String command = "";
  
  #ifdef SERIAL_CONNECT  
    char charReadValue = 0;
    while(true){
       if(Serial.available()){
          charReadValue = Serial.read();
          if(charReadValue==32) break;
          command += (char)charReadValue;
       }else{
         checkDaemons();
       }
    }
    checkDaemons();
  #endif

  #ifdef WIFI_CONNECT
    checkDaemons();
    if(!client)client = server.available();
    
    if (client) {
     
      if(client.connected()){   
        checkDaemons();   
        while(client.available()>0){
          checkDaemons();
          WdtLoop();
          char currentChar = (char)((byte)client.read());
          if(currentChar==' ' || currentChar=='\n'){
            break;
          }
          command += currentChar;
         
        }
      }
    }

  #endif
  return command;
}

void WdtLoop(){
  #ifdef ESP8266
    ESP.wdtDisable();
  #endif
}

int readInt(){
  return readWord().toInt();
}

void(* resetFunc) (void) = 0;

void loop() {
  
  String command = readWord();

  if(command==_BOARD_RESET){
      resetFunc();
  }else if(command==_0_SET_PIN_MODE_INPUT){
      pinMode(readInt(), INPUT);
  }else if(command==_1_SET_PIN_MODE_INPUT_PULLUP){
      pinMode(readInt(), INPUT_PULLUP);
  }else if(command==_2_SET_PIN_MODE_OUTPUT){
      pinMode(readInt(), OUTPUT);
  }else if(command==_3_SET_PIN_INTERRUPT){
      int pin = readInt();
      digitalInterrupt[pin*2] = true;
      digitalInterrupt[pin*2+1] = digitalRead(getPin(pin));
  }else if(command==_4_CLEAR_PIN_INTERRUPT){
      int pin = readInt();
      digitalInterrupt[pin*2] = false;
  }else if(command==_0_DIGITAL_READ){
      int pin = readInt();
      write(_0_DIGITAL_READ);
      write(pin);
      write((int)digitalRead(getPin(pin)));
      Serial.flush();
  }else if(command==_1_DIGITAL_WRITE){
      int pin = readInt();
      int value = readInt();
      digitalWrite(getPin(pin), value);
  }else if(command==_2_ANALOG_READ){
      int pin = readInt();
      int value = analogRead(getPin(pin));
      write(_2_ANALOG_READ);
      write(pin);
      write(value);
      Serial.flush();
  }else if(command==_3_ANALOG_WRITE){
      int pin = readInt();
      int value = readInt();
      analogWrite(getPin(pin), value);
  #ifdef MODULE_PWM_PCA9685
    }else if(command==_MODULE_PWM_PCA9685_STATUS){
      write(_MODULE_PWM_PCA9685_STATUS);
      write((int)getStatus_MODULE_PWM_PCA9685());
      Serial.flush();
    }else if(command==_MODULE_PWM_PCA9685_STEPPWM){
       int NUM              = readInt();
       int NEW_PWM          = readInt();
       int PWN_IN_SECONDS   = readInt();
       setPWM_MODULE_PWM_PCA9685(NUM, NEW_PWM, PWN_IN_SECONDS);
    }else if(command==_MODULE_PWM_PCA9685_STARTVALUE){
       int NUM              = readInt();
       int PWM              = readInt();
       setStartPWM_MODULE_PWM_PCA9685(NUM, PWM);
  #endif
  }else{
      write(_0_ERROR_UNKNOWN_COMMAND);
      write(command);
      Serial.flush();
  }
}

//m_pshw_p 0 1000 100 
