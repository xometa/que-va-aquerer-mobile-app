package com.grupo5.quevaquerer.props;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.grupo5.quevaquerer.R;

//esta clase es creación propia, con la finalidad de personalizar mensajes
public class ToastAlert {
    private int status;
    private String message;
    private Context context;
    private LayoutInflater inflater;
    Toast toast;

    public ToastAlert(int status, String message, Context context, LayoutInflater inflater) {
        this.status = status;
        this.message = message;
        this.context = context;
        this.inflater = inflater;
    }

    public void toastPersonalization() {
        LayoutInflater layoutInflater = inflater;
        View v = layoutInflater.inflate(R.layout.toast, null);
        ImageView image = v.findViewById(R.id.iconotoast);
        TextView text = v.findViewById(R.id.textotoast);
        if (status == 1) {
            image.setImageResource(R.drawable.ic_success);
        } else if (status == 2) {
            image.setImageResource(R.drawable.ic_warning);
        } else {
            image.setImageResource(R.drawable.ic_error);
        }
        text.setText(message);
        toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 10);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(v);
        toast.show();
    }
}
