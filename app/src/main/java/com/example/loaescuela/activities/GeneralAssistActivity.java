package com.example.loaescuela.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.loaescuela.DateHelper;
import com.example.loaescuela.NonSwipeableViewPager;
import com.example.loaescuela.R;
import com.example.loaescuela.ValuesHelper;
import com.example.loaescuela.adapters.PageAssistsAdapter;
import com.example.loaescuela.adapters.SpinnerAdapter;
import com.example.loaescuela.fragment.BaseFragment;
import com.example.loaescuela.fragment.ColonyFragment;
import com.example.loaescuela.fragment.HighschoolAssistsFragment;
import com.example.loaescuela.fragment.SchoolAssistsFragment;
import com.example.loaescuela.network.ApiClient;
import com.example.loaescuela.network.Error;
import com.example.loaescuela.network.GenericCallback;
import com.example.loaescuela.network.models.ReportStudentAsist;
import com.example.loaescuela.network.models.ReportStudentAsistItem;
import com.example.loaescuela.network.models.ReportStudentValue;
import com.example.loaescuela.types.Constants;
import com.example.loaescuela.types.SubCategoryEscuela;
import com.example.loaescuela.types.SubCategoryType;
import com.google.android.material.tabs.TabLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GeneralAssistActivity extends BaseActivity{


    private PageAssistsAdapter mAdapter;
    private TabLayout mTabLayout;
    private LinearLayout button;

    private String mQuery = "";
    private String token = "";

    private NonSwipeableViewPager viewPager;

    private TextView title;

    private static final int ADD_STUDENT_ACTIVITY = 1021;
    private static final int ASSIST_INCOMES_STUDENTS = 1991;

    private TextView cant_presents;
    private TextView cant_students_by_planilla;
    private LinearLayout line_cant_presents;

    private String mCategoria= Constants.TYPE_ALL;
    private String mSubCategoria= Constants.TYPE_ALL;

    private String mActualDate;
    private TextView day;
    private TextView month;
    private TextView year;
    private TextView dayName;
    private LinearLayout line_top_info;
    private RelativeLayout line_date;

    private Boolean initAppSubCat = true;

    private Spinner spinner_sub_cat;

    private Button listOnlyPresents;
    private Boolean enablePresent = true;

    public void startAssistAndPaymentsActivity(ReportStudentAsistItem s, Integer position, String category){

        Intent i = new Intent(this, AssistsCoursesIncomesByStudentActivity.class);
        i.putExtra("ID",s.student_id);
        i.putExtra("NAME",s.nombre);
        i.putExtra("SURNAME", s.apellido);
        i.putExtra("CATEGORY", category);
        i.putExtra("CAMEFROM", "ASISTENCIAPOSITION");

        i.putExtra("POSITION",position);

        System.out.println("POSITION TO EDITh"+position);

        startActivityForResult(i,ASSIST_INCOMES_STUDENTS);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout home= this.findViewById(R.id.home);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        line_cant_presents = findViewById(R.id.line_cant_presents);
        cant_presents = findViewById(R.id.cant_presents);
        cant_students_by_planilla = findViewById(R.id.cant_students_by_planilla);

        line_date = findViewById(R.id.date_picker);
        day = findViewById(R.id.num);
        month = findViewById(R.id.month);
        year =findViewById(R.id.year);
        dayName = findViewById(R.id.dayname);
        line_top_info = findViewById(R.id.line_top_info);

        mActualDate = DateHelper.get().onlyDate(DateHelper.get().getActualDate2());

        day.setText(DateHelper.get().numberDay(DateHelper.get().getActualDate2()));
        brakeDownDate();

        line_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        title = findViewById(R.id.title);

        viewPager =  findViewById(R.id.viewpager);
        mAdapter = new PageAssistsAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        mTabLayout =  findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(viewPager);
        mTabLayout.setSelectedTabIndicatorHeight(15);

        button= findViewById(R.id.fab);

        setTitle("Asistencias ");

        for (int i = 0; i < mAdapter.getCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.tab_text);
                View v= tab.getCustomView();
                TextView t= v.findViewById(R.id.text1);

                setTextByPosition(t,i);
            }
        }

        actionFloatingButton();
        actionSpinner();
        setCategoryByPosition(0);

        search();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //setImageButton();
                actionFloatingButton();
                setVisibilityButton();

                actionSpinner();
                checkEnablePresent(enablePresent);

                setCategoryByPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                View im=tab.getCustomView();
                TextView t=im.findViewById(R.id.text1);
                t.setTextColor(getResources().getColor(R.color.white));


                setCategoryByPosition(  tab.getPosition());
                actionSpinner();
                checkEnablePresent(enablePresent);

                loadStudentsValue(mCategoria, Constants.TYPE_ALL, mActualDate);
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

         /*  listOnlyPresents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnlyPresents.equals("true")){
                    mOnlyPresents = "false";
                }else{
                    mOnlyPresents = "true";
                }
                refreshList(mCategoria, mSubCategoria, mActualDate, mQuery, mOnlyPresents);
                clearView();
            }
        });*/
     /*   String name=getIntent().getStringExtra("NAMEFRAGMENT2");
        if(name.equals("special")){
            selectFragment(1);
        }


        if(SessionPrefs.get(getBaseContext()).getLevel() != null && !SessionPrefs.get(this).getLevel().equals("admin")){
            ((LinearLayout) mTabLayout.getTabAt(1).view).setVisibility(View.GONE);
        }*/

    }

    public void createSpinner(List<String> spinner_sub_cate){
        spinner_sub_cat = findViewById(R.id.spinner_sub_cat);
        createSpinnerSubCat(spinner_sub_cat, spinner_sub_cate);
    }

    private void search(){
        final SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);
                setButtonVisibility(View.GONE);
            }
        });

        searchView.setQueryHint("Buscar");

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                mQuery = "";
                //clearView();
                refresh(mCategoria, mSubCategoria, mActualDate, mQuery, "false");

                setButtonVisibility(View.VISIBLE);
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setButtonVisibility(View.GONE);
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
                    refresh(mCategoria, mSubCategoria, mActualDate, mQuery, "false");
                }
                return false;
            }
        });
    }

    public void loadStudentsValue(String mCategoria, String mSubCategoria, String mActualDate ){

       // this.mCategoria = mCategoria;
        //this.mSubCategoria = mSubCategoria;
        //this.mActualDate = mActualDate;

        System.out.println(this.mCategoria);
        System.out.println(this.mSubCategoria);
        System.out.println(this.mActualDate);

        ApiClient.get().getStudentsValue("", this.mCategoria, this.mSubCategoria, this.mActualDate, new GenericCallback<ReportStudentValue>() {
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

    public void selectFragment(int position){
        viewPager.setCurrentItem(position, true);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.general_assist_activity;
    }

    private void setTextByPosition(TextView t, Integer i) {
        if (i == 0) {
            t.setText("Escuela");
            t.setTextColor(getResources().getColor(R.color.white));
        } else if(i == 1) {
            t.setText("Colonia");
            t.setTextColor(getResources().getColor(R.color.word_clear));
        }else {
            t.setText("Highschool");
            t.setTextColor(getResources().getColor(R.color.word_clear));
        }
    }

    private void setCategoryByPosition(Integer i) {
        if (i == 0) {
            mCategoria = Constants.CATEGORY_ESCUELA;
        } else if(i == 1) {
            mCategoria = Constants.CATEGORY_COLONIA;
        }else {
            mCategoria = Constants.CATEGORY_HIGHSCHOOL;
            mSubCategoria = Constants.CATEGORY_HIGHSCHOOL;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setVisibilityButton(){
        int position = mTabLayout.getSelectedTabPosition();
        Fragment f = mAdapter.getItem(position);

        if( f instanceof BaseFragment){
            button.setVisibility(((BaseFragment)f).getVisibility());
        }
    }

    public void checkEnablePresent(Boolean val){
        int position = mTabLayout.getSelectedTabPosition();
        final Fragment f = mAdapter.getItem(position);
        if(f instanceof BaseFragment){
            ((BaseFragment)f).onEnablePresent(val);
        }

    }

    public void setButtonVisibility(Integer v ){
        button.setVisibility(v);
        line_top_info.setVisibility(v);
        line_cant_presents.setVisibility(v);
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

    public void actionSpinner(){
        int position = mTabLayout.getSelectedTabPosition();
        final Fragment f = mAdapter.getItem(position);

        if(f instanceof BaseFragment){
            createSpinner(((BaseFragment)f).onLoadSpinner());
        }
    }

    public void startAddStudentActivity(Long planilla_id){
        Intent i = new Intent(this, SelectStudentForAssistActivity.class);
        i.putExtra("ID", planilla_id);
        i.putExtra("CATEGORIA", mCategoria);
        i.putExtra("SUBCATEGORIA", mSubCategoria);
        i.putExtra("TYPE", "ASISTENCIA");
        i.putExtra("FRAGMENT", mTabLayout.getSelectedTabPosition());

        startActivityForResult(i, ADD_STUDENT_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("acaaaa");

        if(requestCode == ASSIST_INCOMES_STUDENTS ){

            if(resultCode == RESULT_OK){

                if(data.getIntExtra("FRAGMENT", 0) == 0){
                    selectFragment(0);
                    Fragment f = mAdapter.getItem(0);
                    if( f instanceof SchoolAssistsFragment){
                        ((SchoolAssistsFragment)f).scrollToPositionAndUpdate();
                    }
                }else if(data.getIntExtra("FRAGMENT", 0) == 1){
                    selectFragment(1);
                    Fragment f = mAdapter.getItem(1);
                    if( f instanceof ColonyFragment){
                        ((ColonyFragment)f).scrollToPositionAndUpdate();
                    }
                }else{
                    System.out.println("Values"+ ValuesHelper.get().mPreviousPosition);
                    selectFragment(2);
                    Fragment f = mAdapter.getItem(2);
                    if( f instanceof HighschoolAssistsFragment){
                        ((HighschoolAssistsFragment)f).scrollToPositionAndUpdate();
                    }
                }



            }else if(resultCode == RESULT_CANCELED){
                selectFragment(2);
                Fragment f = mAdapter.getItem(2);
               /* if( f instanceof ToPrepareFragment){
                    ((ToPrepareFragment)f).scrollToPosition();
                }*/
            }
        }

        if (requestCode == ADD_STUDENT_ACTIVITY) {

            if(resultCode == RESULT_OK) {

               this.mCategoria = data.getStringExtra("CATEGORIA");
               this.mSubCategoria = data.getStringExtra("SUBCATEGORIA");

                System.out.println("cat"+this.mCategoria);
                Fragment f;

                if(this.mCategoria.equals(Constants.CATEGORY_ESCUELA)){
                    selectFragment(0);
                     f = mAdapter.getItem(0);
                    if (f instanceof BaseFragment) {
                        ((SchoolAssistsFragment) f).refreshList(this.mCategoria, this.mSubCategoria, this.mActualDate,"","false");
                    }
                }else if(this.mCategoria.equals(Constants.CATEGORY_COLONIA)) {
                    selectFragment(1);
                    f = mAdapter.getItem(1);
                    if(f instanceof BaseFragment) {
                        ((ColonyFragment) f).refreshList(this.mCategoria, this.mSubCategoria, this.mActualDate, "", "false");
                    }
                }else{
                    selectFragment(2);
                    f = mAdapter.getItem(2);
                    if(f instanceof BaseFragment) {
                        ((HighschoolAssistsFragment) f).refreshList(this.mCategoria, this.mSubCategoria, this.mActualDate,"","false");

                    }
                 }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initialSpinner(final Spinner spinner, List<String> data){

        SpinnerAdapter adapterZone = new SpinnerAdapter(this, R.layout.item_custom, data);
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

               // if(initAppSubCat){
                 //   initAppSubCat = false;
                //}else{

                    refresh(mCategoria, mSubCategoria, mActualDate, "", "false");
                    loadStudentsValue(mCategoria, mSubCategoria, mActualDate);
                //}
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }



    private void selectDate() {

        final DatePickerDialog datePickerDialog;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        datePickerDialog = new DatePickerDialog(GeneralAssistActivity.this, R.style.datepicker,
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
                        mActualDate = year + "-" + smonthOfYear + "-" + sdayOfMonth;
                        brakeDownDate();
                        loadStudentsValue(mCategoria, mSubCategoria, mActualDate);
                        refresh(mCategoria, mSubCategoria, mActualDate, "", "false");

                        enablePresent = DateHelper.get().compareDate(mActualDate);
                        checkEnablePresent(enablePresent);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void refresh(String cat, String subcat, String date, String s, String onlyPresent){
        int position = mTabLayout.getSelectedTabPosition();
        final Fragment f = mAdapter.getItem(position);

        if(f instanceof BaseFragment){
            ((BaseFragment)f).refreshList(cat,subcat,date,s,onlyPresent);
        }
    }

    private void brakeDownDate(){
        year.setText(DateHelper.get().getYear(mActualDate));
        day.setText(DateHelper.get().getOnlyDay((mActualDate)));
        month.setText(DateHelper.get().getNameMonth2((mActualDate)).substring(0,3));
        dayName.setText(DateHelper.get().getNameDay((mActualDate)));
    }
}
