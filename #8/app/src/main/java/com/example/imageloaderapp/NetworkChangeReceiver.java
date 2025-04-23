package com.example.imageloaderapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Button;
import android.widget.TextView;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private final Button button;
    private final TextView status;

    public NetworkChangeReceiver(Button button, TextView status) {
        this.button = button;
        this.status = status;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = isNetworkAvailable(context);
        button.setEnabled(isConnected);
        status.setText(isConnected ? "Connected" : "No internet connection");
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}

