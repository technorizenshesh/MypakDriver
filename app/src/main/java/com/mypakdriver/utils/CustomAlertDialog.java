package com.mypakdriver.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class CustomAlertDialog extends AlertDialog {

    private onSuccessListener listener;
    private String title;
    private String message;

    public CustomAlertDialog(@NonNull Context context) {
        super(context);
    }

    public CustomAlertDialog Title(String title){
        this.title=title;
        return this;
    }

    public CustomAlertDialog Message(String title){
        this.message=title;
        return this;
    }

    public void Show(onSuccessListener listener){
        this.listener=listener;
        show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        this.setTitle(title);
        this.setMessage(message);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setButton(BUTTON_POSITIVE,"YES",(dialog, which) -> {
            listener.Success();
            dismiss();
        });
        setButton(BUTTON_NEGATIVE,"NO",(dialog, which) -> {
            dismiss();
        });
    }

    public interface onSuccessListener{
            void Success();
    }

}
