package com.mypakdriver.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import com.mypakdriver.models.ModelOrderHistory;
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
    ModelOrderHistory.Result data;
    GoogleMap gMap;
    SupportMapFragment mapFragment;
    MarkerOptions originOption,dropOffOption;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    private PolylineOptions lineOptions;
    private LatLng dropLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_parcel_detail);
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);

        data = (ModelOrderHistory.Result) getIntent().getSerializableExtra("data");

        init();

    }

    private void navigateToGooglMap(String sAddres,String dAddress) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr="+sAddres+"&daddr="+dAddress));
        mContext.startActivity(intent);
    }

    private void init() {

        binding.setShip(data);

        binding.btNavigationToMap.setOnClickListener(v -> {
            navigateToGooglMap(data.getPickup_lat()+","+data.getPickup_lon(),
                    data.getDrop_lat()+","+data.getDrop_lon());
        });

//        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(ParcelDetailAct.this);

        binding.ivBack.setOnClickListener(v -> {
            finish();
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