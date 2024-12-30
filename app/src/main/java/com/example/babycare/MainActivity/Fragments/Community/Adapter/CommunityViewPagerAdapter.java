package com.example.babycare.MainActivity.Fragments.Community.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.babycare.MainActivity.Fragments.Community.Community;
import com.example.babycare.MainActivity.Fragments.Community.Forum.Forum;
import com.example.babycare.MainActivity.Fragments.Community.Personal.Personal;

public class CommunityViewPagerAdapter extends FragmentStateAdapter {
    private Fragment[] fragments;
    public CommunityViewPagerAdapter(@NonNull Community fragmentActivity) {
        super(fragmentActivity);
        fragments = new Fragment[]{new Forum(), new Personal()};
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return fragments.length;
    }

    // Method to get fragment based on position
    public Fragment getFragment(int position) {
        return fragments[position];
    }
}
