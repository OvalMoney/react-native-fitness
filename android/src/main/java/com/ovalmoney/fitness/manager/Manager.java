package com.ovalmoney.fitness.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.fitness.data.Subscription;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Manager implements ActivityEventListener {

    private final static int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 111;
    private final static int GOOGLE_PLAY_SERVICE_ERROR_DIALOG = 2404;
    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());


    private Promise promise;

    private static boolean isGooglePlayServicesAvailable(final Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, GOOGLE_PLAY_SERVICE_ERROR_DIALOG).show();
            }
            return false;
        }
        return true;
    }

    public boolean isAuthorized(final Activity activity){
        if(isGooglePlayServicesAvailable(activity)) {
            FitnessOptions fitnessOptions = FitnessOptions.builder()
                    .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                    .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                    .addDataType(DataType.TYPE_ACTIVITY_SAMPLES, FitnessOptions.ACCESS_WRITE)
                    .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_WRITE)
                    .build();
            return GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(activity), fitnessOptions);
        }
        return false;
    }

    public void requestPermissions(@NonNull Activity currentActivity, Promise promise) {
        this.promise = promise;
        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_ACTIVITY_SAMPLES, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_WRITE)
                .build();
        GoogleSignIn.requestPermissions(
                currentActivity,
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(currentActivity.getApplicationContext()),
                fitnessOptions);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            promise.resolve(true);
        }
        if (resultCode == Activity.RESULT_CANCELED && requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            promise.resolve(false);
        }
    }

    @Override
    public void onNewIntent(Intent intent) { }
    
    public void subscribeToActivity(Context context, final Promise promise){

      Fitness.getRecordingClient(context, GoogleSignIn.getLastSignedInAccount(context))
              .subscribe(DataType.TYPE_ACTIVITY_SAMPLES)
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void aVoid) {
                      promise.resolve(true);
                  }
              })
              .addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                     promise.resolve(false);
                  }
              });

            }

        public void subscribeToSteps(Context context, final Promise promise){



          Fitness.getRecordingClient(context, GoogleSignIn.getLastSignedInAccount(context))
                  .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
                  .addOnSuccessListener(new OnSuccessListener<Void>() {
                      @Override
                      public void onSuccess(Void aVoid) {
                          promise.resolve(true);
                      }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                         promise.resolve(false);
                      }
                  });

                }

    public void getSteps(Context context, double startDate, double endDate, final Promise promise){
        DataSource ESTIMATED_STEP_DELTAS = new DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .setAppPackageName("com.google.android.gms")
                .build();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(ESTIMATED_STEP_DELTAS,    DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange((long) startDate, (long) endDate, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        if (dataReadResponse.getBuckets().size() > 0) {
                            WritableArray steps = Arguments.createArray();
                            for (Bucket bucket : dataReadResponse.getBuckets()) {
                                List<DataSet> dataSets = bucket.getDataSets();
                                for (DataSet dataSet : dataSets) {
                                    processStep(dataSet, steps);
                                }
                            }
                            promise.resolve(steps);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        promise.reject(e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<DataReadResponse> task) {
                    }
                });
    }

    public void getDistance(Context context, double startDate, double endDate, final Promise promise) {
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange((long) startDate, (long) endDate, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        if (dataReadResponse.getBuckets().size() > 0) {
                            WritableArray distances = Arguments.createArray();
                            for (Bucket bucket : dataReadResponse.getBuckets()) {
                                List<DataSet> dataSets = bucket.getDataSets();
                                for (DataSet dataSet : dataSets) {
                                    processDistance(dataSet, distances);
                                }
                            }
                            promise.resolve(distances);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        promise.reject(e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DataReadResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<DataReadResponse> task) {
                    }
                });
    }

    private void processStep(DataSet dataSet, WritableArray map) {

        WritableMap stepMap = Arguments.createMap();

        for (DataPoint dp : dataSet.getDataPoints()) {
            for(Field field : dp.getDataType().getFields()) {
                stepMap.putString("startDate", dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                stepMap.putString("endDate", dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                stepMap.putDouble("quantity", dp.getValue(field).asInt());
                map.pushMap(stepMap);
            }
        }
    }

    private void processDistance(DataSet dataSet, WritableArray map) {

        WritableMap distanceMap = Arguments.createMap();

        for (DataPoint dp : dataSet.getDataPoints()) {
            for(Field field : dp.getDataType().getFields()) {
                distanceMap.putString("startDate", dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                distanceMap.putString("endDate", dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                distanceMap.putDouble("quantity", dp.getValue(field).asFloat());
                map.pushMap(distanceMap);
            }
        }
    }
}
