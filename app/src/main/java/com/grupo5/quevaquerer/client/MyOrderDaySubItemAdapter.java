package com.grupo5.quevaquerer.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.DetailsOrder;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyOrderDaySubItemAdapter extends RecyclerView.Adapter<MyOrderDaySubItemAdapter.MyViewHolder> {
    Context c;
    ArrayList<DetailsOrder> list;
    DecimalFormat df = new DecimalFormat("0.00");

    public MyOrderDaySubItemAdapter(Context c, ArrayList<DetailsOrder> list) {
        this.c = c;
        this.list = list;
    }

    @NonNull
    @Override
    public MyOrderDaySubItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.myorderproduct, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderDaySubItemAdapter.MyViewHolder holder, int position) {
        holder.details.setText(list.get(position).getProduct().getName() + " x " + String.valueOf(list.get(position).getQuantity()));
        holder.total.setText("$ " + String.valueOf(df.format(list.get(position).getPrice())));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView details, total;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            details = itemView.findViewById(R.id.nombrecantidad);
            total = itemView.findViewById(R.id.precioproducto);
        }
    }
}
