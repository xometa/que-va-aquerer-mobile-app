package com.grupo5.quevaquerer.order;

import android.view.View;
import android.widget.TextView;

import com.grupo5.quevaquerer.entity.OrderHeader;
import com.grupo5.quevaquerer.entity.Product;

public interface OrderItemClickListener {
    void onViesDetailsProduct(View view, OrderHeader orderHeader);

    void quantityOrder(View view, Product product, String option, TextView quantity,int position);

    void setStatusOrder(View view, OrderHeader orderHeader,int status);
}
