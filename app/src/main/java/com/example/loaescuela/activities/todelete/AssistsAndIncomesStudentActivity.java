package com.example.loaescuela.activities.todelete;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.loaescuela.DateHelper;
import com.example.loaescuela.R;
import com.example.loaescuela.activities.BaseActivity;
import com.example.loaescuela.activities.LoginActivity;
import com.example.loaescuela.activities.SelectStudentForAssistActivity;
import com.example.loaescuela.adapters.PageAdapter;
import com.example.loaescuela.adapters.SpinnerAdapter;
import com.example.loaescuela.data.SessionPrefs;
import com.example.loaescuela.fragment.AssistsFragment;
import com.example.loaescuela.fragment.BaseFragment;
import com.example.loaescuela.network.ApiClient;
import com.example.loaescuela.network.Error;
import com.example.loaescuela.network.GenericCallback;
import com.example.loaescuela.network.models.ReportStudentValue;
import com.example.loaescuela.types.CategoryType;
import com.example.loaescuela.types.Constants;
import com.example.loaescuela.types.SubCategoryType;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AssistsAndIncomesStudentActivity extends BaseActivity {

    PageAdapter mAdapter;
    TabLayout mTabLayout;
    LinearLayout button;
    ImageView image_button;

    ViewPager viewPager;

    private LinearLayout home;
    private TextView title;

    private Spinner spinner_cat;
    private Spinner spinner_sub_cat;
    private Spinner spinner_year;

    private String mCategoria= Constants.TYPE_ALL;
    private String mSubCategoria= Constants.TYPE_ALL;
    private String mYear= "2022";

    private String mActualDate;
    private TextView day;
    private TextView month;
    private TextView year;
    private TextView dayName;
    private LinearLayout line_date;
    private LinearLayout selectYear;

    private TextView cant_presents;
    private TextView cant_students_by_planilla;
    private String mQuery;
    private LinearLayout line_filters;
    private LinearLayout filters;

    private Button listOnlyPresents;
    private String mOnlyPresents = "false";

    private Boolean initAppCat = true;
    private Boolean initAppSubCat = true;

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
        selectYear = findViewById(R.id.anio);

        selectYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        listOnlyPresents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnlyPresents.equals("true")){
                    mOnlyPresents = "false";
                }else{
                    mOnlyPresents = "true";
                }
                refreshList();
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

        viewPager =  findViewById(R.id.viewpager);

        mAdapter = new PageAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        mTabLayout =  findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(viewPager);
        mTabLayout.setSelectedTabIndicatorHeight(11);

        button= findViewById(R.id.fab_agregarTod);
        image_button= findViewById(R.id.image_button);

        for (int i = 0; i < mAdapter.getCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.tab_text);
                View v= tab.getCustomView();
                TextView t= v.findViewById(R.id.text1);

                setTextByPosition(t,i);
            }
        }

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                View im=tab.getCustomView();
                TextView t=im.findViewById(R.id.text1);
                t.setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View im=tab.getCustomView();
                TextView t=im.findViewById(R.id.text1);
                t.setTextColor(getResources().getColor(R.color.word_clear));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        actionFloatingButton();
        setImageButton();
       // setVisivilityButton();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                setImageButton();
                actionFloatingButton();
              //  setVisivilityButton();
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        search();
    }

    private void search(){
        final SearchView searchView= findViewById(R.id.searchView);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);
            }
        });

        searchView.setQueryHint("Buscar");
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                mQuery = "";
                refreshList();
               /* mCurrentPage = 0;
                mAdapter.clear();
                listStudents("");*/
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
                if(!newText.trim().toLowerCase().equals(mQuery)) {
                    mQuery = newText.trim().toLowerCase();
                    refreshList();
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
        return R.layout.stud_movements_activity;
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
                    refreshList();
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
                    refreshList();
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

                refreshList();
                loadStudentsValue();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }


    public void refreshList(){
        int position = mTabLayout.getSelectedTabPosition();
        Fragment f = mAdapter.getItem(position);
        if( f instanceof BaseFragment){
            ((BaseFragment)f).refreshList(mCategoria, mSubCategoria, mActualDate, mQuery, mOnlyPresents);
        }
    }

    public void changeDate(String date){
        int position = mTabLayout.getSelectedTabPosition();
        Fragment f = mAdapter.getItem(position);
        if( f instanceof BaseFragment){
            ((BaseFragment)f).changeDate(date);
        }
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

    public void actionFloatingButton(){
        int position = mTabLayout.getSelectedTabPosition();
        final Fragment f = mAdapter.getItem(position);

        if(f instanceof BaseFragment){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((BaseFragment)f).onClickButton();
                }
            });
        }
    }

    private void setTextByPosition(TextView t, Integer i) {
        if (i == 0) {
            t.setText("ASISTENCIAS");
            t.setTextColor(getResources().getColor(R.color.white));
        } else {
            t.setText("PAGOS");
            t.setTextColor(getResources().getColor(R.color.word_clear));
        }
    }
    public void selectFragment(int position){
        viewPager.setCurrentItem(position, true);
    }

    public void setImageButton(){
        int position = mTabLayout.getSelectedTabPosition();
        Fragment f = mAdapter.getItem(position);

        if( f instanceof BaseFragment){
            image_button.setImageResource(((BaseFragment)f).getIconButton());
        }
    }

   /* public void setVisivilityButton(){
        int position = mTabLayout.getSelectedTabPosition();
        Fragment f = mAdapter.getItem(position);

        if( f instanceof BaseFragment){
            button.setVisibility(((BaseFragment)f).getVisibility());
        }
    }*/

    public void startSelectStudentForAssistActivity(Long planilla_id){
        Intent i = new Intent(this, SelectStudentForAssistActivity.class);
        i.putExtra("ID", planilla_id);
        startActivityForResult(i, SELECT_STUDENT_FOR_ASSIST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_STUDENT_FOR_ASSIST) {

            if(resultCode == RESULT_OK){
                Fragment f = mAdapter.getItem(0);
                if( f instanceof AssistsFragment){
                    ((AssistsFragment)f).clearView();
                }
                selectFragment(0);
            }
        }
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
                        refreshList();

                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }


}
