package com.example.timeapi;

import com.example.timeapi.datas.DataClassApi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {
    @GET("/timingsByCity")
    Call <DataClassApi> getPost(@Query("city") String city, @Query("country") String country);
}
