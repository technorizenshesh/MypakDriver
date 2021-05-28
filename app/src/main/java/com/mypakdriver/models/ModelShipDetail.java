package com.cityonedriver.shipping.models;

import java.io.Serializable;

public class ModelShipDetail implements Serializable {

    private Result result;
    private String message;
    private String status;

    public void setResult(Result result){
        this.result = result;
    }
    public Result getResult(){
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

        private String user_id;

        private String driver_id;

        private String pickup_location;

        private String pickup_lat;

        private String pickup_lon;

        private String drop_location;

        private String drop_lat;

        private String drop_lon;

        private String status;

        private String date_time;

        private String pickup_date;

        private String recipient_name;

        private String mobile_no;

        private String parcel_quantity;

        private String parcel_category;

        private String item_detail;

        private String dev_instruction;

        private String direction_json;

        private String dropoff_date;

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setUser_id(String user_id){
            this.user_id = user_id;
        }
        public String getUser_id(){
            return this.user_id;
        }
        public void setDriver_id(String driver_id){
            this.driver_id = driver_id;
        }
        public String getDriver_id(){
            return this.driver_id;
        }
        public void setPickup_location(String pickup_location){
            this.pickup_location = pickup_location;
        }
        public String getPickup_location(){
            return this.pickup_location;
        }
        public void setPickup_lat(String pickup_lat){
            this.pickup_lat = pickup_lat;
        }
        public String getPickup_lat(){
            return this.pickup_lat;
        }
        public void setPickup_lon(String pickup_lon){
            this.pickup_lon = pickup_lon;
        }
        public String getPickup_lon(){
            return this.pickup_lon;
        }
        public void setDrop_location(String drop_location){
            this.drop_location = drop_location;
        }
        public String getDrop_location(){
            return this.drop_location;
        }
        public void setDrop_lat(String drop_lat){
            this.drop_lat = drop_lat;
        }
        public String getDrop_lat(){
            return this.drop_lat;
        }
        public void setDrop_lon(String drop_lon){
            this.drop_lon = drop_lon;
        }
        public String getDrop_lon(){
            return this.drop_lon;
        }
        public void setStatus(String status){
            this.status = status;
        }
        public String getStatus(){
            return this.status;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
        public void setPickup_date(String pickup_date){
            this.pickup_date = pickup_date;
        }
        public String getPickup_date(){
            return this.pickup_date;
        }
        public void setRecipient_name(String recipient_name){
            this.recipient_name = recipient_name;
        }
        public String getRecipient_name(){
            return this.recipient_name;
        }
        public void setMobile_no(String mobile_no){
            this.mobile_no = mobile_no;
        }
        public String getMobile_no(){
            return this.mobile_no;
        }
        public void setParcel_quantity(String parcel_quantity){
            this.parcel_quantity = parcel_quantity;
        }
        public String getParcel_quantity(){
            return this.parcel_quantity;
        }
        public void setParcel_category(String parcel_category){
            this.parcel_category = parcel_category;
        }
        public String getParcel_category(){
            return this.parcel_category;
        }
        public void setItem_detail(String item_detail){
            this.item_detail = item_detail;
        }
        public String getItem_detail(){
            return this.item_detail;
        }
        public void setDev_instruction(String dev_instruction){
            this.dev_instruction = dev_instruction;
        }
        public String getDev_instruction(){
            return this.dev_instruction;
        }
        public void setDirection_json(String direction_json){
            this.direction_json = direction_json;
        }
        public String getDirection_json(){
            return this.direction_json;
        }
        public void setDropoff_date(String dropoff_date){
            this.dropoff_date = dropoff_date;
        }
        public String getDropoff_date(){
            return this.dropoff_date;
        }
    }


}

