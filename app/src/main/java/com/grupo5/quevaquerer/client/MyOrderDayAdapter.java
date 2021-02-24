package com.grupo5.quevaquerer.client;

import android.content.Context;
import android.location.Geocoder;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.OrderHeader;
import com.grupo5.quevaquerer.order.OrderItemClickListener;
import com.grupo5.quevaquerer.props.Props;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MyOrderDayAdapter extends RecyclerView.Adapter<MyOrderDayAdapter.MyViewHolder> {
    Context c;
    ArrayList<OrderHeader> list;
    DecimalFormat df = new DecimalFormat("0.00");
    MyOrderDaySubItemAdapter myorder;
    OrderItemClickListener click;

    public MyOrderDayAdapter(Context c, ArrayList<OrderHeader> list, OrderItemClickListener click) {
        this.c = c;
        this.list = list;
        this.click = click;
    }

    @NonNull
    @Override
    public MyOrderDayAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.myorderheader, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderDayAdapter.MyViewHolder v, int position) {
        v.total.setText(String.valueOf(df.format(list.get(position).getTotalorder())));
        if (list.get(position).getLongitude() == 0 && list.get(position).getLatitude() == 0) {
            v.address.setText("No se ha especificado un dirección de envío");
        } else {
            v.address.setText(Props.getLocationName(c, list.get(position).getLatitude(), list.get(position).getLongitude()));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView c1;
        TextView total, address;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            c1 = itemView.findViewById(R.id.contenedor);
            total = itemView.findViewById(R.id.totalpedido);
            address = itemView.findViewById(R.id.direccionpedido);
            c1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    click.onViesDetailsProduct(v, list.get(getAdapterPosition()));
                }
            });
        }
    }
}
