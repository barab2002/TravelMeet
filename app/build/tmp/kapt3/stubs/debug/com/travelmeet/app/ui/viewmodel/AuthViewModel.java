package com.travelmeet.app.ui.viewmodel;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cJ\u0016\u0010\u001d\u001a\u00020\u001a2\u0006\u0010\u001e\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020\u001cJ\u0006\u0010 \u001a\u00020\u001aJ\u001e\u0010!\u001a\u00020\u001a2\u0006\u0010\u001e\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020\u001c2\u0006\u0010\"\u001a\u00020\u001cJ\u001a\u0010#\u001a\u00020\u001a2\b\u0010\"\u001a\u0004\u0018\u00010\u001c2\b\u0010$\u001a\u0004\u0018\u00010%R\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0013\u0010\u0010\u001a\u0004\u0018\u00010\b8F\u00a2\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012R\u001d\u0010\u0013\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000fR\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\u00070\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u000f\u00a8\u0006&"}, d2 = {"Lcom/travelmeet/app/ui/viewmodel/AuthViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "_authState", "Landroidx/lifecycle/MutableLiveData;", "Lcom/travelmeet/app/util/Resource;", "Lcom/google/firebase/auth/FirebaseUser;", "_profileUpdateState", "_userData", "Lcom/travelmeet/app/data/local/entity/UserEntity;", "authState", "Landroidx/lifecycle/LiveData;", "getAuthState", "()Landroidx/lifecycle/LiveData;", "currentUser", "getCurrentUser", "()Lcom/google/firebase/auth/FirebaseUser;", "profileUpdateState", "getProfileUpdateState", "repository", "Lcom/travelmeet/app/data/repository/AuthRepository;", "userData", "getUserData", "loadUserData", "", "userId", "", "login", "email", "password", "logout", "register", "username", "updateProfile", "photoUri", "Landroid/net/Uri;", "app_debug"})
public final class AuthViewModel extends androidx.lifecycle.AndroidViewModel {
    private final com.travelmeet.app.data.repository.AuthRepository repository = null;
    private final androidx.lifecycle.MutableLiveData<com.travelmeet.app.util.Resource<com.google.firebase.auth.FirebaseUser>> _authState = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.lifecycle.LiveData<com.travelmeet.app.util.Resource<com.google.firebase.auth.FirebaseUser>> authState = null;
    private final androidx.lifecycle.MutableLiveData<com.travelmeet.app.util.Resource<com.google.firebase.auth.FirebaseUser>> _profileUpdateState = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.lifecycle.LiveData<com.travelmeet.app.util.Resource<com.google.firebase.auth.FirebaseUser>> profileUpdateState = null;
    private final androidx.lifecycle.MutableLiveData<com.travelmeet.app.util.Resource<com.travelmeet.app.data.local.entity.UserEntity>> _userData = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.lifecycle.LiveData<com.travelmeet.app.util.Resource<com.travelmeet.app.data.local.entity.UserEntity>> userData = null;
    
    public AuthViewModel(@org.jetbrains.annotations.NotNull
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<com.travelmeet.app.util.Resource<com.google.firebase.auth.FirebaseUser>> getAuthState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<com.travelmeet.app.util.Resource<com.google.firebase.auth.FirebaseUser>> getProfileUpdateState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final androidx.lifecycle.LiveData<com.travelmeet.app.util.Resource<com.travelmeet.app.data.local.entity.UserEntity>> getUserData() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.google.firebase.auth.FirebaseUser getCurrentUser() {
        return null;
    }
    
    public final void register(@org.jetbrains.annotations.NotNull
    java.lang.String email, @org.jetbrains.annotations.NotNull
    java.lang.String password, @org.jetbrains.annotations.NotNull
    java.lang.String username) {
    }
    
    public final void login(@org.jetbrains.annotations.NotNull
    java.lang.String email, @org.jetbrains.annotations.NotNull
    java.lang.String password) {
    }
    
    public final void logout() {
    }
    
    public final void updateProfile(@org.jetbrains.annotations.Nullable
    java.lang.String username, @org.jetbrains.annotations.Nullable
    android.net.Uri photoUri) {
    }
    
    public final void loadUserData(@org.jetbrains.annotations.NotNull
    java.lang.String userId) {
    }
}