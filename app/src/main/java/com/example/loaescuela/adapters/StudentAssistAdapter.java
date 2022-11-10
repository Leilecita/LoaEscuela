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
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loaescuela.Interfaces.OnSelectStudent;
import com.example.loaescuela.Interfaces.OrderFragmentListener;
import com.example.loaescuela.R;
import com.example.loaescuela.activities.AssistsCoursesIncomesByStudentActivity;
import com.example.loaescuela.network.ApiClient;
import com.example.loaescuela.network.Error;
import com.example.loaescuela.network.GenericCallback;
import com.example.loaescuela.network.models.PlanillaPresente;
import com.example.loaescuela.network.models.ReportSeasonPresent;
import com.example.loaescuela.network.models.ReportStudentAsist;
import com.example.loaescuela.network.models.ReportStudentAsistItem;
import com.example.loaescuela.types.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StudentAssistAdapter extends BaseAdapter<ReportStudentAsistItem,StudentAssistAdapter.ViewHolder> {
    private Context mContext;
    private ArrayAdapter<String> adapter;

    List<String> listColor = new ArrayList<>();
    Random random;

    private String datePresent;
    private String category="";
    private Long planilla_id;

    private OnSelectStudent onSelectStudent = null;

    private Boolean enablePresent = true;
    private OrderFragmentListener onOrderFragmentLister= null;

    public void setOnOrderFragmentLister(OrderFragmentListener lister){
        onOrderFragmentLister=lister;
    }

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

    public void setCategory(String category){
        this.category = category;
    }
    public StudentAssistAdapter() {
    }

    public void setEnablePresent(Boolean val){
        this.enablePresent = val;
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
            firstLetter = v.findViewById(R.id.firstLetter);
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
        if (vh.taken_clases != null)
            vh.taken_clases.setText(null);

        if (vh.buyed_classes != null)
            vh.buyed_classes.setText(null);

        if (vh.debt_amount != null)
            vh.debt_amount.setText(null);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final StudentAssistAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final ReportStudentAsistItem currentClient = getItem(position);

        holder.text_name.setText(currentClient.nombre + " " + currentClient.apellido);
        holder.firstLetter.setText(String.valueOf(currentClient.nombre.charAt(0)));

        if(this.category.equals(Constants.CATEGORY_COLONIA)){
            holder.cuad_image.setColorFilter(Color.parseColor(Constants.COLOR_COLONIA), PorterDuff.Mode.SRC_ATOP);
        }else if(this.category.equals(Constants.CATEGORY_ESCUELA)){
            holder.cuad_image.setColorFilter(Color.parseColor(Constants.COLOR_ESCUELA), PorterDuff.Mode.SRC_ATOP);
        }else{
            holder.cuad_image.setColorFilter(Color.parseColor(Constants.COLOR_HIGHSCHOOL), PorterDuff.Mode.SRC_ATOP);
        }

        System.out.println("nombre"+currentClient.nombre);
        System.out.println("taken"+currentClient.taken_classes.size());

        if(currentClient.taken_classes.size() > 0){

            holder.taken_clases.setText(String.valueOf(currentClient.taken_classes.get(0).cant_presents));
            holder.buyed_classes.setText(String.valueOf(currentClient.taken_classes.get(0).cant_buyed_classes));
            holder.debt_amount.setText(String.valueOf(currentClient.taken_classes.get(0).tot_amount - currentClient.taken_classes.get(0).tot_paid_amount));
        }


        if(currentClient.presente.equals("si")){
            holder.check_presente.setChecked(true);
        }else{
            holder.check_presente.setChecked(false);
        }

        if(!enablePresent){
            holder.check_presente.setEnabled(false);
            holder.check_presente.setAlpha(.2f);
            holder.cuad_image.setAlpha(.2f);

        } else{
            holder.check_presente.setEnabled(true);
            holder.check_presente.setAlpha(1.f);
            holder.cuad_image.setAlpha(1.f);
        }

        holder.check_presente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(enablePresent){
                    addPresent(currentClient, position);
                }else{
                    Toast.makeText(mContext, "No es posible modificar presentes de días anteriores", Toast.LENGTH_LONG).show();
                }


            }
        });

        if(currentClient.isOpen){
            holder.line_info_student.setVisibility(View.VISIBLE);
        }else{
            holder.line_info_student.setVisibility(View.GONE);
        }


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

                startEdithOrderActivity(currentClient,position, category);

               // AssistsCoursesIncomesByStudentActivity.start(mContext,currentClient.student_id, currentClient.nombre, currentClient.apellido, category, "ASISTENCIA");
            }
        });
    }

    private void startEdithOrderActivity(ReportStudentAsistItem order,Integer pos, String category){
        if(onOrderFragmentLister!=null){
            onOrderFragmentLister.startNewActivityFragment(order,pos, category);
        }
    }

    private void updatePresentStudent(ReportStudentAsistItem r, Integer pos){

    }

    public void updateItem(final Integer position){
        final ReportStudentAsistItem r = getItem(position);

        ApiClient.get().getResumInfoByStudent(r.student_id, new GenericCallback<List<ReportSeasonPresent>>() {
            @Override
            public void onSuccess(List<ReportSeasonPresent> data) {

                if(data.size() > 0 ) {
                    r.taken_classes = data;

               /* if(data.size() > 0 && r.taken_classes.size() > 0){
                    ReportSeasonPresent d = data.get(0);
                    r.taken_classes.get(0).cant_presents = d.cant_presents;
                    r.taken_classes.get(0).cant_buyed_classes = d.cant_buyed_classes;
                    r.taken_classes.get(0).tot_paid_amount = d.tot_paid_amount;
                    r.taken_classes.get(0).tot_amount = d.tot_amount;
                } */
                }

                r.isOpen = true;

                updateItem(position, r);
            }

            @Override
            public void onError(Error error) {

            }
        });

      /*  ApiClient.get().getReportOrderByOrderId(r.order_id, new GenericCallback<ReportOrder>() {
            @Override
            public void onSuccess(ReportOrder data) {
                r.amount_order=data.amount_order;

                r.items=data.items;
                r.items_add=data.items_add;
                r.items_rem=data.items_rem;

                r.delivery_date=data.delivery_date;
                r.order_obs=data.order_obs;

                r.products_cant = data.products_cant;

                r.prepared_by = data.prepared_by;

                r.assigned_zone = data.assigned_zone;

                r.process_user_name = data.process_user_name;

                updateItem(position,r);

                r.isOpen = true;
            }

            @Override
            public void onError(Error error) {

            }
        });*/
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

                    r.presente = "no";
                    r.planilla_presente_id = -1l;

                    //updateItem(pos, r); lo saque porque me modifica el padding bottom

                    if(onSelectStudent!=null){
                        onSelectStudent.onSelectStudent(-1l,"","","");
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

                    r.presente = "si";
                    r.planilla_presente_id = data.id;

                   // updateItem(pos, r);

                    if(onSelectStudent!=null){
                        onSelectStudent.onSelectStudent(-1l,"","","");
                    }
                }

                @Override
                public void onError(Error error) {

                }
            });
        }
    }



}

