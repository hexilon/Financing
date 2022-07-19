package com.hexon.repository.cache;

import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Copyright (C), 2022-2030
 * ClassName: DiskCacheManagerTest
 * Description:
 * Author: Hexon
 * Date: 2022/6/22 16:33
 * Version V1.0
 */
@RunWith(AndroidJUnit4.class)
public class DiskCacheManagerTest {
    DiskCacheManager mManager;

    @Before
    public void setUp() throws Exception {
        getInstance();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getInstance() {
        mManager = DiskCacheManager.getInstance(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void getDiskCacheDir() {
    }

    @Test
    public void hashKeyForDiskCache() {
    }

    @Test
    public void getData() {
        String testKey = "test";
        String testData = "test";
        mManager.putData(testKey, testData);
        String result = mManager.getData(testKey, String.class);
        assertTrue(result.equals(testData));
    }

    @Test
    public void putData() {
    }

    @Test
    public void getList() {
    }

    @Test
    public void putList() {
    }
}