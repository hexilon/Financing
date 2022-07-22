package com.hexon.financing.ui.noblemetal;

import static com.hexon.financing.common.Constants.INTENT_EXTRA_BANK;
import static com.hexon.financing.common.Constants.INTENT_EXTRA_METAL_TYPE;
import static com.hexon.repository.Constants.MOBILE_UPDATE_PERIOD;
import static com.hexon.repository.Constants.WIFI_UPDATE_PERIOD;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hexon.chartlib.stock.model.RealtimeQuotesEntity;
import com.hexon.financing.BR;
import com.hexon.financing.R;
import com.hexon.financing.databinding.FragmentRealtimeQuotesBinding;
import com.hexon.financing.databinding.ItemRealtimeQuotesBinding;
import com.hexon.mvvm.base.BaseFragment;
import com.hexon.mvvm.base.RecyclerViewBindingAdapter;
import com.hexon.repository.Constants;
import com.hexon.util.LogUtils;
import com.hexon.util.constant.TimeConstants;

public class RealtimeQuotesFragment extends BaseFragment<FragmentRealtimeQuotesBinding, RealtimeQuotesViewModel> {
    static final int SPAN_COUNT = 2;
    CountDownTimer mTimer;
    MaterialDialog mUpdatePeriodSettingDlg;
    RealtimeQuotesAdapter mAdapter;
    Constants.NobleMetalBank mBank;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_realtime_quotes;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public RealtimeQuotesViewModel initViewModel() {
        return new ViewModelProvider(this).get(RealtimeQuotesViewModel.class);
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
        mAdapter = new RealtimeQuotesAdapter();
        mBinding.recyclerView.setAdapter(mAdapter);
        mBinding.btnIcbcSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUpdateSettingDialog();
            }
        });
        mBinding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.updateOnce();
            }
        });
        if (!mViewModel.isMarketOpen()) {
            mBinding.btnUpdate.setText(R.string.market_close);
        }
        mViewModel.getIsStartRefresh().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean update) {
                //LogUtils.d("refresh:" + update);
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
        mTimer = new CountDownTimer(mViewModel.getUpdatePeriod(), TimeConstants.SEC) {
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

    private void showUpdateSettingDialog() {
        mUpdatePeriodSettingDlg = new MaterialDialog.Builder(getContext())
                .title(R.string.update_period)
                .customView(R.layout.dialog_update_period_settings, true)
                .autoDismiss(false)
                .positiveText(android.R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String wifiPeriod =
                                ((EditText) dialog.getCustomView().findViewById(R.id.et_wifi))
                                        .getText().toString();
                        String mobilePeriod =
                                ((EditText) dialog.getCustomView().findViewById(R.id.et_mobile))
                                        .getText().toString();
                        if (!wifiPeriod.isEmpty()) {
                            mViewModel.setWifiUpdatePeriod(Long.parseLong(wifiPeriod) * TimeConstants.SEC);
                        }
                        if (!mobilePeriod.isEmpty()) {
                            mViewModel.setMobileUpdatePeriod(Long.parseLong(mobilePeriod) * TimeConstants.SEC);
                        }
                        dialog.dismiss();
                    }
                })
                .negativeText(android.R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .neutralText(R.string.restore)
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        View customView = dialog.getCustomView();
                        if (customView != null) {
                            ((EditText) customView.findViewById(R.id.et_wifi))
                                    .setText("" + WIFI_UPDATE_PERIOD / TimeConstants.SEC);
                            ((EditText) customView.findViewById(R.id.et_mobile))
                                    .setText("" + MOBILE_UPDATE_PERIOD / TimeConstants.SEC);
                        }
                    }
                })
                .build();
        View customView = mUpdatePeriodSettingDlg.getCustomView();
        if (customView != null) {
            ((EditText) customView.findViewById(R.id.et_wifi))
                    .setText("" + mViewModel.getWifiUpdatePeriod() / TimeConstants.SEC);
            ((EditText) customView.findViewById(R.id.et_mobile))
                    .setText("" + mViewModel.getMobileUpdatePeriod() / TimeConstants.SEC);
        }
        mUpdatePeriodSettingDlg.show();
    }

    private class RealtimeQuotesAdapter
            extends RecyclerViewBindingAdapter<MutableLiveData<RealtimeQuotesEntity>, ViewDataBinding> {
        public RealtimeQuotesAdapter() {
            super(RealtimeQuotesFragment.this.getContext());
            mItemList.addAll(mViewModel.getDataList());
        }

        @Override
        protected int getLayoutResId(int viewType) {
            return R.layout.item_realtime_quotes;
        }

        @Override
        protected void onBindItem(ViewDataBinding binding, MutableLiveData<RealtimeQuotesEntity> item) {
            if (binding instanceof ItemRealtimeQuotesBinding) {
                ItemRealtimeQuotesBinding itemBinding = (ItemRealtimeQuotesBinding) binding;
                item.observe(RealtimeQuotesFragment.this.getActivity(), new Observer<RealtimeQuotesEntity>() {
                    @Override
                    public void onChanged(RealtimeQuotesEntity realtimeQuotesEntity) {
                        itemBinding.setEntity(realtimeQuotesEntity);
                    }
                });
                itemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Constants.MetalType type = Constants.MetalType.values()[itemBinding.getEntity().mType];
                        LogUtils.d("onClick: " + type);
                        Intent intent = new Intent();
                        //intent.setClass(view.getContext(), ProductMainActivity.class);
                        intent.putExtra(INTENT_EXTRA_BANK, mBank);
                        intent.putExtra(INTENT_EXTRA_METAL_TYPE, type);
                        //view.getContext().startActivity(intent);
                    }
                });
            }
        }
    }
}
