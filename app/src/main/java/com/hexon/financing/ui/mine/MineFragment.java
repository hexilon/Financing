package com.hexon.financing.ui.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.hexon.financing.BR;
import com.hexon.financing.R;
import com.hexon.financing.databinding.FragmentMineBinding;
import com.hexon.mvvm.base.BaseFragment;

/**
 * Copyright (C), 2022-2030
 * ClassName: MineFragment
 * Description:
 * Author: Hexon
 * Date: 2022/4/14 15:33
 * Version V1.0
 */
public class MineFragment extends BaseFragment<FragmentMineBinding, MineViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_mine;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }
}
