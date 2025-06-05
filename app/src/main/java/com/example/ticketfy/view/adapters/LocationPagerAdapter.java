package com.example.ticketfy.view.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ticketfy.view.fragments.EventoNacionalFragment;
import com.example.ticketfy.view.fragments.EventoInternacionalFragment;

public class LocationPagerAdapter extends FragmentStateAdapter {

    public LocationPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new EventoNacionalFragment();
        } else {
            return new EventoInternacionalFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

