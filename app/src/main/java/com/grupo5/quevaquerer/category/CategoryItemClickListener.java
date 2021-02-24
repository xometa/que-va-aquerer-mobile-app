package com.grupo5.quevaquerer.category;

import android.view.View;

import com.grupo5.quevaquerer.entity.Category;

public interface CategoryItemClickListener {
    //interface
    //evento para cada item del recycler view
    void onCategoryClick(Category category, View v);

    void onCategoryAction(View v);

    void onCancelDialog(View v);
}
