package com.example.loaescuela.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.loaescuela.fragment.BaseFragment;
import com.example.loaescuela.fragment.IncomesBeachFragment;

import java.util.ArrayList;

public class PageAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private ArrayList<BaseFragment> mFragments;


    public PageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mFragments = new ArrayList<>();

        mFragments.add(new IncomesBeachFragment());

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
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {

       if(position ==0){
           return "ASISTENCIAS";
       }else{
           return "PAGOS";
        }
    }
}