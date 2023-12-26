package com.example.loaescuela.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loaescuela.DateHelper;
import com.example.loaescuela.Interfaces.OnRefresListCourses;
import com.example.loaescuela.Interfaces.OnRefreshIncomes;
import com.example.loaescuela.R;
import com.example.loaescuela.ValidatorHelper;
import com.example.loaescuela.ValuesHelper;
import com.example.loaescuela.network.ApiClient;
import com.example.loaescuela.network.Error;
import com.example.loaescuela.network.GenericCallback;
import com.example.loaescuela.network.models.ClassCourse;
import com.example.loaescuela.network.models.DataIncomeCourse;
import com.example.loaescuela.network.models.Income;
import com.example.loaescuela.network.models.Planilla;
import com.example.loaescuela.network.models.ReportClassCourse;
import com.example.loaescuela.network.models.ReportIncome;
import com.google.android.material.transition.Hold;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ClassCourseAdapter extends BaseAdapter<ReportClassCourse,ClassCourseAdapter.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder>  {
    private Context mContext;
    private String paymentPlace;

    private OnRefresListCourses onRefreshListCourses= null;

    public void setOnRefreshListCourses(OnRefresListCourses lister){
        onRefreshListCourses = lister;
    }


    public ClassCourseAdapter(Context context, List<ReportClassCourse> planillas){
        setItems(planillas);
        mContext = context;
    }

    public void setPaymentPlace(String place){
        this.paymentPlace = place;
    }

    public ClassCourseAdapter(){

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
                .inflate(R.layout.vie_header_course, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (position < getItemCount()) {

            LinearLayout linear = (LinearLayout) holder.itemView;
            final ReportClassCourse e = getItem(position);

            String date = DateHelper.get().getOnlyDate(e.created);
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

    public List<ReportClassCourse> getListClient(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView categoria;
        public TextView classes_number;
        public TextView course_amount;
        public TextView incomes_amount;
        public TextView debt_amount;
        public RecyclerView list_incomes;

        public TextView dateMont;
        public TextView dateDay;
        public TextView circle;

        public RelativeLayout info;
        public LinearLayout load_payment;
        public LinearLayout complete_pack;

        public ViewHolder(View v){
            super(v);
            categoria = v.findViewById(R.id.category);
            classes_number = v.findViewById(R.id.classes_number);
            course_amount = v.findViewById(R.id.amount);
            incomes_amount = v.findViewById(R.id.paid_amount);
            debt_amount = v.findViewById(R.id.debt_amount);
            list_incomes = v.findViewById(R.id.list_incomes);

            info = v.findViewById(R.id.info);

            dateMont = v.findViewById(R.id.date_month);
            dateDay = v.findViewById(R.id.date_day);
            circle = v.findViewById(R.id.circle2);
            load_payment = v.findViewById(R.id.load_income);
            complete_pack = v.findViewById(R.id.complete_pack);
        }
    }

    @Override
    public ClassCourseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_card_class_course,parent,false);
        ClassCourseAdapter.ViewHolder vh = new ClassCourseAdapter.ViewHolder(v);
        return vh;
    }


    private void clearViewHolder(ClassCourseAdapter.ViewHolder vh){
        if(vh.categoria!=null)
            vh.categoria.setText(null);
        if(vh.classes_number!=null)
            vh.classes_number.setText(null);
        if(vh.course_amount!=null)
            vh.course_amount.setText(null);
        if(vh.debt_amount!=null)
            vh.debt_amount.setText(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ClassCourseAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final ReportClassCourse current = getItem(position);

        holder.categoria.setText(current.category.substring(0, 1).toUpperCase()+ current.category.substring(1).toLowerCase());
        holder.classes_number.setText(String.valueOf(current.classes_number));
        holder.course_amount.setText(ValuesHelper.get().getIntegerQuantity(current.amount));
        holder.incomes_amount.setText(ValuesHelper.get().getIntegerQuantity(current.paid_amount));
        holder.debt_amount.setText(ValuesHelper.get().getIntegerQuantity(current.amount - current.paid_amount));

        String dateToShow = DateHelper.get().getDayEvent(current.created);
        String month =DateHelper.get().getNameMonth(DateHelper.get().getOnlyDate(current.created));

        holder.dateDay.setText(dateToShow);
        holder.dateMont.setText(month.substring(0,3));
        
        checkCompletePayment(holder,current);

        if(current.classes_number == 1){
            holder.complete_pack.setVisibility(View.VISIBLE);
        }else{
            holder.complete_pack.setVisibility(View.GONE);
        }

        if(current.paid_amount >= current.amount){
            holder.load_payment.setVisibility(View.GONE);
        }else{
            holder.load_payment.setVisibility(View.VISIBLE);
        }

        holder.complete_pack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "TODO", Toast.LENGTH_LONG).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.info.getVisibility() == View.GONE) {
                    holder.info.setVisibility(View.VISIBLE);
                } else {
                    holder.info.setVisibility(View.GONE);
                }
            }
        });

        if (position == 0) {
           // holder.info.setVisibility(View.VISIBLE);
        }

        //list pagos informacion
        ItemIncomeCourseAdapter adapterIncomeInfo = new ItemIncomeCourseAdapter(mContext, new ArrayList<ReportIncome>(),true,current.class_course_id,
                current.amount,current.paid_amount);
        LinearLayoutManager layoutManagerInfo = new LinearLayoutManager(mContext);
        holder.list_incomes.setLayoutManager(layoutManagerInfo);
        holder.list_incomes.setAdapter(adapterIncomeInfo);
        adapterIncomeInfo.setItems(current.list_incomes);

        adapterIncomeInfo.setOnRefreshIncomes(new OnRefreshIncomes() {
            @Override
            public void onRefreshListIncomes(ReportIncome r) {

                updateCourse(current,position);

                if(onRefreshListCourses != null){
                    onRefreshListCourses.onRefreshListCourses();
                }

               /* current.paid_amount = adapterIncomeInfo.getAmountIncomes();
                holder.course_amount.setText(ValuesHelper.get().getIntegerQuantity(current.paid_amount));

                checkCompletePayment(holder,current);*/
            }

            @Override
            public void onRefreshAmountIncomes() {

            }
        });

        holder.load_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterIncomeInfo.createIncome2(current.class_course_id, current, paymentPlace);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                adapterIncomeInfo.createIncome2(current.class_course_id, current, paymentPlace);
              //  createIncome(current.class_course_id, current,position, holder);
                return false;
            }
        });



    }

    private void updateCourse(ReportClassCourse c, Integer pos){
        ApiClient.get().getCourseById(c.student_id, c.class_course_id, new GenericCallback<List<ReportClassCourse>>() {
            @Override
            public void onSuccess(List<ReportClassCourse> data) {
                if(data.size() > 0){
                    updateItem(pos, data.get(0));
                }

            }

            @Override
            public void onError(Error error) {

            }
        });

    }

    private void checkCompletePayment(ViewHolder courseViewHolder, ReportClassCourse current){
        if(Double.compare(current.paid_amount,0d) == 0){
            courseViewHolder.circle.setBackgroundResource(R.drawable.circle_student);
        }else if (Double.compare(current.amount, current.paid_amount) == 0 || Double.compare(current.amount, current.paid_amount) < 0){
            courseViewHolder.circle.setBackgroundResource(R.drawable.circle_done);
        }else if (Double.compare(current.amount, current.paid_amount) > 0){
            courseViewHolder.circle.setBackgroundResource(R.drawable.circle_student);
        }
    }

    private void selectDate(final TextView date){
        final DatePickerDialog datePickerDialog;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        datePickerDialog = new DatePickerDialog(mContext,R.style.datepicker,
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

                        date.setText(year+"-"+smonthOfYear+"-"+dayOfMonth+" "+DateHelper.get().getOnlyTime());

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

}

