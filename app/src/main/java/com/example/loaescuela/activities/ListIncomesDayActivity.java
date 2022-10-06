package com.example.loaescuela.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loaescuela.CustomLoadingListItemCreator;
import com.example.loaescuela.DateHelper;
import com.example.loaescuela.R;
import com.example.loaescuela.adapters.IncomeStudentAdapter;
import com.example.loaescuela.adapters.StudentAdapter;
import com.example.loaescuela.network.ApiClient;
import com.example.loaescuela.network.Error;
import com.example.loaescuela.network.GenericCallback;
import com.example.loaescuela.network.models.ReportIncomeStudent;
import com.example.loaescuela.network.models.Student;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class ListIncomesDayActivity extends BaseActivity implements Paginate.Callbacks {

    private RecyclerView mRecyclerView;
    private IncomeStudentAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private RecyclerView mRecyclerViewST;
    private StudentAdapter mAdapterST;
    private RecyclerView.LayoutManager layoutManagerST;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private String selectedDate;
    private LinearLayout home;

    private String mQuery = "";

    @Override
    public int getLayoutRes() {
        return R.layout.incomes_day_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //showBackArrow();

        home= this.findViewById(R.id.line_home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        selectedDate= DateHelper.get().getOnlyDate(DateHelper.get().getActualDate())+" 05:00:00";

        mRecyclerView = this.findViewById(R.id.list_events);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new IncomeStudentAdapter(this, new ArrayList<ReportIncomeStudent>());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerViewST = findViewById(R.id.list_users);
        layoutManagerST = new LinearLayoutManager(this    );
        mRecyclerViewST.setLayoutManager(layoutManagerST);
        mAdapterST = new StudentAdapter(this, new ArrayList<Student>());
        mRecyclerViewST.setAdapter(mAdapterST);


        //STICKY
        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        mRecyclerView.addItemDecoration(headersDecor);

        // Add decoration for dividers between list items
        mRecyclerView.addItemDecoration(new DividerItemDecoration(ListIncomesDayActivity.this,
                DividerItemDecoration.VERTICAL));

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });



        registerForContextMenu(mRecyclerView);

        implementsPaginate();


        final SearchView searchView= findViewById(R.id.searchView);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);
                //mRecyclerViewST.setVisibility(View.VISIBLE);
            }
        });



        searchView.setQueryHint("Buscar");

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
               // mRecyclerViewST.setVisibility(View.GONE);
                listClients("");
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() > 0){
                    mRecyclerViewST.setVisibility(View.VISIBLE);
                }else{
                    mRecyclerViewST.setVisibility(View.GONE);
                }
                if(!newText.trim().toLowerCase().equals(mQuery)) {
                    listClients(newText.trim().toLowerCase());
                }
                return false;
            }
        });
    }

    public void listClients(final String query){
        this.mQuery = query;

        ApiClient.get().getStudents2(query,0, new GenericCallback<List<Student>>() {
            @Override
            public void onSuccess(List<Student> data) {

                if (query == mQuery) {
                    int prevSize = mAdapterST.getItemCount();
                    mAdapterST.setItems(data);
                    if(prevSize == 0){
                        layoutManagerST.scrollToPosition(0);
                    }

                }
            }

            @Override
            public void onError(Error error) {

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isLoading()) {
            clearView();
            listEvents();
        }
    }

    private void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
    }

    private void clearAndList(){
        clearView();
        listEvents();
    }
    private void listEvents(){

        loadingInProgress=true;

        ApiClient.get().getAllIncomes(mCurrentPage, new GenericCallback<List<ReportIncomeStudent>>() {
            @Override
            public void onSuccess(List<ReportIncomeStudent> data) {

                if (data.size() == 0) {
                    hasMoreItems = false;
                }else{
                    int prevSize = mAdapter.getItemCount();
                    mAdapter.pushList(data);
                    mCurrentPage++;
                    if(prevSize == 0){
                        layoutManager.scrollToPosition(0);
                    }
                }
                loadingInProgress = false;
            }

            @Override
            public void onError(Error error) {
                loadingInProgress = false;
            }
        });


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
        listEvents();
    }

    @Override
    public boolean isLoading() {
        return loadingInProgress;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return !hasMoreItems;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectDate(){
        final DatePickerDialog datePickerDialog;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        datePickerDialog = new DatePickerDialog(this,R.style.datepicker,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String sdayOfMonth = String.valueOf(dayOfMonth);
                        if (sdayOfMonth.length() == 1) {
                            sdayOfMonth = "0" + dayOfMonth;
                        }
                        String smonthOfYear = String.valueOf(monthOfYear + 1);
                        if (smonthOfYear.length() == 1) {
                            smonthOfYear = "0" + smonthOfYear;
                        }
                        selectedDate=year+"-"+smonthOfYear+"-"+sdayOfMonth;
                        clearAndList();

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }
}
