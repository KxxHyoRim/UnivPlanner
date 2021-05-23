package edu.sungshin.univplanner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<Fragment> items = new ArrayList<Fragment>();
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void addItem(Fragment item) {
        items.add(item);
    }

    @NonNull
    @Override
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Lecture";
            case 1:
                return "Assignment";
            default:
                return "No";
        }
    }
}
