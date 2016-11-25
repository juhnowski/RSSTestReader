package com.teleca.RSSTestReader.BroadcastReceivers;

import com.teleca.RSSTestReader.Service.RSSUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * RSS Update Broadcast Receiver
 * @author ruinilyu
 *
 */
public class RSSUpdateBroadcastReceiver extends BroadcastReceiver {

    public static final String UPDATE_INTENT = "com.teleca.RSSTestReader.action.UPDATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("RSSUpdateBroadcastReceiver","Start update service");
        context.startService(new Intent(context, RSSUpdateService.class));
    }

}
