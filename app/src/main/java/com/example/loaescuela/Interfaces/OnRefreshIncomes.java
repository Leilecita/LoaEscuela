package com.example.loaescuela.Interfaces;

import com.example.loaescuela.network.models.ReportIncome;

public interface OnRefreshIncomes {

    void onRefreshListIncomes(ReportIncome r);
    void onRefreshAmountIncomes();
}
