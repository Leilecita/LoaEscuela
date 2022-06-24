package com.example.loaescuela.network.models;

import java.util.List;

public class ReportStudentAsistItem {


    public String nombre;
    public String apellido;
    public String presente;
   // public Integer taken_classes;
    public List<ReportSeasonPresent> taken_classes;

    public Long planilla_id;

    public Long student_id;
    public Long planilla_presente_id;

    public ReportStudentAsistItem(){}
}
