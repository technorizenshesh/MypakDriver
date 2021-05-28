package com.mypakdriver.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.mypakdriver.R;
import com.mypakdriver.databinding.ActivityVerifyBinding;
import com.mypakdriver.models.ModelLogin;
import com.mypakdriver.utils.Api;
import com.mypakdriver.utils.ApiFactory;
import com.mypakdriver.utils.AppConstant;
import com.mypakdriver.utils.ProjectUtil;
import com.mypakdriver.utils.SharedPref;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyAct extends AppCompatActivity {

    Context mContext = VerifyAct.this;
    ActivityVerifyBinding binding;
    String mobile="";
    String id;
    HashMap<String,String> paramHash = new HashMap<>();
    HashMap<String, File> fileHashMap = new HashMap<>();
    private FirebaseAuth mAuth;
    SharedPref sharedPref;
    Api api;
    ModelLogin modelLogin;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_verify);
        api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        sharedPref = SharedPref.getInstance(mContext);
        mAuth = FirebaseAuth.getInstance();

        paramHash = (HashMap<String, String>) getIntent().getSerializableExtra("resgisterHashmap");
        mobile = getIntent().getStringExtra("mobile");

        init();

        sendVerificationCode();

    }

    private void init() {

        binding.next.setOnClickListener(v -> {
            if(TextUtils.isEmpty(binding.etOtp.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_otp), Toast.LENGTH_SHORT).show();
            } else {
                ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, binding.etOtp.getText().toString().trim());
                signInWithPhoneAuthCredential(credential);
            }
        });

        binding.tvResend.setOnClickListener(v -> {
            sendVerificationCode();
        });

    }

    private void sendVerificationCode() {

        binding.tvVerifyText.setText("We have send you an SMS on " + mobile + " with 6 digit verification code.");

        new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                binding.tvResend.setText("" + millisUntilFinished/1000);
                binding.tvResend.setEnabled(false);
            }

            @Override
            public void onFinish() {
                binding.tvResend.setText(mContext.getString(R.string.resend));
                binding.tvResend.setEnabled(true);
            }
        }.start();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile.replace(" ",""), // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        VerifyAct.this.id = id;
                        Toast.makeText(mContext, getString(R.string.enter_6_digit_code), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        ProjectUtil.pauseProgressDialog();
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        ProjectUtil.pauseProgressDialog();
                        Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ProjectUtil.pauseProgressDialog();

                            FirebaseUser user = task.getResult().getUser();
                            signUpApiCall();

                        } else {
                            ProjectUtil.pauseProgressDialog();
                            Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {}

                        }
                    }
                });

    }

    private void signUpApiCall() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Call<ResponseBody> call = api.signUpApiCall(paramHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        modelLogin = new Gson().fromJson(responseString, ModelLogin.class);

                        sharedPref.setBooleanValue(AppConstant.IS_REGISTER,true);
                        sharedPref.setUserDetails(AppConstant.USER_DETAILS,modelLogin);

                        startActivity(new Intent(mContext,HomeAct.class));
                        finish();

                    } else {
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
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