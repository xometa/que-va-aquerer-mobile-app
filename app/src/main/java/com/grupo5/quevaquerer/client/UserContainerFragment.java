package com.grupo5.quevaquerer.client;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.grupo5.quevaquerer.R;

public class UserContainerFragment extends Fragment {
    View v;
    private TabLayout tabLayout;
    private TabItem itemClient;
    private TabItem itemUser;
    private ViewPager viewPager;
    private PagerAdapterOptions pagerAdapterOptions;

    public UserContainerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_user_container, container, false);
        tabLayout = v.findViewById(R.id.barraopciones);
        itemClient = v.findViewById(R.id.itemclientes);
        itemUser = v.findViewById(R.id.itemusuarios);
        viewPager = v.findViewById(R.id.vistaopciones);
        pagerAdapterOptions = new PagerAdapterOptions(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapterOptions);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
