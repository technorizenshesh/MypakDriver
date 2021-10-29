package com.mypakdriver.models;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.mypakdriver.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelChatList implements Serializable {

    private ArrayList<Result> result;
    private String message;
    private String status;

    public void setResult(ArrayList<Result> result){
        this.result = result;
    }
    public ArrayList<Result> getResult(){
        return this.result;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }

    public class Result
    {
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

        private int no_of_message;

        private String last_message;

        private String last_image;

        private String date;

        private String time;

        private String time_ago;

        private String sender_id;

        private String receiver_id;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setUser_name(String user_name){
            this.user_name = user_name;
        }
        public String getUser_name(){
            return this.user_name;
        }
        public void setMobile(String mobile){
            this.mobile = mobile;
        }
        public String getMobile(){
            return this.mobile;
        }
        public void setEmail(String email){
            this.email = email;
        }
        public String getEmail(){
            return this.email;
        }
        public void setPassword(String password){
            this.password = password;
        }
        public String getPassword(){
            return this.password;
        }
        public void setImage(String image){
            this.image = image;
        }
        public String getImage(){
            return this.image;
        }
        public void setOtp(String otp){
            this.otp = otp;
        }
        public String getOtp(){
            return this.otp;
        }
        public void setStatus(String status){
            this.status = status;
        }
        public String getStatus(){
            return this.status;
        }
        public void setLat(String lat){
            this.lat = lat;
        }
        public String getLat(){
            return this.lat;
        }
        public void setLon(String lon){
            this.lon = lon;
        }
        public String getLon(){
            return this.lon;
        }
        public void setAddress(String address){
            this.address = address;
        }
        public String getAddress(){
            return this.address;
        }
        public void setSocial_id(String social_id){
            this.social_id = social_id;
        }
        public String getSocial_id(){
            return this.social_id;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
        public void setIos_register_id(String ios_register_id){
            this.ios_register_id = ios_register_id;
        }
        public String getIos_register_id(){
            return this.ios_register_id;
        }
        public void setRegister_id(String register_id){
            this.register_id = register_id;
        }
        public String getRegister_id(){
            return this.register_id;
        }
        public void setLand_mark(String land_mark){
            this.land_mark = land_mark;
        }
        public String getLand_mark(){
            return this.land_mark;
        }
        public void setTitle(String title){
            this.title = title;
        }
        public String getTitle(){
            return this.title;
        }
        public void setType(String type){
            this.type = type;
        }
        public String getType(){
            return this.type;
        }
        public void setDocument1(String document1){
            this.document1 = document1;
        }
        public String getDocument1(){
            return this.document1;
        }
        public void setDocument2(String document2){
            this.document2 = document2;
        }
        public String getDocument2(){
            return this.document2;
        }
        public void setOnline_status(String online_status){
            this.online_status = online_status;
        }
        public String getOnline_status(){
            return this.online_status;
        }
        public void setSub_admin_id(String sub_admin_id){
            this.sub_admin_id = sub_admin_id;
        }
        public String getSub_admin_id(){
            return this.sub_admin_id;
        }
        public void setNo_of_message(int no_of_message){
            this.no_of_message = no_of_message;
        }
        public int getNo_of_message(){
            return this.no_of_message;
        }
        public void setLast_message(String last_message){
            this.last_message = last_message;
        }
        public String getLast_message(){
            return this.last_message;
        }
        public void setLast_image(String last_image){
            this.last_image = last_image;
        }
        public String getLast_image(){
            return this.last_image;
        }
        public void setDate(String date){
            this.date = date;
        }
        public String getDate(){
            return this.date;
        }
        public void setTime(String time){
            this.time = time;
        }
        public String getTime(){
            return this.time;
        }
        public void setTime_ago(String time_ago){
            this.time_ago = time_ago;
        }
        public String getTime_ago(){
            return this.time_ago;
        }
        public void setSender_id(String sender_id){
            this.sender_id = sender_id;
        }
        public String getSender_id(){
            return this.sender_id;
        }
        public void setReceiver_id(String receiver_id){
            this.receiver_id = receiver_id;
        }
        public String getReceiver_id(){
            return this.receiver_id;
        }
    }

    @BindingAdapter({"android:image"})
    public static void setImageView(ImageView imageView, String Url) {
        Picasso.get().load(Url)
                .placeholder(R.drawable.default_profile_icon)
                .error(R.drawable.default_profile_icon)
                .into(imageView);
    }


}
