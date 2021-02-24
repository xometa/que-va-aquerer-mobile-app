package com.grupo5.quevaquerer.order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.OrderHeader;
import com.grupo5.quevaquerer.props.Props;
import com.grupo5.quevaquerer.url.UrlOrder;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdapterAllOrders extends RecyclerView.Adapter<AdapterAllOrders.MyViewHolder> {
    Context c;
    ArrayList<OrderHeader> list;
    OrderItemClickListener cl;
    DecimalFormat df = new DecimalFormat("0.00");

    public AdapterAllOrders(Context c, ArrayList<OrderHeader> list, OrderItemClickListener cl) {
        this.c = c;
        this.list = list;
        this.cl = cl;
    }

    @NonNull
    @Override
    public AdapterAllOrders.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(c).inflate(R.layout.item_allorders, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAllOrders.MyViewHolder h, int position) {
        String url = list.get(position).getClient().getImage();
        if (!url.equals("") && url != null && url != "null") {
            Glide.with(c).load(UrlOrder.urlPhoto + url.substring(2, url.length())).into(h.image);
        } else {
            h.image.setImageResource(R.drawable.sinfoto);
        }

        h.name.setText(list.get(position).getClient().getName() + " " + list.get(position).getClient().getLastname());
        if (list.get(position).getLatitude() == 0 && list.get(position).getLongitude() == 0) {
            h.address.setText("No se ha especificado una dirección");
        } else {
            h.address.setText(Props.getLocationName(c, list.get(position).getLatitude(), list.get(position).getLongitude()));
        }
        h.date.setText(Props.formatDate(list.get(position).getDateorder()));
        if (list.get(position).getDetailsOrders().size() > 0 && list.get(position).getDetailsOrders() != null) {
            h.quantity.setText(String.valueOf(list.get(position).getDetailsOrders().size()));
        } else {
            h.quantity.setText(String.valueOf(0));
        }
        h.total.setText("$ " + String.valueOf(df.format(list.get(position).getTotalorder())));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, address, quantity, total, date;
        private ImageButton details;
        private ImageView image;

        public MyViewHolder(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.nombre_do);
            address = v.findViewById(R.id.direccion_do);
            quantity = v.findViewById(R.id.cantidad_do);
            total = v.findViewById(R.id.total_do);
            date = v.findViewById(R.id.fecha_do);
            image = v.findViewById(R.id.circleImageView);
            details = v.findViewById(R.id.btn_verdetalle);
            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cl.onViesDetailsProduct(v, list.get(getAdapterPosition()));
                }
            });
        }
    }
}
