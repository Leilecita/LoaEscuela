package com.example.loaescuela.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.example.loaescuela.adapters.StudentAssistAdapter;
import com.example.loaescuela.network.ApiClient;
import com.example.loaescuela.network.Error;
import com.example.loaescuela.network.GenericCallback;
import com.example.loaescuela.network.models.ReportStudentAsist;
import com.example.loaescuela.network.models.ReportStudentAsistItem;
import com.example.loaescuela.types.Constants;
import com.example.loaescuela.types.SubCategoryEscuela;
import com.example.loaescuela.types.SubCategoryType;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SchoolAssistsFragment extends BaseFragment implements Paginate.Callbacks, OnSelectStudent, OnEnablePresent, OrderFragmentListener {

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
        ((GeneralAssistActivity) getActivity()).startAssistAndPaymentsActivity(r,pos,category);
    }

    public void scrollToPositionAndUpdate(){
        if(!isLoading() && mAdapter != null) {
            mPreviousPosition= ValuesHelper.get().mPreviousPosition;
            mAdapter.updateItem(mPreviousPosition);
            layoutManager.scrollToPosition(mPreviousPosition);
            ValuesHelper.get().mPreviousPosition = 0;
        }
    }

    public void onEnablePresent(Boolean val){
        mAdapter.setEnablePresent(val);
    }

    public void onSelectStudent(Long student_id, String name, String surname, String categoria){
        ((GeneralAssistActivity) requireActivity()).loadStudentsValue(Constants.CATEGORY_ESCUELA, mSubCategoria, mActualDate);
    }

    @Override
    public void refreshList(String cat, String subcat,String date, String query, String onlyPresents){
        categoria = cat;
        subcategoria = subcat;

        datePresent = date;
        mAdapter.setDatePresent(date);

        mQuery = query;

        mOnlyPresents = onlyPresents;

        clearView();
        //ver si esto es necesario
        listStudents(mQuery);
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

    public List<String> onLoadSpinner(){
        //SPINNER Subcat
        List<String> spinner_sub_cate = new ArrayList<>();
        enumNameToStringArraySub(SubCategoryType.values(),spinner_sub_cate);
        return spinner_sub_cate;
    }

    public static <T extends Enum<SubCategoryType>> void enumNameToStringArraySub(SubCategoryType[] values, List<String> spinner_sub_cat) {
        spinner_sub_cat.add(SubCategoryEscuela.SUB_CATEGORY_ESCUELA_INT.getName());
        spinner_sub_cat.add(SubCategoryEscuela.SUB_CATEGORY_ESCUELA_AD.getName());
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
        mAdapter.setOnSelectStudent(SchoolAssistsFragment.this);
        mAdapter.setCategory(Constants.CATEGORY_ESCUELA);
        mAdapter.setOnOrderFragmentLister(this);

        mRecyclerView.setAdapter(mAdapter);

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

    public void listStudents(String query){
        loadingInProgress=true;
        this.mQuery = query;
        final String newToken = UUID.randomUUID().toString();
        this.token =  newToken;

        ApiClient.get().getStudentsAsists(query, mCurrentPage, mCategory, Constants.CATEGORY_ESCUELA, subcategoria, datePresent, mOnlyPresents,new GenericCallback<ReportStudentAsist>() {
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
 /*
  private static <T extends Enum<CategoryType>> void enumNameToStringArray(CategoryType[] values, List<String> spinner_type_cat) {
        for (CategoryType value: values) {
            if(value.getName().equals(Constants.TYPE_ALL)){
                // spinner_type_cat.add(Constants.TYPE_ALL);
                spinner_type_cat.add("Formato");
            }else{
                spinner_type_cat.add(value.getName());
            }
        }
    }

 private void createSpinnerCat(final Spinner spinner, List<String> data){

        initialSpinner(spinner, data);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String itemSelected=String.valueOf(spinner.getSelectedItem());
                if(itemSelected.equals(Constants.TYPE_ALL)){
                    mCategoria = Constants.TYPE_ALL;
                }else{
                    mCategoria = itemSelected;
                }

                if(initAppCat){
                    initAppCat = false;
                }else{
                    clearView();
                    loadStudentsValue();
                }

                System.out.println("entra aca cat ");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    private void selectDate(){
        final DatePickerDialog datePickerDialog;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        datePickerDialog = new DatePickerDialog(getContext(),R.style.datepicker,
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
                        mActualDate = year+"-"+smonthOfYear+"-"+sdayOfMonth;
                        brakeDownDate();
                        clearView();
                        loadStudentsValue();
                        refreshList(mCategoria, mSubCategoria, mActualDate, mQuery, mOnlyPresents);
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();

        private void brakeDownDate(){
        year.setText(DateHelper.get().getYear(mActualDate));

        day.setText(DateHelper.get().getOnlyDay((mActualDate)));
        month.setText(DateHelper.get().getNameMonth2((mActualDate)));
        dayName.setText(DateHelper.get().getNameDay((mActualDate)));
    }


    private void initialSpinner(final Spinner spinner, List<String> data){

        ArrayAdapter<String> adapterZone = new SpinnerAdapter(getContext(), R.layout.item_custom, data);
        spinner.setAdapter(adapterZone);
        spinner.setPopupBackgroundDrawable(this.getResources().getDrawable(R.drawable.rec_rounded_8));
    }


    private void createSpinnerSubCat(final Spinner spinner, List<String> data){

        initialSpinner(spinner, data);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String itemSelected=String.valueOf(spinner.getSelectedItem());
                if(itemSelected.equals(Constants.TYPE_ALL)){
                    mSubCategoria = Constants.TYPE_ALL;
                }else{
                    mSubCategoria = itemSelected;
                }

                if(initAppSubCat){
                    initAppSubCat = false;
                }else{
                    refreshList(mCategoria, mSubCategoria, mActualDate, mQuery, mOnlyPresents);
                    clearView();
                    loadStudentsValue();
                }
                System.out.println("entra aca sub ");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    */