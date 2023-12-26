package com.example.loaescuela.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loaescuela.DateHelper;
import com.example.loaescuela.R;
import com.example.loaescuela.network.models.Planilla;
import com.example.loaescuela.network.models.Student;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.Date;
import java.util.List;

public class PlanillaAdapter  extends BaseAdapter<Planilla,PlanillaAdapter.ViewHolder>  implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private Context mContext;


    public PlanillaAdapter(Context context, List<Planilla> planillas){
        setItems(planillas);
        mContext = context;
    }

    public PlanillaAdapter(){

    }

    @Override
    public long getHeaderId(int position) {
        if (position >= getItemCount()) {
            return -1;
        } else {
            Date date = DateHelper.get().parseDate(DateHelper.get().onlyDateComplete(getItem(position).created));
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
            final Planilla e = getItem(position);

            String date = DateHelper.get().getOnlyDate(e.date);
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

    public List<Planilla> getListClient(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView categoria;
        public TextView subcategoria;
        public TextView anio;
        public TextView date;



        public ViewHolder(View v){
            super(v);
            categoria= v.findViewById(R.id.categoria);
            subcategoria= v.findViewById(R.id.subcategoria);
            anio= v.findViewById(R.id.anio);
            date= v.findViewById(R.id.date);

        }
    }

    @Override
    public PlanillaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_planilla,parent,false);
        PlanillaAdapter.ViewHolder vh = new PlanillaAdapter.ViewHolder(v);
        return vh;
    }


    private void clearViewHolder(PlanillaAdapter.ViewHolder vh){
        if(vh.categoria!=null)
            vh.categoria.setText(null);
        if(vh.subcategoria!=null)
            vh.subcategoria.setText(null);
        if(vh.anio!=null)
            vh.anio.setText(null);
        if(vh.date!=null)
            vh.date.setText(null);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final PlanillaAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final Planilla current = getItem(position);

        holder.categoria.setText(current.categoria);
        holder.subcategoria.setText(current.subcategoria);
        holder.anio.setText(current.anio);
        holder.date.setText(current.created);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

}

