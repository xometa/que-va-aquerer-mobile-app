package com.grupo5.quevaquerer.product;

import android.view.View;
import android.widget.CompoundButton;

import com.grupo5.quevaquerer.entity.Product;

public interface ProductItemClickListener {
    void onClickOptionsProduct(Product product, View view);

    void onCancelDialog(View view);

    void onActionDialog(Product product, View view);

    void onClickCameraGalery(View view);

    void onChangeStatusProduct(Product product, View view, int status);
}
