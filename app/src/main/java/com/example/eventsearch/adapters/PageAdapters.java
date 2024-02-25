package com.example.eventsearch.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.eventsearch.fragments.FavouritesFragment;
import com.example.eventsearch.fragments.SearchFragment;

public class PageAdapters extends FragmentStateAdapter {
    public PageAdapters(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SearchFragment();
            case 1:
                return new FavouritesFragment();
        }

        return new SearchFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
