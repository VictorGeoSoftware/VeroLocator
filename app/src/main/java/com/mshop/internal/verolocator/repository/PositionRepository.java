package com.mshop.internal.verolocator.repository;

import com.mshop.internal.verolocator.repository.bodies.RefreshLocationBody;
import com.mshop.internal.verolocator.repository.responses.BasicResponseDto;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by victor on 8/6/18.
 * Mshop Spain.
 */
public interface PositionRepository {
    @Headers({ "Content-Type: application/json;charset=UTF-8"})

    @POST("/setex/services/userApp/refreshLocation")
    Observable<BasicResponseDto> getData(@Body RefreshLocationBody body);
}
