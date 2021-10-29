package com.mypakdriver.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mypakdriver.R;
import com.mypakdriver.adapters.AdapterUserRequests;
import com.mypakdriver.databinding.ActivityHomeBinding;
import com.mypakdriver.models.ModelLogin;
import com.mypakdriver.models.ModelOrderHistory;
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
    String orderIdScan = "", requestId = "";
    int position = 0;
    // qr code scanner object
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        //intializing scan object
        qrScan = new IntentIntegrator(this);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPref = SharedPref.getInstance(mContext);
        binding.navItems.tvName.setText(modelLogin.getResult().getUser_name());
    }

    private void init() {

        TabLayout.Tab pendingTab = binding.tabLayout.newTab();
        pendingTab.setText("Pending");
        binding.tabLayout.addTab(pendingTab);

        TabLayout.Tab acceptTab = binding.tabLayout.newTab();
        acceptTab.setText("Accept");
        binding.tabLayout.addTab(acceptTab);

        TabLayout.Tab comTab = binding.tabLayout.newTab();
        comTab.setText("Complete");
        binding.tabLayout.addTab(comTab);

        TabLayout.Tab rejectTab = binding.tabLayout.newTab();
        rejectTab.setText("Reject");
        binding.tabLayout.addTab(rejectTab);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                position = tab.getPosition();
                if (tab.getPosition() == 0) {
                    getAllUserRequest("Pending");
                } else if (tab.getPosition() == 1) {
                    getAllUserRequest("Accept");
                } else if (tab.getPosition() == 2) {
                    getAllUserRequest("Complete");
                } else if (tab.getPosition() == 3) {
                    getAllUserRequest("Cancel");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });

        binding.swipLayout.setRefreshing(true);
        getAllUserRequest("Pending");

//        binding.navItems.tvMyTransport.setOnClickListener(v -> {
//            startActivity(new Intent(mContext, DriverTrackAct.class));
//            binding.drawerLayout.closeDrawer(GravityCompat.START);
//        });

        binding.navItems.tvMessages.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ChatListAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

//        binding.navItems.tvMyProfile.setOnClickListener(v -> {
//            startActivity(new Intent(mContext,MyProfileAct.class));
//            binding.drawerLayout.closeDrawer(GravityCompat.START);
//        });

        binding.navItems.tvChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(mContext, ChangePasAct.class));
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.navItems.tvLogout.setOnClickListener(v -> {
            logoutAppDialog();
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });

        binding.ivMenu.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });

        binding.swipLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (position == 0) {
                            getAllUserRequest("Pending");
                        } else if (position == 1) {
                            getAllUserRequest("Accept");
                        } else if (position == 2) {
                            getAllUserRequest("Complete");
                        } else if (position == 3) {
                            getAllUserRequest("Cancel");
                        }
                    }
                });

    }

    public void scanQrCode(String request, String qrCode) {
        orderIdScan = qrCode;
        requestId = request;
        qrScan.initiateScan();
    }

    private void acceptRejectApiCalled() {
        ProjectUtil.showProgressDialog(mContext, true, mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Log.e("sdfsdfdsfdsvf","user_id = " + modelLogin.getResult().getId());

        HashMap<String,String> param = new HashMap<>();
        param.put("shipping_id", requestId);
        param.put("status", "Complete");

        Log.e("sdfsdfdsfdsvf", param.toString());

        Call<ResponseBody> call = api.driverAcceptRejectApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("updateParcel", "updateParcel = " + responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        updateParcel("Complete");
                    }
                } catch (Exception e) {
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }

    private void getAllUserRequest(String status) {
        ProjectUtil.showProgressDialog(mContext, true, getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Log.e("sdfsdfdsfdsvf", "user_id = " + modelLogin.getResult().getId());

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", modelLogin.getResult().getId());
        param.put("status", status);

        Call<ResponseBody> call = api.getAllUserRequestApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if (jsonObject.getString("status").equals("1")) {
                        Log.e("responseString", "responseString = " + responseString);
                        ModelOrderHistory modelOrderHistory = new Gson().fromJson(responseString, ModelOrderHistory.class);

                        AdapterUserRequests adapterUserRequests = new AdapterUserRequests(mContext, modelOrderHistory.getResult(), HomeAct.this::updateParcel);
                        binding.rvRequest.setAdapter(adapterUserRequests);

                    } else {
                        AdapterUserRequests adapterUserRequests = new AdapterUserRequests(mContext, null, HomeAct.this::updateParcel);
                        binding.rvRequest.setAdapter(adapterUserRequests);
                        Toast.makeText(HomeAct.this, getString(R.string.no_request_found), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    //Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception", "Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
                binding.swipLayout.setRefreshing(false);
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                try {
                    if (orderIdScan.equals(result.getContents().trim())) {
                        acceptRejectApiCalled();
                    } else {
                        Toast.makeText(mContext, "Please confirm that QR code is correct", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void updateParcel(String status) {
        Log.e("updateParcel", " updateParcel = " + status);
        if (status.equals("Accept")) {
            binding.tabLayout.getTabAt(1).select();
        } else if (status.equals("Complete")) {
            binding.tabLayout.getTabAt(2).select();
        } else if (status.equals("Cancel")) {
            binding.tabLayout.getTabAt(3).select();
        }
    }

    private void logoutAppDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.logout_text))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sharedPref.clearAllPreferences();
                        Intent loginscreen = new Intent(mContext, LoginAct.class);
                        loginscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        NotificationManager nManager = ((NotificationManager)
                                mContext.getSystemService(Context.NOTIFICATION_SERVICE));
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

    private void exitAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(getString(R.string.close_app_text))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        exitAppDialog();
    }

}