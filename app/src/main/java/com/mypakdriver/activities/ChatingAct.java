package com.mypakdriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.mypakdriver.R;
import com.mypakdriver.databinding.ActivityChatingBinding;
import com.mypakdriver.utils.ProjectUtil;

public class ChatingAct extends AppCompatActivity {

    Context mContext = ChatingAct.this;
    ActivityChatingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chating);
        ProjectUtil.changeStatusBarColor(ChatingAct.this);

        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

    }


}