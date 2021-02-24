package com.grupo5.quevaquerer.client;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.grupo5.quevaquerer.user.UserListFragment;

public class PagerAdapterOptions extends FragmentPagerAdapter {
    private int numOfTab;

    public PagerAdapterOptions(FragmentManager fm, int numOfTab) {
        super(fm);
        this.numOfTab = numOfTab;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ListClientsFragment();
            case 1:
                return new UserListFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTab;
    }
}
