package com.hexon.financing.ui.fund;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;

import com.hexon.financing.R;
import com.hexon.financing.databinding.FragmentFundBinding;
import com.hexon.mvvm.base.BaseFragment;

/**
 * Copyright (C), 2022-2030
 * ClassName: FundFragment
 * Description:
 * Author: Hexon
 * Date: 2022/4/14 15:33
 * Version V1.0
 */
public class FundFragment extends BaseFragment<FragmentFundBinding, FundViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_fund;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }
}
