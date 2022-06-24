package com.example.loaescuela.network.models;

public class ClassCourse {
    public Long id;
    public Long student_id;
    public String category;
    public String sub_category;
    public Integer classes_number;
    public String observation;
    public Double amount;
    public String created;

    public Double paid_amount;
    public String payment_method;

    public ClassCourse(Long student_id, String category, String sub_category, Integer classes_number, Double amount, String obs){
        this.classes_number = classes_number;
        this.student_id = student_id;
        this.category = category;
        this.sub_category = sub_category;
        this.observation = obs;
        this.amount = amount;
    }
    public ClassCourse(){}
}
