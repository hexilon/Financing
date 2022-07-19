package com.hexon.financing.ui.noblemetal;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hexon.chartlib.stock.model.RealtimeQuotesEntity;
import com.hexon.financing.R;
import com.hexon.financing.databinding.FragmentNobleMetalBinding;
import com.hexon.financing.databinding.ItemNobleMetalBinding;
import com.hexon.mvvm.base.BaseFragment;
import com.hexon.mvvm.base.RecyclerViewBindingAdapter;
import com.hexon.repository.Constants;
import com.hexon.util.LogUtils;
import com.hexon.util.constant.TimeConstants;

/**
 * Copyright (C), 2022-2030
 * ClassName: NobleMetalFragment
 * Description:
 * Author: Hexon
 * Date: 2022/4/14 15:33
 * Version V1.0
 */
public class NobleMetalFragment extends BaseFragment<FragmentNobleMetalBinding, NobleMetalViewModel> {
    static final int SPAN_COUNT = 2;
    CountDownTimer mTimer;
    MaterialDialog mUpdateCycleSettingDlg;
    NobleMetalDataAdapter mAdapter;
    Constants.NobleMetalBank mBank;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_noble_metal;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public NobleMetalViewModel initViewModel() {
        return new ViewModelProvider(this).get(NobleMetalViewModel.class);
    }

    @Override
    public void initData() {
        mBinding.spinnerProducts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mBank = Constants.NobleMetalBank.values()[i];
                mViewModel.setNobleMetal(mBank);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mBinding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT));
        mAdapter = new NobleMetalDataAdapter();
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.btnIcbcSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showEditIcbcProductDialog();
            }
        });
        mBinding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        if (!mViewModel.isMarketOpen()) {
            mBinding.btnUpdate.setText(R.string.market_close);
        }
        mViewModel.getIsStartRefresh().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean update) {
                LogUtils.d("refresh:" + update);
                if (update) {
                    startCountDown();
                } else {
                    stopCountDown();
                }
            }
        });
    }

    private void stopCountDown() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void startCountDown() {
        stopCountDown();
        mTimer = new CountDownTimer(mViewModel.getUpdateCycle(), TimeConstants.SEC) {
            @Override
            public void onTick(long l) {
                if (isAdded()) {
                    mBinding.btnUpdate.setText(getString(
                            R.string.update_countdown, (l + 500) / TimeConstants.SEC - 1));
                }
            }

            @Override
            public void onFinish() {
                mBinding.btnUpdate.setText(R.string.refresh);
                mTimer = null;
            }
        };
        mTimer.start();
    }

    private class NobleMetalDataAdapter
            extends RecyclerViewBindingAdapter<MutableLiveData<RealtimeQuotesEntity>, ViewDataBinding> {
        public NobleMetalDataAdapter() {
            super(NobleMetalFragment.this.getContext());
            mItemList.addAll(mViewModel.getDataList());
        }

        @Override
        protected int getLayoutResId(int viewType) {
            return R.layout.item_noble_metal;
        }

        @Override
        protected void onBindItem(ViewDataBinding binding, MutableLiveData<RealtimeQuotesEntity> item) {
            if (binding instanceof ItemNobleMetalBinding) {
                item.observe(NobleMetalFragment.this.getActivity(), new Observer<RealtimeQuotesEntity>() {
                    @Override
                    public void onChanged(RealtimeQuotesEntity realtimeQuotesEntity) {
                        ((ItemNobleMetalBinding) ((ItemNobleMetalBinding) binding)).setEntity(realtimeQuotesEntity);
                    }
                });
            }
        }
    }
}
