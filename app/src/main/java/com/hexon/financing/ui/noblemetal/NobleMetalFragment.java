package com.hexon.financing.ui.noblemetal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;

import com.hexon.financing.R;
import com.hexon.financing.databinding.FragmentNobleMetalBinding;
import com.hexon.mvvm.base.BaseFragment;

/**
 * Copyright (C), 2022-2030
 * ClassName: NobleMetalFragment
 * Description:
 * Author: Hexon
 * Date: 2022/4/14 15:33
 * Version V1.0
 */
public class NobleMetalFragment extends BaseFragment<FragmentNobleMetalBinding, NobleMetalViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_noble_metal;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }
}
