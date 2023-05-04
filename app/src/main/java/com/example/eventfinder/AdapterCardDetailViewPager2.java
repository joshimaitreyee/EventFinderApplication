package com.example.eventfinder;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdapterCardDetailViewPager2 extends FragmentStateAdapter {

    private final Bundle bundle;

    public AdapterCardDetailViewPager2(FragmentManager fragmentManager, Lifecycle lifecycle, Bundle bundle) {
        super(fragmentManager, lifecycle);
        this.bundle = bundle;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            Fragment detail = new Fragment_Detail();
            detail.setArguments(bundle);
            return detail;
        } else if (position == 1) {
            Fragment artists = new Fragment_Artists();
            artists.setArguments(bundle);
            return artists;
        } else {
            Fragment venue = new Fragment_Venue();
            venue.setArguments(bundle);
            return venue;
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}