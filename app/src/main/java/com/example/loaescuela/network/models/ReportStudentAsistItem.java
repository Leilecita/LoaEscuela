package com.example.loaescuela.network.models;

import java.util.List;

public class ReportStudentAsistItem {


    public String nombre, dni;
    public String apellido;
    public String presente;
    public String color, observacion;
   // public Integer taken_classes;
    public List<ReportSeasonPresent> taken_classes;

    public Long planilla_id;

    public Long student_id;
    public Long planilla_presente_id, planilla_alumno_id;

    public Boolean isOpen = false;

    public String nombre_mama;
    public String nombre_papa;
    public String tel_mama;
    public String tel_papa;
    public String tel_adulto;

    public ReportStudentAsistItem(){}
}
