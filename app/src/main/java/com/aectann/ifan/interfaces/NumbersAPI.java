package com.aectann.ifan.interfaces;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NumbersAPI {

    // отримати fact
    @GET("{number}")
    Observable<String> getNumberFact(@Path(value = "number", encoded = true) String number);

}