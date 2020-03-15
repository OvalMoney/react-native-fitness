package com.ovalmoney.fitness.permission;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;


    @Retention(SOURCE)
    @IntDef({
            Permission.STEP,
            Permission.DISTANCE,
            Permission.ACTIVITY,
            Permission.CALORIES,
            Permission.SLEEP_ANALYSIS,
    })

    public @interface Permission {
        int STEP = 0;
        int DISTANCE = 1;
        int ACTIVITY = 2;
        int CALORIES = 3;
        int HEART_RATE = 4;
        int SLEEP_ANALYSIS = 5;
    }

