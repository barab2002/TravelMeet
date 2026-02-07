package com.travelmeet.app.data.local;

import java.lang.System;

@androidx.room.Dao
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\f\bg\u0018\u00002\u00020\u0001J\u0011\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0004J\u0019\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\nH\'J\u0018\u0010\r\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\n2\u0006\u0010\u0006\u001a\u00020\u0007H\'J\u001b\u0010\u000e\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\bJ\u001c\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\n2\u0006\u0010\u0010\u001a\u00020\u0007H\'J\u0019\u0010\u0011\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\fH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0013J\u001f\u0010\u0014\u001a\u00020\u00032\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0016J\u0019\u0010\u0017\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\fH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0013\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0018"}, d2 = {"Lcom/travelmeet/app/data/local/SpotDao;", "", "deleteAll", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteSpotById", "spotId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllSpots", "Landroidx/lifecycle/LiveData;", "", "Lcom/travelmeet/app/data/local/entity/SpotEntity;", "getSpotById", "getSpotByIdSync", "getSpotsByUser", "userId", "insertSpot", "spot", "(Lcom/travelmeet/app/data/local/entity/SpotEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertSpots", "spots", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateSpot", "app_debug"})
public abstract interface SpotDao {
    
    @org.jetbrains.annotations.NotNull
    @androidx.room.Query(value = "SELECT * FROM spots ORDER BY timestamp DESC")
    public abstract androidx.lifecycle.LiveData<java.util.List<com.travelmeet.app.data.local.entity.SpotEntity>> getAllSpots();
    
    @org.jetbrains.annotations.NotNull
    @androidx.room.Query(value = "SELECT * FROM spots WHERE userId = :userId ORDER BY timestamp DESC")
    public abstract androidx.lifecycle.LiveData<java.util.List<com.travelmeet.app.data.local.entity.SpotEntity>> getSpotsByUser(@org.jetbrains.annotations.NotNull
    java.lang.String userId);
    
    @org.jetbrains.annotations.NotNull
    @androidx.room.Query(value = "SELECT * FROM spots WHERE id = :spotId")
    public abstract androidx.lifecycle.LiveData<com.travelmeet.app.data.local.entity.SpotEntity> getSpotById(@org.jetbrains.annotations.NotNull
    java.lang.String spotId);
    
    @org.jetbrains.annotations.Nullable
    @androidx.room.Query(value = "SELECT * FROM spots WHERE id = :spotId")
    public abstract java.lang.Object getSpotByIdSync(@org.jetbrains.annotations.NotNull
    java.lang.String spotId, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.travelmeet.app.data.local.entity.SpotEntity> continuation);
    
    @org.jetbrains.annotations.Nullable
    @androidx.room.Insert(onConflict = 1)
    public abstract java.lang.Object insertSpot(@org.jetbrains.annotations.NotNull
    com.travelmeet.app.data.local.entity.SpotEntity spot, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> continuation);
    
    @org.jetbrains.annotations.Nullable
    @androidx.room.Insert(onConflict = 1)
    public abstract java.lang.Object insertSpots(@org.jetbrains.annotations.NotNull
    java.util.List<com.travelmeet.app.data.local.entity.SpotEntity> spots, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> continuation);
    
    @org.jetbrains.annotations.Nullable
    @androidx.room.Update
    public abstract java.lang.Object updateSpot(@org.jetbrains.annotations.NotNull
    com.travelmeet.app.data.local.entity.SpotEntity spot, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> continuation);
    
    @org.jetbrains.annotations.Nullable
    @androidx.room.Query(value = "DELETE FROM spots WHERE id = :spotId")
    public abstract java.lang.Object deleteSpotById(@org.jetbrains.annotations.NotNull
    java.lang.String spotId, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> continuation);
    
    @org.jetbrains.annotations.Nullable
    @androidx.room.Query(value = "DELETE FROM spots")
    public abstract java.lang.Object deleteAll(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super kotlin.Unit> continuation);
}