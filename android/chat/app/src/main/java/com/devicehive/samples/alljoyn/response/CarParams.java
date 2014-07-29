package com.devicehive.samples.alljoyn.response;

/**
 * Created by dkazakov on 28.07.2014.
 */
public class CarParams {

    /**
     * OBD-II codes
     *
     * @see(http://ru.wikipedia.org/wiki/OBD-II_PIDs)
     * */
    public static final String CAN_CODE = "41";
    public static final String PID_TEMP = "05";  // 1 byte
    public static final String PID_RPM = "0c";   // 2 bytes
    public static final String PID_SPEED = "0d"; // 1 byte

    private static final int HEX_RADIX = 16;

    private String type;
    private String value;

    public CarParams(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isTemperature() {
        return PID_TEMP.equals(type);
    }

    public boolean isRpm() {
        return PID_RPM.equals(type);
    }

    public boolean isSpeed() {
        return PID_SPEED.equals(type);
    }

    /**
     * Convert from gex to decimal and encode by formula
     *
     * @see(http://ru.wikipedia.org/wiki/OBD-II_PIDs)
     * */
    public String asDecimal() {
        Integer decimal = Integer.parseInt(value, HEX_RADIX);
        if (PID_TEMP.equals(type)) {
            decimal -= 40;
        } else if (PID_RPM.equals(PID_RPM)) {
            decimal /= 4;
        }
        return String.valueOf(decimal);
    }

}
