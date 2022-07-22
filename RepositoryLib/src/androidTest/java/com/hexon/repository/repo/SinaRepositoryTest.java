package com.hexon.repository.repo;

import static org.junit.Assert.*;

import android.app.Instrumentation;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

import com.hexon.chartlib.stock.model.HistoryEntity;
import com.hexon.mvvm.base.BaseApplication;
import com.hexon.repository.Constants;
import com.hexon.repository.base.Resource;
import com.hexon.repository.base.Status;
import com.hexon.util.LogUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Copyright (C), 2022-2040
 * ClassName: SinaRepositoryTest
 * Description:
 * Author: Hexon
 * Date: 2022/7/21 15:45
 * Version V1.0
 */
public class SinaRepositoryTest {
    BaseApplication mApp;
    SinaRepository mRepo;
    @Before
    public void setUp() throws Exception {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Context appContext = instrumentation.getTargetContext();
        mApp = (BaseApplication) instrumentation.newApplication(BaseApplication.class, appContext);
        mRepo = SinaRepository.getInstance(mApp);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getInstance() {
        SinaRepository repo = SinaRepository.getInstance(mApp);
        assertSame(mRepo, repo);
    }

    @Test
    public void checkMarketOpen() {
    }

    @Test
    public void getForexData() {
        Resource<List<HistoryEntity>> listResource =
                mRepo.getForexData(Constants.ForexType.DINIW, Constants.PeriodType.DAY).blockingSingle();
        List<HistoryEntity> entityList = listResource.getData();
        LogUtils.d("entityList " + entityList);
        if (listResource.getStatus() == Status.SUCCESS) {
            assertNotNull(entityList);
        } else if (listResource.getStatus() == Status.ERROR) {
            LogUtils.e(listResource.getMessage());
        } else {
            LogUtils.w("Loading...");
        }
    }

    @Test
    public void getRealtimeDataForChart() {
    }

    @Test
    public void getHistoryDataForChart() {
    }
}