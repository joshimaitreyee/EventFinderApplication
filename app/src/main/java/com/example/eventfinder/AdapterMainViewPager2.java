package com.example.eventfinder;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdapterMainViewPager2 extends FragmentStateAdapter {

    public AdapterMainViewPager2(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new Fragment_Favorite();
        } else {
            return new Fragment_Search();
        }

    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
