package com.example.loaescuela.network.models;

public class Student {

    public Long id;
    public String nombre;
    public String apellido;
    public String fecha_nacimiento;
    public String tel_adulto;
    public String category;
    public String dni;
    public String color;

    public String edad;

    public String direccion;
    public String localidad;

    public String created;
    public String updated_date;
    public String nombre_mama;
    public String nombre_papa;
    public String tel_mama;
    public String tel_papa;

    public Student(){}

    public Student(String name, String surname, String fecha_nacimiento, String phone, String dni, String category){

        this.nombre = name;
        this.apellido = surname;
        this.fecha_nacimiento = fecha_nacimiento;
        this.tel_adulto = phone;
        this.category = category;
        this.dni = dni;
    }
}