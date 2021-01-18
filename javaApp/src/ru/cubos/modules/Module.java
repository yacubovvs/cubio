package ru.cubos.modules;

import ru.cubos.Connector;
import ru.cubos.Decoder;

public abstract class Module {
    Connector connector;

    public Module(){
    }

    public Module(Connector connector){
        this.connector = connector;
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    public abstract boolean decode(String string, Decoder decoder);
}
