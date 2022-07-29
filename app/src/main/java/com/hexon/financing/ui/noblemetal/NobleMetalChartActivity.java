package com.hexon.financing.ui.noblemetal;

import static com.hexon.financing.common.Constants.INTENT_EXTRA_BANK;
import static com.hexon.financing.common.Constants.INTENT_EXTRA_METAL_TYPE;

import android.content.Intent;
import android.os.Bundle;


import com.hexon.financing.BR;
import com.hexon.financing.R;
import com.hexon.financing.base.ChartActivity;
import com.hexon.financing.databinding.ActivityNobleMetalChartBinding;
import com.hexon.repository.Constants;
import com.hexon.util.LogUtils;
import com.hexon.util.UIUtils;

/**
 * Copyright (C), 2022-2040
 * ClassName: NobleMetalChartActivity
 * Description:
 * Author: Hexon
 * Date: 2022/7/29 11:35
 * Version V1.0
 */
public class NobleMetalChartActivity extends ChartActivity<ActivityNobleMetalChartBinding, NobleMetalViewModel> {
    private Constants.NobleMetalBank mBank;
    private Constants.MetalType mCurrMetalType;

    @Override
    public void initParam() {
        Intent intent = getIntent();
        mBank = (Constants.NobleMetalBank) intent.getSerializableExtra(INTENT_EXTRA_BANK);
        mCurrMetalType = (Constants.MetalType) intent.getSerializableExtra(INTENT_EXTRA_METAL_TYPE);
        LogUtils.d("initParam mCurrMetalType:" + mCurrMetalType);
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        UIUtils.setTransparentStatusBar(this, false);
        return R.layout.activity_noble_metal_chart;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        if (mBank == Constants.NobleMetalBank.ICBC) {
            mBinding.tvBank.setText(R.string.icbc_noble_metal);
        }
        mViewModel.setFetchDataType(NobleMetalViewModel.FetchDataType.ONE_DATA_REALTIME_DETAIL);
        mViewModel.setNobleMetalType(mCurrMetalType);
        mViewModel.setNobleMetal(mBank);
    }
}
