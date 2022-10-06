package com.example.loaescuela;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by leila on 15/11/17.
 */

public class DateHelper {

    private static DateHelper INSTANCE = new DateHelper();

    private DateHelper() {

    }

    public static DateHelper get() {
        return INSTANCE;
    }

    public String getActualDate() {
        return actualDate();
    }

    public String getActualDate2() {
        return actualDate2();
    }

    public String getOnlyDate(String date) {
        return onlyDate(date);
    }

    public String getOnlyDateToShow(String date) {
        return onlyDateToShow(date);
    }

    public String getOnlyDateComplete(String date) {
        return onlyDateComplete(date);
    }

    public String getOnlyTime(String date) {
        return onlyTime(date);
    }

    public String getActualDateToShow() {
        return actualDateToShow();
    }

    public String getOnlyTimeHour(String date) {
        String date2=onlyTime(date);
        return onlyHourMinut(date2);
    }

    public String getOnlyTime() {
        return onlyTime();
    }

    public String onlyDayMonthComplete(String date) {
        String[] parts = date.split(" ");
        String part1 = parts[0]; // fecha
        String[] partsDate = date.split("-");
        return partsDate[0]+"-"+partsDate[1] + "-01 00:00:00";
    }


    public String getNextDay(String date) {
        try {

            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date1 = format1.parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(date1);
            c.add(Calendar.DATE, 1);
            date1 = c.getTime();

            return format1.format(date1);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "dd/MM/yyyy";
    }

    public String getPreviousDay(String date) {
        try {

            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date1 = format1.parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(date1);
            c.add(Calendar.DATE, -1);
            date1 = c.getTime();

            return format1.format(date1);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "dd/MM/yyyy";
    }


    public String getNextDayBox(String date) {
        try {

            SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date1 = format1.parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(date1);
            c.add(Calendar.DATE, 1);
            date1 = c.getTime();

            return format1.format(date1);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "dd/MM/yyyy";
    }

    public String getNextMonth(String date) {
        try {

            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date1 = format1.parse(date);

            Calendar c = Calendar.getInstance();
            c.setTime(date1);
            c.add(Calendar.MONTH, 1);
            date1 = c.getTime();

            return format1.format(date1);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "dd/MM/yyyy";
    }

    public String getActualDateEmployee() {
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(currentDate);
    }


    private String actualDate() {
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(currentDate);
    }



    private String actualDate2() {
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(currentDate);
    }

    public String actualDateToShow() {
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(currentDate);
    }

    public String changeOrderDate(String date) {
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = format1.parse(date);
            SimpleDateFormat format2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            String stringdate2 = format2.format(date1);
            return stringdate2;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "dd/MM/yyyy";
    }



    public String serverToUser(String date) {

        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date1 = format1.parse(date);
            format1.setTimeZone(TimeZone.getDefault());
            return format1.format(date1);
        } catch (ParseException e) {

        }
        return "";
    }


    public String serverToUserFormatted(String date) {

        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date1 = format1.parse(date);
            format1.setTimeZone(TimeZone.getDefault());
            return changeFormatDate(format1.format(date1));
        } catch (ParseException e) {

        }
        return "";
    }

    public String changeFormatDate(String date) {
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = format1.parse(date);
            SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            String stringdate2 = format2.format(date1);
            return stringdate2;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "dd/MM/yyyy";
    }

    public String changeFormatDateUserToServer(String date) {
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date1 = format1.parse(date);
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String stringdate2 = format2.format(date1);
            return stringdate2;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "dd/MM/yyyy";
    }

    public String userToServer(String date) {

        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format1.setTimeZone(TimeZone.getDefault());
            Date date1 = format1.parse(date);

            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            return format1.format(date1);
        } catch (ParseException e) {

        }
        return "";
    }


    public Date parseDate(String date){
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format1.setTimeZone(TimeZone.getDefault());
            Date date1 = format1.parse(date);
            return date1;
        }catch (Exception e){
            return null;
        }
    }

    public String onlyDate(String date) {
        String[] parts = date.split(" ");
        String part1 = parts[0]; // fecha
        return part1;
    }

