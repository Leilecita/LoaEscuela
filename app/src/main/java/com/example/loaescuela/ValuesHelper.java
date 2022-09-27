package com.example.loaescuela;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class ValuesHelper {
    private static ValuesHelper INSTANCE = new ValuesHelper();

    public Integer mPreviousPosition=0;

    private ValuesHelper(){

    }

    public static ValuesHelper get(){
        return INSTANCE;
    }


    public String getBigNumb(Double d){

        DecimalFormat formatter = new DecimalFormat("0.0");

        return formatter.format(BigDecimal.valueOf(d));
    }


    public String getBigNumb2(Double d){


        // return  String.format("%.2f", d);
       /* BigDecimal b = new BigDecimal(d);
        b = b.setScale(2, RoundingMode.HALF_UP);

        return b.doubleValue();*/
        return BigDecimal.valueOf(d).toPlainString();

        // Double d2=  BigDecimal.valueOf(d).round();

    }



    public String ifDecimalCeroGetIntegerQuantity(Double val){

        //Para numeros chicos, sino, usar el que redondea

       /* String[] arr=String.valueOf(val).split("\\.");
        int[] intArr=new int[2];

        intArr[0]=Integer.parseInt(arr[0]);
        intArr[1]=Integer.parseInt(arr[1]);

        if(intArr[1] == 0){
            return String.valueOf(intArr[0]);
        }else{
            return String.valueOf(val);
        }*/

        // return val.toString();

        //ANDA MUY BIEN PARA NUMEROS MUY GRANDES, SACA EL .O Y SACA LA E DE NOTACION CIENTIFICA
        DecimalFormat decimalFormatter = new DecimalFormat("############");
        return decimalFormatter.format((val));
    }

    public String getIntegerQuantityRoundedWithLongValues(Double val){

        // System.out.println(String.valueOf(val));
        val=roundTwoDecimals(val);


        System.out.println(String.valueOf(val));
        String[] arr=String.valueOf(val).split("\\.");
        System.out.println(String.valueOf(arr[1]));
        System.out.println(String.valueOf(arr[1].length()));

        if(arr[1].length() > 5){
            arr[1] = arr[1].substring(0,4);
        }

        int[] intArr=new int[2];
        intArr[0]=Integer.parseInt(arr[0]);
        intArr[1]=Integer.parseInt(arr[1]);

        if(intArr[1] == 0){
            return String.valueOf(intArr[0]);
        }else{
            return String.valueOf(val);
        }

    }


    public String getIntegerQuantityRounded(Double val){

        val=roundTwoDecimals(val);

        String[] arr=String.valueOf(val).split("\\.");

        int[] intArr=new int[2];
        intArr[0]=Integer.parseInt(arr[0]);
        intArr[1]=Integer.parseInt(arr[1]);

        if(intArr[1] == 0){
            return String.valueOf(intArr[0]);
        }else{
            return String.valueOf(val);
        }

    }


    public String getIntegerQuantityByLei(Double val){

        String[] parts = String.valueOf(val).split(".");
        if(Integer.valueOf(parts[1]) == 0){
            return parts[0];
        }else{
            return String.valueOf(val);
        }

    }

    public String getIntegerQuantity(Double val){
        String[] arr=String.valueOf(val).split("\\.");
        int[] intArr=new int[2];
        intArr[0]=Integer.parseInt(arr[0]);
        intArr[1]=Integer.parseInt(arr[1]);

        if(intArr[1] == 0){
            return String.valueOf(intArr[0]);
        }else{
            return String.valueOf(val);
        }
    }

    public Double roundTwoDecimals(double d)
    {
        double roundOff = (double) Math.round(d * 100) / 100;

        // DecimalFormat twoDForm = new DecimalFormat("#.##");
        //return Double.valueOf(twoDForm.format(d));
        return roundOff;
    }



    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        System.out.println(String.valueOf((double) tmp / factor));
        return (double) tmp / factor;
    }
}
