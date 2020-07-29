package com.ovalmoney.fitness;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.google.android.gms.fitness.FitnessOptions;
import com.ovalmoney.fitness.manager.Manager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.ovalmoney.fitness.manager.FitnessError;
import com.ovalmoney.fitness.permission.Permission;
import com.ovalmoney.fitness.permission.Request;

public class RNFitnessModule extends ReactContextBaseJavaModule{
  private final static String TAG = RNFitnessModule.class.getName();

  private final static String PLATFORM_KEY = "Platform";
  private final static String PLATFORM = "GoogleFit";

  private final static String ERROR_KEY = "Error";
  private final static String ERROR_METHOD_NOT_AVAILABLE_KEY = "methodNotAvailable";

  private final static String PERMISSIONS_KEY = "PermissionKind";
  private final static String STEP_KEY = "Step";
  private final static String ACTIVITY_KEY = "Activity";
  private final static String CALORIES_KEY = "Calories";
  private final static String DISTANCE_KEY = "Distance";
  private final static String HEART_RATE_KEY = "HeartRate";
  private final static String SLEEP_ANALYSIS_KEY = "SleepAnalysis";

  private final static String ACCESS_TYPE_KEY = "PermissionAccess";

  private final static String READ = "READ";
  private final static String WRITE = "WRITE";

  private final static Map<String, Integer> PERMISSIONS = new HashMap<>();
  private final static Map<String, Integer> ACCESSES = new HashMap<>();
  private final static Map<String, Integer> ERRORS = new HashMap<>();

  private final Manager manager;

  public RNFitnessModule(ReactApplicationContext reactContext) {
    super(reactContext);
    feedPermissionsMap();
    feedAccessesTypeMap();
    this.manager = new Manager();
    reactContext.addActivityEventListener(this.manager);
  }

  private void feedPermissionsMap(){
    PERMISSIONS.put(STEP_KEY, Permission.STEP);
    PERMISSIONS.put(DISTANCE_KEY, Permission.DISTANCE);
    PERMISSIONS.put(ACTIVITY_KEY, Permission.ACTIVITY);
    PERMISSIONS.put(CALORIES_KEY, Permission.CALORIES);
    PERMISSIONS.put(HEART_RATE_KEY, Permission.HEART_RATE);
    PERMISSIONS.put(SLEEP_ANALYSIS_KEY, Permission.SLEEP_ANALYSIS);
  }

  private void feedAccessesTypeMap(){
    ACCESSES.put(READ, FitnessOptions.ACCESS_READ);
    ACCESSES.put(WRITE, FitnessOptions.ACCESS_WRITE);
  }

  private void feedErrorsMap(){
    ERRORS.put(ERROR_METHOD_NOT_AVAILABLE_KEY, FitnessError.ERROR_METHOD_NOT_AVAILABLE);
  }

  @Override
  public String getName() {
    return "Fitness";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put(PLATFORM_KEY, PLATFORM);
    constants.put(PERMISSIONS_KEY, PERMISSIONS);
    constants.put(ACCESS_TYPE_KEY, ACCESSES);
    constants.put(ERROR_KEY, ERRORS);
    return constants;
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
  public void getDistance(double startDate, double endDate, String interval, Promise promise){
    try {
      manager.getDistance(getCurrentActivity(), startDate, endDate, interval, promise);
    }catch(Error e){
      promise.reject(e);
    }
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
