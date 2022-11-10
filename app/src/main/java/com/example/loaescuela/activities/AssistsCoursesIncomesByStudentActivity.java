package com.example.loaescuela.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loaescuela.CustomLoadingListItemCreator;
import com.example.loaescuela.DateHelper;
import com.example.loaescuela.Interfaces.OnRefresListCourses;
import com.example.loaescuela.R;
import com.example.loaescuela.ValuesHelper;
import com.example.loaescuela.adapters.ClassCourseAdapter;
import com.example.loaescuela.adapters.SpinnerAdapter;
import com.example.loaescuela.adapters.StudentPresentAdapter;
import com.example.loaescuela.fragment.BaseFragment;
import com.example.loaescuela.fragment.ColonyFragment;
import com.example.loaescuela.fragment.HighschoolAssistsFragment;
import com.example.loaescuela.fragment.SchoolAssistsFragment;
import com.example.loaescuela.network.ApiClient;
import com.example.loaescuela.network.Error;
import com.example.loaescuela.network.GenericCallback;
import com.example.loaescuela.network.models.ClassCourse;
import com.example.loaescuela.network.models.ReportClassCourse;
import com.example.loaescuela.network.models.ReportPresent;
import com.example.loaescuela.network.models.ReportSeasonPresent;
import com.example.loaescuela.types.CategoryType;
import com.example.loaescuela.types.Constants;
import com.example.loaescuela.types.SubCategoryType;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

public class AssistsCoursesIncomesByStudentActivity extends BaseActivity implements Paginate.Callbacks , OnRefresListCourses {

    private RecyclerView mRecyclerView;
    private StudentPresentAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private RecyclerView mRecyclerViewCC;
    private ClassCourseAdapter mCCAdapter;
    private RecyclerView.LayoutManager layoutManagerCC;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    private TextView name;

    //Resum by student
    private TextView taken_classes;
    private TextView rest_classes;
    private TextView paid_amount;
    private TextView tot_amount;
    private TextView circle;

    private Double paid_amount_d = 0d;
    private Double tot_amount_d = 0d;

    private LinearLayout home;
    private ImageView cuad_image;

    private Long student_id = -1l;

    private LinearLayout add;
    private FrameLayout viewListPresents;

    private StickyRecyclerHeadersDecoration headersDecorCC;
    private StickyRecyclerHeadersDecoration headersDecor;

    private TextView text_name;
    private TextView firstLetter;
    private TextView category;

    private TextView month;
    private TextView dateYear;
    private TextView dateDay;

    private String mActualDate;
    private TextView listPresents;

    private Integer mPreviousAdapterPosition;
    private Integer mFragmentPosition;  //desde que planialla de asistencia lo llama

    private FrameLayout editStudent;

    private static final int EDITH_STUDENT_ACTIVITY = 1121;

    public static void start(Context mContext, Long id, String name, String surname, String category, String cameFrom){
        Intent i = new Intent(mContext, AssistsCoursesIncomesByStudentActivity.class);
        i.putExtra("ID",id);
        i.putExtra("NAME",name);
        i.putExtra("SURNAME",surname);
        i.putExtra("CATEGORY",category);
        i.putExtra("CAMEFROM",cameFrom);
        mContext.startActivity(i);
    }

    public void onRefreshListCourses(){
        getResumStudent(student_id);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_assist_by_student;
    }

