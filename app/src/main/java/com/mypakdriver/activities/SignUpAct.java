package com.mypakdriver.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mypakdriver.R;
import com.mypakdriver.databinding.ActivitySignupBinding;
import com.mypakdriver.utils.ProjectUtil;

import java.util.HashMap;

public class SignUpAct extends AppCompatActivity {

    Context mContext = SignUpAct.this;
    ActivitySignupBinding binding;
    private String registerId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_signup);
        ProjectUtil.changeStatusBarColor(SignUpAct.this);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        if (!task.isSuccessful()) {
                            return;
                        }

                        String token = task.getResult().getToken();
                        registerId = token;

                    }

                });

        init();

    }
    private void init() {

        binding.btSignUp.setOnClickListener(v -> {
            if(TextUtils.isEmpty(binding.etName.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_username), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_email_add), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etPhone.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_phone_add), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etPass.getText().toString().trim())){
                Toast.makeText(mContext, getString(R.string.please_enter_pass), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(binding.etConPass.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_enter_conf_pass), Toast.LENGTH_SHORT).show();
            } else if(!(binding.etPass.getText().toString().trim().length() > 4 )) {
                Toast.makeText(mContext, getString(R.string.password_validation_text), Toast.LENGTH_SHORT).show();
            } else if(!(binding.etPass.getText().toString().trim().equals(binding.etConPass.getText().toString().trim()))){
                Toast.makeText(mContext, getString(R.string.password_not_match), Toast.LENGTH_SHORT).show();
            } else if(!ProjectUtil.isValidEmail(binding.etEmail.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            } else if(!validateUsing_libphonenumber(binding.etPhone.getText().toString().replace(" ","")
                    ,binding.ccp.getSelectedCountryCode())) {
                Toast.makeText(mContext, getString(R.string.invalid_number), Toast.LENGTH_SHORT).show();
            } else {
                HashMap<String,String> params = new HashMap<>();

                params.put("user_name",binding.etName.getText().toString().trim());
                params.put("email",binding.etEmail.getText().toString().trim());
                params.put("mobile",binding.etPhone.getText().toString().trim());
                params.put("register_id",registerId);
                params.put("password",binding.etPass.getText().toString().trim());
                params.put("type","DRIVER");

                String mobileWithCounCode = (binding.ccp.getSelectedCountryCodeWithPlus()
                        + binding.etPhone.getText().toString().trim()).replace(" ","");

                startActivity(new Intent(mContext,VerifyAct.class)
                        .putExtra("resgisterHashmap" , params)
                        .putExtra("mobile" , mobileWithCounCode)
                        .putExtra("request" , "user")
                );

            }
        });

        binding.tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(mContext,LoginAct.class));
            finish();
        });

    }

    private boolean validateUsing_libphonenumber(String phNumber,String code) {

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(code));
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            //phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode);
        } catch (Exception e) {
            System.err.println(e);
        }

        boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
        if (isValid) {
            String internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            // Toast.makeText(this, "Phone Number is Valid " + internationalFormat, Toast.LENGTH_LONG).show();
            return true;
        } else {
            // Toast.makeText(this, "Phone Number is Invalid " + phoneNumber, Toast.LENGTH_LONG).show();
            return false;
        }

    }


}