package com.example.loaescuela.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loaescuela.R;
import com.example.loaescuela.adapters.SpinnerAdapter;
import com.example.loaescuela.network.ApiClient;
import com.example.loaescuela.network.Error;
import com.example.loaescuela.network.GenericCallback;
import com.example.loaescuela.network.models.ReportResp;
import com.example.loaescuela.network.models.Student;
import com.example.loaescuela.types.CategoryType;
import com.example.loaescuela.types.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class CreateStudentActivity extends BaseActivity{

    private EditText mStudentName;
    private EditText mStudentSurName;
    private EditText mStudentPhone;
    private Spinner mStudentCategory;
    private EditText mClientObservation;
    private EditText mStudentDni;
    private TextView mStudentNacimiento;
    private String mAge;

    private CheckBox checkMenorEdad;
    private LinearLayout lineMenorEdad;
    private TextView nombreResponsabe;
    private TextView telResponsabe;

    private LinearLayout save;

    private ArrayAdapter<String> adapterCategory;

    public ImageView returnn;
    public TextView title;

    public String mExistAlum;

    public Long student_id;
    public String typeAct;


    public void setStyleToolbar(){
        returnn= this.findViewById(R.id.returnn);
        title = findViewById(R.id.title);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.create_student;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // showBackArrow();

        LinearLayout home= this.findViewById(R.id.home);
        setStyleToolbar();
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // if(typeAct != null && typeAct.equals("EDITAR")){
                 //   setResult(RESULT_OK);

                  /*  Intent i=new Intent();

                    i.putExtra("ID",student_id);
                    i.putExtra("NAME", mStudentName.getText().toString());
                    i.putExtra("SURNAME",mStudentSurName.getText().toString());
                    i.putExtra("CATEGORY",mStudentCategory.get);
                  //  i.putExtra("CAMEFROM",cameFrom);
              */
                finish();
            //    }
            }
        });

        typeAct = getIntent().getStringExtra("TYPE");

        if(typeAct != null && typeAct.equals("EDITAR")){
            student_id = getIntent().getLongExtra("STUDENTID", -1);
            getStudent(student_id);
        }

        setTitle("Alumno nuevo");

        mExistAlum = "";

        mStudentName = findViewById(R.id.student_name);
        mStudentSurName = findViewById(R.id.surname);
        mStudentPhone =  findViewById(R.id.phone);
        mStudentCategory =  findViewById(R.id.client_category);
        mStudentNacimiento =  findViewById(R.id.fecha_nacimiento);
        mStudentDni =  findViewById(R.id.dni);

        save =  findViewById(R.id.save);

        checkMenorEdad =  findViewById(R.id.check_menor_edad);
        lineMenorEdad =  findViewById(R.id.line_menor_edad);
        nombreResponsabe =  findViewById(R.id.nombre_responsable);
        telResponsabe =  findViewById(R.id.phone_responsable);

        checkMenorEdad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(checkMenorEdad.isChecked()){
                    lineMenorEdad.setVisibility(View.VISIBLE);
                }else{
                    lineMenorEdad.setVisibility(View.GONE);
                }
            }
        });

        mStudentNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(mStudentNacimiento);
            }
        });

        List<String> list = new ArrayList<>();

        adapterCategory = new SpinnerAdapter(getApplicationContext(), R.layout.item_custom_cat, enumCat(CategoryType.values(),list));
        mStudentCategory.setAdapter(adapterCategory);
        mStudentCategory.setPopupBackgroundDrawable(getResources().getDrawable(R.drawable.rec_find));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mExistAlum.equals("true")){
                    saveExistStudent();
                }else if(mExistAlum.equals("false")){
                    saveStudent();
                }else{
                    checkExistStudent(mStudentDni.getText().toString().trim());
                }
            }
        });
    }

    private void saveExistStudent(){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = (LayoutInflater)this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
            final View dialogView = inflater.inflate(R.layout.exist_student, null);
            builder.setView(dialogView);

            final TextView cancel=  dialogView.findViewById(R.id.cancel);
            final Button ok=  dialogView.findViewById(R.id.ok);

            final AlertDialog dialog = builder.create();
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveStudent();
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

    private void saveStudent(){
        String name = mStudentName.getText().toString().trim();
        String surname = mStudentSurName.getText().toString().trim();
        String fecha_nac = mStudentNacimiento.getText().toString().trim();
        String phone = mStudentPhone.getText().toString().trim();
        String category= mStudentCategory.getSelectedItem().toString().trim();
        String dni= mStudentDni.getText().toString().trim();

        if(mStudentDni.getText().toString().matches("") || mStudentName.getText().toString().matches("") || (lineMenorEdad.getVisibility() == View.GONE && mStudentPhone.getText().toString().matches(""))){
            Toast.makeText(getBaseContext(), "Los campos obligatorios deben estar completos" , Toast.LENGTH_SHORT).show();
        }else{

            Student student = new Student(name, surname, fecha_nac, phone, dni, category );
            student.edad = mAge;

            if(checkMenorEdad.isChecked()){
                student.tel_mama = telResponsabe.getText().toString().trim();
                student.nombre_mama = nombreResponsabe.getText().toString().trim();
            }

            final ProgressDialog progress = ProgressDialog.show(CreateStudentActivity.this, "Creando alumno",
                    "Aguarde un momento", true);


            ApiClient.get().postStudent(student, new GenericCallback<Student>() {
                @Override
                public void onSuccess(Student data) {
                    hideKeyboard(getWindow().getDecorView().findViewById(android.R.id.content));
                    progress.dismiss();

                    if(typeAct != null && typeAct.equals("EDITAR")) {
                        Intent i = new Intent();
                        i.putExtra("ID",data.id);
                        i.putExtra("NAME", data.nombre);
                        i.putExtra("SURNAME",data.apellido);
                        i.putExtra("CATEGORY",data.category);

                        setResult(RESULT_OK, i);
                    }
                    finish();
                }

                @Override
                public void onError(Error error) {

                }
            });
        }
    }

    private Integer getSelected(String cat){

        if(cat.equals(CategoryType.COLONIA.getName())){
            return 1;
        }else if(cat.equals(CategoryType.ESCUELA.getName())){
            return 2;
        }else{
            return 3;
        }
    }


    private void getStudent(Long id){
        ApiClient.get().getStudent(id, new GenericCallback<Student>() {
                    @Override
                    public void onSuccess(Student data) {

                        mStudentDni.setText(data.dni);
                        mStudentName.setText(data.nombre);
                        mStudentSurName.setText(data.apellido);
                        mStudentNacimiento.setText(data.fecha_nacimiento);
                        mStudentPhone.setText(data.tel_adulto);
                        mStudentCategory.setSelection(getSelected(data.category));


                    }

                    @Override
                    public void onError(Error error) {

                    }
                }
        );
    }
    private void checkExistStudent(String dni){
        ApiClient.get().checkExistStudent(dni, new GenericCallback<ReportResp>() {
            @Override
            public void onSuccess(ReportResp data) {
                if(data.val.equals("true")){
                    mExistAlum = "true";
                }else{
                    mExistAlum = "false";
                    saveStudent();
                }
            }

            @Override
            public void onError(Error error) {

            }
        });
    }


    private void selectDate(TextView nacimiento){
        final DatePickerDialog datePickerDialog;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day

        datePickerDialog = new DatePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT,
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

                        Calendar today = Calendar.getInstance();
                        c.set(year, Integer.valueOf(smonthOfYear) , Integer.valueOf(sdayOfMonth));

                        int age = today.get(Calendar.YEAR) - c.get(Calendar.YEAR);

                        if (today.get(Calendar.DAY_OF_YEAR) < c.get(Calendar.DAY_OF_YEAR)){
                            age--;
                        }

                        mAge = String.valueOf(age);

                        nacimiento.setText(year+"-"+smonthOfYear+"-"+sdayOfMonth);
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                hideKeyboard(getWindow().getDecorView().findViewById(android.R.id.content));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static <T extends Enum<CategoryType>> List<String> enumCat(CategoryType[] values, List<String> spinner_type) {
        for (CategoryType value : values) {
            if(value.getName().equals(Constants.TYPE_ALL)){

            }else{
                spinner_type.add(value.getName());
            }

        }
        return spinner_type;
    }

    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}