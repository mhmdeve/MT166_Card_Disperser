package com.mt.mt166demo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 1 -> new TypeABFragment();
            case 2 -> new Mf1Fragment();
            case 3 -> new ULFragment();
            default -> new BaseFragment();
        };
    }

    @Override
    public int getItemCount() {
        return 4; // number of tabs
    }
}
