package ru.cubos.modules;

import ru.cubos.Connector;
import ru.cubos.Decoder;

public abstract class CounterModule extends Module {

    final static String _MODULE_COUNTER_SET_COUNTER     = "MODULE_COUNTER_s";
    final static String _MODULE_COUNTER_CLEAR_COUNTER   = "MODULE_COUNTER_c";
    final static String _MODULE_COUNTER_INTERRUPT       = "MODULE_COUNTER_i";
    final static String _MODULE_COUNTER_RESET           = "MODULE_COUNTER_r";

    public CounterModule() {
    }

    public CounterModule(Connector connector) {
        super(connector);
    }

    public void resetCounter(){
        connector.write(_MODULE_COUNTER_RESET + " ");
        connector.write("\n");
    }

    public void setCounter(int pin, int value, int counterNumber){
        connector.write(_MODULE_COUNTER_SET_COUNTER + " " + pin + " " + value + " " + counterNumber + " ");
        //connector.write("\n");
    }

    public void clearCounter(int counterNumber){
        connector.write(_MODULE_COUNTER_CLEAR_COUNTER + " " + counterNumber + " ");
        //connector.write("\n");
    }

    @Override
    public boolean decode(String string, Decoder decoder) {
        switch (string){
            case _MODULE_COUNTER_INTERRUPT:
                int pin = decoder.readInt();
                int counterNumber = decoder.readInt();
                long millis = decoder.readLong();
                onCounterInterrupt(pin, counterNumber, millis);
                return true;
        }
        return false;
    }

    public abstract void onCounterInterrupt(int pin, int counterNumber, long millis);
}
