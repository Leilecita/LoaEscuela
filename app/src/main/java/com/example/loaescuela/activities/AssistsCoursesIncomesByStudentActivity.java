package com.example.loaescuela.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loaescuela.CustomLoadingListItemCreator;
import com.example.loaescuela.DateHelper;
import com.example.loaescuela.Interfaces.OnRefresListCourses;
import com.example.loaescuela.R;
import com.example.loaescuela.adapters.ClassCourseAdapter;
import com.example.loaescuela.adapters.StudentPresentAdapter;
import com.example.loaescuela.network.ApiClient;
import com.example.loaescuela.network.Error;
import com.example.loaescuela.network.GenericCallback;
import com.example.loaescuela.network.models.ClassCourse;
import com.example.loaescuela.network.models.ReportClassCourse;
import com.example.loaescuela.network.models.ReportPresent;
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
    private LinearLayout home;

    private Long student_id = -1l;

    private LinearLayout add;

    private StickyRecyclerHeadersDecoration headersDecorCC;
    private StickyRecyclerHeadersDecoration headersDecor;


    public static void start(Context mContext, Long id, String name, String surname){
        Intent i = new Intent(mContext, AssistsCoursesIncomesByStudentActivity.class);
        i.putExtra("ID",id);
        i.putExtra("NAME",name);
        i.putExtra("SURNAME",surname);
        mContext.startActivity(i);
    }

    public void onRefreshListCourses(){
        clearView();
        listCourses();
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_assist_by_student;
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

        name = findViewById(R.id.name);
        name.setText(getIntent().getStringExtra("NAME")+" "+getIntent().getStringExtra("SURNAME"));

        student_id = getIntent().getLongExtra("ID",-1);

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


        mRecyclerViewCC = findViewById(R.id.list_courses);
        layoutManagerCC = new LinearLayoutManager(this    );
        mRecyclerViewCC.setLayoutManager(layoutManagerCC);
        mCCAdapter = new ClassCourseAdapter(this, new ArrayList<ReportClassCourse>());
        mRecyclerViewCC.setAdapter(mCCAdapter);
        mCCAdapter.setOnRefreshListCourses(this);

        headersDecorCC = new StickyRecyclerHeadersDecoration(mCCAdapter);
        mRecyclerViewCC.addItemDecoration(headersDecorCC);

        mCCAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                headersDecorCC.invalidateHeaders();
            }
        });


      /*  headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        mRecyclerView.addItemDecoration(headersDecor);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });*/

        add = findViewById(R.id.fab);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClassCourse();
            }
        });

        listCourses();

        implementsPaginate();
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

    public void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;

        //renueva los cursos
        listCourses();
    }

    public void listCourses(){

        ApiClient.get().getCoursesByStudentId(student_id, new GenericCallback<List<ReportClassCourse>>() {
            @Override
            public void onSuccess(List<ReportClassCourse> data) {
                mCCAdapter.setItems(data);
            }

            @Override
            public void onError(Error error) {

            }
        });

    }






    public void listPlanillas(){

        loadingInProgress=true;

        ApiClient.get().getAssistsByStudents(mCurrentPage, student_id,new GenericCallback<List<ReportPresent>>() {
            @Override
            public void onSuccess(List<ReportPresent> data) {
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


    private void createClassCourse(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.create_course_class, null);
        builder.setView(dialogView);

        final EditText anio=  dialogView.findViewById(R.id.anio);
        final EditText obs=  dialogView.findViewById(R.id.observation);

        final TextView date=  dialogView.findViewById(R.id.date);
        final EditText amount_course=  dialogView.findViewById(R.id.amount);
        final EditText paid_amount_course =  dialogView.findViewById(R.id.paid_amount);
        final EditText number_classes=  dialogView.findViewById(R.id.number_classes);

        final ImageView date_picker=  dialogView.findViewById(R.id.date_picker);

        final Spinner spinnerType=  dialogView.findViewById(R.id.spinner_cat);
        final Spinner spinnerSubCat=  dialogView.findViewById(R.id.spinner_sub_cat);
        final Spinner spinnerMonth =  dialogView.findViewById(R.id.spinner_month);


        //SPINNER Categoria
        List<String> spinner_type_cat = new ArrayList<>();
        enumNameToStringArray(CategoryType.values(),spinner_type_cat);

        ArrayAdapter<String> adapter_type = new ArrayAdapter<String>(this,
                R.layout.spinner_item,spinner_type_cat);
        adapter_type.setDropDownViewResource(R.layout.spinner_item);
        spinnerType.setAdapter(adapter_type);


        //SPINNER Sub Categoria
        List<String> spinner_sub_cat = new ArrayList<>();
        //enumNameToStringArraySub(SubCategoryType.values(),spinner_sub_cat);

        for (SubCategoryType value: SubCategoryType.values()) {
            if(value.getName().equals(Constants.TYPE_ALL)){
                spinner_sub_cat.add("Sub Categoria");
            }else{
                spinner_sub_cat.add(value.getName());
            }
        }

        ArrayAdapter<String> adapter_sub_cat = new ArrayAdapter<String>(this,
                R.layout.spinner_item,spinner_sub_cat);
        adapter_sub_cat.setDropDownViewResource(R.layout.spinner_item);
        spinnerSubCat.setAdapter(adapter_sub_cat);

        date.setText(DateHelper.get().getActualDate2());
        date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // selectDate(date);
            }
        });


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

                course.created = date.getText().toString().trim();

                ApiClient.get().postClassCourse(course, new GenericCallback<ClassCourse>() {
                    @Override
                    public void onSuccess(ClassCourse data) {
                        listCourses();
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

    private static <T extends Enum<CategoryType>> void enumNameToStringArray(CategoryType[] values, List<String> spinner_type_cat) {
        for (CategoryType value: values) {
            if(value.getName().equals(Constants.TYPE_ALL)){
                spinner_type_cat.add("Categoria");
            }else{
                spinner_type_cat.add(value.getName());
            }
        }
    }
}
