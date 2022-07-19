package com.hexon.repository.base;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.hexon.util.LogUtils;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Copyright (C), 2020-2025
 * FileName    : NetworkBoundResource
 * Description :
 * Author      : Hexon
 * Date        : 2020/9/4 10:50
 * Version     : V1.0
 */
public abstract class NetworkBoundResource<ResultType, RequestType> {
    private Observable<Resource<ResultType>> mResult;

    @MainThread
    public NetworkBoundResource() {
        Observable<Resource<ResultType>> source;
        Resource<ResultType> dbResource = loadFromDb().map(Resource::loading)
                .subscribeOn(Schedulers.io()).blockingSingle();
        //LogUtils.d(dbResource.getData());
        if (shouldFetch(dbResource)) {
            source = createCall()
                    .subscribeOn(Schedulers.io())
                    .doOnNext(apiResponse -> saveCallResult(processResponse(apiResponse)))
                    .flatMap(apiResponse -> loadFromDb().toObservable().map(Resource::success))
                    .doOnError(this::onFetchFailed)
                    .onErrorResumeNext(t -> {
                        return loadFromDb()
                                .toObservable()
                                .map(data -> Resource.error(t.getMessage(), data));
                    })
                    .observeOn(AndroidSchedulers.mainThread());
        } else {
            source = loadFromDb()
                    .toObservable()
                    .subscribeOn(Schedulers.io())
                    .map(Resource::success);
        }

        mResult = source;
        //result = Observable.concat(loadFromDb().toObservable()
        //        .subscribeOn(Schedulers.io()).map(Resource::loading).take(1), source);
    }

    public Observable<Resource<ResultType>> asObservable() {
        return mResult;
    }

    protected void onFetchFailed(Throwable t) {
        LogUtils.e(t, "onFetchFailed");
    }

    @WorkerThread
    protected RequestType processResponse(ApiResponse<RequestType> response) {
        return response.body;
    }

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    @MainThread
    protected abstract boolean shouldFetch(Resource<ResultType> dbResource);

    @NonNull
    @MainThread
    protected abstract Flowable<ResultType> loadFromDb();

    @NonNull
    @MainThread
    protected abstract Observable<ApiResponse<RequestType>> createCall();
}
