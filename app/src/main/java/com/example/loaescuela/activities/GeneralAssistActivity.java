package com.example.loaescuela.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.loaescuela.DateHelper;
import com.example.loaescuela.NonSwipeableViewPager;
import com.example.loaescuela.R;
import com.example.loaescuela.ValuesHelper;
import com.example.loaescuela.adapters.PageAssistsAdapter;
import com.example.loaescuela.adapters.SpinnerAdapter;
import com.example.loaescuela.data.SessionPrefs;
import com.example.loaescuela.fragment.BaseFragment;
import com.example.loaescuela.fragment.ColonyFragment;
import com.example.loaescuela.fragment.HighschoolAssistsFragment;
import com.example.loaescuela.fragment.IntSchoolFragment;
import com.example.loaescuela.fragment.MiniFragment;
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

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private LinearLayout line_cant_alumnos;

    private String mCategoria;
   // private String mCategoria= Constants.TYPE_ALL;
    private String mSubCategoria;
  //  private String mSubCategoria= Constants.TYPE_ALL;

    private String mActualDate;
    private TextView day;
    private TextView month;
    private TextView year;
    private TextView dayName;
    private LinearLayout line_top_info;
    private RelativeLayout line_date;

    private Boolean initAppSubCat = true;
    private Boolean mOnlyPresent = false;

    private Spinner spinner_sub_cat;

    private LinearLayout listOnlyPresents;
    private LinearLayout lineOrderBy, refresh;
    private Boolean enablePresent = true;
    private String orderBy;

    public void startAssistAndPaymentsActivity(ReportStudentAsistItem s, Integer position, String category, Integer numberfragment){

        Intent i = new Intent(this, AssistsCoursesIncomesByStudentActivity.class);
        i.putExtra("ID",s.student_id);
        i.putExtra("NAME",s.nombre);
        i.putExtra("SURNAME", s.apellido);
        i.putExtra("CATEGORY", category);
        i.putExtra("CAMEFROM", "ASISTENCIAPOSITION");

        i.putExtra("POSITION",position);
        i.putExtra("NUMBERFRAGMENT",numberfragment);

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
        line_cant_alumnos = findViewById(R.id.line_cant_alumnos);
        cant_presents = findViewById(R.id.cant_presents);
        cant_students_by_planilla = findViewById(R.id.cant_students_by_planilla);

        line_date = findViewById(R.id.date_picker);
        day = findViewById(R.id.num);
        month = findViewById(R.id.month);
        year =findViewById(R.id.year);
        dayName = findViewById(R.id.dayname);
        line_top_info = findViewById(R.id.line_top_info);
        listOnlyPresents = findViewById(R.id.onlypresents);
        lineOrderBy = findViewById(R.id.orderBy);
        refresh = findViewById(R.id.refresh);

        orderBy = "alf";
        //lineOrderBy.setVisibility(View.GONE);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh(mCategoria,mSubCategoria,mActualDate,mQuery,"false",orderBy);

                loadStudentsValue(mCategoria, mSubCategoria, mActualDate);
            }
        });

        lineOrderBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderBy.equals("alf")){
                    orderBy = "s";
                }else{
                    orderBy = "alf";
                }

                refresh(mCategoria,mSubCategoria,mActualDate,mQuery,"false",orderBy);
            }
        });

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
        setCategoryByPosition(0);
        actionSpinner();
        checkEnablePresent(enablePresent,0);

        search();

        mOnlyPresent = false;
        listOnlypresentsMethod();


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
                checkEnablePresent(enablePresent, position);

                setCategoryByPosition(position);

                listOnlypresentsMethod();
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
                checkEnablePresent(enablePresent, tab.getPosition());

                loadStudentsValue(mCategoria, mSubCategoria, mActualDate);

                listOnlypresentsMethod();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View im=tab.getCustomView();
                TextView t=im.findViewById(R.id.text1);
                t.setTextColor(getResources().getColor(R.color.coloRose_soft_2));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        line_cant_presents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(line_cant_alumnos.getVisibility() == View.VISIBLE){
                    line_cant_alumnos.setVisibility(View.GONE);
                }else{
                    line_cant_alumnos.setVisibility(View.VISIBLE);
                }
            }
        });




     /*   String name=getIntent().getStringExtra("NAMEFRAGMENT2");
        if(name.equals("special")){
            selectFragment(1);
        }


        if(SessionPrefs.get(getBaseContext()).getLevel() != null && !SessionPrefs.get(this).getLevel().equals("admin")){
            ((LinearLayout) mTabLayout.getTabAt(1).view).setVisibility(View.GONE);
        }*/

        String name=getIntent().getStringExtra("NAMEFRAGMENT");
        if(name != null){
            if(name.equals("mini")){
                selectFragment(3);
            }else if(name.equals("kids")){
                selectFragment(2);
            }else if(name.equals("high")){
                selectFragment(4);
            }else{
                selectFragment(0);
            }
        }else{
            selectFragment(0);
        }



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
                refresh(mCategoria, mSubCategoria, mActualDate, mQuery, "false",orderBy);

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
                    refresh(mCategoria, mSubCategoria, mActualDate, mQuery, "false",orderBy);
                }
                return false;
            }
        });
    }

    public void loadStudentsValue(String mCategoria, String mSubCategoria, String mActualDate ){

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
            t.setText("Adultos");
            t.setTextColor(getResources().getColor(R.color.white));
        } else if(i == 1) {
            t.setText("Int");
            t.setTextColor(getResources().getColor(R.color.coloRose_soft_2));

        }else if(i == 2) {
            t.setText("Kids");
            t.setTextColor(getResources().getColor(R.color.coloRose_soft_2));

        }else if(i == 3) {
            t.setText("Mini");
            t.setTextColor(getResources().getColor(R.color.coloRose_soft_2));

        }else {
            t.setText("High");
            t.setTextColor(getResources().getColor(R.color.coloRose_soft_2));
        }
    }

    private void setCategoryByPosition(Integer i) {

        int position = mTabLayout.getSelectedTabPosition();
        final Fragment f = mAdapter.getItem(position);

        if(f instanceof BaseFragment){
            mCategoria = ((BaseFragment)f).getCategory();
            mSubCategoria = ((BaseFragment)f).getSubCategory();
        }



        if (i == 0) {
            mCategoria = Constants.CATEGORY_ESCUELA;
            mSubCategoria = Constants.SUB_CATEGORY_ESCUELA_ADULTOS;
        } else if(i == 1) {
            mCategoria = Constants.CATEGORY_ESCUELA;
            mSubCategoria = Constants.SUB_CATEGORY_ESCUELA_INTERMEDIOS;
        }else if(i == 2) {
            mCategoria = Constants.CATEGORY_COLONIA;
            mSubCategoria = Constants.SUB_CATEGORY_COLONIA_KIDS;
        } else if(i == 3) {
            mCategoria = Constants.CATEGORY_COLONIA;
            mSubCategoria = Constants.SUB_CATEGORY_COLONIA_MINI;
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

    public void checkEnablePresent(Boolean val, Integer pos){
        int pos2 = mTabLayout.getSelectedTabPosition();
        final Fragment f = mAdapter.getItem(pos2);
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

    public void listOnlypresentsMethod(){
        int position = mTabLayout.getSelectedTabPosition();
        final Fragment f = mAdapter.getItem(position);

        if(f instanceof BaseFragment){
            listOnlyPresents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String val="";
                    if(mOnlyPresent){
                        mOnlyPresent = false;
                        val = "false";
                    }else{
                        mOnlyPresent = true;
                        val = "true";
                    }

                    ((BaseFragment)f).refreshList(mCategoria, mSubCategoria, mActualDate, mQuery, val,orderBy);
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

        if(SessionPrefs.get(this).getLevel().equals("admin")){
            Intent i = new Intent(this, SelectStudentForAssistActivity.class);
            i.putExtra("ID", planilla_id);
            i.putExtra("CATEGORIA", mCategoria);
            i.putExtra("SUBCATEGORIA", mSubCategoria);
            i.putExtra("TYPE", "ASISTENCIA");
            i.putExtra("FRAGMENT", mTabLayout.getSelectedTabPosition());

            startActivityForResult(i, ADD_STUDENT_ACTIVITY);
        }else{
            Toast.makeText(this,"consultar a lei", Toast.LENGTH_LONG).show();
        }

    }

    protected void hideKeyboard(View view) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_HIDDEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ASSIST_INCOMES_STUDENTS ){

            if(resultCode == RESULT_OK){

                System.out.println("fragment"+data.getIntExtra("FRAGMENT", 0));

                if(data.getIntExtra("FRAGMENT", 0) == 0){
                    selectFragment(0);
                    Fragment f = mAdapter.getItem(0);
                    if( f instanceof SchoolAssistsFragment){
                        ((SchoolAssistsFragment)f).scrollToPositionAndUpdate();
                    }
                }else if(data.getIntExtra("FRAGMENT", 0) == 1){
                    selectFragment(1);
                    Fragment f = mAdapter.getItem(1);
                    if( f instanceof IntSchoolFragment){
                        ((IntSchoolFragment)f).scrollToPositionAndUpdate();
                    }
                }else if(data.getIntExtra("FRAGMENT", 0) == 2){
                    selectFragment(2);
                    Fragment f = mAdapter.getItem(2);
                    if( f instanceof ColonyFragment){
                        ((ColonyFragment)f).scrollToPositionAndUpdate();
                    }
                }else if(data.getIntExtra("FRAGMENT", 0) == 3){
                    selectFragment(3);
                    Fragment f = mAdapter.getItem(3);
                    if( f instanceof MiniFragment){
                        ((MiniFragment)f).scrollToPositionAndUpdate();
                    }
                }else{
                    System.out.println("Values"+ ValuesHelper.get().mPreviousPosition);
                    selectFragment(4);
                    Fragment f = mAdapter.getItem(4);
                    if( f instanceof HighschoolAssistsFragment){
                        ((HighschoolAssistsFragment)f).scrollToPositionAndUpdate();
                    }
                }

            }else if(resultCode == RESULT_CANCELED){

                if(data.getIntExtra("FRAGMENT", 0) == 0){
                    selectFragment(0);
                }else if(data.getIntExtra("FRAGMENT", 0) == 1){
                    selectFragment(1);
                }else if(data.getIntExtra("FRAGMENT", 0) == 2){
                    selectFragment(2);
                }else if(data.getIntExtra("FRAGMENT", 0) == 3) {
                    selectFragment(3);
                }else{
                    selectFragment(4);
                }

                //Fragment f = mAdapter.getItem(2);
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
                System.out.println("subcat"+this.mSubCategoria);
                Fragment f;

                if(this.mCategoria.equals(Constants.CATEGORY_ESCUELA)){
                    if(this.mSubCategoria.equals(Constants.SUB_CATEGORY_ESCUELA_ADULTOS)) {
                        selectFragment(0);
                        f = mAdapter.getItem(0);
                        if (f instanceof BaseFragment) {
                            ((SchoolAssistsFragment) f).refreshList(this.mCategoria, this.mSubCategoria, this.mActualDate,"","false",orderBy);
                        }
                    }else{
                        selectFragment(1);
                        f = mAdapter.getItem(1);
                        if(f instanceof BaseFragment) {
                            ((IntSchoolFragment) f).refreshList(this.mCategoria, this.mSubCategoria, this.mActualDate, "", "false",orderBy);
                        }
                    }

                }else if(this.mCategoria.equals(Constants.CATEGORY_COLONIA)) {
                    if(this.mSubCategoria.equals(Constants.SUB_CATEGORY_COLONIA_KIDS)) {
                        selectFragment(2);
                        f = mAdapter.getItem(2);
                        if(f instanceof BaseFragment) {
                            ((ColonyFragment) f).refreshList(this.mCategoria, this.mSubCategoria, this.mActualDate, "", "false",orderBy);
                        }
                    }else{
                        selectFragment(3);
                        f = mAdapter.getItem(3);
                        if(f instanceof BaseFragment) {
                            ((MiniFragment) f).refreshList(this.mCategoria, this.mSubCategoria, this.mActualDate,"","false",orderBy);
                        }
                    }

                }else{
                    selectFragment(4);
                    f = mAdapter.getItem(4);
                    if(f instanceof BaseFragment) {
                        ((HighschoolAssistsFragment) f).refreshList(this.mCategoria, this.mSubCategoria, this.mActualDate,"","false",orderBy);
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

               // mSubCategoria = Constants.SUB_CATEGORY_COLONIA_MINI;
               // if(initAppSubCat){
                 //   initAppSubCat = false;
                //}else{

                System.out.println(mCategoria);
                System.out.println(mSubCategoria);
                System.out.println(mActualDate);

                    refresh(mCategoria, mSubCategoria, mActualDate, "", "false",orderBy);
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
                        refresh(mCategoria, mSubCategoria, mActualDate, "", "false",orderBy);

                        enablePresent = DateHelper.get().compareDate(mActualDate);
                        checkEnablePresent(enablePresent,0);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void refresh(String cat, String subcat, String date, String s, String onlyPresent, String orderBy){
        int position = mTabLayout.getSelectedTabPosition();
        final Fragment f = mAdapter.getItem(position);

        if(f instanceof BaseFragment){
            ((BaseFragment)f).refreshList(cat,subcat,date,s,onlyPresent, orderBy);
        }
    }

    private void brakeDownDate(){
        year.setText(DateHelper.get().getYear(mActualDate));
        day.setText(DateHelper.get().getOnlyDay((mActualDate)));
        month.setText(DateHelper.get().getNameMonth2((mActualDate)).substring(0,3));
        dayName.setText(DateHelper.get().getNameDay((mActualDate)));
    }
}
