//
//  RCTFitness+Permissions.m
//  react-native-fitness
//
//  Created by Francesco Voto on 28/01/2020.
//

#import <HealthKit/HealthKit.h>
#import "RCTFitness+Permissions.h"

@implementation RCTFitness(Permissions)

+(HKObjectType*)getQuantityType:(RCTFitnessPermissionKind)permission
{
    switch (permission) {
        case STEPS:
             return [HKObjectType quantityTypeForIdentifier: HKQuantityTypeIdentifierStepCount];
        case DISTANCES:
             return [HKObjectType quantityTypeForIdentifier: HKQuantityTypeIdentifierDistanceWalkingRunning];
        case ACTIVITY:
                return nil;
        case CALORIES:
                return [HKObjectType quantityTypeForIdentifier: HKQuantityTypeIdentifierActiveEnergyBurned];
        case HEART_RATE:
                return [HKObjectType quantityTypeForIdentifier: HKQuantityTypeIdentifierHeartRate];
        case SLEEP_ANALYSIS: 
                return [HKObjectType categoryTypeForIdentifier: HKCategoryTypeIdentifierSleepAnalysis];
        default:
            return nil;
    }
   
}


@end
