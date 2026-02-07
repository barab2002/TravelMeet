package com.travelmeet.app.ui.viewmodel;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010R\u001a\u0010\u0003\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u00050\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0012"}, d2 = {"Lcom/travelmeet/app/ui/viewmodel/WeatherViewModel;", "Landroidx/lifecycle/ViewModel;", "()V", "_weatherData", "Landroidx/lifecycle/MutableLiveData;", "Lcom/travelmeet/app/util/Resource;", "Lcom/travelmeet/app/data/remote/model/WeatherResponse;", "repository", "Lcom/travelmeet/app/data/repository/WeatherRepository;", "weatherData", "Landroidx/lifecycle/LiveData;", "getWeatherData", "()Landroidx/lifecycle/LiveData;", "fetchWeather", "", "latitude", "", "longitude", "app_debug"})
public final class WeatherViewModel extends androidx.lifecycle.ViewModel {
    private final com.travelmeet.app.data.repository.WeatherRepository repository = null;
    private final androidx.lifecycle.MutableLiveData<com.travelmeet.app.util.Resource<com.travelmeet.app.data.remote.model.WeatherResponse>> _weatherData = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.lifecycle.LiveData<com.travelmeet.app.util.Resource<com.travelmeet.app.data.remote.model.WeatherResponse>> weatherData = null;
    
    public WeatherViewModel() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<com.travelmeet.app.util.Resource<com.travelmeet.app.data.remote.model.WeatherResponse>> getWeatherData() {
        return null;
    }
    
    public final void fetchWeather(double latitude, double longitude) {
    }
}