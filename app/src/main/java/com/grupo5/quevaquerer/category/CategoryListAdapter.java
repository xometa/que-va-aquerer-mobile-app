package com.grupo5.quevaquerer.category;

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
import com.grupo5.quevaquerer.entity.Category;
import com.grupo5.quevaquerer.url.UrlCategory;

import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.MyViewHolder> {
    Context ctx;
    List<Category> list;
    CategoryItemClickListener categoryItemClickListener;

    public CategoryListAdapter(Context ctx, List<Category> list, CategoryItemClickListener categoryItemClickListener) {
        this.ctx = ctx;
        this.list = list;
        this.categoryItemClickListener = categoryItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_listcategory, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvnamecategory.setText(list.get(position).getName());
        holder.tvdescriptioncategory.setText(list.get(position).getDescription());
        String url = list.get(position).getImage();
        if (!url.equals("") && url != null && url != "null") {
            Glide.with(ctx).load(UrlCategory.urlPhoto + url.substring(2, url.length())).into(holder.imagecategory);
        } else {
            holder.imagecategory.setImageResource(R.drawable.sinfoto);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagecategory;
        private ImageView optiones;
        private TextView tvnamecategory;
        private TextView tvdescriptioncategory;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagecategory = itemView.findViewById(R.id.imagecategoria);
            tvnamecategory = itemView.findViewById(R.id.tvnombrecategoria);
            tvdescriptioncategory = itemView.findViewById(R.id.tvdescripcioncategoria);
            optiones = itemView.findViewById(R.id.masopcionescategoria);
            optiones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    categoryItemClickListener.onCategoryClick(list.get(getAdapterPosition()), v);
                }
            });
        }
    }
}
