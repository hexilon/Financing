package com.hexon.financing.ui.noblemetal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.hexon.financing.R;
import com.hexon.financing.databinding.FragmentLiveNewsBinding;
import com.hexon.mvvm.base.BaseFragment;

public class LiveNewsFragment extends BaseFragment<FragmentLiveNewsBinding, LiveNewsViewModel> {
    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_live_news;
    }

    @Override
    public int initVariableId() {
        return 0;
    }
}
