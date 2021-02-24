package com.grupo5.quevaquerer.slider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.grupo5.quevaquerer.R;

import java.util.List;

public class SliderPageAdapter extends PagerAdapter {
    private Context ctx;
    private List<Slider> lista;

    public SliderPageAdapter() {
    }

    public SliderPageAdapter(Context ctx, List<Slider> lista) {
        this.ctx = ctx;
        this.lista = lista;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater=(LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.slider,null);
        ImageView img=view.findViewById(R.id.img);
        TextView t1=view.findViewById(R.id.text1);
        TextView t2=view.findViewById(R.id.text2);

        img.setImageResource(lista.get(position).getImage());
        t1.setText(lista.get(position).getTitleone());
        t2.setText(lista.get(position).getTitletwo());

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
