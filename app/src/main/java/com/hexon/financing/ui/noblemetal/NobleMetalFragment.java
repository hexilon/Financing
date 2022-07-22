package com.hexon.financing.ui.noblemetal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;

import com.hexon.financing.R;
import com.hexon.financing.databinding.FragmentNobleMetalBinding;
import com.hexon.mvvm.base.BaseFragment;

import java.util.ArrayList;

/**
 * Copyright (C), 2022-2030
 * ClassName: NobleMetalFragment
 * Description:
 * Author: Hexon
 * Date: 2022/4/14 15:33
 * Version V1.0
 */
public class NobleMetalFragment extends BaseFragment<FragmentNobleMetalBinding, NobleMetalViewModel> {
    private ArrayList<Fragment> mFragList;
    ArrayList<String> mTitleList = new ArrayList<>();

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
        mFragList = new ArrayList<>();
        mFragList.add(new RealtimeQuotesFragment());
        mTitleList.add(getResources().getString(R.string.realtime_quotes));

        mFragList.add(new ExchangeRateFragment());
        mTitleList.add(getResources().getString(R.string.exchange_rate));

        mFragList.add(new ForexCalendarFragment());
        mTitleList.add(getResources().getString(R.string.forex_calendar));

        mFragList.add(new LiveNewsFragment());
        mTitleList.add(getResources().getString(R.string.live_news));

        //mBinding.pager.setOffscreenPageLimit(2);
        mBinding.viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public int getCount() {
                return mFragList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragList.get(position);
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitleList.get(position);
            }
        });

        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager, true);
    }
}
