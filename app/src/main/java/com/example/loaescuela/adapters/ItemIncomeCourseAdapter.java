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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loaescuela.DateHelper;
import com.example.loaescuela.Interfaces.OnRefreshIncomes;
import com.example.loaescuela.R;
import com.example.loaescuela.ValidatorHelper;
import com.example.loaescuela.ValuesHelper;
import com.example.loaescuela.network.ApiClient;
import com.example.loaescuela.network.Error;
import com.example.loaescuela.network.GenericCallback;
import com.example.loaescuela.network.models.DataIncomeCourse;
import com.example.loaescuela.network.models.Income;
import com.example.loaescuela.network.models.ReportClassCourse;
import com.example.loaescuela.network.models.ReportIncome;
import com.example.loaescuela.types.Constants;

import java.util.Calendar;
import java.util.List;

public class ItemIncomeCourseAdapter extends BaseAdapter<ReportIncome, ItemIncomeCourseAdapter.ViewHolder>{
    private Context mContext;

    private OnRefreshIncomes onRefreshIncomes= null;

    public void setOnRefreshIncomes(OnRefreshIncomes lister){
        onRefreshIncomes=lister;
    }


    private Boolean mViewInfo;

    private Long mClassCourseId=null;
    private Double mCourseAmount=null;
    private Double mCoursePaidAmount=null;


    public ItemIncomeCourseAdapter(Context context, List<ReportIncome> events, Boolean viewInfo, Long classCourseId, Double courseAmount, Double coursePaidAmount){
        setItems(events);
        mContext = context;
        mViewInfo=viewInfo;
        mClassCourseId=classCourseId;
        mCourseAmount=courseAmount;
        mCoursePaidAmount=coursePaidAmount;
    }

    public ItemIncomeCourseAdapter(){

    }

    public Double getAmountIncomes(){
        Double amount = 0.0;
        for(int i =0; i < getItemCount(); i++){
            amount+=getItem(i).amount;
        }
        return amount;
    }


