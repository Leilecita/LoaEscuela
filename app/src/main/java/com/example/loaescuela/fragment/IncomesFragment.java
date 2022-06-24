package com.example.loaescuela.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loaescuela.CustomLoadingListItemCreator;
import com.example.loaescuela.R;
import com.example.loaescuela.adapters.StudentAdapter;
import com.example.loaescuela.network.models.Student;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IncomesFragment  extends BaseFragment implements Paginate.Callbacks {

    private RecyclerView mRecyclerView;
    private StudentAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private View mRootView;
    private String mSelectDate;
    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;


    public void changeDate(String date){
    }

    public void onClickButton(){
       // addExtraction();
    }
    public int getIconButton(){
        return R.drawable.add3;
    }

    public int getVisibility(){
        return 0;
    }

    private void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView=inflater.inflate(R.layout.fragment_assists, container, false);

        mRecyclerView = mRootView.findViewById(R.id.list_students);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new StudentAdapter(getActivity(), new ArrayList<Student>());

        // registerForContextMenu(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        setHasOptionsMenu(true);



        //------------------------



        implementsPaginate();

        return mRootView;
    }


    private void listExtractions(){

    }

    private void implementsPaginate(){

        loadingInProgress=false;
        mCurrentPage=0;
        hasMoreItems = true;

        paginate= Paginate.with(mRecyclerView, this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator())
                .setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
                    @Override
                    public int getSpanSize() {
                        return 0;
                    }
                })
                .build();
    }


    @Override
    public void onLoadMore() {
        listExtractions();
    }

    @Override
    public boolean isLoading() {
        return loadingInProgress;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return !hasMoreItems;
    }

}
