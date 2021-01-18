
#ifdef MODULE_COUNTER

  #define MODULE_COUNTER_MAX_ELEMENTS 1 
  
  boolean   module_counter_enable[MODULE_COUNTER_MAX_ELEMENTS];
  boolean   module_counter_pin_curent_value[MODULE_COUNTER_MAX_ELEMENTS];
  byte      module_counter_pin[MODULE_COUNTER_MAX_ELEMENTS];
  uint32_t  module_counter_count_value[MODULE_COUNTER_MAX_ELEMENTS];
  uint32_t  module_counter_current_count_value[MODULE_COUNTER_MAX_ELEMENTS];
  long      module_counter_timer[MODULE_COUNTER_MAX_ELEMENTS];
  
  void setup_MODULE_COUNTER(){
    resetCounter_MODULE_COUNTER();
  }

  void resetCounter_MODULE_COUNTER(){
    for(int i=0; i<MODULE_COUNTER_MAX_ELEMENTS; i++){
      module_counter_enable[i]              = 0;
      module_counter_pin_curent_value[i]    = 0;
      module_counter_pin[i]                 = 0;
      module_counter_count_value[i]         = 0;
      module_counter_current_count_value[i] = 0;
    }
  }

  void setCounter_MODULE_COUNTER(byte pin, uint32_t count_value, byte counter_number){
    if(counter_number>=MODULE_COUNTER_MAX_ELEMENTS) return;
    
    module_counter_enable[counter_number]                = true;
    module_counter_pin_curent_value[counter_number]      = digitalRead(getPin(pin));
    module_counter_pin[counter_number]                   = pin;
    module_counter_count_value[counter_number]           = count_value;
    module_counter_current_count_value[counter_number]   = 0;
    module_counter_timer[counter_number]                 = millis();
  }

  void clearCounter_MODULE_COUNTER(byte counter_number){
    if(counter_number>=MODULE_COUNTER_MAX_ELEMENTS) return;
    module_counter_enable[counter_number] = false;
  }

  void loop_MODULE_MODULE_COUNTER(){
    for(int i=0; i<MODULE_COUNTER_MAX_ELEMENTS; i++){
        if(module_counter_enable[i]){
          int pin = module_counter_pin[i];
          boolean pinValue = digitalRead(getPin(pin));
          if(pinValue!=module_counter_pin_curent_value[i]){
            module_counter_pin_curent_value[i] = pinValue;
            if(pinValue){
              module_counter_current_count_value[i]++;
              if(module_counter_current_count_value[i]>=module_counter_count_value[i]){

                write(_MODULE_COUNTER_INTERRUPT);
                write(i);
                write(pin);
                write((long)(millis() - module_counter_timer[i]));
                write("\n");
                
                module_counter_timer[i] = millis();
                module_counter_current_count_value[i] = 0;
              }
            }
          }
          
        }
      }
  }

  
#endif
