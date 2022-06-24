package com.example.loaescuela.network.models;

public class ReportIncome {

    public String created,description,payment_method,type;
    public Double amount,totalamount;
    public Long income_id;

    public ReportIncome(){}

   public ReportIncome(Double amount,String payment_method,String created){
        this.amount = amount;
        this.payment_method = payment_method;
        this.created = created;
    }
}
