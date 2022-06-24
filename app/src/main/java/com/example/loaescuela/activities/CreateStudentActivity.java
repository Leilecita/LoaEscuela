package com.example.loaescuela.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
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
import com.example.loaescuela.network.models.Student;
import com.example.loaescuela.types.CategoryType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CreateStudentActivity extends BaseActivity{

    private EditText mStudentName;
    private EditText mStudentSurName;
    private EditText mStudentPhone;
    private Spinner mStudentCategory;
    private EditText mClientObservation;
    private EditText mStudentDni;
    private EditText mStudentNacimiento;

    private LinearLayout save;

    private ArrayAdapter<String> adapterCategory;

    public ImageView returnn;
    public TextView title;

    public void setStyleToolbar(){
        returnn= this.findViewById(R.id.returnn);
        title = findViewById(R.id.title);

       /* if(getResources().getString(R.string.app_name).equals( "Fisherton app")){
            title.setTextColor(getResources().getColor(R.color.textColorAppFisherton));
            returnn.setColorFilter(this.getResources().getColor(R.color.textColorAppFisherton));
        }else{
            title.setTextColor(getResources().getColor(R.color.white));
        }*/

    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_create_student;
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
                finish();
            }
        });

        setTitle("Alumno nuevo");

        mStudentName = findViewById(R.id.student_name);
        mStudentSurName = findViewById(R.id.surname);
        mStudentPhone =  findViewById(R.id.phone);
        mStudentCategory =  findViewById(R.id.client_category);
        mStudentNacimiento =  findViewById(R.id.fecha_nacimiento);
        mStudentDni =  findViewById(R.id.dni);

        save =  findViewById(R.id.save);

        List<String> list = new ArrayList<>();

        adapterCategory = new SpinnerAdapter(getApplicationContext(), R.layout.item_custom_cat, enumCat(CategoryType.values(),list));
        mStudentCategory.setAdapter(adapterCategory);
        mStudentCategory.setPopupBackgroundDrawable(getResources().getDrawable(R.drawable.rec_find));


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = mStudentName.getText().toString().trim();
                String surname = mStudentSurName.getText().toString().trim();
                String fecha_nac = mStudentNacimiento.getText().toString().trim();
                String phone = mStudentPhone.getText().toString().trim();
                String category= mStudentCategory.getSelectedItem().toString().trim();
                String dni= mStudentDni.getText().toString().trim();

                if(mStudentName.getText().toString().matches("") || mStudentPhone.getText().toString().matches("")){
                    Toast.makeText(getBaseContext(), "Los campos obligatorios deben estar completos" , Toast.LENGTH_SHORT).show();
                }else{

                    Student student = new Student(name, surname, fecha_nac, phone, dni, category );

                    final ProgressDialog progress = ProgressDialog.show(CreateStudentActivity.this, "Creando alumno",
                            "Aguarde un momento", true);


                    ApiClient.get().postStudent(student, new GenericCallback<Student>() {
                        @Override
                        public void onSuccess(Student data) {
                            hideKeyboard(getWindow().getDecorView().findViewById(android.R.id.content));
                            progress.dismiss();

                            finish();
                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });

                }
            }
        });
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
            spinner_type.add(value.getName());
        }
        return spinner_type;
    }

    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}