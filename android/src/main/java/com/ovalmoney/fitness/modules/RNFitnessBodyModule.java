package com.ovalmoney.fitness.modules;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.ovalmoney.fitness.managers.AuthManager;
import com.ovalmoney.fitness.managers.BodyManager;

public class RNFitnessBodyModule extends ReactContextBaseJavaModule {

  private final BodyManager manager;

  public RNFitnessBodyModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.manager = new BodyManager();
  }

  @Override
  public String getName() {
    return "BodyFitness";
  }

  @ReactMethod
  public void getCalories(double startDate, double endDate, String interval, Promise promise){
    try {
      manager.getCalories(getCurrentActivity(), startDate, endDate, interval, promise);
    }catch(Error e){
      promise.reject(e);
    }
  }

  @ReactMethod
  public void getHeartRate(double startDate, double endDate, String interval, Promise promise){
    try {
      manager.getHeartRate(getCurrentActivity(), startDate, endDate, interval, promise);
    }catch(Error e){
      promise.reject(e);
    }
  }

  @ReactMethod
  public void getSleepAnalysis(double startDate, double endDate, Promise promise){
    try {
      manager.getSleepAnalysis(getCurrentActivity(), startDate, endDate, promise);
    }catch(Error e){
      promise.reject(e);
    }
  }
}

