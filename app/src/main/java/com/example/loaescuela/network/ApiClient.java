package com.example.loaescuela.network;

import android.util.Log;

import com.example.loaescuela.network.models.ClassCourse;
import com.example.loaescuela.network.models.DataIncomeCourse;
import com.example.loaescuela.network.models.Income;
import com.example.loaescuela.network.models.Outcome;
import com.example.loaescuela.network.models.Planilla;
import com.example.loaescuela.network.models.PlanillaAlumno;
import com.example.loaescuela.network.models.PlanillaPresente;
import com.example.loaescuela.network.models.ReportClassCourse;
import com.example.loaescuela.network.models.ReportIncomeStudent;
import com.example.loaescuela.network.models.ReportOutcome;
import com.example.loaescuela.network.models.ReportPresent;
import com.example.loaescuela.network.models.ReportResumAsist;
import com.example.loaescuela.network.models.ReportStudentAsist;
import com.example.loaescuela.network.models.ReportStudentValue;
import com.example.loaescuela.network.models.Student;
import com.example.loaescuela.network.models.User;
import com.example.loaescuela.network.models.UserToken;
import com.google.gson.Gson;


import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ApiClient {


    private static ApiClient INSTANCE = new ApiClient();

    private ApiClient(){}

    public static ApiClient get(){
        return INSTANCE;
    }

    //PLANILLAS_ALUMNOS

    public void postPlanillaAlumno(PlanillaAlumno c,GenericCallback<PlanillaAlumno> callback){
        handleRequest( ApiUtils.getAPISessionService().postAlumnoPlanilla(c), callback);
    }

    //PLANILLAS_PRESENTES

    public void postPlanillaPresente(PlanillaPresente c,GenericCallback<PlanillaPresente> callback){
        handleRequest( ApiUtils.getAPISessionService().postPresentePlanilla(c), callback);
    }

    public void putPlanillaPresnete(PlanillaPresente c,GenericCallback<PlanillaPresente> callback){
        handleRequest( ApiUtils.getAPISessionService().putPresentePlanilla(c), callback);
    }

    public void deletePlanillaPresente(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPISessionService().deletePlanillaPresente(id), callback);
    }



    public void getAssistsResumByDay(Integer page, final GenericCallback<List<ReportResumAsist>> callback){
        handleRequest( ApiUtils.getAPISessionService().getAssistsResumByDay("getDayResumPresents", page), callback);
    }


    public void getAssistsByStudents(Integer page, Long id,final GenericCallback<List<ReportPresent>> callback){
        handleRequest( ApiUtils.getAPISessionService().getAssistsByStudent("getPresentsByStudent", page, id), callback);
    }

    //CURSOS

    public void postClassCourse(ClassCourse c, GenericCallback<ClassCourse> callback){
        handleRequest( ApiUtils.getAPISessionService().postClassCourse(c), callback);
    }

    public void getCoursesByStudentId(Long id, Integer page, final GenericCallback<List<ReportClassCourse>> callback){
        handleRequest( ApiUtils.getAPISessionService().getCoursesByStudentId("getCoursesByStudent", page, id), callback);
    }


    public void getCourseById(Long student_id,Long id, final GenericCallback<List<ReportClassCourse>> callback){
        handleRequest( ApiUtils.getAPISessionService().getCourseById( "getCoursesByStudent",student_id, id), callback);
    }


    //INCOMES_COURSE

    public void postIncomeCourse(DataIncomeCourse inc, GenericCallback<Income> callback){
        handleRequest( ApiUtils.getAPIService().postIncomeCourse(inc), callback);
    }



    //INCOMES

    public void putIncome(Income inc, GenericCallback<Income> callback){
        handleRequest( ApiUtils.getAPIService().putIncome(inc), callback);
    }

    public void getAllIncomes(Integer page, final GenericCallback<List<ReportIncomeStudent>> callback){
        handleRequest( ApiUtils.getAPISessionService().getAllIncomes("getAllIncomes", page), callback);
    }


    //OUTCOMES

    public void postOutcome(Outcome inc, GenericCallback<Outcome> callback){
        handleRequest( ApiUtils.getAPIService().postOutcome(inc), callback);
    }

    public void putOutcome(Outcome inc, GenericCallback<Outcome> callback){
        handleRequest( ApiUtils.getAPIService().putOutcome(inc), callback);
    }

    public void delteOutcome(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPISessionService().deleteOutcome(id), callback);
    }

    public void getAllOutcomes(Integer page, final GenericCallback<List<ReportOutcome>> callback){
        handleRequest( ApiUtils.getAPISessionService().getAllOutcomes("getAllOutcomes", page), callback);
    }


    //PLANILLAS

    public void getPlanillas( Integer page,final GenericCallback<List<Planilla>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getPlanillas("getAll",page), callback);
    }

    public void postPlanilla(Planilla c,GenericCallback<Planilla> callback){
        handleRequest( ApiUtils.getAPISessionService().postPlanilla(c), callback);
    }

    public void deltePlanilla(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPISessionService().deletePlanilla(id), callback);
    }


    //STUDENTS


    public void postStudent(Student c,GenericCallback<Student> callback){
        handleRequest( ApiUtils.getAPISessionService().postStudent(c), callback);
    }

    public void getStudents(String query, Integer page,String category, String orderby, final GenericCallback<List<Student>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getStudents(page,query, "getStudents",category, orderby), callback);
    }

    public void getStudents2(String query, Integer page,final GenericCallback<List<Student>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getStudents2(query,page, "getStudents"), callback);
    }

    public void getStudentsAsists(String query,Integer page,String category,String categoria, String subcategoria, String datePresent ,String onlyPresents, final GenericCallback<ReportStudentAsist> callback ){
                handleRequest( ApiUtils.getAPISessionService().getStudentsAsists( "getStudentsByAssists",query,page,category,categoria,subcategoria, datePresent, onlyPresents), callback);
    }

    public void getStudentsValue(String category,String categoria, String subcategoria, String datePresent ,final GenericCallback<ReportStudentValue> callback ){
        handleRequest( ApiUtils.getAPISessionService().getStudentsValues( "getValues",category,categoria,subcategoria, datePresent), callback);
    }

    //USER
    public void login(String name,String password, GenericCallback<UserToken> callback){
        handleRequest( ApiUtils.getAPIService().login(name,password,"login"), callback);
    }

    public void register(User u, String key, GenericCallback<User> callback){
        handleRequest( ApiUtils.getAPIService().register(u,key,"register"), callback);
    }



    private <T> void handleRequest(rx.Observable<Response<T>> request, final GenericCallback<T> callback){
        request.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<T>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Error error = new Error();
                        error.result = "error";
                        error.message = "Generic Error";
                        e.printStackTrace();
                        if( e instanceof HttpException){
                            try {
                                String body = ((HttpException) e).response().errorBody().string();
                                Gson gson = new Gson();
                                error =  gson.fromJson(body,Error.class);
                            }catch (Exception e1){
                                e1.printStackTrace();
                            }
                        }
                        callback.onError(error);
                    }

                    @Override
                    public void onNext(Response<T>  response) {
                        callback.onSuccess(response.data);
                    }
                });
    }

    private <T> void handleRequest2(rx.Observable<Response<T>> request, final GenericCallback<T> callback){
        request.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<T>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Error error = new Error();
                        error.result = "error";
                        error.message = "Generic Error";
                        e.printStackTrace();
                        if( e instanceof HttpException){
                            try {
                                String body = ((HttpException) e).response().errorBody().string();
                                Gson gson = new Gson();
                                error =  gson.fromJson(body,Error.class);
                            }catch (Exception e1){
                                e1.printStackTrace();
                            }
                        }
                        callback.onError(error);
                    }

                    @Override
                    public void onNext(Response<T>  response) {
                        callback.onSuccess(response.data);
                    }
                });
    }

    private void handleDeleteRequest(Observable<ResponseBody> request, final GenericCallback<Void> callback){
        request.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Error error = new Error();
                        error.result = "error";
                        error.message = "Generic Error";
                        e.printStackTrace();
                        Log.e("RETRO", e.getMessage());
                        if( e instanceof HttpException){
                            try {
                                String body = ((HttpException) e).response().errorBody().string();
                                Gson gson = new Gson();
                                error =  gson.fromJson(body,Error.class);
                                Log.e("RETRO", body);
                            }catch (Exception e1){
                                e1.printStackTrace();
                            }
                        }
                        callback.onError(error);
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        callback.onSuccess(null);
                    }
                });
    }


}
