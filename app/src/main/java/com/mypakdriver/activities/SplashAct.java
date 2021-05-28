package com.mypakdriver.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.mypakdriver.R;
import com.mypakdriver.utils.AppConstant;
import com.mypakdriver.utils.ProjectUtil;
import com.mypakdriver.utils.SharedPref;

public class SplashAct extends AppCompatActivity {

    Context mContext = SplashAct.this;
    final static int SPLASH_TIME_OUT = 2000;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPref = SharedPref.getInstance(mContext);
        ProjectUtil.changeStatusBarColor(SplashAct.this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(sharedPref.getUserDetails(AppConstant.USER_DETAILS) != null) {
                    startActivity(new Intent(mContext,HomeAct.class));
                    finish();
                } else {
                    startActivity(new Intent(mContext,WelcomeAct.class));
                    finish();
                }
            }
        },SPLASH_TIME_OUT);
    }

}