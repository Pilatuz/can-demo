package com.devicehive.samples.alljoyn.response;

/**
 * Created by dkazakov on 28.07.2014.
 */
public class Response {

    /*
    *
    *
    "41 0C 0E 96",
    "41 0D 0E 96",
    "41 05 0E 96",
    *
    * */

    private final static int RESPONSE_CODE_BYTE = 2;
    private final static int PID_BYTE = 4;
    private final static int VALUE_BYTE = 6;
    private final static int VALUE_RMP_BYTE = 8;

    private static final int MIN_RESPONSE = 6;

    public CarParams handleMessage(final String msg) {
        msg.toLowerCase();
        if (msg.length() < MIN_RESPONSE) {
            return new CarParams(null);
        }

        final String code = msg.substring(0, RESPONSE_CODE_BYTE);
        if (!CarParams.CAN_CODE.equals(code)) {
            return new CarParams(null);
        }

        final String pid = msg.substring(RESPONSE_CODE_BYTE, PID_BYTE);
        CarParams params = new CarParams(pid);
        if (CarParams.PID_RPM.equals(pid)) {
            params.setValue(msg.substring(PID_BYTE, VALUE_RMP_BYTE));
        } else if (CarParams.PID_SPEED.equals(pid)
                || CarParams.PID_TEMP.equals(pid) ) {
            params.setValue(msg.substring(PID_BYTE, VALUE_BYTE));
        }
        return params;
    }

}
