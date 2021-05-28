package com.mypakdriver.utils;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DrawPollyLine {
    private Context context;
    private LatLng origin;
    private LatLng destination;
    List<List<HashMap<String, String>>> routes=new ArrayList<>();
    ArrayList<LatLng> points=new ArrayList<>();
    private PolylineOptions lineOptions;

    public DrawPollyLine(Context context) {
        this.context=context;
    }

    public static DrawPollyLine get(Context context) {
        return new DrawPollyLine(context);
    }
    public DrawPollyLine setOrigin(LatLng origin){
        this.origin=origin;
        return this;
    }
    public DrawPollyLine setDestination(LatLng destination){
        this.destination=destination;
        return this;
    }
    public void execute(onPolyLineResponse listener) {

        String URL = ProjectUtil.getPolyLineUrl(context,origin,destination);
        AndroidNetworking.get(URL).setTag("Direction").build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object=new JSONObject(response);
                            DataParser parser = new DataParser();
                            routes = parser.parse(object);
                            for (int i = 0; i < routes.size(); i++) {
                                points = new ArrayList<>();

                                List<HashMap<String, String>> path = routes.get(i);

                                for (int j = 0; j < path.size(); j++) {
                                    HashMap<String, String> point = path.get(j);
                                    double lat = Double.parseDouble(point.get("lat"));
                                    double lng = Double.parseDouble(point.get("lng"));
                                    LatLng position = new LatLng(lat, lng);
                                    points.add(position);
                                }
                                Log.e("SIZE POINT", " True >> " + points.size());
                                listener.Success(points);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        Log.e("anError","anError = " + anError.getErrorBody());
                        Log.e("anError","anError = " + anError.getErrorDetail());
                    }
                });
    }

    public interface onPolyLineResponse{
        void Success(ArrayList<LatLng> latLngs);
    }

}
