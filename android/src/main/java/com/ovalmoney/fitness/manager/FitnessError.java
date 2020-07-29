package com.ovalmoney.fitness.manager;

import androidx.annotation.IntDef;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

    @Retention(SOURCE)
    @IntDef({
        FitnessError.ERROR_METHOD_NOT_AVAILABLE,
    })

    public @interface FitnessError {
        int ERROR_METHOD_NOT_AVAILABLE = -101;
    }