    public List<ReportIncome> getListStockEvents(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {

        public TextView date;
        public ImageView payment_method;
        public TextView amount;

        public TextView value;

        public LinearLayout line_edit;
        public LinearLayout item;
        public TextView date_edit;
        public TextView payment_method_edit;
        public EditText amount_edit;

        public ImageView ok;
        public ImageView close;

        public ImageView imageAdd;

        public ViewHolder(View v){
            super(v);

            date= v.findViewById(R.id.date);
            payment_method= v.findViewById(R.id.payment_method);
            payment_method_edit = v.findViewById(R.id.payment_method_edit);
            amount = v.findViewById(R.id.amount);
            amount_edit = v.findViewById(R.id.amount_edit);

            ok= v.findViewById(R.id.ok);
            close= v.findViewById(R.id.close);
        }
    }

    @Override
    public ItemIncomeCourseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_income_class_course,parent,false);
        ItemIncomeCourseAdapter.ViewHolder vh = new ItemIncomeCourseAdapter.ViewHolder(v);
        return vh;
    }

    private void clearViewHolder(ItemIncomeCourseAdapter.ViewHolder vh){

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final ReportIncome current = getItem(position);

        holder.amount.setText(ValuesHelper.get().getIntegerQuantity(current.amount));
        holder.date.setText(DateHelper.get().onlyDate(current.created));

       /* if(current.payment_method.equals(Constants.TYPE_TARJETA)){
            holder.payment_method.setImageResource(R.mipmap.card_w);
        }else if(current.payment_method.equals(Constants.TYPE_EFECTIVO)){
            holder.payment_method.setImageResource(R.mipmap.money_w);
        } */

        holder.amount_edit.setText(String.valueOf(current.amount));
       // holder.date_edit.setText(current.created);
        holder.payment_method_edit.setText(current.payment_method);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // edithPayment(holder,current,position);
            }
        });

    }

   /* private void selectDate(final ReportIncome reportInc){
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

                        reportInc.created=year+"-"+smonthOfYear+"-"+sdayOfMonth+" "+ DateHelper.get().onlyTimeFromDate(reportInc.created);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }*/


   /* private void infoPayment(final ReportIncome income){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_info_payment, null);
        builder.setView(dialogView);

        final TextView date=  dialogView.findViewById(R.id.date);
        final TextView hour=  dialogView.findViewById(R.id.hour);
        final TextView value=  dialogView.findViewById(R.id.amount);
        final LinearLayout efect=  dialogView.findViewById(R.id.efect);
        final LinearLayout card=  dialogView.findViewById(R.id.card);
        String textD= DateHelper.get().getStringByDate(income.created);
        String textH=  DateHelper.get().onlyTimeRounded(income.created);

        date.setHint(textD);
        hour.setHint(textH);

        date.setHintTextColor(mContext.getResources().getColor(R.color.word));
        value.setHint(ValuesHelper.get().getIntegerQuantity(income.amount));
        value.setHintTextColor(mContext.getResources().getColor(R.color.word));
        hour.setHintTextColor(mContext.getResources().getColor(R.color.word));

        if(income.payment_method.equals(Constants.TYPE_EFECTIVO)){
            card.setBackgroundResource(R.drawable.circle_unselected);
            efect.setBackgroundResource(R.drawable.circle2);

        }else if(income.payment_method.equals(Constants.TYPE_TARJETA)){
            card.setBackgroundResource(R.drawable.circle2);
            efect.setBackgroundResource(R.drawable.circle_unselected);
        }

        final TextView ok=  dialogView.findViewById(R.id.ok);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
    }*/

 /*   private void edithPayment(final ViewHolder holder, final ReportIncome income,final Integer pos){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_edith_payment, null);
        builder.setView(dialogView);

        final TextView date=  dialogView.findViewById(R.id.date);
        final TextView hour=  dialogView.findViewById(R.id.hour);
        final TextView value=  dialogView.findViewById(R.id.amount);
        final LinearLayout efect=  dialogView.findViewById(R.id.efect);
        final LinearLayout card=  dialogView.findViewById(R.id.card);

        String textD= DateHelper.get().getStringByDate(income.created);
        String textH=  DateHelper.get().onlyTimeRounded(income.created);

        date.setText(textD);
        hour.setText(textH);


        value.setHint(ValuesHelper.get().getIntegerQuantity(income.amount));
        value.setHintTextColor(mContext.getResources().getColor(R.color.word_clear));

        if(income.payment_method.equals(Constants.TYPE_EFECTIVO)){
            card.setBackgroundResource(R.drawable.circle_unselected);
            efect.setBackgroundResource(R.drawable.circle2);

        }else if(income.payment_method.equals(Constants.TYPE_TARJETA)){
            card.setBackgroundResource(R.drawable.circle2);
            efect.setBackgroundResource(R.drawable.circle_unselected);
        }

        efect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setBackgroundResource(R.drawable.circle_unselected);
                efect.setBackgroundResource(R.drawable.circle2);
                income.payment_method=Constants.TYPE_EFECTIVO;
            }
        });

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setBackgroundResource(R.drawable.circle2);
                efect.setBackgroundResource(R.drawable.circle_unselected);
                income.payment_method=Constants.TYPE_TARJETA;
            }
        });


        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final TextView ok=  dialogView.findViewById(R.id.ok);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valueT=value.getText().toString().trim();
                if(!valueT.equals("") && ValidatorHelper.get().isTypeDouble(valueT)){

                    income.amount=Double.valueOf(valueT);

                    putIncome(holder,income,pos);
                    dialog.dismiss();


                }else{
                    Toast.makeText(mContext,"Tipo de dato inválido",Toast.LENGTH_SHORT).show();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }*/

    private void putIncome(final ViewHolder holder, final ReportIncome income, final Integer position){

        Income in= new Income();
        in.id=income.income_id;
        in.payment_method=income.payment_method;
        in.amount=income.amount;
        in.created=income.created;

        System.out.println("id" + String.valueOf(income.income_id));

        ApiClient.get().putIncome(in, new GenericCallback<Income>() {
            @Override
            public void onSuccess(Income data) {
                income.amount=data.amount;
                income.payment_method=data.payment_method;
                income.created=data.created;

                // holder.line_edit.setVisibility(View.GONE);
                updateItem(position,income);

               /* if(onRefreshIncomes!=null){
                    onRefreshIncomes.onRefreshAmountIncomes();
                }*/
            }

            @Override
            public void onError(Error error) {

            }
        });


    }

    public void createIncome2(final Long mClassCourseId, ReportClassCourse currentCourse){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.cuad_add_payment, null);
        builder.setView(dialogView);

        final TextView date=  dialogView.findViewById(R.id.date);
        final EditText value=  dialogView.findViewById(R.id.value_payment);
        final TextView payed=  dialogView.findViewById(R.id.payed);
        final TextView pack_value=  dialogView.findViewById(R.id.pack_value);
        final TextView rest=  dialogView.findViewById(R.id.rest);

        date.setText(DateHelper.get().getActualDate());
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(date);
            }
        });

        //payment method
        final LinearLayout efect=  dialogView.findViewById(R.id.efect);
        final LinearLayout card=  dialogView.findViewById(R.id.card);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setBackgroundResource(R.drawable.circle2);
                efect.setBackgroundResource(R.drawable.circle_unselected);
            }
        });

        efect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setBackgroundResource(R.drawable.circle_unselected);
                efect.setBackgroundResource(R.drawable.circle2);
            }
        });

        //input
        pack_value.setText(ValuesHelper.get().getIntegerQuantity(mCourseAmount));
        payed.setText(ValuesHelper.get().getIntegerQuantity(mCoursePaidAmount));
        Double d= mCourseAmount - mCoursePaidAmount;
        rest.setText(String.valueOf(d));

        value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                value.setCursorVisible(true);
            }
        });

        value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        final LinearLayout cancel=  dialogView.findViewById(R.id.cancel);
        final LinearLayout ok=  dialogView.findViewById(R.id.ok);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String detail = currentCourse.category+" "+currentCourse.sub_category+" "+currentCourse.classes_number+"cl";

                if(ValidatorHelper.get().isTypeDouble(value.getText().toString().trim())){
                    createIncome(Double.valueOf(value.getText().toString()),mClassCourseId, date, detail);
                    dialog.dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void createIncome(Double amount,Long class_course_id, TextView date, String detail){

        DataIncomeCourse inc= new DataIncomeCourse(amount,"efectivo", class_course_id, detail);
        // inc.datetime= DateHelper.get().getActualDate();
        inc.datetime = date.getText().toString().trim();
        inc.created = date.getText().toString().trim();

        ApiClient.get().postIncomeCourse(inc, new GenericCallback<Income>() {
            @Override
            public void onSuccess(Income data) {
                ReportIncome r= new ReportIncome(data.amount,data.payment_method,data.created);
                r.income_id=data.id;



               if(onRefreshIncomes!=null){
                    onRefreshIncomes.onRefreshListIncomes(r);
                }
            }

            @Override
            public void onError(Error error) {

            }
        });
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
