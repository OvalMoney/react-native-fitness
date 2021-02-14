package com.ovalmoney.fitness.modules;

import android.app.Activity;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.google.android.gms.fitness.FitnessOptions;
import com.ovalmoney.fitness.managers.AuthManager;
import com.ovalmoney.fitness.permission.Request;

import java.util.ArrayList;

public class RNFitnessAuthModule extends ReactContextBaseJavaModule {

  private final static String TAG = com.ovalmoney.fitness.RNFitnessModule.class.getName();

  private final AuthManager manager;

  public RNFitnessAuthModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.manager = new AuthManager();
    reactContext.addActivityEventListener(this.manager);
  }

  @Override
  public String getName() {
    return "AuthFitness";
  }

  @ReactMethod
  public void isAuthorized(ReadableArray permissions, Promise promise){
    promise.resolve(manager.isAuthorized(getCurrentActivity(), createRequestFromReactArray(permissions)));
  }

  @ReactMethod
  public void requestPermissions(ReadableArray permissions, Promise promise){
    final Activity activity = getCurrentActivity();
    if(activity != null) {
      manager.requestPermissions(activity,createRequestFromReactArray(permissions), promise);
    }else{
      promise.reject(new Throwable());
    }
  }

  @ReactMethod
  public void logout(Promise promise){
    final Activity activity = getCurrentActivity();
    if(activity != null) {
      manager.logout(activity, promise);
    }else{
      promise.reject(new Throwable());
    }
  }

  @ReactMethod
  public void disconnect(Promise promise){
    final Activity activity = getCurrentActivity();
    if(activity != null) {
      manager.disconnect(activity, promise);
    }else{
      promise.reject(new Throwable());
    }

  }

  private ArrayList<Request> createRequestFromReactArray(ReadableArray permissions){
    ArrayList<Request> requestPermissions = new ArrayList<>();
    int size = permissions.size();
    for(int i = 0; i < size; i++) {
      try {
        ReadableMap singlePermission = permissions.getMap(i);
        final int permissionKind = singlePermission.getInt("kind");
        final int permissionAccess = singlePermission.hasKey("access") ? singlePermission.getInt("access") : FitnessOptions.ACCESS_READ;
        requestPermissions.add(new Request(permissionKind, permissionAccess));
      } catch (NullPointerException e) {
        Log.e(TAG, e.getMessage());
      }
    }
    return requestPermissions;
  }
}
