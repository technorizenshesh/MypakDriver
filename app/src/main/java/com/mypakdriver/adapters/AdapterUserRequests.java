package com.mypakdriver.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.mypakdriver.R;
import com.mypakdriver.activities.ChatingAct;
import com.mypakdriver.activities.HomeAct;
import com.mypakdriver.activities.ParcelDetailAct;
import com.mypakdriver.databinding.AdapterUserRequestsBinding;
import com.mypakdriver.models.ModelLogin;
import com.mypakdriver.models.ModelOrderHistory;
import com.mypakdriver.utils.Api;
import com.mypakdriver.utils.ApiFactory;
import com.mypakdriver.utils.AppConstant;
import com.mypakdriver.utils.ProjectUtil;
import com.mypakdriver.utils.SharedPref;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterUserRequests extends RecyclerView.Adapter<AdapterUserRequests.StoreHolder> {

    Context mContext;
    ArrayList<ModelOrderHistory.Result> storeList;
    SharedPref sharedPref;
    ModelLogin modelLogin;
    UpdateParcel updateParcel;

    public AdapterUserRequests(Context mContext,ArrayList<ModelOrderHistory.Result> storeList, UpdateParcel updateParcel) {
        this.mContext = mContext;
        this.storeList = storeList;
        this.updateParcel = updateParcel;
        sharedPref = SharedPref.getInstance(mContext);
        modelLogin = sharedPref.getUserDetails(AppConstant.USER_DETAILS);
    }

    @NonNull
    @Override
    public AdapterUserRequests.StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdapterUserRequestsBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext),R.layout.adapter_user_requests,parent,false);
        return new AdapterUserRequests.StoreHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUserRequests.StoreHolder holder, int position) {
        ModelOrderHistory.Result data = storeList.get(position);

        holder.binding.setData(data);

        Log.e("asdasdasdasd","Status = " + data.getStatus());

        if("Pending".equals(data.getStatus())) {
            holder.binding.btAcceptOrChat.setText("Accept");
            holder.binding.btRejectOrShowOnMap.setText("Reject");
            holder.binding.llARP.setVisibility(View.VISIBLE);
        } else if("Accept".equals(data.getStatus())) {
            holder.binding.btAcceptOrChat.setText("Chat");
            holder.binding.btRejectOrShowOnMap.setText("Track");
            holder.binding.llARP.setVisibility(View.VISIBLE);
            holder.binding.btScanCode.setVisibility(View.VISIBLE);
        } else if("Complete".equals(data.getStatus())) {
            holder.binding.llARP.setVisibility(View.GONE);
            holder.binding.btScanCode.setVisibility(View.GONE);
        } else if("Cancel".equals(data.getStatus())) {
            holder.binding.llARP.setVisibility(View.GONE);
            holder.binding.btScanCode.setVisibility(View.GONE);
        }

        holder.binding.btScanCode.setOnClickListener(v -> {
            ((HomeAct)mContext).scanQrCode(data.getId(),data.getQr_code().trim());
//            if("Complete".equals(data.getStatus())) {
//                Toast.makeText(mContext, "We are working on this", Toast.LENGTH_SHORT).show();
//            } else {
//                acceptRejectApiCalled(position,data.getId(),"Complete");
//            }
        });

        holder.binding.btParcel.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext,ParcelDetailAct.class)
                .putExtra("data",data)
            );
        });

        holder.binding.btAcceptOrChat.setOnClickListener(v -> {
            if("Pending".equals(data.getStatus())) {
                acceptRejectApiCalled(position,data.getId(),"Accept");
            } else {
                mContext.startActivity(new Intent(mContext,ChatingAct.class)
                        .putExtra("sender_id",modelLogin.getResult().getId())
                        .putExtra("receiver_id",data.getUser_id())
                        .putExtra("name",data.getUser_name())
                );
            }
        });

        holder.binding.btRejectOrShowOnMap.setOnClickListener(v -> {
            if("Pending".equals(data.getStatus())) {
                acceptRejectApiCalled(position,data.getId(),"Cancel");
            } else {
                navigateToGooglMap(data.getPickup_lat()+","+data.getPickup_lon(),
                        data.getDrop_lat()+","+data.getDrop_lon());
            }
        });

    }

    private void navigateToGooglMap(String sAddres,String dAddress) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW ,
                Uri.parse("http://maps.google.com/maps?saddr="+sAddres+"&daddr="+dAddress));
        mContext.startActivity(intent);
    }

    public interface UpdateParcel {
       void onSuccess(String status);
    }

    private void acceptRejectApiCalled(int position,String request,String status) {
        ProjectUtil.showProgressDialog(mContext,true,mContext.getString(R.string.please_wait));
        Api api = ApiFactory.getClientWithoutHeader(mContext).create(Api.class);

        Log.e("sdfsdfdsfdsvf","user_id = " + modelLogin.getResult().getId());

        HashMap<String,String> param = new HashMap<>();
        param.put("shipping_id",request);
        param.put("status",status);

        Log.e("sdfsdfdsfdsvf",param.toString());

        Call<ResponseBody> call = api.driverAcceptRejectApiCall(param);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ProjectUtil.pauseProgressDialog();
                try {
                    String responseString = response.body().string();
                    JSONObject jsonObject = new JSONObject(responseString);

                    Log.e("updateParcel","updateParcel = " + responseString);

                    if(jsonObject.getString("status").equals("1")) {
                        Log.e("updateParcel"," position = " + position);
                        storeList.remove(position);
                        Log.e("updateParcel"," Removed = " + position);
                        notifyDataSetChanged();
                        Log.e("updateParcel"," onSuccess = " + position);
                        updateParcel.onSuccess(status);
                    } else {}
                } catch (Exception e) {
                    //Toast.makeText(mContext, "Exception = " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Exception","Exception = " + e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ProjectUtil.pauseProgressDialog();
            }

        });
    }

    @Override
    public int getItemCount() {
        return storeList == null?0:storeList.size();
    }

    public class StoreHolder extends RecyclerView.ViewHolder{

        AdapterUserRequestsBinding binding;

        public StoreHolder(AdapterUserRequestsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}



