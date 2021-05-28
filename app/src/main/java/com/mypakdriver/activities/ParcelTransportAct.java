package com.mypakdriver.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.mypakdriver.R;
import com.mypakdriver.databinding.ActivityParcelTransportBinding;

public class ParcelTransportAct extends AppCompatActivity {

    Context mContext = ParcelTransportAct.this;
    ActivityParcelTransportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_parcel_transport);

        init();

    }

    private void init() {

        binding.btParcel.setOnClickListener(v -> {
            startActivity(new Intent(mContext,ParcelDetailAct.class));
        });

        binding.btChat.setOnClickListener(v -> {
            startActivity(new Intent(mContext,ChatingAct.class));
        });

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }


}