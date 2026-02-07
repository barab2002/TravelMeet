package com.travelmeet.app.data.remote;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\bf\u0018\u0000 \f2\u00020\u0001:\u0001\fJ?\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\u00062\b\b\u0001\u0010\b\u001a\u00020\t2\b\b\u0003\u0010\n\u001a\u00020\tH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u000b\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\r"}, d2 = {"Lcom/travelmeet/app/data/remote/WeatherApiService;", "", "getCurrentWeather", "Lretrofit2/Response;", "Lcom/travelmeet/app/data/remote/model/WeatherResponse;", "latitude", "", "longitude", "apiKey", "", "units", "(DDLjava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "app_debug"})
public abstract interface WeatherApiService {
    @org.jetbrains.annotations.NotNull
    public static final com.travelmeet.app.data.remote.WeatherApiService.Companion Companion = null;
    
    @org.jetbrains.annotations.Nullable
    @retrofit2.http.GET(value = "weather")
    public abstract java.lang.Object getCurrentWeather(@retrofit2.http.Query(value = "lat")
    double latitude, @retrofit2.http.Query(value = "lon")
    double longitude, @org.jetbrains.annotations.NotNull
    @retrofit2.http.Query(value = "appid")
    java.lang.String apiKey, @org.jetbrains.annotations.NotNull
    @retrofit2.http.Query(value = "units")
    java.lang.String units, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super retrofit2.Response<com.travelmeet.app.data.remote.model.WeatherResponse>> continuation);
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 3)
    public final class DefaultImpls {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0005\u001a\u00020\u0006R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/travelmeet/app/data/remote/WeatherApiService$Companion;", "", "()V", "BASE_URL", "", "create", "Lcom/travelmeet/app/data/remote/WeatherApiService;", "app_debug"})
    public static final class Companion {
        private static final java.lang.String BASE_URL = "https://api.openweathermap.org/data/2.5/";
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.travelmeet.app.data.remote.WeatherApiService create() {
            return null;
        }
    }
}