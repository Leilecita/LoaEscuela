package com.example.loaescuela.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loaescuela.CustomLoadingListItemCreator;
import com.example.loaescuela.DateHelper;
import com.example.loaescuela.R;
import com.example.loaescuela.adapters.PlanillaAdapter;
import com.example.loaescuela.network.ApiClient;
import com.example.loaescuela.network.Error;
import com.example.loaescuela.network.GenericCallback;
import com.example.loaescuela.network.models.Planilla;
import com.example.loaescuela.types.CategoryType;
import com.example.loaescuela.types.Constants;
import com.example.loaescuela.types.SubCategoryType;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

public class PlanillasActivity extends BaseActivity implements Paginate.Callbacks {

    private RecyclerView mRecyclerView;
    private PlanillaAdapter mAdapter;
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
        return R.layout.list_planillas;
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

        mRecyclerView = findViewById(R.id.list_users);
        layoutManager = new LinearLayoutManager(this    );
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new PlanillaAdapter(this, new ArrayList<Planilla>());
        mRecyclerView.setAdapter(mAdapter);

        headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
        mRecyclerView.addItemDecoration(headersDecor);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });


        add = findViewById(R.id.fab);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPlanilla();
            }
        });

        implementsPaginate();
    }


    private void clearView(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
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

    private void createPlanilla(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PlanillasActivity.this);
        LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.add_new_planilla, null);
        builder.setView(dialogView);

        final EditText anio=  dialogView.findViewById(R.id.anio);

        final TextView date=  dialogView.findViewById(R.id.date);

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

        //SPINNER Month

        ArrayAdapter<String> adapter_month = new ArrayAdapter<String>(this,
                R.layout.spinner_item,DateHelper.get().getListMonths());
        adapter_month.setDropDownViewResource(R.layout.spinner_item);
        spinnerMonth.setAdapter(adapter_month);


        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final Button ok=  dialogView.findViewById(R.id.ok);

        final AlertDialog dialog = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Planilla p = new Planilla();
                p.categoria = String.valueOf( spinnerType.getSelectedItem());
                p.subcategoria = String.valueOf( spinnerSubCat.getSelectedItem());
                p.anio = anio.getText().toString().trim();
                p.date = "2023-12-12";

                ApiClient.get().postPlanilla(p, new GenericCallback<Planilla>() {
                    @Override
                    public void onSuccess(Planilla data) {
                        clearView();
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(PlanillasActivity.this, "err "+error.message+ " "+error.result,Toast.LENGTH_LONG).show();
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

    public void listPlanillas(){

        loadingInProgress=true;

        ApiClient.get().getPlanillas(mCurrentPage, new GenericCallback<List<Planilla>>() {
            @Override
            public void onSuccess(List<Planilla> data) {
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

    private static <T extends Enum<CategoryType>> void enumNameToStringArray(CategoryType[] values, List<String> spinner_type_cat) {
        for (CategoryType value: values) {
            if(value.getName().equals(Constants.TYPE_ALL)){
                spinner_type_cat.add("Categoria");
            }else{
                spinner_type_cat.add(value.getName());
            }
        }
    }

    private static <T extends Enum<SubCategoryType>> void enumNameToStringArraySub(SubCategoryType[] values, List<String> spinner_sub_cat) {
        for (SubCategoryType value: values) {
            if(value.getName().equals(Constants.TYPE_ALL)){
                spinner_sub_cat.add("Categoria");
            }else{
                spinner_sub_cat.add(value.getName());
            }
        }
    }





}
