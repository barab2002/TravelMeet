package com.travelmeet.app.util;

import java.lang.System;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/travelmeet/app/util/Constants;", "", "()V", "CAMERA_PERMISSION_REQUEST", "", "COLLECTION_SPOTS", "", "COLLECTION_USERS", "IMAGE_QUALITY", "LOCATION_PERMISSION_REQUEST", "MAX_IMAGE_SIZE_KB", "STORAGE_PERMISSION_REQUEST", "STORAGE_PROFILE_IMAGES", "STORAGE_SPOT_IMAGES", "WEATHER_API_KEY", "app_debug"})
public final class Constants {
    @org.jetbrains.annotations.NotNull
    public static final com.travelmeet.app.util.Constants INSTANCE = null;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String WEATHER_API_KEY = "YOUR_OPENWEATHERMAP_API_KEY";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String STORAGE_PROFILE_IMAGES = "profile_images";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String STORAGE_SPOT_IMAGES = "spot_images";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String COLLECTION_USERS = "users";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String COLLECTION_SPOTS = "spots";
    public static final int MAX_IMAGE_SIZE_KB = 500;
    public static final int IMAGE_QUALITY = 80;
    public static final int LOCATION_PERMISSION_REQUEST = 1001;
    public static final int CAMERA_PERMISSION_REQUEST = 1002;
    public static final int STORAGE_PERMISSION_REQUEST = 1003;
    
    private Constants() {
        super();
    }
}