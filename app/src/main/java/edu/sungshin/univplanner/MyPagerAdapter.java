package edu.sungshin.univplanner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MyPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> frags;

    public MyPagerAdapter(@NonNull FragmentManager fm,  ArrayList<Fragment> fList) {
        super(fm);

        frags = new ArrayList<>();
        this.frags = fList;
    }
        // 해당 프라그먼트 호출 함수.
        @Override
        public Fragment getItem(int position) {
            return this.frags.get(position);
        }

        @Override
        public int getCount() {
            return frags.size();
        }

}