    public String onlyDateToShow(String date) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getDefault());
        String dateToShow = formatter.format(date);

        String[] parts = dateToShow.split(" ");
        return parts[0];
    }

    public String getDateFromMonthAndYear(String month, String year){
        String numberMonth = getNumberMonth(month);

        return year+"-"+numberMonth+"-01 00:00:00";
    }
    public String onlyDateComplete(String date) {
        String[] parts = date.split(" ");
        String part1 = parts[0]; // fecha
        return part1 + " 00:00:00";
    }

    public String onlyDay2(String date) {
        String[] parts = date.split("-");
        String part1 = parts[0]; // año
        String part2 = parts[1]; // mes
        String part = parts[2]; // dia
        return part ;
    }

    public String getNameMonth(String date) {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String monthName = new SimpleDateFormat("MM").format(cal.getTime());

            String[] monthNames = {"","Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
            return monthNames[Integer.valueOf(monthName)];
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getDayEvent(String date){
        String[] parts = date.split(" ");
        String part1 = parts[0]; // fecha

        String part3= onlyDay2(part1);

        if(Integer.valueOf(part3) < 10){
            return part3.substring(1);
        }else{
            return part3;
        }
    }

    public String onlyMonthCompplete(String date) {
        String[] parts = date.split(" ");
        String part1 = parts[0]; // fecha
        return part1 + " 00:00:00";
    }

    public String onlyDayMonth(String date) {
        String[] parts = date.split("/");
        String part1 = parts[0]; // dia
        String part2 = parts[1]; // mes
        String part = parts[2]; // año
        return part1 + "-" + part2;
    }


    public String onlyDayMonthSimpleDate(String date) {
        String[] parts = date.split("-");
        String part1 = parts[0]; // año
        String part2 = parts[1]; // mes
        String part3 = parts[2]; // dia
        return part3 + "-" + part2;
    }

    public String onlyYearSimpleDate(String date) {
        String[] parts = date.split("-");
        String part1 = parts[0]; // año
        String part2 = parts[1]; // mes
        String part3 = parts[2]; // dia
        return part1;
    }

    public String getDayMonth(String date) {
        String[] onlydate=date.split(" ");

        String[] parts = onlydate[0].split("-");

        String part1 = parts[0]; // año
        String part2 = parts[1]; // mes
        String part = parts[2]; // dia
        return part + "-" + part2;
    }

    public String getOnlymonth(String date2) {

        String[] date = date2.split(" ");

        String[] parts = date[0].split("-");
        String part1 = parts[0]; // dia
        String part2 = parts[1]; // mes
        String part = parts[2]; // año
        return part + "-" + part2 + "-00 00:00:00";
    }

    public String getOnlyYear(String date) {
        String[] parts = date.split("-");
        String part1 = parts[0]; // dia
        String part2 = parts[1]; // mes
        String part3 = parts[2]; // año
        return part3;
    }

    public String getYear(String date) {
        String[] parts = date.split("-");
        String part1 = parts[0]; // year
        String part2 = parts[1]; // mes
        String part3 = parts[2]; // adia
        return part1;
    }

    public String onlyMonth(String date) {
        String[] parts = date.split("/");
        String part1 = parts[0]; // dia
        String part2 = parts[1]; // mes
        String part3 = parts[2]; // año
        return part2;
    }

    public String onlyDay(String date) {
        String[] parts = date.split("-");
        String part1 = parts[0]; // dia
        String part2 = parts[1]; // mes
        String part3 = parts[2]; // año
        return part1;
    }

    public String getOnlyDay(String date) {
        String[] parts = date.split("-");
        String part1 = parts[0]; // year
        String part2 = parts[1]; // mes
        String part3 = parts[2]; // dia
        return part3;
    }

    public String getDay(String date) {
        String[] parts = date.split("-");
        String part1 = parts[2]; // dia
        String part2 = parts[1]; // mes
        String part3 = parts[0]; // año
        return part1;
    }

    public String numberDay(String date2) {

        String date= onlyDate(date2);
        String[] parts = date.split("-");
        String part1 = parts[0]; // año
        String part2 = parts[1]; // mes
        String part3 = parts[2]; // dia

        if (Integer.valueOf(part3) < 10) {

            return part3.substring(1);
        } else {
            return part3;
        }

    }
    private String onlyTime(String date) {
        String[] parts = date.split(" ");
        if (parts.length > 1) {
            return parts[1]; // time
        } else {
            return "";
        }
    }

    public String getOnlyTimeFromCreated(String date) {
        if(date.length() > 0 ){
            String[] parts = date.split(" ");
            if (parts.length > 1) {

                String[] parts2 = parts[1].split(":");

                String hour=parts2[0];

                if(Integer.valueOf(hour) < 10){
                    return hour.substring(1)+":"+parts2[1]; // time
                }else{
                    return hour+":"+parts2[1]; // time
                }
            } else {
                return "";
            }

        }else{
            return "";
        }
    }

    private String onlyTime() {
        String date = actualDate2();

        if(date.length() > 0 ){
            String[] parts = date.split(" ");
            if (parts.length > 1) {

                String[] parts2 = parts[1].split(":");

                String hour=parts2[0];

                if(Integer.valueOf(hour) < 10){
                    return hour.substring(1)+":"+parts2[1]; // time
                }else{
                    return hour+":"+parts2[1]; // time
                }

            } else {
                return "";
            }

        }else{
            return "";
        }
    }


    public String onlyHourMinut(String time) {
        String[] parts = time.split(":");
        if (parts.length > 1) {
            return parts[0]+":"+parts[1]; // time
        } else {
            return "";
        }
    }



   /* public String getNameMonth(String date) {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String monthName = new SimpleDateFormat("MM").format(cal.getTime());
            String[] monthNames = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre","Enero"};
            System.out.println(Integer.valueOf(monthName));
            System.out.println(monthName);
            System.out.println(monthNames[Integer.valueOf(monthName)]);


            return monthNames[Integer.valueOf(monthName)];
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }*/

    private String getNumberMonth(String month){
        if(month.equals("Diciembre")){
            return "12";
        }else if(month.equals("Enero")){
            return "01";
        }else if(month.equals("Febrero")){
            return "02";
        }else if(month.equals("Marzo")){
            return "03";
        }else if(month.equals("Abril")){
            return "04";
        }else{
            return "05";
        }
    }

    public List<String> getListMonths(){
        ArrayList<String> m = new ArrayList<>();

        m.add("Mes");
        m.add("Diciembre");
        m.add("Enero");
        m.add("Febrero");
        m.add("Marzo");
        m.add("Abril");

        return m;
    }

    public String getNameMonth2(String date) {
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            String monthName = new SimpleDateFormat("MM").format(cal.getTime());
            String[] monthNames = {"","Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};


            return monthNames[Integer.valueOf(monthName)];
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }



    public String getNameDay(String input_date){
        try {

            SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd");
            Date dt1=format1.parse(input_date);

            DateFormat format2=new SimpleDateFormat("EEE");
            String finalDay=format2.format(dt1);

           // System.out.println(finalDay.substring(0,3));
            return getNamSpanish(finalDay.substring(0,3));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";

    }

    public String getNamSpanish(String dayEnglish) {

        if(dayEnglish.equals("Sun")|| dayEnglish.equals("dom")){
            return "Dom";
        }else if(dayEnglish.equals("Mon") || dayEnglish.equals("lun")){
            return "Lun";
        }else if(dayEnglish.equals("Tue")|| dayEnglish.equals("mar")){
            return "Mar";
        }else if(dayEnglish.equals("Wed")|| dayEnglish.equals("mié")){
            return "Mie";
        }else if(dayEnglish.equals("Thu")|| dayEnglish.equals("jue")){
            return "Jue";
        }else if(dayEnglish.equals("Fri")|| dayEnglish.equals("vie")){
            return "Vie";
        }else if(dayEnglish.equals("Sat")|| dayEnglish.equals("sáb")){
            return "Sab";
        }else {
            return "juernes";
        }

    }



}
