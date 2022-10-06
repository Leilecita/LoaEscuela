package com.example.loaescuela.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
import com.example.loaescuela.DateHelper;
import com.example.loaescuela.R;
import com.example.loaescuela.adapters.OutcomeAdapter;
import com.example.loaescuela.network.ApiClient;
import com.example.loaescuela.network.Error;
import com.example.loaescuela.network.GenericCallback;
import com.example.loaescuela.network.models.Outcome;
import com.example.loaescuela.network.models.ReportIncomeStudent;
import com.example.loaescuela.network.models.ReportOutcome;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OutcomesActivity extends BaseActivity implements Paginate.Callbacks {

    private RecyclerView mRecyclerView;
    private OutcomeAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private LinearLayout button;

    private LinearLayout listAll;
    private LinearLayout day;
    private LinearLayout month;

    private ImageView im_day;
    private ImageView im_month;
    private ImageView im_all;

    private String mSelectedDate;

    private TextView emptyRecyclerView;

    public ImageView returnn;
    public TextView title;

    public LinearLayout bottomSheet;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_outcomes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //showBackArrow();

        setTitle("Gastos");
        LinearLayout home= this.findViewById(R.id.home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        emptyRecyclerView = findViewById(R.id.empty);

        mRecyclerView = findViewById(R.id.list_outcomes);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new OutcomeAdapter(this, new ArrayList<>());
        mRecyclerView.setAdapter(mAdapter);

        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        mRecyclerView.addItemDecoration(headersDecor);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });

        button= findViewById(R.id.add_outcome);

        implementsPaginate();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOutcome();
            }
        });

        bottomSheet = findViewById(R.id.bottomSheet);

        topBarListener(bottomSheet);
    }

    private void topBarListener(LinearLayout bottomSheet) {

       /* mGridCoinRecyclerView = bottomSheet.findViewById(R.id.list_coins);
        layoutManagerCoin = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mGridCoinRecyclerView.setLayoutManager(layoutManagerCoin);
        mCircleCoinAdapter = new CircleCoinAdapter(this, new ArrayList<Coin>());
        mGridCoinRecyclerView.setAdapter(mCircleCoinAdapter);
        mCircleCoinAdapter.setOnCoinSelected(this);*/

        listAll = bottomSheet.findViewById(R.id.all);
        day = bottomSheet.findViewById(R.id.day);
        month = bottomSheet.findViewById(R.id.month);

        im_day = bottomSheet.findViewById(R.id.im_day);
        im_month = bottomSheet.findViewById(R.id.im_month);
        im_all = bottomSheet.findViewById(R.id.im_all);

        im_all.setAlpha(1f);

        listAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFilters();

                clearview();
            }
        });

        day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFilters();
                mAdapter.setGroupBy("day");
                im_day.setAlpha(1f);
                clearview();
            }
        });

        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFilters();
                mAdapter.setGroupBy("month");
                im_month.setAlpha(1f);
                clearview();
            }
        });

    }

    private void clearFilters(){
        im_all.setAlpha(0.6f);
        im_day.setAlpha(0.6f);
        im_month.setAlpha(0.6f);
    }

    private void clearview(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isLoading()) {
            mCurrentPage = 0;
            mAdapter.clear();
            hasMoreItems=true;
        }
    }

    public void listOutcomes(){
        loadingInProgress=true;

        ApiClient.get().getAllOutcomes(mCurrentPage, new GenericCallback<List<ReportOutcome>>() {
            @Override
            public void onSuccess(List<ReportOutcome> data) {
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

                if(mCurrentPage == 0 && data.size()==0){
                    emptyRecyclerView.setVisibility(View.VISIBLE);
                }else{
                    emptyRecyclerView.setVisibility(View.GONE);
                }
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
        listOutcomes();
    }

    @Override
    public boolean isLoading() {
        return loadingInProgress;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return !hasMoreItems;
    }


    private void createOutcome(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_add_outcome, null);
        builder.setView(dialogView);

        final TextView date=  dialogView.findViewById(R.id.date);
        final EditText descr=  dialogView.findViewById(R.id.description);
        final EditText type=  dialogView.findViewById(R.id.type);

        final EditText value=  dialogView.findViewById(R.id.amount);
        final Spinner spinnerCoin=  dialogView.findViewById(R.id.coin);

        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final Button ok=  dialogView.findViewById(R.id.ok);

        mSelectedDate = DateHelper.get().getActualDate();
        date.setText(DateHelper.get().onlyDate(DateHelper.get().getActualDateToShow()));

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate(date);
            }
        });

        value.requestFocus();


        final AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String descrT= ((descr.getText().toString().trim().matches("")) ? "" : descr.getText().toString().trim());
                String typeT= ((type.getText().toString().trim().matches("")) ? "" : type.getText().toString().trim());
                Double valueT=Double.valueOf(((value.getText().toString().trim().matches("")) ? "0" : value.getText().toString().trim()));

                //String coin = spinnerCoin.getSelectedItem().toString().trim();

                Outcome out = new Outcome();
                out.amount = valueT;
                out.observation = descrT;

                ApiClient.get().postOutcome(out, new GenericCallback<Outcome>() {
                    @Override
                    public void onSuccess(Outcome data) {
                        clearview();
                    }

                    @Override
                    public void onError(Error error) {

                    }
                });


                dialog.dismiss();
            }

        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }




    private void selectDate(final TextView date){
        final DatePickerDialog datePickerDialog;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(this ,R.style.datepicker,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        String sdayOfMonth = String.valueOf(dayOfMonth);
                        if (sdayOfMonth.length() == 1) {
                            sdayOfMonth = "0" + dayOfMonth;
                        }

                        String smonthOfYear = String.valueOf(monthOfYear + 1);
                        if (smonthOfYear.length() == 1) {
                            smonthOfYear = "0" + smonthOfYear;
                        }

                        String time= DateHelper.get().getOnlyTime(DateHelper.get().getActualDate());

                        String datePicker=year + "-" + smonthOfYear + "-" +  sdayOfMonth +" "+time ;
                        mSelectedDate = datePicker;

                        date.setText(DateHelper.get().changeFormatDate(datePicker));

                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }
}
