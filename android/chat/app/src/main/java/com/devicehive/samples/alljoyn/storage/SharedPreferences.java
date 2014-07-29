package com.devicehive.samples.alljoyn.storage;

import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * Created by dkazakov on 28.07.2014.
 */
public class SharedPreferences {

    protected final static String NAMESPACE = SharedPreferences.class.getName();

    private final static String KEY_INDEX_METADATA = NAMESPACE.concat(".KEY_DEMO_CHANNEl");


    private WeakReference<Context> context;
    private android.content.SharedPreferences prefs;

    public SharedPreferences(Context context) {
        this.context = new WeakReference<Context>(context);
        prefs = context.getSharedPreferences(context.getPackageName() + "_alljoyn", Context.MODE_PRIVATE);
    }

    public void demoChannelCreated() {
        prefs.edit().putBoolean(KEY_INDEX_METADATA, true).commit();
    }

    public boolean isDemoChannel() {
        return prefs.getBoolean(KEY_INDEX_METADATA, false);
    }
}
