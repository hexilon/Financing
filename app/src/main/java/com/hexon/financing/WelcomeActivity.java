package com.hexon.financing;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.hexon.commonui.PageIndicator;
import com.hexon.financing.databinding.ActivityWelcomeBinding;
import com.hexon.mvvm.base.BaseActivity;
import com.hexon.util.LogUtils;
import com.hexon.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends BaseActivity<ActivityWelcomeBinding, WelcomeViewModel>
        implements ViewPager.OnPageChangeListener {
    private List<View> mPageViews = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initParam() {
        UIUtils.setTransparentStatusBar(this, false);
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_welcome;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        int[] imgRes = getResources().getIntArray(R.array.guides);
        for (int i = 0; i < imgRes.length; i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_page_guide,null);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_guide);
            imageView.setImageResource(
                    getResources().obtainTypedArray(R.array.guides).getResourceId(i, 0));
            mPageViews.add(view);
            mBinding.pageIndicator.addMarker(
                    i, new PageIndicator.PageMarkerResources(),true);
        }

        mBinding.viewPager.setAdapter(new GuidePageAdapter());
        //mBinding.viewPager.setPageTransformer(true,new ());
        mBinding.viewPager.addOnPageChangeListener(this);
        mBinding.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        LogUtils.d("onPageScrolled position:" +position + " positionOffset:"
                + positionOffset + " positionOffsetPixels:" +positionOffsetPixels);
//        mBinding.pageIndicator.setActiveMarker(position);
//        //当为最后一个时，显示button，并隐藏圆点
//        if (position == mPageViews.size() - 1) {
//            mBinding.pageIndicator.setVisibility(View.GONE);
//            mBinding.btnStart.setVisibility(View.VISIBLE);
//            ObjectAnimator animator = ObjectAnimator.ofFloat(
//                    mBinding.btnStart, "alpha", 0f, 1f);
//            animator.setDuration(1000);
//            animator.start();
//        } else {
//            mBinding.pageIndicator.setVisibility(View.VISIBLE);
//            mBinding.btnStart.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onPageSelected(int position) {
        LogUtils.d("onPageSelected position:" +position);
        mBinding.pageIndicator.setActiveMarker(position);
        //当为最后一个时，显示button，并隐藏圆点
        if (position == mPageViews.size() - 1) {
            mBinding.pageIndicator.setVisibility(View.GONE);
            mBinding.btnStart.setVisibility(View.VISIBLE);
            mBinding.btnStart.animate().alpha(1f).setDuration(1000).start();
        } else {
            mBinding.pageIndicator.setVisibility(View.VISIBLE);
            mBinding.btnStart.setVisibility(View.GONE);
            mBinding.btnStart.animate().alpha(0f).setDuration(1000).start();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        LogUtils.d("onPageScrollStateChanged state:" +state);

    }

    private class GuidePageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return mPageViews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // return super.instantiateItem(container, position);
            container.addView(mPageViews.get(position));
            return  mPageViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // super.destroyItem(container, position, object);
            container.removeView(mPageViews.get(position));
        }
    }
}
