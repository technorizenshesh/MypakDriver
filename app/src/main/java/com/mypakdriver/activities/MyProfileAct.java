package com.mypakdriver.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.mypakdriver.R;
import com.mypakdriver.databinding.ActivityMyProfileBinding;
import com.mypakdriver.utils.ProjectUtil;

public class MyProfileAct extends AppCompatActivity {

    Context mContext = MyProfileAct.this;
    ActivityMyProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_profile);
        ProjectUtil.changeStatusBarColor(MyProfileAct.this);
        init();
    }

    private void init() {

        binding.ivMySendings.setOnClickListener(v -> {
            startActivity(new Intent(mContext,DriverTrackAct.class));
        });

        binding.cvLogout.setOnClickListener(v -> {
            logoutAppDialog();
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });


    }

    private void logoutAppDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.logout_text))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // sharedPref.clearAllPreferences();
                        Intent loginscreen = new Intent(mContext,LoginAct.class);
                        loginscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        NotificationManager nManager = ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE));
                        nManager.cancelAll();
                        startActivity(loginscreen);
                        finishAffinity();
                    }
                }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}