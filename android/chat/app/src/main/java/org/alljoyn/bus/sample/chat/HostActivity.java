/*
 * Copyright (c) 2011, AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package org.alljoyn.bus.sample.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.app.Activity;
import android.app.Dialog;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import android.util.Log;

import com.devicehive.samples.alljoyn.response.CarParams;

public class HostActivity extends Activity implements Observer {
    private static final String TAG = "chat.HostActivity";

    private TextView rpm;
    private TextView coolantTemp;
    private TextView speed;

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.host);

        rpm = (TextView) findViewById(R.id.rpm);
        coolantTemp = (TextView) findViewById(R.id.coolant_temp);
        speed = (TextView) findViewById(R.id.speed);

//        mChannelName = (TextView)findViewById(R.id.hostChannelName);
//        mChannelName.setText("");
//
//        mChannelStatus = (TextView)findViewById(R.id.hostChannelStatus);
//        mChannelStatus.setText("Idle");
/*
        mSetNameButton = (Button)findViewById(R.id.hostSetName);
        mSetNameButton.setEnabled(true);
        mSetNameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_SET_NAME_ID);
            }
        });

        mStartButton = (Button)findViewById(R.id.hostStart);
        mStartButton.setEnabled(false);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_START_ID);
            }
        });

        mStopButton = (Button)findViewById(R.id.hostStop);
        mStopButton.setEnabled(false);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_STOP_ID);
            }
        });*/

        /*
         * Keep a pointer to the Android Appliation class around.  We use this
         * as the Model for our MVC-based application.  Whenever we are started
         * we need to "check in" with the application so it can ensure that our
         * required services are running.
         */
        mChatApplication = (ChatApplication)getApplication();
        mChatApplication.checkin();

        /*
         * Call down into the model to get its current state.  Since the model
         * outlives its Activities, this may actually be a lot of state and not
         * just empty.
         */
        updateChannelState();

        /*
         * Now that we're all ready to go, we are ready to accept notifications
         * from other components.
         */
        mChatApplication.addObserver(this);


