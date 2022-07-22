package com.hexon.repository.base;

import com.hexon.chartlib.stock.model.HistoryEntity;
import com.hexon.chartlib.stock.model.RealtimeEntity;
import com.hexon.repository.Constants;

import java.util.List;

import io.reactivex.Observable;

/**
 * Copyright (C), 2022-2040
 * ClassName: FinancingRepository
 * Description:
 * Author: Hexon
 * Date: 2022/7/20 16:24
 * Version V1.0
 */
public interface IFinancingRepository<T> {
    Boolean checkMarketOpen();

    Observable<Resource<List<RealtimeEntity>>> getRealtimeDataForChart(
            T type);

    Observable<Resource<List<HistoryEntity>>> getHistoryDataForChart(
            T type, Constants.PeriodType period);
}
