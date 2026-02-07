package com.travelmeet.app.data.remote.model;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\bH\u00c6\u0003J\t\u0010\u0017\u001a\u00020\nH\u00c6\u0003J7\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\nH\u00c6\u0001J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001c\u001a\u00020\u001dH\u00d6\u0001J\t\u0010\u001e\u001a\u00020\nH\u00d6\u0001R\u0016\u0010\t\u001a\u00020\n8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u001c\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00058\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0016\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013\u00a8\u0006\u001f"}, d2 = {"Lcom/travelmeet/app/data/remote/model/WeatherResponse;", "", "main", "Lcom/travelmeet/app/data/remote/model/MainWeather;", "weather", "", "Lcom/travelmeet/app/data/remote/model/WeatherInfo;", "wind", "Lcom/travelmeet/app/data/remote/model/Wind;", "cityName", "", "(Lcom/travelmeet/app/data/remote/model/MainWeather;Ljava/util/List;Lcom/travelmeet/app/data/remote/model/Wind;Ljava/lang/String;)V", "getCityName", "()Ljava/lang/String;", "getMain", "()Lcom/travelmeet/app/data/remote/model/MainWeather;", "getWeather", "()Ljava/util/List;", "getWind", "()Lcom/travelmeet/app/data/remote/model/Wind;", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
public final class WeatherResponse {
    @org.jetbrains.annotations.NotNull
    @com.google.gson.annotations.SerializedName(value = "main")
    private final com.travelmeet.app.data.remote.model.MainWeather main = null;
    @org.jetbrains.annotations.NotNull
    @com.google.gson.annotations.SerializedName(value = "weather")
    private final java.util.List<com.travelmeet.app.data.remote.model.WeatherInfo> weather = null;
    @org.jetbrains.annotations.NotNull
    @com.google.gson.annotations.SerializedName(value = "wind")
    private final com.travelmeet.app.data.remote.model.Wind wind = null;
    @org.jetbrains.annotations.NotNull
    @com.google.gson.annotations.SerializedName(value = "name")
    private final java.lang.String cityName = null;
    
    @org.jetbrains.annotations.NotNull
    public final com.travelmeet.app.data.remote.model.WeatherResponse copy(@org.jetbrains.annotations.NotNull
    com.travelmeet.app.data.remote.model.MainWeather main, @org.jetbrains.annotations.NotNull
    java.util.List<com.travelmeet.app.data.remote.model.WeatherInfo> weather, @org.jetbrains.annotations.NotNull
    com.travelmeet.app.data.remote.model.Wind wind, @org.jetbrains.annotations.NotNull
    java.lang.String cityName) {
        return null;
    }
    
    @java.lang.Override
    public boolean equals(@org.jetbrains.annotations.Nullable
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override
    public int hashCode() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public java.lang.String toString() {
        return null;
    }
    
    public WeatherResponse(@org.jetbrains.annotations.NotNull
    com.travelmeet.app.data.remote.model.MainWeather main, @org.jetbrains.annotations.NotNull
    java.util.List<com.travelmeet.app.data.remote.model.WeatherInfo> weather, @org.jetbrains.annotations.NotNull
    com.travelmeet.app.data.remote.model.Wind wind, @org.jetbrains.annotations.NotNull
    java.lang.String cityName) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.travelmeet.app.data.remote.model.MainWeather component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.travelmeet.app.data.remote.model.MainWeather getMain() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.travelmeet.app.data.remote.model.WeatherInfo> component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<com.travelmeet.app.data.remote.model.WeatherInfo> getWeather() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.travelmeet.app.data.remote.model.Wind component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.travelmeet.app.data.remote.model.Wind getWind() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getCityName() {
        return null;
    }
}