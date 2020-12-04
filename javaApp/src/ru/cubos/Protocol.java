package ru.cubos;

public class Protocol {
    public static final String _BOARD_STARTED                        = "s";
    public static final String _BOARD_RESET                          = "t";

    public static final String _0_SET_PIN_MODE_INPUT                 = "i";
    public static final String _1_SET_PIN_MODE_INPUT_PULLUP          = "p";
    public static final String _2_SET_PIN_MODE_OUTPUT                = "o";
    public static final String _3_SET_PIN_INTERRUPT                  = "R";
    public static final String _4_CLEAR_PIN_INTERRUPT                = "c";


    public static final String _0_DIGITAL_READ                       = "r";
    public static final String _1_DIGITAL_WRITE                      = "w";
    public static final String _2_ANALOG_READ                        = "A";
    public static final String _3_ANALOG_WRITE                       = "W";
    public static final String _4_PIN_INTERRUPT                      = "I";

    public static final String _0_ERROR_UNKNOWN_COMMAND              = "u";

}
