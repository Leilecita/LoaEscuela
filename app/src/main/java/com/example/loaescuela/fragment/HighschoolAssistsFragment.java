package com.example.loaescuela.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loaescuela.CustomLoadingListItemCreator;
import com.example.loaescuela.DateHelper;
import com.example.loaescuela.Interfaces.OnEnablePresent;
import com.example.loaescuela.Interfaces.OnSelectStudent;
import com.example.loaescuela.Interfaces.OrderFragmentListener;
import com.example.loaescuela.R;
import com.example.loaescuela.ValuesHelper;
import com.example.loaescuela.activities.GeneralAssistActivity;
import com.example.loaescuela.activities.MainActivity;
import com.example.loaescuela.adapters.SpinnerAdapter;
import com.example.loaescuela.adapters.StudentAssistAdapter;
import com.example.loaescuela.network.ApiClient;
import com.example.loaescuela.network.Error;
import com.example.loaescuela.network.GenericCallback;
import com.example.loaescuela.network.models.ReportStudentAsist;
import com.example.loaescuela.network.models.ReportStudentAsistItem;
import com.example.loaescuela.types.Constants;
import com.example.loaescuela.types.SubCategoryColonia;
import com.example.loaescuela.types.SubCategoryEscuela;
import com.example.loaescuela.types.SubCategoryType;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class HighschoolAssistsFragment extends BaseFragment implements Paginate.Callbacks, OnSelectStudent, OnEnablePresent, OrderFragmentListener {

    private RecyclerView mRecyclerView;
    private StudentAssistAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;
    private String mQuery = "";
    private String token = "";

    private String mCategory = "todos";

    private String categoria = "Todo";
    private String subcategoria = "Todo";
    private String datePresent = "";
    private String mOnlyPresents = "false";

    private String mSubCategoria= Constants.TYPE_ALL;

    private Long planilla_id = -1l;

    private String mActualDate;

    private LinearLayout line_filters;
    private LinearLayout filters;

    private View mRootView;

    private Integer mPreviousPosition;

    public void startNewActivityFragment(ReportStudentAsistItem r,Integer pos, String category){
        ((GeneralAssistActivity) getActivity()).startAssistAndPaymentsActivity(r,pos,category,2);
    }

    public void scrollToPositionAndUpdate(){
        if(!isLoading() && mAdapter != null) {
            mPreviousPosition=ValuesHelper.get().mPreviousPosition;
            mAdapter.updateItem(mPreviousPosition);
            layoutManager.scrollToPosition(mPreviousPosition);
            ValuesHelper.get().mPreviousPosition=0;
        }
    }

    public void onEnablePresent(Boolean val){
        if(mAdapter != null){
            mAdapter.setEnablePresent(val);
        }
    }

    public String getCategory(){
        return Constants.CATEGORY_HIGHSCHOOL;
    }

    public String getSubcategoria(){
        return Constants.CATEGORY_HIGHSCHOOL;
    }

    public void onSelectStudent(Long id, String name, String surname, String category){
        ((GeneralAssistActivity) requireActivity()).loadStudentsValue( Constants.CATEGORY_HIGHSCHOOL, mSubCategoria, mActualDate);
        // loadStudentsValue();
        //esto se usa para refrescar solo la cant de presentes
        //y no tener que refresacr toda la lista
        // clearView();
    }


    @Override
    public void refreshList(String cat, String subcat,String date, String query, String onlyPresents, String orderby){
        categoria = cat;
        subcategoria = subcat;

        datePresent = date;
        mAdapter.setDatePresent(date);

        mQuery = query;

        mOnlyPresents = onlyPresents;

        clearView();
        //ver si esto es necesario
        listStudents(mQuery, orderby);
    }

    public void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
    }

    public void onClickButton(){
        if( planilla_id != null && planilla_id >= 0 ){
            ((GeneralAssistActivity) requireActivity()).startAddStudentActivity(planilla_id);
        }else{
            Toast.makeText(getContext(), "Debe seleccionar las categorias", Toast.LENGTH_SHORT).show();
        }
    }

    public int getVisibility(){
        return 0;
    }

    public void changeDate(String date){
        mAdapter.setDatePresent(date);
    }

    public static <T extends Enum<SubCategoryType>> void enumNameToStringArraySub(SubCategoryType[] values, List<String> spinner_sub_cat) {
        spinner_sub_cat.add("HIGHSCHOOL");
    }

    public List<String> onLoadSpinner(){
        //SPINNER Subcat
        List<String> spinner_sub_cate = new ArrayList<>();
        spinner_sub_cate.add(Constants.CATEGORY_HIGHSCHOOL);
        enumNameToStringArraySub(SubCategoryType.values(),spinner_sub_cate);
        return  spinner_sub_cate;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.assists_activity, container, false);

        mRecyclerView = mRootView.findViewById(R.id.list_students);
        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new StudentAssistAdapter(getContext(), new ArrayList<ReportStudentAsistItem>());
        mAdapter.setDatePresent(DateHelper.get().getActualDate2());
        mAdapter.setOnSelectStudent(HighschoolAssistsFragment.this);
        mAdapter.setCategory(Constants.CATEGORY_HIGHSCHOOL);
        mAdapter.setOnOrderFragmentLister(this);

        mRecyclerView.setAdapter(mAdapter);

        mPreviousPosition=0;

        implementsPaginate();

        line_filters = mRootView.findViewById(R.id.line_filters);
        filters = mRootView.findViewById(R.id.filters);

        filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(line_filters.getVisibility() == View.VISIBLE){
                    line_filters.setVisibility(View.GONE);
                }else{
                    line_filters.setVisibility(View.VISIBLE);
                }
            }
        });

        return mRootView;
    }

    public void listStudents(String query, String orderby){
        loadingInProgress=true;
        this.mQuery = query;
        final String newToken = UUID.randomUUID().toString();
        this.token =  newToken;

        System.out.println(mCategory);
        System.out.println(mSubCategoria);

        ApiClient.get().getStudentsAsists(query, mCurrentPage, mCategory, Constants.CATEGORY_HIGHSCHOOL, subcategoria, datePresent, mOnlyPresents, orderby,new GenericCallback<ReportStudentAsist>() {
            @Override
            public void onSuccess(ReportStudentAsist data) {
                System.out.println("planillaid"+data.planilla_id);
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
        listStudents(mQuery,"" );
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

