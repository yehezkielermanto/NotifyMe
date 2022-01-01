package com.ukdc.notifyme;

import android.content.Context;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyAdapter extends FragmentPagerAdapter {
    private Context myContext;
    int totalTabs;
    public MyAdapter(Context context, FragmentManager fm, int totalTabs){
        super(fm);
        myContext = context;
        this.totalTabs =totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position2) {
        switch (position2){
            case 0:
                fragment_beranda fragmentBeranda = new fragment_beranda();
                return fragmentBeranda;
            case 1:
                fragment_teman fragmentTeman = new fragment_teman();
                return fragmentTeman;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
