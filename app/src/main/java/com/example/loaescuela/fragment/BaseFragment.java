package com.example.loaescuela.fragment;

import androidx.fragment.app.Fragment;

import com.example.loaescuela.Interfaces.OnEnablePresent;
import com.example.loaescuela.Interfaces.OnFloatingButton;
import com.example.loaescuela.Interfaces.OnInfoFragment;
import com.example.loaescuela.R;

import java.util.ArrayList;
import java.util.List;

public class BaseFragment extends Fragment implements OnFloatingButton, OnInfoFragment, OnEnablePresent {

    public void refreshList(String cat, String subcat, String date, String query, String onlyPresents, String orderby){
        refreshList(cat,subcat, date, query, onlyPresents, orderby);
    }

    public void changeDate(String date){
        changeDate(date);
    }

    public void onClickButton(){}

    public int getIconButton(){
        return R.drawable.ic_launcher_background;
    }

    public int getVisibility(){return 0;}
    public String getCategory(){return "";}
    public String getSubCategory(){return "";}

    public List<String> onLoadSpinner(){return new ArrayList<>() ;}

    public void onEnablePresent(Boolean val){
        onEnablePresent(val);
    }

}
