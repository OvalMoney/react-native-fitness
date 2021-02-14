package com.ovalmoney.fitness.managers;

import android.content.Context;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ActivitiesManager {

  private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());

  private static TimeUnit getInterval(String customInterval) {
    if(customInterval.equals("minute")) {
      return TimeUnit.MINUTES;
    }
    if(customInterval.equals("hour")) {
      return TimeUnit.HOURS;
    }
    return TimeUnit.DAYS;
  }

  public void subscribeToActivity(Context context, final Promise promise){
    final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
    if(account == null){
      promise.resolve(false);
      return;
    }
    Fitness.getRecordingClient(context, account)
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
    final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
    if(account == null){
      promise.resolve(false);
      return;
    }
    Fitness.getRecordingClient(context, account)
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

  public void getSteps(Context context, double startDate, double endDate, String customInterval, final Promise promise){
    DataSource ESTIMATED_STEP_DELTAS = new DataSource.Builder()
      .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
      .setType(DataSource.TYPE_DERIVED)
      .setStreamName("estimated_steps")
      .setAppPackageName("com.google.android.gms")
      .build();

    TimeUnit interval = getInterval(customInterval);

    DataReadRequest readRequest = new DataReadRequest.Builder()
      .aggregate(ESTIMATED_STEP_DELTAS,    DataType.AGGREGATE_STEP_COUNT_DELTA)
      .bucketByTime(1, interval)
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

  public void getDistances(Context context, double startDate, double endDate, String customInterval, final Promise promise) {
    TimeUnit interval = getInterval(customInterval);

    DataReadRequest readRequest = new DataReadRequest.Builder()
      .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
      .bucketByTime(1, interval)
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
