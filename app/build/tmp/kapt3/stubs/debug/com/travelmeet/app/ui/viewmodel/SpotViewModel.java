package com.travelmeet.app.ui.viewmodel;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\r\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J>\u0010\u001f\u001a\u00020\n2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020!2\f\u0010#\u001a\b\u0012\u0004\u0012\u00020$0\r2\u0006\u0010%\u001a\u00020&2\u0006\u0010\'\u001a\u00020&2\b\u0010(\u001a\u0004\u0018\u00010!J\u000e\u0010)\u001a\u00020\n2\u0006\u0010*\u001a\u00020!J\u0016\u0010+\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\b0\u00102\u0006\u0010*\u001a\u00020!J\u001a\u0010,\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\r0\u00102\u0006\u0010-\u001a\u00020!J\b\u0010.\u001a\u00020\nH\u0014J\u0006\u0010/\u001a\u00020\nJ\u000e\u00100\u001a\u00020\n2\u0006\u0010*\u001a\u00020!J(\u00101\u001a\u00020\n2\u0006\u0010*\u001a\u00020!2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020!2\b\u00102\u001a\u0004\u0018\u00010$R\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R \u0010\f\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\r0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u001d\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\r0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0012R\u001d\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\u00070\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0012R\u001d\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0012R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u0004\u00a2\u0006\u0002\n\u0000R#\u0010\u001b\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\r0\u00070\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0012R\u001d\u0010\u001d\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0012\u00a8\u00063"}, d2 = {"Lcom/travelmeet/app/ui/viewmodel/SpotViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "_addSpotState", "Landroidx/lifecycle/MutableLiveData;", "Lcom/travelmeet/app/util/Resource;", "Lcom/travelmeet/app/data/local/entity/SpotEntity;", "_deleteSpotState", "", "_likeState", "_syncState", "", "_updateSpotState", "addSpotState", "Landroidx/lifecycle/LiveData;", "getAddSpotState", "()Landroidx/lifecycle/LiveData;", "allSpots", "getAllSpots", "deleteSpotState", "getDeleteSpotState", "likeState", "getLikeState", "repository", "Lcom/travelmeet/app/data/repository/SpotRepository;", "syncState", "getSyncState", "updateSpotState", "getUpdateSpotState", "addSpot", "title", "", "description", "imageUris", "Landroid/net/Uri;", "latitude", "", "longitude", "locationName", "deleteSpot", "spotId", "getSpotById", "getSpotsByUser", "userId", "onCleared", "syncSpots", "toggleLike", "updateSpot", "newImageUri", "app_debug"})
public final class SpotViewModel extends androidx.lifecycle.AndroidViewModel {
    private final com.travelmeet.app.data.repository.SpotRepository repository = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.lifecycle.LiveData<java.util.List<com.travelmeet.app.data.local.entity.SpotEntity>> allSpots = null;
    private final androidx.lifecycle.MutableLiveData<com.travelmeet.app.util.Resource<com.travelmeet.app.data.local.entity.SpotEntity>> _addSpotState = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.lifecycle.LiveData<com.travelmeet.app.util.Resource<com.travelmeet.app.data.local.entity.SpotEntity>> addSpotState = null;
    private final androidx.lifecycle.MutableLiveData<com.travelmeet.app.util.Resource<com.travelmeet.app.data.local.entity.SpotEntity>> _updateSpotState = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.lifecycle.LiveData<com.travelmeet.app.util.Resource<com.travelmeet.app.data.local.entity.SpotEntity>> updateSpotState = null;
    private final androidx.lifecycle.MutableLiveData<com.travelmeet.app.util.Resource<kotlin.Unit>> _deleteSpotState = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.lifecycle.LiveData<com.travelmeet.app.util.Resource<kotlin.Unit>> deleteSpotState = null;
    private final androidx.lifecycle.MutableLiveData<com.travelmeet.app.util.Resource<java.util.List<com.travelmeet.app.data.local.entity.SpotEntity>>> _syncState = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.lifecycle.LiveData<com.travelmeet.app.util.Resource<java.util.List<com.travelmeet.app.data.local.entity.SpotEntity>>> syncState = null;
    private final androidx.lifecycle.MutableLiveData<com.travelmeet.app.util.Resource<com.travelmeet.app.data.local.entity.SpotEntity>> _likeState = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.lifecycle.LiveData<com.travelmeet.app.util.Resource<com.travelmeet.app.data.local.entity.SpotEntity>> likeState = null;
    
    public SpotViewModel(@org.jetbrains.annotations.NotNull
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<java.util.List<com.travelmeet.app.data.local.entity.SpotEntity>> getAllSpots() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<com.travelmeet.app.util.Resource<com.travelmeet.app.data.local.entity.SpotEntity>> getAddSpotState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<com.travelmeet.app.util.Resource<com.travelmeet.app.data.local.entity.SpotEntity>> getUpdateSpotState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<com.travelmeet.app.util.Resource<kotlin.Unit>> getDeleteSpotState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<com.travelmeet.app.util.Resource<java.util.List<com.travelmeet.app.data.local.entity.SpotEntity>>> getSyncState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<com.travelmeet.app.util.Resource<com.travelmeet.app.data.local.entity.SpotEntity>> getLikeState() {
        return null;
    }
    
    public final void syncSpots() {
    }
    
    @java.lang.Override
    protected void onCleared() {
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<java.util.List<com.travelmeet.app.data.local.entity.SpotEntity>> getSpotsByUser(@org.jetbrains.annotations.NotNull
    java.lang.String userId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<com.travelmeet.app.data.local.entity.SpotEntity> getSpotById(@org.jetbrains.annotations.NotNull
    java.lang.String spotId) {
        return null;
    }
    
    public final void addSpot(@org.jetbrains.annotations.NotNull
    java.lang.String title, @org.jetbrains.annotations.NotNull
    java.lang.String description, @org.jetbrains.annotations.NotNull
    java.util.List<? extends android.net.Uri> imageUris, double latitude, double longitude, @org.jetbrains.annotations.Nullable
    java.lang.String locationName) {
    }
    
    public final void updateSpot(@org.jetbrains.annotations.NotNull
    java.lang.String spotId, @org.jetbrains.annotations.NotNull
    java.lang.String title, @org.jetbrains.annotations.NotNull
    java.lang.String description, @org.jetbrains.annotations.Nullable
    android.net.Uri newImageUri) {
    }
    
    public final void deleteSpot(@org.jetbrains.annotations.NotNull
    java.lang.String spotId) {
    }
    
    public final void toggleLike(@org.jetbrains.annotations.NotNull
    java.lang.String spotId) {
    }
}