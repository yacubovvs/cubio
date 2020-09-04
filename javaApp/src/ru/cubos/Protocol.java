package ru.cubos;

public class Protocol {
    public static final byte _0_ERROR_UNKNOWN_COMMAND              = (byte)0x10;

    public static final byte _BOARD_STARTED                        = (byte)0xB0;
    public static final byte _BOARD_RESET                          = (byte)0xB1;

    public static final byte _0_SET_PIN_MODE_INPUT                 = (byte)0xE0;
    public static final byte _1_SET_PIN_MODE_INPUT_PULLUP          = (byte)0xE1;
    public static final byte _2_SET_PIN_MODE_OUTPUT                = (byte)0xE2;

    public static final byte _0_DIGITAL_READ                       = (byte)0xF2;
    public static final byte _1_DIGITAL_WRITE                      = (byte)0xF3;
    public static final byte _2_ANALOG_READ                        = (byte)0xF4;
    public static final byte _3_ANALOG_WRITE                       = (byte)0xF5;

}