    private void returnToPreviousPosition(){

        ValuesHelper.get().mPreviousPosition = mPreviousAdapterPosition;

        Intent i = new Intent();
        i.putExtra("FRAGMENT", mFragmentPosition);
        setResult(RESULT_OK,i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        home = findViewById(R.id.line_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getIntent().getStringExtra("CAMEFROM").equals("PAGOS")){
                    Intent intent=new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }

                returnToPreviousPosition();
                finish();
            }
        });

        editStudent = findViewById(R.id.editStudent);
        editStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), CreateStudentActivity.class);
                i.putExtra("STUDENTID", student_id);
                i.putExtra("TYPE", "EDITAR");

                startActivityForResult(i, EDITH_STUDENT_ACTIVITY);
            }
        });


        text_name = findViewById(R.id.text_name);
        firstLetter = findViewById(R.id.firstLetter);
        circle = findViewById(R.id.circle2);
        category = findViewById(R.id.category);
        cuad_image = findViewById(R.id.cuad_image);

        String name = getIntent().getStringExtra("NAME");
        text_name.setText(name.substring(0, 1).toUpperCase()+ name.substring(1).toLowerCase()+" "+getIntent().getStringExtra("SURNAME"));
        category.setText(getIntent().getStringExtra("CATEGORY"));

        if(getIntent().getStringExtra("CAMEFROM").equals("ASISTENCIAPOSITION")) {
            mPreviousAdapterPosition = getIntent().getIntExtra("POSITION", -1);
            mFragmentPosition = getIntent().getIntExtra("FRAGMENT", -1);
        }

        month = findViewById(R.id.month);
        dateDay = findViewById(R.id.date_day);
        dateYear = findViewById(R.id.date_year);

        String dateToShow = DateHelper.get().getActualDate2();
        String month_ =DateHelper.get().getNameMonth(DateHelper.get().getOnlyDate(dateToShow));
        String year_ =DateHelper.get().getYear(dateToShow);
        String day_ =DateHelper.get().getOnlyDay(DateHelper.get().getOnlyDate(dateToShow));

        dateYear.setText(year_);
        month.setText(month_.substring(0,3));
        dateDay.setText(day_);


        if(category.getText().toString().equals(Constants.CATEGORY_COLONIA)){
           cuad_image.setColorFilter(Color.parseColor(Constants.COLOR_COLONIA), PorterDuff.Mode.SRC_ATOP);
        }else if(category.getText().toString().equals(Constants.CATEGORY_ESCUELA)){
           cuad_image.setColorFilter(Color.parseColor(Constants.COLOR_ESCUELA), PorterDuff.Mode.SRC_ATOP);
        }else{
           cuad_image.setColorFilter(Color.parseColor(Constants.COLOR_HIGHSCHOOL), PorterDuff.Mode.SRC_ATOP);
        }

        firstLetter.setText(String.valueOf(name.charAt(0)));

        student_id = getIntent().getLongExtra("ID",-1);

        //resum by season

        taken_classes = findViewById(R.id.taken_classes);
        rest_classes = findViewById(R.id.rest_classes);
        paid_amount = findViewById(R.id.paid_amount);
        tot_amount = findViewById(R.id.tot_amount);

        //presentes
        listPresents = findViewById(R.id.list_presents);
        viewListPresents = findViewById(R.id.view_list_presents);

        mRecyclerView = findViewById(R.id.list_reports);
        layoutManager = new LinearLayoutManager(this    );
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new StudentPresentAdapter(this, new ArrayList<ReportPresent>());
        mRecyclerView.setAdapter(mAdapter);
        headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        mRecyclerView.addItemDecoration(headersDecor);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });

        listPresents();

        listPresents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if( viewListPresents.getVisibility() == View.VISIBLE){
                   viewListPresents.setVisibility(View.GONE);
               }else{
                   viewListPresents.setVisibility(View.VISIBLE);
               }
            }
        });

        //fin presentes

        mActualDate = DateHelper.get().onlyDate(DateHelper.get().getActualDate2());

        mRecyclerViewCC = findViewById(R.id.list_courses);
        layoutManagerCC = new LinearLayoutManager(this    );
        mRecyclerViewCC.setLayoutManager(layoutManagerCC);
        mCCAdapter = new ClassCourseAdapter(this, new ArrayList<ReportClassCourse>());
        mRecyclerViewCC.setAdapter(mCCAdapter);
        mCCAdapter.setOnRefreshListCourses(this);

        add = findViewById(R.id.fab);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(paid_amount_d < tot_amount_d){
                    Toast.makeText(getBaseContext(), "No puede crear curso nuevo sin finalizar pago del curso anterior", Toast.LENGTH_LONG).show();
                }else {
                    createClassCourse();
                }
            }
        });

        //listCourses();

        getResumStudent(student_id);

        implementsPaginate();
    }

    private void checkCompletePayment(){
        if(Double.compare(paid_amount_d,0d) == 0){
            circle.setBackgroundResource(R.drawable.circle_student);
        }else if (Double.compare(tot_amount_d, paid_amount_d) == 0 || Double.compare(tot_amount_d, paid_amount_d) < 0){
            circle.setBackgroundResource(R.drawable.circle_done);
        }else if (Double.compare(tot_amount_d, paid_amount_d) > 0){
            circle.setBackgroundResource(R.drawable.circle_student);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if(!isLoading()) {
            mCurrentPage = 0;
            mCCAdapter.clear();
            hasMoreItems=true;
        }
    }

    public void clearView(){
        mCurrentPage = 0;
        mCCAdapter.clear();
        hasMoreItems=true;

        //renueva los cursos
       // listCourses();
    }

    public void listCourses(){
        loadingInProgress=true;
        ApiClient.get().getCoursesByStudentId(student_id, mCurrentPage, new GenericCallback<List<ReportClassCourse>>() {
            @Override
            public void onSuccess(List<ReportClassCourse> data) {
                if (data.size() == 0) {
                    hasMoreItems = false;
                }else{
                    int prevSize = mCCAdapter.getItemCount();
                    mCCAdapter.pushList(data);
                    mCurrentPage++;
                    if(prevSize == 0){
                        layoutManagerCC.scrollToPosition(0);
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



    public void getResumStudent(Long id){
        ApiClient.get().getResumInfoByStudent(id, new GenericCallback<List<ReportSeasonPresent>>() {
            @Override
            public void onSuccess(List<ReportSeasonPresent> data) {
                ReportSeasonPresent r = data.get(0);

                paid_amount_d = r.tot_paid_amount;
                tot_amount_d = r.tot_amount;

                taken_classes.setText(String.valueOf(r.cant_presents));
                rest_classes.setText(String.valueOf(r.cant_buyed_classes - r.cant_presents));
                paid_amount.setText(ValuesHelper.get().getIntegerQuantity(r.tot_paid_amount));
                tot_amount.setText(ValuesHelper.get().getIntegerQuantity(r.tot_amount - r.tot_paid_amount));

                checkCompletePayment();
            }

            @Override
            public void onError(Error error) {

            }
        });
    }


    public void listPresents(){

        ApiClient.get().getAssistsByStudents(student_id,new GenericCallback<List<ReportPresent>>() {
            @Override
            public void onSuccess(List<ReportPresent> data) {
                mAdapter.setItems(data);
            }

            @Override
            public void onError(Error error) {

            }
        });

    }

    private void implementsPaginate(){

        loadingInProgress=false;
        mCurrentPage=0;
        hasMoreItems = true;

        paginate= Paginate.with(mRecyclerViewCC, this)
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
        listCourses();
    }

    @Override
    public boolean isLoading() {
        return loadingInProgress;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return !hasMoreItems;
    }


    private void createClassCourse(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.create_course_class, null);
        builder.setView(dialogView);

        final EditText anio=  dialogView.findViewById(R.id.anio);
        final EditText obs=  dialogView.findViewById(R.id.observation);

        final LinearLayout date=  dialogView.findViewById(R.id.date);
        final EditText amount_course=  dialogView.findViewById(R.id.amount);
        final EditText paid_amount_course =  dialogView.findViewById(R.id.paid_amount);
        final EditText number_classes=  dialogView.findViewById(R.id.number_classes);

        final Spinner spinnerType=  dialogView.findViewById(R.id.spinner_cat);
        final Spinner spinnerSubCat=  dialogView.findViewById(R.id.spinner_sub_cat);

        final TextView  day = dialogView.findViewById(R.id.num);
        final TextView  month = dialogView.findViewById(R.id.month);
        final TextView year = dialogView.findViewById(R.id.year);
        final TextView dayName = dialogView.findViewById(R.id.dayname);

        String mOnlyDate = DateHelper.get().onlyDate(mActualDate);

        year.setText(DateHelper.get().getYear(mOnlyDate));
        day.setText(DateHelper.get().getOnlyDay((mOnlyDate)));
        month.setText(DateHelper.get().getNameMonth2((mOnlyDate)));
        dayName.setText(DateHelper.get().getNameDay((mOnlyDate)));

        //SPINNER Categoria
        List<String> spinner_type_cat = new ArrayList<>();
        enumNameToStringArray(CategoryType.values(),spinner_type_cat);
        initialSpinner(spinnerType, spinner_type_cat);

        //SPINNER Subcat
        List<String> spinner_sub_cat = new ArrayList<>();
        enumNameToStringArraySub(SubCategoryType.values(),spinner_sub_cat);
        initialSpinner(spinnerSubCat, spinner_sub_cat);


        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final Button ok=  dialogView.findViewById(R.id.ok);

        final AlertDialog dialog = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClassCourse course = new ClassCourse(student_id,String.valueOf( spinnerType.getSelectedItem()),String.valueOf( spinnerSubCat.getSelectedItem()),
                        Integer.valueOf( number_classes.getText().toString().trim()),Double.valueOf(amount_course.getText().toString().trim()),obs.getText().toString().trim() );

                course.paid_amount = Double.valueOf(paid_amount_course.getText().toString().trim());
                course.payment_method = "efectivo";

                course.created = mActualDate;

                ApiClient.get().postClassCourse(course, new GenericCallback<ClassCourse>() {
                    @Override
                    public void onSuccess(ClassCourse data) {
                        clearView();
                        getResumStudent(student_id);

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

    private void initialSpinner(final Spinner spinner, List<String> data){
        ArrayAdapter<String> adapterZone = new SpinnerAdapter(this, R.layout.item_custom, data);
        spinner.setAdapter(adapterZone);
        spinner.setPopupBackgroundDrawable(this.getResources().getDrawable(R.drawable.rec_rounded_8));
    }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("acaaaa");

        if (requestCode == EDITH_STUDENT_ACTIVITY) {

            if(resultCode == RESULT_OK) {
                text_name.setText(data.getStringExtra("NAME").substring(0, 1).toUpperCase()+ data.getStringExtra("NAME").substring(1).toLowerCase()+" "+data.getStringExtra("SURNAME"));
                category.setText(data.getStringExtra("CATEGORY"));

                if(category.getText().toString().equals(Constants.CATEGORY_COLONIA)){
                    cuad_image.setColorFilter(Color.parseColor(Constants.COLOR_COLONIA), PorterDuff.Mode.SRC_ATOP);
                }else if(category.getText().toString().equals(Constants.CATEGORY_ESCUELA)){
                    cuad_image.setColorFilter(Color.parseColor(Constants.COLOR_ESCUELA), PorterDuff.Mode.SRC_ATOP);
                }else{
                    cuad_image.setColorFilter(Color.parseColor(Constants.COLOR_HIGHSCHOOL), PorterDuff.Mode.SRC_ATOP);
                }

               // firstLetter.setText(String.valueOf(name.charAt(0)));
            }
        }
    }



}
