package com.mypakdriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.mypakdriver.R;
import com.mypakdriver.databinding.ActivityForgotBinding;
import com.mypakdriver.utils.Api;
import com.mypakdriver.utils.ApiFactory;
import com.mypakdriver.utils.ProjectUtil;
import org.json.JSONObject;
import java.util.HashMap;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotAct extends AppCompatActivity {

    Context mContext = ForgotAct.this;
    ActivityForgotBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_forgot);

        init();

    }

    private void init() {
        binding.btSubmit.setOnClickListener(v -> {
            if(TextUtils.isEmpty(binding.etEmail.getText().toString().trim())){
                Toast.makeText(mContext,getString(R.string.please_enter_email_add),Toast.LENGTH_SHORT).show();
            } else {
                changePassword();
            }
        });

    }

    private void changePassword() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));

        HashMap<String,String> paramHash = new HashMap<>();
        paramHash.put("email",binding.etEmail.getText().toString().trim());

        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        Call<ResponseBody> call = api.forgotPass(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        Toast.makeText(mContext, getString(R.string.reset_link_msg), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(mContext, jsonObject.getString("result"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

}