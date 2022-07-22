package com.hexon.financing.ui.noblemetal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hexon.financing.R;
import com.hexon.financing.databinding.FragmentExchangeRateBinding;
import com.hexon.financing.databinding.ItemExchangeRateBinding;
import com.hexon.mvvm.base.BaseFragment;
import com.hexon.mvvm.base.RecyclerViewBindingAdapter;
import com.hexon.repository.model.ExchangeRate;
import com.hexon.util.LogUtils;

import java.text.DecimalFormat;

public class ExchangeRateFragment extends BaseFragment<FragmentExchangeRateBinding, ExchangeRateViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_exchange_rate;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public ExchangeRateViewModel initViewModel() {
        return new ViewModelProvider(this).get(ExchangeRateViewModel.class);
    }

    @Override
    public void initData() {
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recyclerView.setAdapter(new ExchangeRateAdapter(getContext()));
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), RecyclerView.VERTICAL);
        mBinding.recyclerView.addItemDecoration(decoration);
    }

    private class ExchangeRateAdapter extends RecyclerViewBindingAdapter
            <MutableLiveData<ExchangeRate>, ItemExchangeRateBinding> {
        public ExchangeRateAdapter(Context context) {
            super(context);
            mItemList.addAll(mViewModel.getDataList());
        }

        @Override
        protected int getLayoutResId(int viewType) {
            return R.layout.item_exchange_rate;
        }

        @Override
        protected void onBindItem(ItemExchangeRateBinding binding, MutableLiveData<ExchangeRate> item) {
            binding.setDecimalFormat(new DecimalFormat("0.0000"));
            item.observe(getViewLifecycleOwner(), new Observer<ExchangeRate>() {
                @Override
                public void onChanged(ExchangeRate exchangeRate) {
                    //LogUtils.d("exchangeRate " + exchangeRate);
                    binding.setExchangeRate(exchangeRate);
                }
            });
        }
    }
}
