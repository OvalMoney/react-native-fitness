package com.ovalmoney.fitness.permission;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;


    @Retention(SOURCE)
    @IntDef({
            Permissions.STEPS,
            Permissions.DISTANCES,
            Permissions.ACTIVITY,
            Permissions.CALORIES,
            Permissions.HEART_RATE,
            Permissions.SLEEP_ANALYSIS,
            Permissions.WEIGHT,
            Permissions.HEIGHT,
    })

    public @interface Permissions {
        int STEPS = 0;
        int DISTANCES = 1;
        int ACTIVITY = 2;
        int CALORIES = 3;
        int HEART_RATE = 4;
        int SLEEP_ANALYSIS = 5;
        int WEIGHT = 6;
        int HEIGHT = 7;
    }

