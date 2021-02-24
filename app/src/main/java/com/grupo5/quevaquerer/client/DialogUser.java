package com.grupo5.quevaquerer.client;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.textfield.TextInputLayout;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.Client;

import java.util.List;

public class DialogUser extends AppCompatDialogFragment {
    private Client client;
    private ClientItemClickListener click;
    private String action;
    private EditText name, lastname, phone, user;
    private TextInputLayout password;
    private Button btnAction, btnCancel;
    private TextView title;

    public DialogUser(Client client, ClientItemClickListener click, String action) {
        this.client = client;
        this.click = click;
        this.action = action;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ClientItemClickListener getClick() {
        return click;
    }

    public void setClick(ClientItemClickListener click) {
        this.click = click;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public EditText getName() {
        return name;
    }

    public void setName(EditText name) {
        this.name = name;
    }

    public EditText getLastname() {
        return lastname;
    }

    public void setLastname(EditText lastname) {
        this.lastname = lastname;
    }

    public EditText getPhone() {
        return phone;
    }

    public void setPhone(EditText phone) {
        this.phone = phone;
    }

    public EditText getUser() {
        return user;
    }

    public void setUser(EditText user) {
        this.user = user;
    }

    public TextInputLayout getPassword() {
        return password;
    }

    public void setPassword(TextInputLayout password) {
        this.password = password;
    }

    public Button getBtnAction() {
        return btnAction;
    }

    public void setBtnAction(Button btnAction) {
        this.btnAction = btnAction;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

    public void setBtnCancel(Button btnCancel) {
        this.btnCancel = btnCancel;
    }

    public TextView getTitle() {
        return title;
    }

    public void setTitle(TextView title) {
        this.title = title;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyTransparentDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_user, null);
        builder.setView(v);
        //lista de los elementos
        title = v.findViewById(R.id.titulo);
        name = v.findViewById(R.id.nombreuser);
        lastname = v.findViewById(R.id.apellidouser);
        phone = v.findViewById(R.id.telefonouser);
        user = v.findViewById(R.id.usuario);
        password = v.findViewById(R.id.contrasena);
        btnAction = v.findViewById(R.id.btnguardaruser);
        btnCancel = v.findViewById(R.id.btncancelaruser);

        //asignando valores a los elementos
        title.setText(action);
        if (client != null) {
            name.setText(client.getName());
            lastname.setText(client.getLastname());
            phone.setText(client.getPhone());
            user.setText(client.getUsername());
            password.getEditText().setText(client.getUserpassword());
            btnAction.setText("Editar");
        }
        //eventos de los botones
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onActionDialogClient(v);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onCancelDialogClient(v);
            }
        });
        return builder.create();
    }
}
