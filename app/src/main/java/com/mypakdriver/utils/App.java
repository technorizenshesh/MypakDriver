package com.mypakdriver.utils;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.view.View;
import android.widget.Toast;

import com.mypakdriver.R;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

public class App extends Application {

    public static IntentFilter intentFilter;

    @Override
    public void onCreate() {
        super.onCreate();
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
    }

    public static void showConnectionDialog(Context mContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.please_check_internet))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }


}
