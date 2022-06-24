package com.example.loaescuela.adapters.assistsResumByDay;

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

public class AssistResumAdapter  extends BaseAdapter<ReportResumAsist ,AssistResumAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private Context mContext;


    public AssistResumAdapter(Context context, List<ReportResumAsist> planillas){
        setItems(planillas);
        mContext = context;
    }

    public AssistResumAdapter(){

    }

    @Override
    public long getHeaderId(int position) {
        if (position >= getItemCount()) {
            return -1;
        } else {
            Date date = DateHelper.get().parseDate(DateHelper.get().onlyDateComplete(getItem(position).day));
            return date.getYear();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header_assists, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (position < getItemCount()) {

            LinearLayout linear = (LinearLayout) holder.itemView;
            final ReportResumAsist e = getItem(position);

            String date = DateHelper.get().getOnlyDate(e.day);
            String year = DateHelper.get().getYear(date);

            int count = linear.getChildCount();
            View v = null;
            View v2 = null;

            for (int k = 0; k < count; k++) {
                v = linear.getChildAt(k);
                if (k == 0) {

                    TextView t = (TextView) v;
                    t.setText(year);
                }
            }
        }
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


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final AssistResumAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final ReportResumAsist current = getItem(position);

        holder.day.setText(DateHelper.get().onlyDayMonthSimpleDate(current.day));
        holder.year.setText(DateHelper.get().onlyYearSimpleDate(current.day));

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

        final PlanillaResumAdapter adapterIncomeInfo = new PlanillaResumAdapter(mContext, new ArrayList<ReportResumPlanilla>());
        GridLayoutManager gridlayoutmanager = new GridLayoutManager(mContext, 2);
        holder.list_planillas.setLayoutManager(gridlayoutmanager);
        holder.list_planillas.setAdapter(adapterIncomeInfo);
        adapterIncomeInfo.setItems(current.planillas);


    }

}

