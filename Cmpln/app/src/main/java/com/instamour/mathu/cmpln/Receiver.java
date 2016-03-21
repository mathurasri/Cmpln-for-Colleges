package com.instamour.mathu.cmpln;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

public class Receiver extends ParsePushBroadcastReceiver{
    @Override
    protected void onPushOpen(Context context, Intent intent) {
        //super.onPushOpen(context, intent);
       // Log.e("Push", "Clicked");
    }
}
