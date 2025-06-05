package com.example.ticketfy.view.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ticketfy.view.fragments.FestivalFragment;
import com.example.ticketfy.view.fragments.GrupoFragment;

public class FragmentAdapter extends FragmentStateAdapter {

    private final GrupoFragment grupoFragment = new GrupoFragment();
    private final FestivalFragment festivalFragment = new FestivalFragment();

    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return grupoFragment;
        else return festivalFragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public GrupoFragment getGrupoFragment() {
        return grupoFragment;
    }

    public FestivalFragment getFestivalFragment() {
        return festivalFragment;
    }
}