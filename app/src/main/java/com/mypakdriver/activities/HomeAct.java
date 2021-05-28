package com.mypakdriver.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mypakdriver.R;
import com.mypakdriver.adapters.AdapterShipRequest;
import com.mypakdriver.databinding.ActivityHomeBinding;
import com.mypakdriver.models.ModelLogin;
import com.mypakdriver.models.ModelShipRequest;
import com.mypakdriver.utils.Api;
import com.mypakdriver.utils.ApiFactory;
import com.mypakdriver.utils.AppConstant;
import com.mypakdriver.utils.ProjectUtil;
import com.mypakdriver.utils.SharedPref;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeAct extends AppCompatActivity {

    Context mContext = HomeAct.this;
    ActivityHomeBinding binding;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    ModelShipRequest modelShipRequest;
    // AddShippDialogBinding dialogBinding;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_home);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPref = SharedPref.getInstance(mContext);
        binding.navItems.tvName.setText(modelLogin.getResult().getUser_name());
    }

    private void init() {
        
        binding.navItems.tvMyTransport.setOnClickListener(v -> {
            startActivity(new Intent(mContext,ParcelTransportAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.navItems.tvMessages.setOnClickListener(v -> {
            startActivity(new Intent(mContext,ChatListAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.navItems.tvMyProfile.setOnClickListener(v -> {
            startActivity(new Intent(mContext,MyProfileAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.navItems.tvChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(mContext,ChangePasAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.navItems.tvLogout.setOnClickListener(v -> {
            logoutAppDialog();
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.ivMenu.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });

        getAllShipRequest();

        binding.swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllShipRequest();
            }
        });

    }

    private void getAllShipRequest() {
        ProjectUtil.showProgressDialog(mContext,true,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Log.e("sdfsdfdsfdsvf","user_id = " + modelLogin.getResult().getId());

        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",modelLogin.getResult().getId());

        Call<ResponseBody> call = api.getAllShRequestApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        modelShipRequest = new Gson().fromJson(responseString, ModelShipRequest.class);

                        AdapterShipRequest adapterShipRequest = new AdapterShipRequest(mContext,modelShipRequest.getResult());
                        binding.rvRequest.setAdapter(adapterShipRequest);

                        Log.e("sdfsdfdsfdsvf","responseString = " + responseString);

                    } else {
                        Toast.makeText(mContext, getString(R.string.no_request_found), Toast.LENGTH_SHORT).show();
                        AdapterShipRequest adapterShipRequest = new AdapterShipRequest(mContext,null);
                        binding.rvRequest.setAdapter(adapterShipRequest);
                        // Toast.makeText(mContext, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    //Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
            }

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