package com.example.ticketfy.view.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ticketfy.view.fragments.EventoInternacionalFragment;
import com.example.ticketfy.view.fragments.EventoNacionalFragment;

public class FestivalLocationAdapter extends FragmentStateAdapter {

    public FestivalLocationAdapter(@NonNull FragmentActivity fragmentActivity) {
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