//        mQuitButton = (Button)findViewById(R.id.hostQuit);
//        mQuitButton.setEnabled(true);
//        mQuitButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                mChatApplication.quit();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final boolean withMenu = !AllJoynService.WITH_DEFAULT_CHANNEL;
        if (withMenu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
        }
        return withMenu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.set_channel_name:
                showDialog(DIALOG_SET_NAME_ID);
                return true;

            case R.id.start_channel:
                showDialog(DIALOG_START_ID);
                return true;

            case R.id.stop_channel:
                showDialog(DIALOG_STOP_ID);
                return true;

            case R.id.quit_application:
                mChatApplication.quit();
                return true;

            case R.id.open_debug:
                startActivity(new Intent(this, UseActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        mChatApplication = (ChatApplication)getApplication();
        mChatApplication.deleteObserver(this);
        super.onDestroy();
    }

    private ChatApplication mChatApplication = null;

    static final int DIALOG_SET_NAME_ID = 0;
    static final int DIALOG_START_ID = 1;
    static final int DIALOG_STOP_ID = 2;
    public static final int DIALOG_ALLJOYN_ERROR_ID = 3;

    protected Dialog onCreateDialog(int id) {
        Log.i(TAG, "onCreateDialog()");
        Dialog result = null;
        switch(id) {
        case DIALOG_SET_NAME_ID:
            {
                DialogBuilder builder = new DialogBuilder();
                result = builder.createHostNameDialog(this, mChatApplication);
            }
            break;
        case DIALOG_START_ID:
            {
                DialogBuilder builder = new DialogBuilder();
                result = builder.createHostStartDialog(this, mChatApplication);
            }
            break;
        case DIALOG_STOP_ID:
            {
                DialogBuilder builder = new DialogBuilder();
                result = builder.createHostStopDialog(this, mChatApplication);
            }
            break;
        case DIALOG_ALLJOYN_ERROR_ID:
            {
                DialogBuilder builder = new DialogBuilder();
                result = builder.createAllJoynErrorDialog(this, mChatApplication);
            }
            break;
        }
        return result;
    }

    public synchronized void update(Observable o, Object arg) {
        Log.i(TAG, "update(" + arg + ")");
        String qualifier = (String)arg;

        if (qualifier.equals(ChatApplication.APPLICATION_QUIT_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_APPLICATION_QUIT_EVENT);
            mHandler.sendMessage(message);
        }

        if (qualifier.equals(ChatApplication.HOST_CHANNEL_STATE_CHANGED_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_CHANNEL_STATE_CHANGED_EVENT);
            mHandler.sendMessage(message);
        }

        if (qualifier.equals(ChatApplication.ALLJOYN_ERROR_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_ALLJOYN_ERROR_EVENT);
            mHandler.sendMessage(message);
        }

        if (qualifier.equals(ChatApplication.SPEED_CHANGED_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_SPEED_CHANGED_EVENT);
            mHandler.sendMessage(message);
        }

        if (qualifier.equals(ChatApplication.RPM_CHANGED_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_RPM_CHANGED_EVENT);
            mHandler.sendMessage(message);
        }

        if (qualifier.equals(ChatApplication.TEMPERATURE_CHANGED_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_TEMPERATURE_CHANGED_EVENT);
            mHandler.sendMessage(message);
        }
    }

    private void updateChannelState() {
        AllJoynService.HostChannelState channelState = mChatApplication.hostGetChannelState();
        String name = mChatApplication.hostGetChannelName();
        boolean haveName = true;
        if (name == null) {
            haveName = false;
            name = "Not set";
        }
//        mChannelName.setText(name);
//        switch (channelState) {
//        case IDLE:
//            mChannelStatus.setText("Idle");
//            break;
//        case NAMED:
//            mChannelStatus.setText("Named");
//            break;
//        case BOUND:
//            mChannelStatus.setText("Bound");
//            break;
//        case ADVERTISED:
//            mChannelStatus.setText("Advertised");
//            break;
//        case CONNECTED:
//            mChannelStatus.setText("Connected");
//            break;
//        default:
//            mChannelStatus.setText("Unknown");
//            break;
//        }

//        if (channelState == AllJoynService.HostChannelState.IDLE) {
//            mSetNameButton.setEnabled(true);
//            if (haveName) {
//                mStartButton.setEnabled(true);
//            } else {
//                mStartButton.setEnabled(false);
//            }
//            mStopButton.setEnabled(false);
//        } else {
//            mSetNameButton.setEnabled(false);
//            mStartButton.setEnabled(false);
//            mStopButton.setEnabled(true);
//        }
    }

    private void updateTemperature(){
        CarParams params = mChatApplication.getTemperature();
        coolantTemp.setText(params == null ?  "0": params.asDecimal());
    }

    private void updateRpm(){
        CarParams params = mChatApplication.getRpm();
        String de = params.asDecimal();
        rpm.setText(params == null ?  "0" : de);
    }

    private void updateSpeed(){
        CarParams params = mChatApplication.getSpeed();
        speed.setText(params == null ?   "0" : params.asDecimal());
    }

//    private TextView mChannelName;
//    private TextView mChannelStatus;
//    private Button mSetNameButton;
//    private Button mStartButton;
//    private Button mStopButton;
//    private Button mQuitButton;

    private void alljoynError() {
        if (mChatApplication.getErrorModule() == ChatApplication.Module.GENERAL ||
                mChatApplication.getErrorModule() == ChatApplication.Module.USE) {
            showDialog(DIALOG_ALLJOYN_ERROR_ID);
        }
    }

    private static final int HANDLE_APPLICATION_QUIT_EVENT = 0;
    private static final int HANDLE_CHANNEL_STATE_CHANGED_EVENT = 1;
    private static final int HANDLE_ALLJOYN_ERROR_EVENT = 2;
    private static final int HANDLE_SPEED_CHANGED_EVENT = 3;
    private static final int HANDLE_RPM_CHANGED_EVENT = 4;
    private static final int HANDLE_TEMPERATURE_CHANGED_EVENT = 5;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case HANDLE_APPLICATION_QUIT_EVENT:
                {
                    Log.i(TAG, "mHandler.handleMessage(): HANDLE_APPLICATION_QUIT_EVENT");
                    finish();
                }
                break;
            case HANDLE_CHANNEL_STATE_CHANGED_EVENT:
                {
                    Log.i(TAG, "mHandler.handleMessage(): HANDLE_CHANNEL_STATE_CHANGED_EVENT");
                    updateChannelState();
                }
                break;
            case HANDLE_ALLJOYN_ERROR_EVENT:
                {
                    Log.i(TAG, "mHandler.handleMessage(): HANDLE_ALLJOYN_ERROR_EVENT");
                    alljoynError();
                }
                break;
            case HANDLE_SPEED_CHANGED_EVENT:
                updateSpeed();
                break;
            case HANDLE_RPM_CHANGED_EVENT:
                updateRpm();
                break;
            case HANDLE_TEMPERATURE_CHANGED_EVENT:
                updateTemperature();
                break;
            default:
                break;
            }
        }
    };

}