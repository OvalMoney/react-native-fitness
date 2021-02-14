package com.ovalmoney.fitness.modules;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.ovalmoney.fitness.managers.ActivitiesManager;

public class RNFitnessActivitiesModule extends ReactContextBaseJavaModule {

  private final ActivitiesManager manager;

  public RNFitnessActivitiesModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.manager = new ActivitiesManager();
  }

  @Override
  public String getName() {
    return "ActivitiesFitness";
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
  public void getSteps(double startDate, double endDate, String interval, Promise promise){
    try {
      manager.getSteps(getCurrentActivity(), startDate, endDate, interval, promise);
    }catch(Error e){
      promise.reject(e);
    }
  }

  @ReactMethod
  public void getDistances(double startDate, double endDate, String interval, Promise promise){
    try {
      manager.getDistances(getCurrentActivity(), startDate, endDate, interval, promise);
    }catch(Error e){
      promise.reject(e);
    }
  }
}
