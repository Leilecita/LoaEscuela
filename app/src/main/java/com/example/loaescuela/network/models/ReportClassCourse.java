package com.example.loaescuela.network.models;

import java.util.ArrayList;

public class ReportClassCourse {

    public Long class_course_id;
    public Long student_id;
    public String student_name;
    public String category;
    public String sub_category;
    public Integer classes_number;
    public String observation;
    public Double amount;
    public Double paid_amount;
    public String created;

    public ArrayList<ReportIncome> list_incomes;

    public ReportClassCourse(){}
}
