package com.ovalmoney.fitness;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.facebook.react.bridge.JavaScriptModule;
import com.ovalmoney.fitness.modules.RNFitnessActivitiesModule;
import com.ovalmoney.fitness.modules.RNFitnessAuthModule;
import com.ovalmoney.fitness.modules.RNFitnessBodyModule;

public class RNFitnessPackage implements ReactPackage {

  @NonNull
  @Override
  public List<NativeModule> createNativeModules(@NonNull ReactApplicationContext reactContext) {
    return Arrays.<NativeModule>asList(
      new RNFitnessModule(reactContext),
      new RNFitnessAuthModule(reactContext),
      new RNFitnessActivitiesModule(reactContext),
      new RNFitnessBodyModule(reactContext)
    );
  }

  // Deprecated from RN 0.47
  public List<Class<? extends JavaScriptModule>> createJSModules() {
    return Collections.emptyList();
  }

  @NonNull
  @Override
  public List<ViewManager> createViewManagers(@NonNull ReactApplicationContext reactContext) {
    return Collections.emptyList();
  }
}
