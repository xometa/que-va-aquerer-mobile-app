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
import com.grupo5.quevaquerer.entity.Product;
import com.grupo5.quevaquerer.url.UrlProduct;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.MyViewHolder> {
    private Context c;
    private ArrayList<Product> list;
    private OrderItemClickListener clickListener;
    DecimalFormat df = new DecimalFormat("0.00");

    public AdapterOrder(Context c, ArrayList<Product> list, OrderItemClickListener clickListener) {
        this.c = c;
        this.list = list;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public AdapterOrder.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(c).inflate(R.layout.item_order, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterOrder.MyViewHolder holder, int position) {
        String url = list.get(position).getImage();
        if (!url.equals("") && url != null && url != "null") {
            Glide.with(c).load(UrlProduct.urlPhoto + url.substring(2, url.length())).into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.sinfoto);
        }
        holder.name.setText(list.get(position).getName());
        holder.price.setText("$ " + String.valueOf(df.format(list.get(position).getPrice())));
        holder.quantity.setText(String.valueOf(list.get(position).getQuantity()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image, clear, add, delete;
        private TextView name, price, quantity;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageorder);
            name = itemView.findViewById(R.id.nombreproducto);
            price = itemView.findViewById(R.id.precioproducto);
            delete = itemView.findViewById(R.id.btnremoveorder);
            clear = itemView.findViewById(R.id.btndisminuir);
            add = itemView.findViewById(R.id.btnagregar);
            quantity = itemView.findViewById(R.id.cantidadorden);

            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.quantityOrder(v, list.get(getAdapterPosition()), "-", quantity, getAdapterPosition());
                }
            });
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.quantityOrder(v, list.get(getAdapterPosition()), "+", quantity, getAdapterPosition());
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.quantityOrder(v, list.get(getAdapterPosition()), "remove", quantity, getAdapterPosition());
                }
            });
        }
    }
}
