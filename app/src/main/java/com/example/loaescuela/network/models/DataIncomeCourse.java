package com.example.loaescuela.network.models;

public class DataIncomeCourse {

    public Double amount;
    public String payment_method,created,datetime;
    public Long class_course_id;


    public DataIncomeCourse(Double amount, String payment_method, Long class_course_id){
        this.amount=amount;
        this.payment_method=payment_method;
        this.class_course_id=class_course_id;

    }
}