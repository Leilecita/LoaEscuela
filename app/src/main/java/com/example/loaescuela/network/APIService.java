package com.example.loaescuela.network;

import com.example.loaescuela.adapters.PlanillaAdapter;
import com.example.loaescuela.network.models.BeachBox;
import com.example.loaescuela.network.models.ClassCourse;
import com.example.loaescuela.network.models.DataIncomeCourse;
import com.example.loaescuela.network.models.Income;
import com.example.loaescuela.network.models.Outcome;
import com.example.loaescuela.network.models.Planilla;
import com.example.loaescuela.network.models.PlanillaAlumno;
import com.example.loaescuela.network.models.PlanillaPresente;
import com.example.loaescuela.network.models.ReportBox;
import com.example.loaescuela.network.models.ReportClassCourse;
import com.example.loaescuela.network.models.ReportIncomeStudent;
import com.example.loaescuela.network.models.ReportNewBox;
import com.example.loaescuela.network.models.ReportOutcome;
import com.example.loaescuela.network.models.ReportPresent;
import com.example.loaescuela.network.models.ReportResp;
import com.example.loaescuela.network.models.ReportResumAsist;
import com.example.loaescuela.network.models.ReportSeasonPresent;
import com.example.loaescuela.network.models.ReportStudentAsist;
import com.example.loaescuela.network.models.ReportStudentValue;
import com.example.loaescuela.network.models.Student;
import com.example.loaescuela.network.models.User;
import com.example.loaescuela.network.models.UserToken;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import rx.Observable;

public interface APIService {

    //PLANILLAS_ALUMNOS

    @POST("planillas_alumnos.php")
    Observable<Response<PlanillaAlumno>> postAlumnoPlanilla(@Body PlanillaAlumno c);

    //PLANILLAS_PRESENTES

    @GET("planillas_presentes.php")
    Observable<Response<List<ReportPresent>>> getAssistsByStudent(@Query("method") String method,  @Query("id") Long alumno_id);

    @GET("planillas_presentes.php")
    Observable<Response<List<ReportResumAsist>>> getAssistsResumByDay(@Query("method") String method, @Query("page") Integer page);

    @POST("planillas_presentes.php")
    Observable<Response<PlanillaPresente>> postPresentePlanilla(@Body PlanillaPresente c);

    @PUT("planillas_presentes.php")
    Observable<Response<PlanillaPresente>> putPresentePlanilla(@Body PlanillaPresente c);

    @DELETE("planillas_presentes.php")
    Observable<ResponseBody>  deletePlanillaPresente(@Query("id") Long id);


    //CURSOS

    @POST("class_courses.php")
    Observable<Response<ClassCourse>> postClassCourse(@Body ClassCourse c);

    @GET("class_courses.php")
    Observable<Response<List<ReportClassCourse>>> getCoursesByStudentId(@Query("method") String method, @Query("page") Integer page, @Query("student_id") Long id);

    @GET("class_courses.php")
    Observable<Response<List<ReportClassCourse>>> getCourseById(@Query("method") String method, @Query("student_id") Long id,  @Query("course_id") Long course_id);




    //INCOMES
    @POST("incomes_class_courses.php")
    Observable<Response<Income>> postIncomeCourse(@Body DataIncomeCourse inc);

    @GET("incomes.php")
    Observable<Response<List<ReportIncomeStudent>>> getAllIncomes(@Query("method") String method, @Query("page") Integer page, @Query("payment_place") String payment_place);

    //BEACH BOXES
    @POST("beach_boxes.php")
    Observable<Response<BeachBox>> postBeachBox(@Body BeachBox b);

    @GET("beach_boxes.php")
    Observable<Response<List<BeachBox>>> getBoxes(@Query("page") Integer page);

    @GET("beach_boxes.php")
    Observable<Response<ReportNewBox>> getPreviousBox(@Query("method") String method, @Query("to") String created, @Query("date") String date, @Query("dateTo") String dateTo);

    @GET("beach_boxes.php")
    Observable<Response<ReportBox>> getPaidAmountByDay(@Query("method") String method, @Query("date") String date);





    @DELETE("incomes.php")
    Observable<ResponseBody>  deleteIncome(@Query("id") Long id);

    @PUT("incomes.php")
    Observable<Response<Income>> putIncome(@Body Income income);

    //OUTCOMES

    @POST("outcomes.php")
    Observable<Response<Outcome>> postOutcome(@Body Outcome inc);

    @DELETE("outcomes.php")
    Observable<ResponseBody>  deleteOutcome(@Query("id") Long id);

    @PUT("outcomes.php")
    Observable<Response<Outcome>> putOutcome(@Body Outcome outcome);

    @GET("outcomes.php")
    Observable<Response<List<ReportOutcome>>> getAllOutcomes(@Query("method") String method , @Query("page") Integer page );

    //PLANILLAS
    @GET("planillas.php")
    Observable<Response<List<Planilla>>> getPlanillas(@Query("method") String method ,@Query("page") Integer page );

    @POST("planillas.php")
    Observable<Response<Planilla>> postPlanilla(@Body Planilla c);

    @DELETE("planillas.php")
    Observable<ResponseBody>  deletePlanilla(@Query("id") Long id);

    //STUDENTS

    @POST("students.php")
    Observable<Response<Student>> postStudent(@Body Student c);

    @GET("students.php")
    Observable<Response<ReportResp>> checkExistStudent(@Query("dni") String dni, @Query("method") String method);

    @GET("students.php")
    Observable<Response<Student>> getStudent(@Query("id") Long student_id);

    @GET("students.php")
    Observable<Response<List<Student>>> getStudents(@Query("page") Integer page, @Query("query") String query, @Query("method") String method , @Query("category") String category, @Query("order") String orderby );

    @GET("students.php")
    Observable<Response<List<Student>>> getStudents2( @Query("query") String query, @Query("page") Integer page,@Query("method") String method);


    @GET("students.php")
    Observable<Response<ReportStudentAsist>> getStudentsAsists(@Query("method") String method , @Query("query") String query, @Query("page") Integer page, @Query("category") String category, @Query("categoria") String categoria, @Query("subcategoria") String subcategoria ,@Query("date") String datepresent, @Query("onlyPresents") String onlyP );

    @PUT("students.php")
    Observable<Response<ReportStudentAsist>> updateStudentAsist(@Query("method") String method, @Body ReportStudentAsist studentAsist);

    @GET("students.php")
    Observable<Response<ReportStudentValue>> getStudentsValues(@Query("method") String method , @Query("category") String category, @Query("categoria") String categoria, @Query("subcategoria") String subcategoria , @Query("date") String datepresent );

    //SEASSONS

    @GET("seasons.php")
    Observable<Response<List<ReportSeasonPresent>>> getResumInfoByStudent(@Query("method") String method, @Query("student_id") Long student_id);

    //USERS
    @GET("login.php")
    Observable<Response<UserToken>> login(@Query("name") String name, @Query("hash_password") String password,
                                          @Query("method") String method);

    @POST("login.php")
    Observable<Response<User>> register(@Body User u, @Query("key_access") String key, @Query("method") String method);

}
