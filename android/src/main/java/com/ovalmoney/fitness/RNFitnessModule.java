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
import com.ovalmoney.fitness.permission.Permissions;
import com.ovalmoney.fitness.permission.Request;

public class RNFitnessModule extends ReactContextBaseJavaModule{
  private final static String TAG = RNFitnessModule.class.getName();

  private final static String PLATFORM_KEY = "Platform";
  private final static String PLATFORM = "GoogleFit";

  private final static String ERRORS_KEY = "Errors";
  private final static String ERROR_METHOD_NOT_AVAILABLE_KEY = "methodNotAvailable";

  private final static String PERMISSIONS_KEY = "PermissionKinds";
  private final static String STEPS_KEY = "Steps";
  private final static String ACTIVITY_KEY = "Activity";
  private final static String CALORIES_KEY = "Calories";
  private final static String DISTANCES_KEY = "Distances";
  private final static String HEART_RATE_KEY = "HeartRate";
  private final static String SLEEP_ANALYSIS_KEY = "SleepAnalysis";
  private final static String SLEEP_ANALYSIS_KEY = "Weight";
  private final static String SLEEP_ANALYSIS_KEY = "Height";

  private final static String ACCESS_TYPE_KEY = "PermissionAccesses";

  private final static String READ = "Read";
  private final static String WRITE = "Write";

  private final static Map<String, Integer> PERMISSIONS = new HashMap<>();
  private final static Map<String, Integer> ACCESSES = new HashMap<>();
  private final static Map<String, Integer> ERRORS = new HashMap<>();

  private final Manager manager;

  public RNFitnessModule(ReactApplicationContext reactContext) {
    super(reactContext);
    feedPermissionsMap();
    feedAccessesTypeMap();
    feedErrorsMap();
    this.manager = new Manager();
    reactContext.addActivityEventListener(this.manager);
  }

  private void feedPermissionsMap(){
    PERMISSIONS.put(STEPS_KEY, Permissions.STEPS);
    PERMISSIONS.put(DISTANCES_KEY, Permissions.DISTANCES);
    PERMISSIONS.put(ACTIVITY_KEY, Permissions.ACTIVITY);
    PERMISSIONS.put(CALORIES_KEY, Permissions.CALORIES);
    PERMISSIONS.put(HEART_RATE_KEY, Permissions.HEART_RATE);
    PERMISSIONS.put(SLEEP_ANALYSIS_KEY, Permissions.SLEEP_ANALYSIS);
    PERMISSIONS.put(WEIGHT_KEY, Permissions.WEIGHT);
    PERMISSIONS.put(HEIGHT_KEY, Permissions.HEIGHT);
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
    constants.put(ERRORS_KEY, ERRORS);
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

  @ReactMethod
  public void getCalories(double startDate, double endDate, String interval, Promise promise){
    try {
      manager.getCalories(getCurrentActivity(), startDate, endDate, interval, promise);
    }catch(Error e){
      promise.reject(e);
    }
  }
  
   @ReactMethod
  public void getWidthAndHeight(Context context, Promise promise){
    try {
      manager.getWidthAndHeight(getCurrentActivity(), promise);
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
