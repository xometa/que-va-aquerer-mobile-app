package com.grupo5.quevaquerer.client;

import android.view.View;

import com.grupo5.quevaquerer.entity.Client;

public interface ClientItemClickListener {
    void onClientClick(Client client, View view);

    void onActionDialogClient(View view);

    void onCancelDialogClient(View view);
}
