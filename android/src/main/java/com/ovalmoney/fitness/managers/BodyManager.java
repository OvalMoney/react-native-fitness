package com.ovalmoney.fitness.managers;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.SessionReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BodyManager {
  private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());

  private static TimeUnit getInterval(String customInterval) {
    if(customInterval.equals("minute")) {
      return TimeUnit.MINUTES;
    }
    if(customInterval.equals("hour")) {
      return TimeUnit.HOURS;
    }
    return TimeUnit.DAYS;
  }

  public void getCalories(Context context, double startDate, double endDate, String customInterval, final Promise promise) {
    TimeUnit interval = getInterval(customInterval);

    DataReadRequest readRequest = new DataReadRequest.Builder()
      .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
      .bucketByTime(1, interval)
      .setTimeRange((long) startDate, (long) endDate, TimeUnit.MILLISECONDS)
      .build();

    Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
      .readData(readRequest)
      .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
        @Override
        public void onSuccess(DataReadResponse dataReadResponse) {
          if (dataReadResponse.getBuckets().size() > 0) {
            WritableArray calories = Arguments.createArray();
            for (Bucket bucket : dataReadResponse.getBuckets()) {
              List<DataSet> dataSets = bucket.getDataSets();
              for (DataSet dataSet : dataSets) {
                processCalories(dataSet, calories);
              }
            }
            promise.resolve(calories);
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

  public void getHeartRate(Context context, double startDate, double endDate, String customInterval,final Promise promise) {
    TimeUnit interval = getInterval(customInterval);

    DataReadRequest readRequest = new DataReadRequest.Builder()
      .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
      .bucketByTime(1, interval)
      .setTimeRange((long) startDate, (long) endDate, TimeUnit.MILLISECONDS)
      .build();

    Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
      .readData(readRequest)
      .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
        @Override
        public void onSuccess(DataReadResponse dataReadResponse) {
          if (dataReadResponse.getBuckets().size() > 0) {
            WritableArray heartRates = Arguments.createArray();
            for (Bucket bucket : dataReadResponse.getBuckets()) {
              List<DataSet> dataSets = bucket.getDataSets();
              for (DataSet dataSet : dataSets) {
                processHeartRate(dataSet, heartRates);
              }
            }
            promise.resolve(heartRates);
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

  @RequiresApi(api = Build.VERSION_CODES.N)
  public void getSleepAnalysis(Context context, double startDate, double endDate, final Promise promise) {
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N){
      promise.reject(String.valueOf(FitnessError.ERROR_METHOD_NOT_AVAILABLE), "Method not available");
      return;
    }

    SessionReadRequest request = new SessionReadRequest.Builder()
      .readSessionsFromAllApps()
      .read(DataType.TYPE_ACTIVITY_SEGMENT)
      .setTimeInterval((long) startDate, (long) endDate, TimeUnit.MILLISECONDS)
      .build();

    Fitness.getSessionsClient(context, GoogleSignIn.getLastSignedInAccount(context))
      .readSession(request)
      .addOnSuccessListener(new OnSuccessListener<SessionReadResponse>() {
        @Override
        public void onSuccess(SessionReadResponse response) {
          List<Object> sleepSessions = response.getSessions()
            .stream()
            .filter(new Predicate<Session>() {
              @Override
              public boolean test(Session s) {
                return s.getActivity().equals(FitnessActivities.SLEEP);
              }
            })
            .collect(Collectors.toList());

          WritableArray sleep = Arguments.createArray();

          for (Object session : sleepSessions) {
            List<DataSet> dataSets = response.getDataSet((Session) session);

            for (DataSet dataSet : dataSets) {
              processSleep(dataSet, (Session) session, sleep);
            }
          }

          promise.resolve(sleep);
        }
      })
      .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          promise.reject(e);
        }
      });
  }

  private void processHeartRate(DataSet dataSet, WritableArray map) {

    WritableMap heartRateMap = Arguments.createMap();

    for (DataPoint dp : dataSet.getDataPoints()) {
      for(Field field : dp.getDataType().getFields()) {
        heartRateMap.putString("startDate", dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
        heartRateMap.putString("endDate", dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
        heartRateMap.putDouble("quantity", dp.getValue(field).asFloat());
        map.pushMap(heartRateMap);
      }
    }
  }

  private void processCalories(DataSet dataSet, WritableArray map) {

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

  private void processSleep(DataSet dataSet, Session session, WritableArray map) {

    WritableMap sleepMap = Arguments.createMap();

    for (DataPoint dp : dataSet.getDataPoints()) {
      for (Field field : dp.getDataType().getFields()) {
        sleepMap.putString("value", dp.getValue(field).asActivity());
        sleepMap.putString("sourceId", session.getIdentifier());
        sleepMap.putString("startDate", dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
        sleepMap.putString("endDate", dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
        map.pushMap(sleepMap);
      }
    }
  }
}
