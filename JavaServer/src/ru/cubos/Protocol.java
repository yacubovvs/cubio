package ru.cubos;

public class Protocol {
    public static final byte _START_FINISH_BYTE                     = serialByte(0xA0);
    public static final byte _START_MESSAGE_EXEC_ON_SUCCESS_HASH    = serialByte(0xA1);
    public static final byte _START_MESSAGE_EXEC_IMMEDIATELY        = serialByte(0xA2);
    public static final byte _FINISH_MESSAGE                        = serialByte(0xA3);

    public static final byte _DATA_SIZE                             = serialByte(0xA4);

    public static final byte _0_PIN_COMMAND                         = serialByte(0x01);
    public static final byte _0_1_SET_MODE                          = serialByte(0x01);
    public static final byte _0_2_DIGITAL_READ                      = serialByte(0x02);
    public static final byte _0_3_DIGITAL_WRITE                     = serialByte(0x03);
    public static final byte _0_4_ANALOG_READ                       = serialByte(0x04);
    public static final byte _0_5_ANALOG_WRITE                      = serialByte(0x05);

    public static byte serialByte(int b){
        return (byte)(b-256);
    }

    public static byte[] createMesage(byte[] message){
        return null;
    }

}
