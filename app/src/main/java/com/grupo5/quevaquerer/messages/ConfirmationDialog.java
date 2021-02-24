package com.grupo5.quevaquerer.messages;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.grupo5.quevaquerer.R;

public class ConfirmationDialog extends BottomSheetDialogFragment {
    private ClickMessages click;
    private Context c;
    private int image;
    private String title;
    private String body;
    private String textButtom;
    private Object object;

    public ConfirmationDialog(ClickMessages click, Context c, int image, String title, String body, String textButtom, Object object) {
        this.click = click;
        this.c = c;
        this.image = image;
        this.title = title;
        this.body = body;
        this.textButtom = textButtom;
        this.object = object;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.confirmation_dialog, container, false);
        //obteniendo los elementos del layout
        ImageView image = v.findViewById(R.id.imageaction);
        TextView title = v.findViewById(R.id.titulo1);
        TextView body = v.findViewById(R.id.titulo2);
        Button btnAcept = v.findViewById(R.id.btnConfirmacion);
        //agregando los valores a los elementos
        image.setImageResource(this.image);
        title.setText(this.title);
        body.setText(this.body);
        btnAcept.setText(this.textButtom);
        btnAcept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.actionBottomSheetDialog(v, object);
            }
        });
        return v;
    }
}