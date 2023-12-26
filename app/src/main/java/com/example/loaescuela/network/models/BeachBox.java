package com.example.loaescuela.network.models;

public class BeachBox {
    public Double counted_sale, card, total_box, rest_box, deposit, rest_box_ant;
    public String category,payment_place,detail,created;
    public Long id;

    public BeachBox(){

    }

    public BeachBox( Double counted_sale, Double credit_card,Double total_box, Double rest_box,
                Double deposit, String detail,Double rest_box_ant){

        this.counted_sale=counted_sale;
        this.card=credit_card;
        this.total_box=total_box;
        this.rest_box=rest_box;
        this.deposit=deposit;
        this.detail=detail;
        this.rest_box_ant=rest_box_ant;
    }
}
