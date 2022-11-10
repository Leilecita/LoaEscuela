package com.example.loaescuela.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loaescuela.DateHelper;
import com.example.loaescuela.R;
import com.example.loaescuela.ValuesHelper;
import com.example.loaescuela.network.models.ReportIncomeStudent;
import com.example.loaescuela.network.models.ReportOutcome;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.Date;
import java.util.List;

public class IncomeStudentAdapter extends BaseAdapter<ReportIncomeStudent, IncomeStudentAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private Context mContext;


    public IncomeStudentAdapter(Context context, List<ReportIncomeStudent> events){
        setItems(events);
        mContext = context;
    }

    public IncomeStudentAdapter(){

    }

    @Override
    public long getHeaderId(int position) {
        if (position >= getItemCount()) {
            return -1;
        } else {
            Date date = DateHelper.get().parseDate(DateHelper.get().onlyDateComplete(getItem(position).income_created));
            return date.getTime();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_header_out, parent, false);

        return new RecyclerView.ViewHolder(view) {

        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < getItemCount()) {


            LinearLayout linear = (LinearLayout) holder.itemView;
            final ReportIncomeStudent e = getItem(position);

            String dateToShow2 = DateHelper.get().getNameDay(e.income_created);
            String numberDay = DateHelper.get().numberDay(e.income_created);
            String month = DateHelper.get().getNameMonth2(DateHelper.get().onlyDate(e.income_created));
            String year = DateHelper.get().getYear(DateHelper.get().onlyDate(e.income_created));

            int count = linear.getChildCount();
            View v = null;
            View v2 = null;
            View v3 = null;

            for (int k = 0; k < count; k++) {
                v3 = linear.getChildAt(k);
                if (k == 0) {
                    LinearLayout linear4 = (LinearLayout) v3;

                    int count3 = linear4.getChildCount();

                    for (int i = 0; i < count3; i++) {
                        v = linear4.getChildAt(i);
                        if (i == 0) {

                            TextView t = (TextView) v;
                            t.setText(dateToShow2);
                            //if(groupBy.equals("day")){
                                t.setVisibility(View.VISIBLE);
                            //}else{
                             //   t.setVisibility(View.GONE);
                            //}


                        } else if (i == 1) {

                            TextView t2 = (TextView) v;
                            t2.setText(numberDay);
                           // if(groupBy.equals("day")){
                                t2.setVisibility(View.VISIBLE);
                            //}else{
                            //    t2.setVisibility(View.GONE);
                            //}


                        }else if (i == 2) {

                            TextView t2 = (TextView) v;
                            t2.setText(month);

                        }else if (i == 3) {

                            TextView t2 = (TextView) v;
                            t2.setText(year);

                        }
                    }
                }
            }
        }
    }



    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView hour;

        public TextView student_name;
        public TextView observation;
        public TextView amount;
        public TextView type;

        public LinearLayout lineDetail;
        public LinearLayout lineDetailText;
        public TextView circlePendient;
        public LinearLayout moreInfo;

        public ImageView image;

        public LinearLayout linePayment;
        public TextView textPayment;
        public TextView valPayment;
        public TextView detail;
        public TextView surname;


        public RecyclerView list_events;

        public ViewHolder(View v){
            super(v);

            hour= v.findViewById(R.id.hour);
            student_name= v.findViewById(R.id.student_name);
            surname= v.findViewById(R.id.surname);
            amount= v.findViewById(R.id.amount);
            type= v.findViewById(R.id.type);
            lineDetail= v.findViewById(R.id.line_detail);
           // circlePendient= v.findViewById(R.id.circle_pendient);
            image= v.findViewById(R.id.image);

            lineDetailText = v.findViewById(R.id.line_detail_text);
            detail = v.findViewById(R.id.detail);
            linePayment= v.findViewById(R.id.line_payment);
            textPayment= v.findViewById(R.id.textpayment);
            valPayment= v.findViewById(R.id.valpayment);


            list_events = v.findViewById(R.id.list_events);
        }
    }

    @Override
    public IncomeStudentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_income_student_day,parent,false);
        IncomeStudentAdapter.ViewHolder vh = new IncomeStudentAdapter.ViewHolder(v);

        return vh;
    }

    private void clearViewHolder(IncomeStudentAdapter.ViewHolder vh){
        if(vh.amount != null)
            vh.amount.setText(null);
        if(vh.student_name!=null)
            vh.student_name.setText(null);
        if(vh.detail!=null)
            vh.detail.setText(null);

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final IncomeStudentAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final ReportIncomeStudent current_general = getItem(position);

        holder.amount.setText(ValuesHelper.get().getIntegerQuantity(current_general.amount));
        holder.detail.setText(current_general.detail);

        String[] name = current_general.description.split(" ");

        holder.student_name.setText(name[0]);

        if(name.length > 1){
            holder.surname.setText(name[1]);
        }


        if(current_general.income_created == null){
            holder.hour.setText("");
        }else{
            holder.hour.setText(DateHelper.get().getOnlyTimeFromCreated(current_general.income_created));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.lineDetailText.getVisibility() == View.VISIBLE){
                    holder.lineDetailText.setVisibility(View.GONE);
                }else{
                    holder.lineDetailText.setVisibility(View.VISIBLE);
                }
            }
        });

        holder.lineDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent intent=new Intent(mContext, ReportByStudentActivity.class);
                intent.putExtra("NAME",current_general.name);
                intent.putExtra("SURNAME",current_general.surname);
                intent.putExtra("ID",current_general.student_id);

                mContext.startActivity(intent);*/
            }
        });


    }


}
