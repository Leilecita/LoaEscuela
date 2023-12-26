package com.example.loaescuela.activities;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loaescuela.CustomLoadingListItemCreator;
import com.example.loaescuela.R;
import com.example.loaescuela.adapters.assistsResumByDay.AssistResumAdapter;
import com.example.loaescuela.network.ApiClient;
import com.example.loaescuela.network.Error;
import com.example.loaescuela.network.GenericCallback;
import com.example.loaescuela.network.models.ReportResumAsist;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

public class AssistsResumByDayActivity extends BaseActivity implements Paginate.Callbacks {

    private RecyclerView mRecyclerView;
    private AssistResumAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private LinearLayout add;
    private LinearLayout home;

    private StickyRecyclerHeadersDecoration headersDecor;

    @Override
    public int getLayoutRes() {
        return R.layout.resum_assists_day_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        home = findViewById(R.id.line_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView = findViewById(R.id.list_reports);
        layoutManager = new LinearLayoutManager(this    );
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new AssistResumAdapter(this, new ArrayList<ReportResumAsist>());
        mRecyclerView.setAdapter(mAdapter);

        implementsPaginate();
    }

    private void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
    }

    public void listPlanillas(){

        loadingInProgress=true;

        ApiClient.get().getAssistsResumByDay(mCurrentPage, new GenericCallback<List<ReportResumAsist>>() {
            @Override
            public void onSuccess(List<ReportResumAsist> data) {
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
        listPlanillas();
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
