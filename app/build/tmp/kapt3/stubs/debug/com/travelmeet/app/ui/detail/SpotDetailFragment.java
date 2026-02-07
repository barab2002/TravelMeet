package com.travelmeet.app.ui.detail;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u00002\u00020\u00012\u00020\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J\u0010\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u0010H\u0002J\b\u0010!\u001a\u00020\u001fH\u0002J\b\u0010\"\u001a\u00020\u001fH\u0002J\b\u0010#\u001a\u00020\u001fH\u0002J$\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\'2\b\u0010(\u001a\u0004\u0018\u00010)2\b\u0010*\u001a\u0004\u0018\u00010+H\u0016J\b\u0010,\u001a\u00020\u001fH\u0016J\u0010\u0010-\u001a\u00020\u001f2\u0006\u0010.\u001a\u00020\u0012H\u0016J\u001a\u0010/\u001a\u00020\u001f2\u0006\u00100\u001a\u00020%2\b\u0010*\u001a\u0004\u0018\u00010+H\u0016J\b\u00101\u001a\u00020\u001fH\u0002J\b\u00102\u001a\u00020\u001fH\u0002R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0006\u001a\u00020\u00078BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\n\u0010\u000b\u001a\u0004\b\b\u0010\tR\u0014\u0010\f\u001a\u00020\u00058BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\r\u0010\u000eR\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0013\u001a\u00020\u00148BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0017\u0010\u0018\u001a\u0004\b\u0015\u0010\u0016R\u001b\u0010\u0019\u001a\u00020\u001a8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001d\u0010\u0018\u001a\u0004\b\u001b\u0010\u001c\u00a8\u00063"}, d2 = {"Lcom/travelmeet/app/ui/detail/SpotDetailFragment;", "Landroidx/fragment/app/Fragment;", "Lcom/google/android/gms/maps/OnMapReadyCallback;", "()V", "_binding", "Lcom/travelmeet/app/databinding/FragmentSpotDetailBinding;", "args", "Lcom/travelmeet/app/ui/detail/SpotDetailFragmentArgs;", "getArgs", "()Lcom/travelmeet/app/ui/detail/SpotDetailFragmentArgs;", "args$delegate", "Landroidx/navigation/NavArgsLazy;", "binding", "getBinding", "()Lcom/travelmeet/app/databinding/FragmentSpotDetailBinding;", "currentSpot", "Lcom/travelmeet/app/data/local/entity/SpotEntity;", "googleMap", "Lcom/google/android/gms/maps/GoogleMap;", "spotViewModel", "Lcom/travelmeet/app/ui/viewmodel/SpotViewModel;", "getSpotViewModel", "()Lcom/travelmeet/app/ui/viewmodel/SpotViewModel;", "spotViewModel$delegate", "Lkotlin/Lazy;", "weatherViewModel", "Lcom/travelmeet/app/ui/viewmodel/WeatherViewModel;", "getWeatherViewModel", "()Lcom/travelmeet/app/ui/viewmodel/WeatherViewModel;", "weatherViewModel$delegate", "bindSpotData", "", "spot", "observeDeleteState", "observeSpot", "observeWeather", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onDestroyView", "onMapReady", "map", "onViewCreated", "view", "setupClickListeners", "setupMap", "app_debug"})
public final class SpotDetailFragment extends androidx.fragment.app.Fragment implements com.google.android.gms.maps.OnMapReadyCallback {
    private com.travelmeet.app.databinding.FragmentSpotDetailBinding _binding;
    private final androidx.navigation.NavArgsLazy args$delegate = null;
    private final kotlin.Lazy spotViewModel$delegate = null;
    private final kotlin.Lazy weatherViewModel$delegate = null;
    private com.google.android.gms.maps.GoogleMap googleMap;
    private com.travelmeet.app.data.local.entity.SpotEntity currentSpot;
    
    public SpotDetailFragment() {
        super();
    }
    
    private final com.travelmeet.app.databinding.FragmentSpotDetailBinding getBinding() {
        return null;
    }
    
    private final com.travelmeet.app.ui.detail.SpotDetailFragmentArgs getArgs() {
        return null;
    }
    
    private final com.travelmeet.app.ui.viewmodel.SpotViewModel getSpotViewModel() {
        return null;
    }
    
    private final com.travelmeet.app.ui.viewmodel.WeatherViewModel getWeatherViewModel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    @java.lang.Override
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override
    public void onViewCreated(@org.jetbrains.annotations.NotNull
    android.view.View view, @org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupClickListeners() {
    }
    
    private final void setupMap() {
    }
    
    @java.lang.Override
    public void onMapReady(@org.jetbrains.annotations.NotNull
    com.google.android.gms.maps.GoogleMap map) {
    }
    
    private final void observeSpot() {
    }
    
    private final void bindSpotData(com.travelmeet.app.data.local.entity.SpotEntity spot) {
    }
    
    private final void observeWeather() {
    }
    
    private final void observeDeleteState() {
    }
    
    @java.lang.Override
    public void onDestroyView() {
    }
}