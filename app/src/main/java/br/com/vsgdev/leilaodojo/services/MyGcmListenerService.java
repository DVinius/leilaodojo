package br.com.vsgdev.leilaodojo.services;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by axdesousa on 18/09/15.
 */
public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(MyGcmListenerService.class.getName(), "From: " + from);
        Log.d(MyGcmListenerService.class.getName(), "Message: " + message);
        //TODO XXX continue


    }
}
