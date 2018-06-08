package com.mshop.internal.verolocator.presenter;

import com.mshop.internal.verolocator.BuildConfig;
import com.mshop.internal.verolocator.data.MessageCode;
import com.mshop.internal.verolocator.repository.PositionRepository;
import com.mshop.internal.verolocator.repository.bodies.RefreshLocationBody;
import com.mshop.internal.verolocator.repository.responses.BasicResponseDto;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by victor on 8/6/18.
 * Mshop Spain.
 */
public class PositionPresenter {
    private static final int TIME_OUT = 30000;

    private PositionView positionView;
    private Retrofit restAdapter;
    private PositionRepository positionRepository;
    private Scheduler mainThreadScheduler;
    private Scheduler subscriberScheduler;


    public PositionPresenter(Scheduler mainThreadScheduler, Scheduler subscriberScheduler) {
        this.mainThreadScheduler = mainThreadScheduler;
        this.subscriberScheduler = subscriberScheduler;

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
                .addInterceptor(logging)
                .build();


        restAdapter = new Retrofit.Builder()
//                .baseUrl("http://192.168.1.59:8080")  -> local Vero
                .baseUrl("https://setex.m-shop.mobi")  //  -> desarrollo
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        positionRepository = restAdapter.create(PositionRepository.class);
    }



    // -------------------------------------------------------------------------------------------------------------------
    // --------------------------------------------- ACTIVITY VIEW INTERFACE ---------------------------------------------
    public  interface PositionView {

        void onPositionUpdated(BasicResponseDto any);

        void onPositionServerError(MessageCode any);

        void onPositionError(Throwable e);
    }



    // -------------------------------------------------------------------------------------------------------------------
    // ------------------------------------------------- PRESENTER METHODS -----------------------------------------------
    public void setPositionView(PositionView positionView) {
        this.positionView = positionView;
    }

    public void callToRefreshLocation(RefreshLocationBody body) {
        positionRepository.getData(body)
                .observeOn(mainThreadScheduler)
                .subscribeOn(subscriberScheduler)
                .subscribe(new Observer<BasicResponseDto>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BasicResponseDto basicResponseDto) {
                        if (positionView != null) {
                            if (basicResponseDto.getErrors().isEmpty()) {
                                positionView.onPositionUpdated(basicResponseDto);
                            } else {
                                positionView.onPositionServerError(basicResponseDto.getErrors().get(0));
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (positionView != null) {
                            positionView.onPositionError(e);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void onDestroy() {
        this.positionView = null;
    }

}
