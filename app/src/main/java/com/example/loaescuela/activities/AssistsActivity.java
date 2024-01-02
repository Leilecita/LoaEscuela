package com.example.loaescuela.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.loaescuela.CustomLoadingListItemCreator;
import com.example.loaescuela.DateHelper;
import com.example.loaescuela.Interfaces.OnSelectStudent;
import com.example.loaescuela.R;
import com.example.loaescuela.adapters.SpinnerAdapter;
import com.example.loaescuela.adapters.StudentAssistAdapter;
import com.example.loaescuela.data.SessionPrefs;
import com.example.loaescuela.network.ApiClient;
import com.example.loaescuela.network.Error;
import com.example.loaescuela.network.GenericCallback;
import com.example.loaescuela.network.models.ReportStudentAsist;
import com.example.loaescuela.network.models.ReportStudentAsistItem;
import com.example.loaescuela.network.models.ReportStudentValue;
import com.example.loaescuela.types.CategoryType;
import com.example.loaescuela.types.Constants;
import com.example.loaescuela.types.SubCategoryType;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AssistsActivity extends BaseActivity implements Paginate.Callbacks, OnSelectStudent {

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

    private LinearLayout line_date;
    private LinearLayout spendingsFilterSanti;


    private String categoria = "Todo";
    private String subcategoria = "Todo";
    private String datePresent = "";
    private String mOnlyPresents = "false";

    private String mCategoria= Constants.TYPE_ALL;
    private String mSubCategoria= Constants.TYPE_ALL;
    private String mYear= "2022";

    private TextView emptySelection;

    private Long planilla_id = -1l;

    LinearLayout button;
    ImageView image_button;

    ViewPager viewPager;

    private LinearLayout home;
    private TextView title;

    private Spinner spinner_cat;
    private Spinner spinner_sub_cat;
    private Spinner spinner_year;

    private String mActualDate;
    private TextView day;
    private TextView month;
    private TextView year;
    private TextView dayName;
    //private LinearLayout line_date;
    private LinearLayout selectYear;

    private TextView cant_presents;
    private TextView cant_students_by_planilla;
    private LinearLayout line_filters;
    private LinearLayout filters;

    private Button listOnlyPresents;

    private Boolean initAppCat = true;
    private Boolean initAppSubCat = true;

    private LinearLayout line_top_info;

    public void onSelectStudent(Long student_id,String name, String surname, String categoria){
        loadStudentsValue();
        //esto se usa para refrescar solo la cant de presentes
        //y no tener que refresacr toda la lista
       // clearView();
    }

    public void refreshList(String cat, String subcat,String date, String query, String onlyPresents, String orderby){
        categoria = cat;
        subcategoria = subcat;

        datePresent = date;
        mAdapter.setDatePresent(date);

        mQuery = query;

        mOnlyPresents = onlyPresents;

        System.out.println("clear");
        clearView();
        //ver si esto es necesario
        listStudents(mQuery, orderby);
    }

    public void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
    }


    public void changeDate(String date){
        mAdapter.setDatePresent(date);
    }

    private static final int SELECT_STUDENT_FOR_ASSIST = 1021;

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

    private static <T extends Enum<SubCategoryType>> void enumNameToStringArraySub(SubCategoryType[] values, List<String> spinner_sub_cat) {
        for (SubCategoryType value : values) {
            if (value.getName().equals(Constants.TYPE_ALL)) {
                // spinner_sub_cat.add(Constants.TYPE_ALL);
                spinner_sub_cat.add("Categoria");
            } else {
                spinner_sub_cat.add(value.getName());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SessionPrefs.get(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        emptySelection = findViewById(R.id.empty);

        mRecyclerView = findViewById(R.id.list_students);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new StudentAssistAdapter(this, new ArrayList<ReportStudentAsistItem>());
        mAdapter.setDatePresent(DateHelper.get().getActualDate2());
        mAdapter.setOnSelectStudent(this);

        mRecyclerView.setAdapter(mAdapter);

        implementsPaginate();

        cant_presents = findViewById(R.id.cant_presents);
        cant_students_by_planilla = findViewById(R.id.cant_students_by_planilla);
        line_filters = findViewById(R.id.line_filters);
        filters = findViewById(R.id.filters);
        line_date = findViewById(R.id.date);
        day = findViewById(R.id.num);
        month = findViewById(R.id.month);
        year = findViewById(R.id.year);
        dayName = findViewById(R.id.dayname);
        listOnlyPresents = findViewById(R.id.presentes);

        line_top_info = findViewById(R.id.line_top_info);

        day.setText(DateHelper.get().numberDay(DateHelper.get().getActualDate2()));

        listOnlyPresents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnlyPresents.equals("true")){
                    mOnlyPresents = "false";
                }else{
                    mOnlyPresents = "true";
                }
                refreshList(mCategoria, mSubCategoria, mActualDate, mQuery, mOnlyPresents, "");
                clearView();
            }
        });

        mActualDate = DateHelper.get().onlyDate(DateHelper.get().getActualDate2());
        brakeDownDate();

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


        line_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        spinner_cat = findViewById(R.id.spinner_cat);
        spinner_sub_cat = findViewById(R.id.spinner_sub_cat);
        spinner_year = findViewById(R.id.spinner_year);

        //SPINNER Categoria
        List<String> spinner_type_cat = new ArrayList<>();
        enumNameToStringArray(CategoryType.values(),spinner_type_cat);

        //SPINNER Subcat
        List<String> spinner_sub_cate = new ArrayList<>();
        enumNameToStringArraySub(SubCategoryType.values(),spinner_sub_cate);

        //SPINNER year
        List<String> spinner_year_lis = new ArrayList<>();
        spinner_year_lis.add("2022");
        // enumNameToStringArraySub(SubCategoryType.values(),spinner_sub_cate);

        createSpinnerCat(spinner_cat, spinner_type_cat);
        createSpinnerSubCat(spinner_sub_cat, spinner_sub_cate);
        createSpinnerSubCat(spinner_year, spinner_year_lis);

        home = findViewById(R.id.line_home);
        title = findViewById(R.id.title);

        title.setText("Loa Escuela de Surf");
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button = findViewById(R.id.fab_agregarTod);
        image_button= findViewById(R.id.image_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( planilla_id != null && planilla_id >= 0 ){
                    Intent i = new Intent(getBaseContext(), SelectStudentForAssistActivity.class);
                    i.putExtra("ID", planilla_id);
                    startActivityForResult(i, SELECT_STUDENT_FOR_ASSIST);
                }else{
                    Toast.makeText(getBaseContext(), "Debe seleccionar las categorias", Toast.LENGTH_SHORT).show();
                }

            }
        });

        search();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_STUDENT_FOR_ASSIST) {
            if(resultCode == RESULT_OK){
                clearView();
            }
        }
    }

    private void search(){
        final SearchView searchView= findViewById(R.id.searchView);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);
                if(line_top_info.getVisibility() == View.VISIBLE){
                    line_top_info.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                }else{
                    line_top_info.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                }
            }
        });

        searchView.setQueryHint("Buscar");

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                mQuery = "";
                //refreshList();
                clearView();
               /* mCurrentPage = 0;
                mAdapter.clear();
                listStudents("");*/

                line_top_info.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(line_top_info.getVisibility() == View.VISIBLE){
                    line_top_info.setVisibility(View.GONE);
                    button.setVisibility(View.GONE);
                }else{
                    line_top_info.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.trim().toLowerCase().equals(mQuery)) {
                    mQuery = newText.trim().toLowerCase();
                    refreshList(mCategoria, mSubCategoria, mActualDate, mQuery, mOnlyPresents,"");
                    clearView();

                   /* mCurrentPage = 0;
                    mAdapter.clear();
                    listStudents(newText.trim().toLowerCase());*/
                }
                return false;
            }
        });
    }


    @Override
    public int getLayoutRes() {
        return R.layout.assists_activity;
    }

    private void brakeDownDate(){
        year.setText(DateHelper.get().getYear(mActualDate));

        day.setText(DateHelper.get().getOnlyDay((mActualDate)));
        month.setText(DateHelper.get().getNameMonth2((mActualDate)));
        dayName.setText(DateHelper.get().getNameDay((mActualDate)));
    }


    private void initialSpinner(final Spinner spinner, List<String> data){

        ArrayAdapter<String> adapterZone = new SpinnerAdapter(this, R.layout.item_custom, data);
        spinner.setAdapter(adapterZone);
        spinner.setPopupBackgroundDrawable(this.getResources().getDrawable(R.drawable.rec_rounded_8));
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
                    refreshList(mCategoria, mSubCategoria, mActualDate, mQuery, mOnlyPresents,"");
                    clearView();
                    loadStudentsValue();
                }
                System.out.println("entra aca sub ");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void createSpinnerYear(final Spinner spinner, List<String> data){

        initialSpinner(spinner, data);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String itemSelected=String.valueOf(spinner.getSelectedItem());
                mYear = itemSelected;

                refreshList(mCategoria, mSubCategoria, mActualDate, mQuery, mOnlyPresents,"");
                loadStudentsValue();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void loadStudentsValue(){

        ApiClient.get().getStudentsValue("", mCategoria, mSubCategoria, mActualDate, new GenericCallback<ReportStudentValue>() {
            @Override
            public void onSuccess(ReportStudentValue data) {
                cant_presents.setText(String.valueOf(data.tot_presents));
                cant_students_by_planilla.setText(String.valueOf(data.tot_students));
            }

            @Override
            public void onError(Error error) {
            }
        });
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
                        mActualDate = year+"-"+smonthOfYear+"-"+sdayOfMonth;
                        brakeDownDate();
                        clearView();
                        loadStudentsValue();
                        refreshList(mCategoria, mSubCategoria, mActualDate, mQuery, mOnlyPresents,"");
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }


    public void listStudents(String query, String orderby){
        loadingInProgress=true;
        this.mQuery = query;
        final String newToken = UUID.randomUUID().toString();
        this.token =  newToken;

        ApiClient.get().getStudentsAsists(query, mCurrentPage, mCategory, categoria, subcategoria, datePresent, mOnlyPresents, orderby,new GenericCallback<ReportStudentAsist>() {
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
        listStudents(mQuery,"");
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
