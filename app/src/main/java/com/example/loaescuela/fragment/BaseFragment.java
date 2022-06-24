package com.example.loaescuela.fragment;

import androidx.fragment.app.Fragment;

import com.example.loaescuela.Interfaces.OnFloatingButton;
import com.example.loaescuela.R;

public class BaseFragment extends Fragment implements OnFloatingButton {

    public void refreshList(String cat, String subcat, String date, String query, String onlyPresents){
        refreshList(cat,subcat, date, query, onlyPresents);
    }

    public void changeDate(String date){
        changeDate(date);
    }

    public void onClickButton(){}

    public int getIconButton(){
        return R.drawable.ic_launcher_background;
    }

    public int getVisibility(){return 0;}

}
