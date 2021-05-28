package com.mypakdriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mypakdriver.R;
import com.mypakdriver.databinding.ActivityChatListBinding;
import com.mypakdriver.utils.ProjectUtil;

public class ChatListAct extends AppCompatActivity {

    Context mContext = ChatListAct.this;
    ActivityChatListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chat_list);
        ProjectUtil.changeStatusBarColor(ChatListAct.this);
        init();

    }

    private void init() {

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.cvChat.setOnClickListener(v -> {
            startActivity(new Intent(mContext,ChatingAct.class));
        });

    }


}