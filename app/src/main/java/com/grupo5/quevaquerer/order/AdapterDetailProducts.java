package com.grupo5.quevaquerer.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.DetailsOrder;
import com.grupo5.quevaquerer.url.UrlOrder;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdapterDetailProducts extends RecyclerView.Adapter<AdapterDetailProducts.MyViewHolder> {
    Context c;
    ArrayList<DetailsOrder> list;
    DecimalFormat df = new DecimalFormat("0.00");

    public AdapterDetailProducts(Context c, ArrayList<DetailsOrder> list) {
        this.c = c;
        this.list = list;
    }

    @NonNull
    @Override
    public AdapterDetailProducts.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.item_detailorder, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDetailProducts.MyViewHolder holder, int position) {
        String url = list.get(position).getProduct().getImage();
        if (!url.equals("") && url != null && url != "null") {
            Glide.with(c).load(UrlOrder.urlPhoto + url.substring(2, url.length())).into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.sinfoto);
        }
        holder.name.setText(list.get(position).getProduct().getName());
        holder.quantity.setText(String.valueOf(list.get(position).getQuantity()));
        holder.price.setText(String.valueOf(df.format(list.get(position).getPrice())));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name;
        private TextView quantity;
        private TextView price;

        public MyViewHolder(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.imageView);
            name = v.findViewById(R.id.nombre_dp);
            quantity = v.findViewById(R.id.cantidad_dp);
            price = v.findViewById(R.id.precio_dp);
        }
    }
}
