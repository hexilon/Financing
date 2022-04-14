package com.hexon.financing.ui.stock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.hexon.financing.BR;
import com.hexon.financing.R;
import com.hexon.financing.databinding.FragmentStockBinding;
import com.hexon.mvvm.base.BaseFragment;

/**
 * Copyright (C), 2022-2030
 * ClassName: StockFragment
 * Description:
 * Author: Hexon
 * Date: 2022/4/14 15:33
 * Version V1.0
 */
public class StockFragment extends BaseFragment<FragmentStockBinding, StockViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_stock;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }
}
