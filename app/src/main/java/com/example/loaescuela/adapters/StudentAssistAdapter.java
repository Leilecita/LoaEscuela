package com.example.loaescuela.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loaescuela.Interfaces.OnSelectStudent;
import com.example.loaescuela.R;
import com.example.loaescuela.activities.AssistsCoursesIncomesByStudentActivity;
import com.example.loaescuela.network.ApiClient;
import com.example.loaescuela.network.Error;
import com.example.loaescuela.network.GenericCallback;
import com.example.loaescuela.network.models.PlanillaPresente;
import com.example.loaescuela.network.models.ReportStudentAsistItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StudentAssistAdapter extends BaseAdapter<ReportStudentAsistItem,StudentAssistAdapter.ViewHolder> {
    private Context mContext;
    private ArrayAdapter<String> adapter;

    List<String> listColor = new ArrayList<>();
    Random random;

    private String datePresent;
    private Long planilla_id;

    private OnSelectStudent onSelectStudent = null;

    public void setOnSelectStudent(OnSelectStudent listener){
        this.onSelectStudent = listener;
    }

    public StudentAssistAdapter(Context context, List<ReportStudentAsistItem> clients) {
        setItems(clients);
        mContext = context;

        listColor.add("#685c85");
        listColor.add("#4f426b");
        listColor.add("#A49FB8");
        listColor.add("#817699");
        listColor.add("#a197b8");
        listColor.add("#9088a3");
        listColor.add("#C6BCDA");
        random = new Random();

    }

    public StudentAssistAdapter() {
    }

    public void setDatePresent(String date){
        this.datePresent = date;
    }

    public void setPlanillaId(Long planillaid){
        this.planilla_id = planillaid;
    }

    public List<ReportStudentAsistItem> getListClient() {
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text_name;
        public TextView text_name2;
        public TextView text_year;
        public TextView taken_clases;
        public TextView buyed_classes;
        public TextView debt_amount;

        public TextView firstLetter;
        public CheckBox check_presente;

        public LinearLayout line_info_student;
        public LinearLayout more_info;

        public ImageView cuad_image;


        public ViewHolder(View v) {
            super(v);
            text_name = v.findViewById(R.id.text_name);
            text_name2 = v.findViewById(R.id.name2_student);
            check_presente = v.findViewById(R.id.check_presente);
            line_info_student = v.findViewById(R.id.info_student);
            taken_clases = v.findViewById(R.id.taken_classes);
            more_info = v.findViewById(R.id.more_info);
            buyed_classes = v.findViewById(R.id.buyed_classes);
            debt_amount = v.findViewById(R.id.debt_amount);
            cuad_image = v.findViewById(R.id.cuad_image);
        }
    }

    @Override
    public StudentAssistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_student_assist, parent, false);
        StudentAssistAdapter.ViewHolder vh = new StudentAssistAdapter.ViewHolder(v);
        return vh;
    }


    private void clearViewHolder(StudentAssistAdapter.ViewHolder vh) {
        if (vh.text_name != null)
            vh.text_name.setText(null);
        if (vh.text_year != null)
            vh.text_year.setText(null);


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final StudentAssistAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final ReportStudentAsistItem currentClient = getItem(position);

        holder.text_name.setText(currentClient.nombre + " " + currentClient.apellido);

        //holder.cuad_image.setColorFilter(Color.parseColor(#5C4D7C), PorterDuff.Mode.SRC_ATOP);
        holder.cuad_image.setColorFilter(Color.parseColor("#5C4D7C"), PorterDuff.Mode.SRC_ATOP);

        holder.taken_clases.setText(String.valueOf(currentClient.taken_classes.get(0).cant_presents));
        holder.buyed_classes.setText(String.valueOf(currentClient.taken_classes.get(0).cant_buyed_classes));
        holder.debt_amount.setText(String.valueOf(currentClient.taken_classes.get(0).tot_amount - currentClient.taken_classes.get(0).tot_paid_amount));


        if(currentClient.presente.equals("si")){
            holder.check_presente.setChecked(true);
        }else{
            holder.check_presente.setChecked(false);
        }

        holder.check_presente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPresent(currentClient, position);
                if(holder.check_presente.isChecked()){

                }
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.line_info_student.getVisibility() == View.VISIBLE){
                    holder.line_info_student.setVisibility(View.GONE);
                }else{
                    holder.line_info_student.setVisibility(View.VISIBLE);
                }

            }
        });

        holder.more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssistsCoursesIncomesByStudentActivity.start(mContext,currentClient.student_id, currentClient.nombre, currentClient.apellido);
            }
        });
    }


    private void addPresent(ReportStudentAsistItem r, Integer pos){

        PlanillaPresente p = new PlanillaPresente();
        p.alumno_id = r.student_id;
        p.planilla_id = r.planilla_id;
        p.fecha_presente = datePresent;

        if(r.planilla_presente_id >= 0){
            ApiClient.get().deletePlanillaPresente(r.planilla_presente_id, new GenericCallback<Void>() {
                @Override
                public void onSuccess(Void data) {


                    if(onSelectStudent!=null){
                        onSelectStudent.onSelectStudent();
                    }
                }

                @Override
                public void onError(Error error) {

                }
            });

        }else{

            ApiClient.get().postPlanillaPresente(p , new GenericCallback<PlanillaPresente>() {
                @Override
                public void onSuccess(PlanillaPresente data) {

                    if(onSelectStudent!=null){
                        onSelectStudent.onSelectStudent();
                    }
                }

                @Override
                public void onError(Error error) {

                }
            });
        }
    }



}

