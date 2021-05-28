package com.mypakdriver.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.AsyncTask;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mypakdriver.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

    List<LatLng> listLatLng = new ArrayList<>();
    Context context;
    GoogleMap googleMap;
    private Polyline blackPolyLine, greyPolyLine, blackPolyLine1, greyPolyLine1;
    CallPolyLineMap callPolyLineMap;

    public ParserTask(Context context, GoogleMap googleMap, CallPolyLineMap callPolyLineMap) {
        this.context = context;
        this.googleMap = googleMap;
        this.callPolyLineMap = callPolyLineMap;
    }

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;
        try {
            jObject = new JSONObject(jsonData[0]);
            DirectionsJSONParser parser = new DirectionsJSONParser();
            routes = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
       /* ArrayList points = null;

        // MarkerOptions markerOptions = new MarkerOptions();

        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList();
            List<HashMap<String, String>> path = result.get(i);
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat").toString());
                double lng = Double.parseDouble(point.get("lng").toString());

                LatLng position = new LatLng(lat, lng);
                points.add(position);
            }
            listLatLng.addAll(points);


            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.width(10);
            lineOptions.color(context.getResources().getColor(R.color.black));
            lineOptions.startCap(new SquareCap());
            lineOptions.endCap(new SquareCap());
            lineOptions.jointType(ROUND);
            blackPolyLine = googleMap.addPolyline(lineOptions);

          *//*  PolylineOptions greyOptions = new PolylineOptions();
            greyOptions.width(10);
            greyOptions.color(context.getResources().getColor(R.color.black));
            greyOptions.startCap(new SquareCap());
            greyOptions.endCap(new SquareCap());
            greyOptions.jointType(ROUND);
            greyPolyLine = googleMap.addPolyline(greyOptions);*//*
           // animatePolyLine();

            callPolyLineMap.getPolyline(blackPolyLine, greyPolyLine);

        }*/

        ArrayList<LatLng> points;
        PolylineOptions lineOptions = null;
        // Traversing through all the routes
        if (result != null)
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(context.getResources().getColor(R.color.darkGreen));
            }

        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null) {
            blackPolyLine = googleMap.addPolyline(lineOptions);
        }
        callPolyLineMap.getPolyline(blackPolyLine, greyPolyLine);
    }

    private void animatePolyLine() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {

                List<LatLng> latLngList = blackPolyLine.getPoints();
                int initialPointSize = latLngList.size();
                int animatedValue = (int) animator.getAnimatedValue();
                int newPoints = (animatedValue * listLatLng.size()) / 100;

                if (initialPointSize < newPoints) {
                    latLngList.addAll(listLatLng.subList(initialPointSize, newPoints));
                    blackPolyLine.setPoints(latLngList);
                }
            }
        });
        animator.addListener(polyLineAnimationListener);
        animator.start();

    }

    Animator.AnimatorListener polyLineAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {

            List<LatLng> blackLatLng = blackPolyLine.getPoints();
            List<LatLng> greyLatLng = greyPolyLine.getPoints();

            greyLatLng.clear();
            greyLatLng.addAll(blackLatLng);
            blackLatLng.clear();

            blackPolyLine.setPoints(blackLatLng);
            greyPolyLine.setPoints(greyLatLng);
            blackPolyLine.setZIndex(2);

            animator.start();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };


}
