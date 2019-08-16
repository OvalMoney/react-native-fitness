package com.ovalmoney.fitness;

import android.app.Activity;
import java.util.HashMap;
import java.util.Map;
import com.ovalmoney.fitness.manager.Manager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;

public class RNFitnessModule extends ReactContextBaseJavaModule{

  private final static String PLATFORM_KEY = "Platform";
  private final static String PLATFORM = "GoogleFit";

  private final Manager manager;

  public RNFitnessModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.manager = new Manager();
    reactContext.addActivityEventListener(this.manager);
  }

  @Override
  public String getName() {
    return "Fitness";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put(PLATFORM_KEY, PLATFORM);
    return constants;
  }

  @ReactMethod
  public void isAuthorized(Promise promise){
    promise.resolve(manager.isAuthorized(getCurrentActivity()));
  }

  @ReactMethod
  public void requestPermissions(Promise promise){
    final Activity activity = getCurrentActivity();
    if(activity != null) {
      manager.requestPermissions(activity, promise);
    }else{
      promise.reject(new Throwable());
    }
  }
  
    @ReactMethod
  public void subscribeToActivity(Promise promise){
    try {
      manager.subscribeToActivity(getCurrentActivity(), promise);
    }catch(Error e){
      promise.reject(e);
    }
  }

      @ReactMethod
    public void subscribeToSteps(Promise promise){
      try {
        manager.subscribeToSteps(getCurrentActivity(), promise);
      }catch(Error e){
        promise.reject(e);
      }
    }

  @ReactMethod
  public void getSteps(double startDate, double endDate, Promise promise){
    try {
      manager.getSteps(getCurrentActivity(), startDate, endDate, promise);
    }catch(Error e){
      promise.reject(e);
    }
  }

  @ReactMethod
  public void getDistance(double startDate, double endDate, Promise promise){
    try {
      manager.getDistance(getCurrentActivity(), startDate, endDate, promise);
    }catch(Error e){
      promise.reject(e);
    }
  }
}
