package com.mypakdriver.models;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.mypakdriver.R;
import com.squareup.picasso.Picasso;

public class ModelLogin {

    private Result result;
    private String message;
    private String status;

    public void setResult(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return this.result;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public class Result {
        private String id;

        private String user_name;

        private String mobile;

        private String email;

        private String password;

        private String image;

        private String otp;

        private String status;

        private String lat;

        private String lon;

        private String address;

        private String social_id;

        private String date_time;

        private String ios_register_id;

        private String register_id;

        private String land_mark;

        private String title;

        private String type;

        private String document1;

        private String document2;

        private String online_status;

        private String sub_admin_id;

        private String booking_status;

        private String car_type_id;

        private String brand;

        private String car_model;

        private String year_of_manufacture;

        private String car_number;

        private String car_image;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getUser_name() {
            return this.user_name;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getMobile() {
            return this.mobile;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmail() {
            return this.email;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPassword() {
            return this.password;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getImage() {
            return this.image;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        public String getOtp() {
            return this.otp;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLat() {
            return this.lat;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getLon() {
            return this.lon;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddress() {
            return this.address;
        }

        public void setSocial_id(String social_id) {
            this.social_id = social_id;
        }

        public String getSocial_id() {
            return this.social_id;
        }

        public void setDate_time(String date_time) {
            this.date_time = date_time;
        }

        public String getDate_time() {
            return this.date_time;
        }

        public void setIos_register_id(String ios_register_id) {
            this.ios_register_id = ios_register_id;
        }

        public String getIos_register_id() {
            return this.ios_register_id;
        }

        public void setRegister_id(String register_id) {
            this.register_id = register_id;
        }

        public String getRegister_id() {
            return this.register_id;
        }

        public void setLand_mark(String land_mark) {
            this.land_mark = land_mark;
        }

        public String getLand_mark() {
            return this.land_mark;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

        public void setDocument1(String document1) {
            this.document1 = document1;
        }

        public String getDocument1() {
            return this.document1;
        }

        public void setDocument2(String document2) {
            this.document2 = document2;
        }

        public String getDocument2() {
            return this.document2;
        }

        public void setOnline_status(String online_status) {
            this.online_status = online_status;
        }

        public String getOnline_status() {
            return this.online_status;
        }

        public void setSub_admin_id(String sub_admin_id) {
            this.sub_admin_id = sub_admin_id;
        }

        public String getSub_admin_id() {
            return this.sub_admin_id;
        }

        public void setBooking_status(String booking_status) {
            this.booking_status = booking_status;
        }

        public String getBooking_status() {
            return this.booking_status;
        }

        public void setCar_type_id(String car_type_id) {
            this.car_type_id = car_type_id;
        }

        public String getCar_type_id() {
            return this.car_type_id;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getBrand() {
            return this.brand;
        }

        public void setCar_model(String car_model) {
            this.car_model = car_model;
        }

        public String getCar_model() {
            return this.car_model;
        }

        public void setYear_of_manufacture(String year_of_manufacture) {
            this.year_of_manufacture = year_of_manufacture;
        }

        public String getYear_of_manufacture() {
            return this.year_of_manufacture;
        }

        public void setCar_number(String car_number) {
            this.car_number = car_number;
        }

        public String getCar_number() {
            return this.car_number;
        }

        public void setCar_image(String car_image) {
            this.car_image = car_image;
        }

        public String getCar_image() {
            return this.car_image;
        }

    }

    @BindingAdapter({"android:image"})
    public static void loadImage(ImageView imageView, String imageUrl) {
        Picasso.get().load(imageUrl)
                .placeholder(R.drawable.default_profile_icon)
                .error(R.drawable.default_profile_icon)
                .into(imageView);
    }

}
