package com.devicehive.samples.alljoyn;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by dkazakov on 29.07.2014.
 */
public class TestStub {


    private static List<String> list = new ArrayList<String>();

    static {
        list.add("0300410d96");
        list.add("0400410c0e96");
        list.add("0300410586");
        list.add("040041100e96");
        list.add("0500420d0e96aa");
    }

    public String generateResponse() {
        String str = list.get(new Random().nextInt(5));
        Log.d("TEST", "Test string " + str);
        return str;
    }

}
