package com.travelmeet.app.data.repository;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\'\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\f"}, d2 = {"Lcom/travelmeet/app/data/repository/WeatherRepository;", "", "weatherApiService", "Lcom/travelmeet/app/data/remote/WeatherApiService;", "(Lcom/travelmeet/app/data/remote/WeatherApiService;)V", "getCurrentWeather", "Lcom/travelmeet/app/util/Resource;", "Lcom/travelmeet/app/data/remote/model/WeatherResponse;", "latitude", "", "longitude", "(DDLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class WeatherRepository {
    private final com.travelmeet.app.data.remote.WeatherApiService weatherApiService = null;
    
    public WeatherRepository(@org.jetbrains.annotations.NotNull
    com.travelmeet.app.data.remote.WeatherApiService weatherApiService) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object getCurrentWeather(double latitude, double longitude, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.travelmeet.app.util.Resource<com.travelmeet.app.data.remote.model.WeatherResponse>> continuation) {
        return null;
    }
}