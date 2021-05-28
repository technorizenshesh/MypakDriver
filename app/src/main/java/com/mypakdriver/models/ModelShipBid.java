package com.cityonedriver.shipping.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ModelShipBid implements Serializable {

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

        private String driver_id;

        private String shipping_id;

        private String price;

        private String pick_date;

        private String drop_date;

        private String comment;

        private String date_time;

        private String name;

        private String image;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setId(String id){
            this.id = id;
        }
        public String getId(){
            return this.id;
        }
        public void setDriver_id(String driver_id){
            this.driver_id = driver_id;
        }
        public String getDriver_id(){
            return this.driver_id;
        }
        public void setShipping_id(String shipping_id){
            this.shipping_id = shipping_id;
        }
        public String getShipping_id(){
            return this.shipping_id;
        }
        public void setPrice(String price){
            this.price = price;
        }
        public String getPrice(){
            return this.price;
        }
        public void setPick_date(String pick_date){
            this.pick_date = pick_date;
        }
        public String getPick_date(){
            return this.pick_date;
        }
        public void setDrop_date(String drop_date){
            this.drop_date = drop_date;
        }
        public String getDrop_date(){
            return this.drop_date;
        }
        public void setComment(String comment){
            this.comment = comment;
        }
        public String getComment(){
            return this.comment;
        }
        public void setDate_time(String date_time){
            this.date_time = date_time;
        }
        public String getDate_time(){
            return this.date_time;
        }
    }

}
