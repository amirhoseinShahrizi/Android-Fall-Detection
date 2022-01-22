package com.example.fall_detection;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {

//    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
//        super(fragmentManager, lifecycle);
//    }

    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) { super(fragmentActivity); }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){

            case 1: return new TestModeFragment();

            case 2: return new SettingsFragment();

            default: return new FallDetectionFragment();
        }

//        return new FallDetectionFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
