package com.example.loaescuela.adapters.assistsResumByDay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loaescuela.DateHelper;
import com.example.loaescuela.R;
import com.example.loaescuela.adapters.BaseAdapter;
import com.example.loaescuela.network.models.ReportResumAsist;
import com.example.loaescuela.network.models.ReportResumPlanilla;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AssistResumAdapter  extends BaseAdapter<ReportResumAsist ,AssistResumAdapter.ViewHolder> {
    private Context mContext;


    public AssistResumAdapter(Context context, List<ReportResumAsist> planillas){
        setItems(planillas);
        mContext = context;
    }

    public AssistResumAdapter(){

    }


    public List<ReportResumAsist> getListClient(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView day;
        public TextView year;
        public TextView tot_presents;
        public TextView tot_incomes;
        public RecyclerView list_planillas;

        public TextView incomes_colonia;
        public TextView incomes_escuela;
        public TextView incomes_highschool;
        public TextView incomes_colonia_highschool;
        public LinearLayout line_detail_formatos;
        public LinearLayout line_tot_formatos;

        public ViewHolder(View v){
            super(v);
            year = v.findViewById(R.id.year);
            day = v.findViewById(R.id.day);
            tot_presents = v.findViewById(R.id.tot_presents);
            tot_incomes = v.findViewById(R.id.tot_incomes);
            list_planillas = v.findViewById(R.id.list_planillas);

            incomes_highschool = v.findViewById(R.id.incomes_highschool);
            incomes_escuela = v.findViewById(R.id.incomes_escuela);
            incomes_colonia = v.findViewById(R.id.incomes_colonia);
            incomes_colonia_highschool = v.findViewById(R.id.incomes_col_y_high);
            line_detail_formatos = v.findViewById(R.id.line_detail_formatos);
            line_tot_formatos = v.findViewById(R.id.line_tot_formatos);
        }
    }

    @Override
    public AssistResumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_resum_assist_day,parent,false);
        AssistResumAdapter.ViewHolder vh = new AssistResumAdapter.ViewHolder(v);
        return vh;
    }


    private void clearViewHolder(AssistResumAdapter.ViewHolder vh){
        if(vh.day!=null)
            vh.day.setText(null);
        if(vh.year!=null)
            vh.year.setText(null);
        if(vh.tot_presents!=null)
            vh.tot_presents.setText(null);
        if(vh.tot_incomes!=null)
            vh.tot_incomes.setText(null);
        if(vh.incomes_escuela!=null)
            vh.incomes_escuela.setText(null);

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final AssistResumAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final ReportResumAsist current = getItem(position);

        String nameDay = DateHelper.get().getNameDay(current.day);
        String numberDay = DateHelper.get().numberDay(current.day);
        String month = DateHelper.get().getNameMonth2(DateHelper.get().onlyDate(current.day));
        String year = DateHelper.get().getYear(DateHelper.get().onlyDate(current.day));


        holder.day.setText(nameDay+" "+numberDay+" "+month);
        holder.year.setText(year);

        holder.tot_presents.setText(String.valueOf(current.tot_presents));
        holder.tot_incomes.setText(String.valueOf(current.tot_incomes));

        holder.incomes_colonia.setText(String.valueOf(current.tot_incomes_colonia));
        holder.incomes_escuela.setText(String.valueOf(current.tot_incomes_escuela));
        holder.incomes_highschool.setText(String.valueOf(current.tot_incomes_highschool));
        holder.incomes_colonia_highschool.setText(String.valueOf(current.tot_incomes_highschool + current.tot_incomes_colonia));

        //list pagos informacion
       /* final PlanillaResumAdapter adapterIncomeInfo = new PlanillaResumAdapter(mContext, new ArrayList<ReportResumPlanilla>());
        LinearLayoutManager layoutManagerInfo = new LinearLayoutManager(mContext);
        holder.list_planillas.setLayoutManager(layoutManagerInfo);
        holder.list_planillas.setAdapter(adapterIncomeInfo);
        adapterIncomeInfo.setItems(current.planillas);*/

        final PlanillaResumAdapter adapterIncomeInfo = new PlanillaResumAdapter(mContext, new ArrayList<ReportResumPlanilla>(),"s");
        LinearLayoutManager gridlayoutmanager =new LinearLayoutManager(mContext);
        holder.list_planillas.setLayoutManager(gridlayoutmanager);
        holder.list_planillas.setAdapter(adapterIncomeInfo);
        adapterIncomeInfo.setItems(current.planillas);

        holder.line_tot_formatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.line_detail_formatos.getVisibility() == View.VISIBLE){
                    holder.line_detail_formatos.setVisibility(View.GONE);
                }else{
                    holder.line_detail_formatos.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}

