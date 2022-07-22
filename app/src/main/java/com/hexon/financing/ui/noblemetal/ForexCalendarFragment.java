package com.hexon.financing.ui.noblemetal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.hexon.financing.R;
import com.hexon.financing.databinding.FragmentForexCalendarBinding;
import com.hexon.mvvm.base.BaseFragment;

public class ForexCalendarFragment extends BaseFragment<FragmentForexCalendarBinding, ForexCalendarViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_forex_calendar;
    }

    @Override
    public int initVariableId() {
        return 0;
    }
}
