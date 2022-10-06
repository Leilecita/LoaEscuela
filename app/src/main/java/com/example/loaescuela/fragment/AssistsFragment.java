package com.example.loaescuela.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loaescuela.CustomLoadingListItemCreator;
import com.example.loaescuela.DateHelper;
import com.example.loaescuela.Interfaces.OnSelectStudent;
import com.example.loaescuela.R;
import com.example.loaescuela.activities.todelete.AssistsAndIncomesStudentActivity;
import com.example.loaescuela.adapters.StudentAssistAdapter;
import com.example.loaescuela.network.ApiClient;
import com.example.loaescuela.network.Error;
import com.example.loaescuela.network.GenericCallback;
import com.example.loaescuela.network.models.ReportStudentAsist;
import com.example.loaescuela.network.models.ReportStudentAsistItem;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.UUID;

public class AssistsFragment extends BaseFragment implements Paginate.Callbacks, OnSelectStudent {

    private RecyclerView mRecyclerView;
    private StudentAssistAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private View mRootView;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;
    private String mQuery = "";
    private String token = "";

    private String mCategory = "todos";

    private LinearLayout line_date;
    private LinearLayout spendingsFilterSanti;


    private String categoria = "Todo";
    private String subcategoria = "Todo";
    private String datePresent = "";
    private String mOnlyPresents = "false";

    private TextView emptySelection;

    private Long planilla_id = -1l;


    public void onClickButton(){

        if( planilla_id != null && planilla_id >= 0 ){
            ((AssistsAndIncomesStudentActivity) getActivity()).startSelectStudentForAssistActivity(planilla_id);
        }else{
            Toast.makeText(getContext(), "Debe seleccionar las categorias", Toast.LENGTH_SHORT).show();
        }

    }

    public void onSelectStudent(){
        ((AssistsAndIncomesStudentActivity) getActivity()).loadStudentsValue();
        //((AssistsAndIncomesStudentActivity) getActivity()).refreshList();
        clearView();
    }

    public int getIconButton(){
        return R.drawable.add3;
    }

    public int getVisibility(){
        return 0;
    }

    public void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
    }


    public void refreshList(String cat, String subcat,String date, String query, String onlyPresents){
        categoria = cat;
        subcategoria = subcat;

        datePresent = date;
        mAdapter.setDatePresent(date);

        mQuery = query;

        mOnlyPresents = onlyPresents;

        System.out.println("clear");
        clearView();
        //ver si esto es necesario
        listStudents(mQuery);

    }

    public void changeDate(String date){
        mAdapter.setDatePresent(date);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView=inflater.inflate(R.layout.fragment_assists, container, false);

        emptySelection = mRootView.findViewById(R.id.empty);

        mRecyclerView = mRootView.findViewById(R.id.list_students);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new StudentAssistAdapter(getActivity(), new ArrayList<ReportStudentAsistItem>());
        mAdapter.setDatePresent(DateHelper.get().getActualDate2());
        mAdapter.setOnSelectStudent(this);

        mRecyclerView.setAdapter(mAdapter);
        setHasOptionsMenu(true);

        implementsPaginate();

        return mRootView;
    }


    private void checkSelectPlanilla(){
        if(categoria.equals("Todo") || subcategoria.equals("Todo")){
            emptySelection.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }else{
            emptySelection.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }





    public void listStudents(String query){
        loadingInProgress=true;
        this.mQuery = query;
        final String newToken = UUID.randomUUID().toString();
        this.token =  newToken;

        ApiClient.get().getStudentsAsists(query, mCurrentPage, mCategory, categoria, subcategoria, datePresent, mOnlyPresents,new GenericCallback<ReportStudentAsist>() {
            @Override
            public void onSuccess(ReportStudentAsist data) {
                 planilla_id = data.planilla_id;
                 mAdapter.setPlanillaId(planilla_id);

                if(token.equals(newToken)){
                    if (query == mQuery) {

                        if (data.list_rep.size() == 0) {
                            hasMoreItems = false;
                        }else{
                            int prevSize = mAdapter.getItemCount();
                            mAdapter.pushList(data.list_rep);
                            mCurrentPage++;
                            if(prevSize == 0){
                                layoutManager.scrollToPosition(0);
                            }
                        }
                        loadingInProgress = false;
                    }
                }else{
                    Log.e("TOKEN", "Descarta token: " + newToken);
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
        listStudents(mQuery);
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
