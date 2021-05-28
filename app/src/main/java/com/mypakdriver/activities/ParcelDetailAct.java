package com.mypakdriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.mypakdriver.R;
import com.mypakdriver.adapters.AdapterShipBids;
import com.mypakdriver.databinding.ActivityParcelDetailBinding;
import com.mypakdriver.databinding.AddBidDialogBinding;
import com.mypakdriver.models.ModelLogin;
import com.mypakdriver.models.ModelShipBid;
import com.mypakdriver.models.ModelShipDetail;
import com.mypakdriver.utils.Api;
import com.mypakdriver.utils.ApiFactory;
import com.mypakdriver.utils.AppConstant;
import com.mypakdriver.utils.DrawPollyLine;
import com.mypakdriver.utils.ProjectUtil;
import com.mypakdriver.utils.SharedPref;
import com.mypakdriver.utils.onDateSetListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParcelDetailAct extends AppCompatActivity
        implements OnMapReadyCallback {

    Context mContext = ParcelDetailAct.this;
    ActivityParcelDetailBinding binding;
    String parcelId = "";
    GoogleMap gMap;
    SupportMapFragment mapFragment;
    MarkerOptions originOption,dropOffOption;
    LatLng originLatLng,dropLatLng;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    long pickUpDateMilli = 0;
    AdapterShipBids adapterShipBids;
    ModelShipBid modelShipBid;
    boolean orderType;
    private PolylineOptions lineOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_parcel_detail);
        parcelId = getIntent().getStringExtra("parcelid");
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        orderType = getIntent().getBooleanExtra("type",false);

        if(orderType) {
            binding.rvBids.setVisibility(View.GONE);
            binding.btBidNow.setVisibility(View.GONE);
        } else {
            binding.rvBids.setVisibility(View.VISIBLE);
            binding.btBidNow.setVisibility(View.VISIBLE);
        }

        init();

        shipDetailApi();
        getAllBidsNotLoader();
    }

    private void init() {

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(ParcelDetailAct.this);

        binding.ivBack.setOnClickListener(v -> {
            finish();
        });

        binding.btBidNow.setOnClickListener(v -> {
            openAddBid();
        });


    }

    private void openAddBid() {

        Dialog dialog = new Dialog(mContext, WindowManager.LayoutParams.MATCH_PARENT);
        AddBidDialogBinding dialogBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),R.layout.add_bid_dialog,null,false);
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.ivBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.etPickDate.setOnClickListener(v -> {
            ProjectUtil.DatePicker(0,mContext, new onDateSetListener() {
                @Override
                public void SelectedDate(String data) {
                    pickUpDateMilli = ProjectUtil.getTimeInMillSec(data);
                    dialogBinding.etDropDate.setText("");
                    dialogBinding.etPickDate.setText(data);
                }
            });
        });

        dialogBinding.etDropDate.setOnClickListener(v -> {
            ProjectUtil.DatePicker(pickUpDateMilli,mContext, new onDateSetListener() {
                @Override
                public void SelectedDate(String data) {
                    pickUpDateMilli = ProjectUtil.getTimeInMillSec(data);
                    dialogBinding.etDropDate.setText(data);
                }
            });
        });

        dialogBinding.btApply.setOnClickListener(v -> {
            if(TextUtils.isEmpty(dialogBinding.etPrice.getText().toString().trim())){
                Toast.makeText(mContext, getString(R.string.please_enter_price), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(dialogBinding.etPickDate.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_pickup_date), Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(dialogBinding.etDropDate.getText().toString().trim())) {
                Toast.makeText(mContext, getString(R.string.please_select_dropoff_date), Toast.LENGTH_SHORT).show();
            } else {
                dialog.dismiss();
                Toast.makeText(mContext, "We are working on this", Toast.LENGTH_SHORT).show();
//                addBidApi(dialogBinding.etPrice.getText().toString().trim()
//                        ,dialogBinding.etPickDate.getText().toString().trim(),
//                        dialogBinding.etDropDate.getText().toString().trim(),
//                        dialogBinding.etComment.getText().toString().trim(),dialog);
            }
        });

        dialog.show();

    }

    private void getAllBidsNotLoader() {
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> param = new HashMap<>();
        param.put("shipping_id",parcelId);

        Call<ResponseBody> call = api.getBidApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String stringRes = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringRes);
                    if(jsonObject.getString("status").equals("1")) {
                        modelShipBid = new Gson().fromJson(stringRes,ModelShipBid.class);
                        adapterShipBids = new AdapterShipBids(mContext,modelShipBid.getResult());
                        binding.rvBids.setHasFixedSize(true);
                        binding.rvBids.setAdapter(adapterShipBids);
                    } else {}
                } catch (Exception e) {}
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }

    private void addBidApi(String price, String pickDate, String dropDate, String comment,Dialog dialog) {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        HashMap<String,String> paramsHash = new HashMap<>();
        paramsHash.put("driver_id",modelLogin.getResult().getId());
        paramsHash.put("price",price);
        paramsHash.put("pick_date",pickDate);
        paramsHash.put("drop_date",dropDate);
        paramsHash.put("comment",comment);
        paramsHash.put("shipping_id",parcelId);

        Log.e("paramsHash","paramsHash = " + paramsHash.toString());

        Call<ResponseBody> call = api.addBidApiCall(paramsHash);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        Log.e("responseStringfdgdg","responseString = " + responseString);
                        Toast.makeText(mContext, getString(R.string.success), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        getAllBidsNotLoader();
                    } else {}

                } catch (Exception e) {
                    // Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });

    }

    private void shipDetailApi() {
        ProjectUtil.showProgressDialog(mContext,false,getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);
        HashMap<String,String> params = new HashMap<>();
        params.put("shipping_id",parcelId);

        Log.e("shipping_id","shipping_id = " + parcelId);

        Call<ResponseBody> call = api.getShipDetailApiCall(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    if(jsonObject.getString("status").equals("1")) {

                        ModelShipDetail modelShipDetail = new Gson().fromJson(responseString,ModelShipDetail.class);
                        binding.setShip(modelShipDetail.getResult());

                        originLatLng = new LatLng(Double.parseDouble(modelShipDetail.getResult().getPickup_lat())
                                ,Double.parseDouble(modelShipDetail.getResult().getPickup_lon()));

                        dropLatLng = new LatLng(Double.parseDouble(modelShipDetail.getResult().getDrop_lat())
                                ,Double.parseDouble(modelShipDetail.getResult().getDrop_lon()));

                        originOption = new MarkerOptions().position(originLatLng).title(modelShipDetail.getResult().getPickup_location());
                        dropOffOption = new MarkerOptions().position(dropLatLng).title(modelShipDetail.getResult().getDrop_location());

                        drawRoute();

                        Log.e("responseString","responseString = " + responseString);

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

    private void drawRoute() {
        gMap.addMarker(originOption);
        gMap.addMarker(dropOffOption);

        ArrayList<LatLng> listLatLon = new ArrayList<>();
        listLatLon.add(originOption.getPosition());
        listLatLon.add(dropOffOption.getPosition());

        zoomRoute(gMap,listLatLon);
        DrawPolyLine();
    }

    private void DrawPolyLine() {
        DrawPollyLine.get(this).setOrigin(originOption.getPosition())
                .setDestination(dropOffOption.getPosition())
                .execute(new DrawPollyLine.onPolyLineResponse() {
            @Override
            public void Success(ArrayList<LatLng> latLngs) {
                Log.e("SuccessSuccess","latLngs = " + latLngs);
                lineOptions = new PolylineOptions();
                lineOptions.addAll(latLngs);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);
                ArrayList<LatLng> latlonList = new ArrayList<>();
                latlonList.add(originOption.getPosition());
                latlonList.add(dropLatLng);
                zoomRoute(gMap,latlonList);
                gMap.addPolyline(lineOptions);
            }
        });
    }

    public void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {
        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 100;
        LatLngBounds latLngBounds = boundsBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
    }

}