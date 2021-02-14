package com.ovalmoney.fitness.managers;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ovalmoney.fitness.permission.Request;

import java.util.ArrayList;

import static com.ovalmoney.fitness.permission.Permissions.ACTIVITY;
import static com.ovalmoney.fitness.permission.Permissions.CALORIES;
import static com.ovalmoney.fitness.permission.Permissions.DISTANCES;
import static com.ovalmoney.fitness.permission.Permissions.HEART_RATE;
import static com.ovalmoney.fitness.permission.Permissions.SLEEP_ANALYSIS;
import static com.ovalmoney.fitness.permission.Permissions.STEPS;

public class AuthManager implements ActivityEventListener {

  private final static int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 111;
  private final static int GOOGLE_PLAY_SERVICE_ERROR_DIALOG = 2404;

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

  private FitnessOptions.Builder addPermissionToFitnessOptions(final FitnessOptions.Builder fitnessOptions, final ArrayList<Request> permissions){
    int length = permissions.size();
    for(int i = 0; i < length; i++){
      Request currentRequest = permissions.get(i);
      switch(currentRequest.permissionKind){
        case STEPS:
          fitnessOptions
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, currentRequest.permissionAccess)
            .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, currentRequest.permissionAccess);
          break;
        case DISTANCES:
          fitnessOptions.addDataType(DataType.TYPE_DISTANCE_DELTA, currentRequest.permissionAccess);
          break;
        case CALORIES:
          fitnessOptions.addDataType(DataType.TYPE_CALORIES_EXPENDED, currentRequest.permissionAccess);
          break;
        case ACTIVITY:
          fitnessOptions.addDataType(DataType.TYPE_ACTIVITY_SEGMENT, currentRequest.permissionAccess);
          break;
        case HEART_RATE:
          fitnessOptions.addDataType(DataType.TYPE_HEART_RATE_BPM, currentRequest.permissionAccess);
          break;
        case SLEEP_ANALYSIS:
        default:
          break;
      }
    }

    return fitnessOptions;
  }

  public boolean isAuthorized(final Activity activity, final ArrayList<Request> permissions){
    if(isGooglePlayServicesAvailable(activity)) {
      final FitnessOptions fitnessOptions = addPermissionToFitnessOptions(FitnessOptions.builder(), permissions)
        .build();
      return GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(activity), fitnessOptions);
    }
    return false;
  }

  public void requestPermissions(@NonNull Activity currentActivity, final ArrayList<Request> permissions, Promise promise) {
    try {
      this.promise = promise;
      FitnessOptions fitnessOptions = addPermissionToFitnessOptions(FitnessOptions.builder(), permissions)
        .build();
      GoogleSignIn.requestPermissions(
        currentActivity,
        GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
        GoogleSignIn.getLastSignedInAccount(currentActivity.getApplicationContext()),
        fitnessOptions);
    }catch(Exception e){
      Log.e(getClass().getName(), e.getMessage());
    }
  }


  public void logout(@NonNull Activity currentActivity, final Promise promise) {
    final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
    GoogleSignIn.getClient(currentActivity, gso)
      .revokeAccess()
      .addOnCanceledListener(new OnCanceledListener() {
        @Override
        public void onCanceled() {
          promise.resolve(false);
        }
      })
      .addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
          promise.resolve(true);
        }
      })
      .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          promise.reject(e);
        }
      });
  }

  public void disconnect(@NonNull Activity currentActivity, final Promise promise) {
    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(currentActivity.getApplicationContext());

    if (account == null) {
      promise.reject(new Exception());
      return;
    }

    Fitness.getConfigClient(
      currentActivity,
      account
    )
      .disableFit()
      .addOnCanceledListener(new OnCanceledListener() {
        @Override
        public void onCanceled() {
          promise.resolve(false);
        }
      })
      .addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
          promise.resolve(true);
        }
      })
      .addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
          promise.reject(e);
        }
      });
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
}
