package com.example.loaescuela.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.loaescuela.fragment.BaseFragment;
import com.example.loaescuela.fragment.ColonyFragment;
import com.example.loaescuela.fragment.HighschoolAssistsFragment;
import com.example.loaescuela.fragment.IntSchoolFragment;
import com.example.loaescuela.fragment.MiniFragment;
import com.example.loaescuela.fragment.SchoolAssistsFragment;

import java.util.ArrayList;

public class PageAssistsAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private ArrayList<BaseFragment> mFragments;


    public PageAssistsAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mFragments = new ArrayList<>();

        mFragments.add(new SchoolAssistsFragment());
        mFragments.add(new IntSchoolFragment());
        mFragments.add(new ColonyFragment());
        mFragments.add(new MiniFragment());
        mFragments.add(new HighschoolAssistsFragment());

    }


    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }


    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if(position ==0){
            return "Adultos";
        }else if(position == 1){
            return "Int";
        }else if(position == 2){
            return "Kids";
        }else if(position == 3){
            return "MINI";
        }else{
            return "HIGH";
        }
    }
}
