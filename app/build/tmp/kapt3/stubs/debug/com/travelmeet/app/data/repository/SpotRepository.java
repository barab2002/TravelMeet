package com.travelmeet.app.data.repository;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000r\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\f\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJO\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00152\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00190\u00182\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001b2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0015H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001eJ\u0010\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\u0019H\u0002J\u001f\u0010\"\u001a\b\u0012\u0004\u0012\u00020#0\u00122\u0006\u0010$\u001a\u00020\u0015H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010%J\u0012\u0010&\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u00180\'J\u0016\u0010(\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00130\'2\u0006\u0010$\u001a\u00020\u0015J\u001a\u0010)\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u00180\'2\u0006\u0010*\u001a\u00020\u0015J\u0006\u0010+\u001a\u00020#J\u0006\u0010,\u001a\u00020#J\u001d\u0010-\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u00180\u0012H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010.J\u001f\u0010/\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\u0006\u0010$\u001a\u00020\u0015H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010%J9\u00100\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\u0006\u0010$\u001a\u00020\u00152\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00152\b\u00101\u001a\u0004\u0018\u00010\u0019H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u00102R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u00063"}, d2 = {"Lcom/travelmeet/app/data/repository/SpotRepository;", "", "spotDao", "Lcom/travelmeet/app/data/local/SpotDao;", "auth", "Lcom/google/firebase/auth/FirebaseAuth;", "firestore", "Lcom/google/firebase/firestore/FirebaseFirestore;", "storage", "Lcom/google/firebase/storage/FirebaseStorage;", "context", "Landroid/content/Context;", "(Lcom/travelmeet/app/data/local/SpotDao;Lcom/google/firebase/auth/FirebaseAuth;Lcom/google/firebase/firestore/FirebaseFirestore;Lcom/google/firebase/storage/FirebaseStorage;Landroid/content/Context;)V", "scope", "Lkotlinx/coroutines/CoroutineScope;", "snapshotListener", "Lcom/google/firebase/firestore/ListenerRegistration;", "addSpot", "Lcom/travelmeet/app/util/Resource;", "Lcom/travelmeet/app/data/local/entity/SpotEntity;", "title", "", "description", "imageUris", "", "Landroid/net/Uri;", "latitude", "", "longitude", "locationName", "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;DDLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "compressImage", "", "uri", "deleteSpot", "", "spotId", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllSpots", "Landroidx/lifecycle/LiveData;", "getSpotById", "getSpotsByUser", "userId", "startRealtimeSync", "stopRealtimeSync", "syncSpots", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toggleLike", "updateSpot", "newImageUri", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroid/net/Uri;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class SpotRepository {
    private final com.travelmeet.app.data.local.SpotDao spotDao = null;
    private final com.google.firebase.auth.FirebaseAuth auth = null;
    private final com.google.firebase.firestore.FirebaseFirestore firestore = null;
    private final com.google.firebase.storage.FirebaseStorage storage = null;
    private final android.content.Context context = null;
    private com.google.firebase.firestore.ListenerRegistration snapshotListener;
    private final kotlinx.coroutines.CoroutineScope scope = null;
    
    public SpotRepository(@org.jetbrains.annotations.NotNull
    com.travelmeet.app.data.local.SpotDao spotDao, @org.jetbrains.annotations.NotNull
    com.google.firebase.auth.FirebaseAuth auth, @org.jetbrains.annotations.NotNull
    com.google.firebase.firestore.FirebaseFirestore firestore, @org.jetbrains.annotations.NotNull
    com.google.firebase.storage.FirebaseStorage storage, @org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<java.util.List<com.travelmeet.app.data.local.entity.SpotEntity>> getAllSpots() {
        return null;
    }
    
    public final void startRealtimeSync() {
    }
    
    public final void stopRealtimeSync() {
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
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object syncSpots(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.travelmeet.app.util.Resource<java.util.List<com.travelmeet.app.data.local.entity.SpotEntity>>> continuation) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object addSpot(@org.jetbrains.annotations.NotNull
    java.lang.String title, @org.jetbrains.annotations.NotNull
    java.lang.String description, @org.jetbrains.annotations.NotNull
    java.util.List<? extends android.net.Uri> imageUris, double latitude, double longitude, @org.jetbrains.annotations.Nullable
    java.lang.String locationName, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.travelmeet.app.util.Resource<com.travelmeet.app.data.local.entity.SpotEntity>> continuation) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object updateSpot(@org.jetbrains.annotations.NotNull
    java.lang.String spotId, @org.jetbrains.annotations.NotNull
    java.lang.String title, @org.jetbrains.annotations.NotNull
    java.lang.String description, @org.jetbrains.annotations.Nullable
    android.net.Uri newImageUri, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.travelmeet.app.util.Resource<com.travelmeet.app.data.local.entity.SpotEntity>> continuation) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object deleteSpot(@org.jetbrains.annotations.NotNull
    java.lang.String spotId, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.travelmeet.app.util.Resource<kotlin.Unit>> continuation) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object toggleLike(@org.jetbrains.annotations.NotNull
    java.lang.String spotId, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.travelmeet.app.util.Resource<com.travelmeet.app.data.local.entity.SpotEntity>> continuation) {
        return null;
    }
    
    private final byte[] compressImage(android.net.Uri uri) {
        return null;
    }
}