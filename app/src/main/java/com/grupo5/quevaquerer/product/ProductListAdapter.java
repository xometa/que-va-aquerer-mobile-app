package com.grupo5.quevaquerer.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grupo5.quevaquerer.R;
import com.grupo5.quevaquerer.entity.Product;
import com.grupo5.quevaquerer.url.UrlProduct;

import java.text.DecimalFormat;
import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.MyViewHolder> {
    Context c;
    List<Product> list;
    ProductItemClickListener productItemClickListener;
    DecimalFormat df = new DecimalFormat("0.00");

    public ProductListAdapter(Context c, List<Product> list, ProductItemClickListener productItemClickListener) {
        this.c = c;
        this.list = list;
        this.productItemClickListener = productItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(c).inflate(R.layout.item_listproduct, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.price.setText("$ " + String.valueOf(df.format(list.get(position).getPrice())));
        holder.category.setText(list.get(position).getCategory().getName());
        holder.quantity.setText(String.valueOf(list.get(position).getQuantity()));
        holder.description.setText(list.get(position).getDescription());
        String path = list.get(position).getImage();
        if (!path.equals("") && path != null && path != "null") {
            Glide.with(c).load(UrlProduct.urlPhoto + path.substring(2, path.length())).into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.sinfoto);
        }
        if (list.get(position).getAvailable() == 1) {
            holder.status.setChecked(true);
        } else {
            holder.status.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView options;
        private ImageView image;
        private TextView name;
        private TextView price;
        private TextView category;
        private TextView quantity;
        private TextView description;
        private Switch status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            options = itemView.findViewById(R.id.masopcionesproducto);
            image = itemView.findViewById(R.id.imgproducto);
            name = itemView.findViewById(R.id.tvnombreproducto);
            price = itemView.findViewById(R.id.tvprecio);
            category = itemView.findViewById(R.id.tvcategoria);
            quantity = itemView.findViewById(R.id.tvstock);
            description = itemView.findViewById(R.id.tvdescripcion);
            status = itemView.findViewById(R.id.estadoproducto);
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    productItemClickListener.onClickOptionsProduct(list.get(getAdapterPosition()), v);
                }
            });
            status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    productItemClickListener.onChangeStatusProduct(list.get(getAdapterPosition()), v, status.isChecked() ? 1 : 0);
                }
            });
        }
    }
}
