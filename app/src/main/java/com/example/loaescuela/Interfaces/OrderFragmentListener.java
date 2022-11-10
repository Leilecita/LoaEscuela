package com.example.loaescuela.Interfaces;


import com.example.loaescuela.network.models.ReportStudentAsistItem;

public interface OrderFragmentListener {

    void startNewActivityFragment(ReportStudentAsistItem student, Integer pos, String category);

}
