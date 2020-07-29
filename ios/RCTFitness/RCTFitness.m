#import <HealthKit/HealthKit.h>
#import <React/RCTConvert.h>
#import "RCTFitness.h"
#import "RCTFitness+Utils.h"
#import "RCTFitness+Errors.h"
#import "RCTFitness+Permissions.h"

@interface RCTFitness()
@property (nonatomic, strong) HKHealthStore *healthStore;
@end

@implementation RCTConvert (Fitness)
RCT_ENUM_CONVERTER(RCTFitnessError, (@{ @"hkNotAvailable" : @(ErrorHKNotAvailable),
                                        @"methodNotAvailable" : @(ErrorMethodNotAvailable),
                                        @"dateNotCorrect" : @(ErrorDateNotCorrect),
                                        @"errorNoEvents" : @(ErrorNoEvents),
                                        @"errorEmptyPermissions" : @(ErrorEmptyPermissions)}),
                   ErrorHKNotAvailable, integerValue)
@end

@implementation RCTConvert (Permission)
RCT_ENUM_CONVERTER(RCTFitnessPermissionKind, (@{ @"Step" : @(STEP),
                                                 @"Distance" : @(DISTANCE),
                                                 @"Calories" : @(CALORIES),
                                                 @"Activity" : @(ACTIVITY),
                                                 @"HeartRate" : @(HEART_RATE),
                                                 @"SleepAnalysis" : @(SLEEP_ANALYSIS)}),
                   STEP, integerValue)
@end

@implementation RCTConvert (PermissionAccess)
RCT_ENUM_CONVERTER(RCTFitnessPermissionAccess, (@{ @"Read" : @(READ),
                                                   @"Write" : @(WRITE)}),
                   READ, integerValue)
@end

@implementation RCTFitness

@synthesize bridge = _bridge;

- (instancetype)init {
    if (self = [super init]) {
        self.healthStore = [[HKHealthStore alloc] init];
    }
    return self;
}

+ (BOOL) requiresMainQueueSetup {
    return YES;
}

RCT_EXPORT_MODULE(Fitness);

- (void) handlePermissions:(NSArray*)permissions returnBlock:(void (^)(NSSet* readPerms, NSSet* sharePerms))returnBlock
{
    if ([HKHealthStore isHealthDataAvailable]) {
        NSMutableSet *sharePerms = [NSMutableSet setWithCapacity:1];
        NSMutableSet *readPerms = [NSMutableSet setWithCapacity:1];
        for (NSDictionary * permission in permissions) {
            RCTFitnessPermissionKind p = [RCTConvert NSInteger: permission[@"kind"]];
            RCTFitnessPermissionAccess access = [RCTConvert NSInteger: permission[@"access"]];
            if(access && access == WRITE){
                [sharePerms addObject:[RCTFitness getQuantityType: p]];
            } else {
                [readPerms addObject:[RCTFitness getQuantityType: p]];
            }
        }
        if([readPerms count] == 0) {
            @throw [RCTFitness createErrorWithCode:ErrorEmptyPermissions andDescription: RCT_ERROR_EMPTY_PERMISSIONS];
        }
        
        returnBlock(readPerms, sharePerms);
    }else{
        @throw [RCTFitness createErrorWithCode:ErrorHKNotAvailable andDescription:RCT_ERROR_HK_NOT_AVAILABLE];
    }
}

#pragma mark - constants export

- (NSDictionary *)constantsToExport{
    return @{
        @"Platform" : @"AppleHealth",
        @"Error": @{
                @"hkNotAvailable" : @(ErrorHKNotAvailable),
                @"methodNotAvailable" : @(ErrorMethodNotAvailable),
                @"dateNotCorrect" : @(ErrorDateNotCorrect),
                @"emptyPermission" : @(ErrorEmptyPermissions),
        },
        @"PermissionKind": @{
                @"Step": @(STEP),
                @"Distance": @(DISTANCE),
                @"Calories": @(CALORIES),
                @"Activity": @(ACTIVITY),
                @"HeartRate": @(HEART_RATE),
                @"SleepAnalysis" : @(SLEEP_ANALYSIS),
        },
        @"PermissionAccess": @{
                @"Read": @(READ),
                @"Write": @(WRITE),
        },
    };
}

RCT_REMAP_METHOD(requestPermissions,
                 withPermissions: (NSArray*) permissions
                 withRequestResolver:(RCTPromiseResolveBlock)resolve
                 andRequestRejecter:(RCTPromiseRejectBlock)reject) {
    @try{
        [self handlePermissions:permissions returnBlock: ^(NSSet *readPerms, NSSet* sharePerms){
            [self.healthStore requestAuthorizationToShareTypes:sharePerms readTypes:readPerms completion:^(BOOL success, NSError *error) {
                if (!success) {
                    NSError * error = [RCTFitness createErrorWithCode:ErrorEmptyPermissions andDescription:RCT_ERROR_NO_EVENTS];
                    [RCTFitness handleRejectBlock:reject error:error];
                    return;
                }
                dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                    resolve(@YES);
                });
            }];
        }];
    }@catch (NSError *error) {
        [RCTFitness handleRejectBlock:reject error: error];
    }
}

RCT_REMAP_METHOD(isAuthorized,
                 withPermissions: (NSArray*) permissions
                 withAuthorizedResolver:(RCTPromiseResolveBlock)resolve
                 andAuthorizedRejecter:(RCTPromiseRejectBlock)reject){
    if (@available(iOS 12.0, *)) {
        @try{
            [self handlePermissions:permissions returnBlock: ^(NSSet *readPerms, NSSet* sharePerms){
                [self.healthStore getRequestStatusForAuthorizationToShareTypes: sharePerms readTypes: readPerms completion:^(HKAuthorizationRequestStatus status, NSError *error) {
                    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                        resolve(status == HKAuthorizationStatusSharingAuthorized ? @YES : @NO);
                    });
                }];
                
            }];
            
        }@catch (NSError *error) {
            [RCTFitness handleRejectBlock:reject error: error];
        }
    }else{
        NSError * error = [RCTFitness createErrorWithCode:ErrorMethodNotAvailable andDescription:RCT_ERROR_METHOD_NOT_AVAILABLE];
        [RCTFitness handleRejectBlock:reject error:error];
    }
    
}

RCT_REMAP_METHOD(getSteps,
                 withStartDate: (double) startDate
                 andEndDate: (double) endDate
                 andInterval: (NSString *) customInterval
                 withStepsResolver:(RCTPromiseResolveBlock)resolve
                 andStepsRejecter:(RCTPromiseRejectBlock)reject){
    
    if(!startDate){
        NSError * error = [RCTFitness createErrorWithCode:ErrorDateNotCorrect andDescription:RCT_ERROR_DATE_NOT_CORRECT];
        [RCTFitness handleRejectBlock:reject error:error];
        return;
    }
    
    HKQuantityType *type =
    [HKObjectType quantityTypeForIdentifier:HKQuantityTypeIdentifierStepCount];
    
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents *interval = [[NSDateComponents alloc] init];
    
    if([customInterval isEqual: @"minute"]){
        interval.minute = 1;
    }else if([customInterval isEqual: @"hour"]){
        interval.hour = 1;
    }else{
        interval.day = 1;
    }
    
    
    NSDateComponents *anchorComponents = [calendar components:NSCalendarUnitDay | NSCalendarUnitMonth | NSCalendarUnitYear
                                                     fromDate:[NSDate date]];
    anchorComponents.hour = 0;
    NSDate *anchorDate = [calendar dateFromComponents:anchorComponents];
    HKStatisticsCollectionQuery *query = [[HKStatisticsCollectionQuery alloc] initWithQuantityType:type
                                                                           quantitySamplePredicate:nil
                                                                                           options:HKStatisticsOptionCumulativeSum
                                                                                        anchorDate:anchorDate
                                                                                intervalComponents:interval];
    query.initialResultsHandler =
    ^(HKStatisticsCollectionQuery *query, HKStatisticsCollection *results, NSError *error) {
        
        if (error) {
            NSError * error = [RCTFitness createErrorWithCode:ErrorNoEvents andDescription:RCT_ERROR_NO_EVENTS];
            [RCTFitness handleRejectBlock:reject error:error];
            return;
        }
        
        NSDate * sd = [RCTFitness dateFromTimeStamp: startDate / 1000];
        NSDate * ed = [RCTFitness dateFromTimeStamp: endDate   / 1000];
        
        NSMutableArray *data = [NSMutableArray arrayWithCapacity:1];
        [results
         enumerateStatisticsFromDate: sd
         toDate:ed
         withBlock:^(HKStatistics *result, BOOL *stop) {
            
            HKQuantity *quantity = result.sumQuantity;
            if (quantity) {
                NSDate *startDate = result.startDate;
                NSDate *endDate = result.endDate;
                NSDictionary *elem = @{
                    @"quantity" : @([quantity doubleValueForUnit:[HKUnit countUnit]]),
                    @"startDate" : [RCTFitness ISO8601StringFromDate: startDate],
                    @"endDate" : [RCTFitness ISO8601StringFromDate: endDate],
                };
                [data addObject:elem];
            }
        }];
        dispatch_async(dispatch_get_main_queue(), ^{
            resolve(data);
        });
    };
    
    [self.healthStore executeQuery:query];
}

RCT_REMAP_METHOD(getDistance,
                 withStartDate: (double) startDate
                 andEndDate: (double) endDate
                 andInterval: (NSString *) customInterval
                 withDistanceResolver:(RCTPromiseResolveBlock)resolve
                 andDistanceRejecter:(RCTPromiseRejectBlock)reject){
    
    if(!startDate){
        NSError * error = [RCTFitness createErrorWithCode:ErrorDateNotCorrect andDescription:RCT_ERROR_DATE_NOT_CORRECT];
        [RCTFitness handleRejectBlock:reject error:error];
        return;
    }
    
    HKQuantityType *type =
    [HKObjectType quantityTypeForIdentifier:HKQuantityTypeIdentifierDistanceWalkingRunning];
    
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents *interval = [[NSDateComponents alloc] init];
    
    if([customInterval isEqual: @"minute"]){
        interval.minute = 1;
    }else if([customInterval isEqual: @"hour"]){
        interval.hour = 1;
    }else{
        interval.day = 1;
    }
    
    NSDateComponents *anchorComponents = [calendar components:NSCalendarUnitDay | NSCalendarUnitMonth | NSCalendarUnitYear
                                                     fromDate:[NSDate date]];
    anchorComponents.hour = 0;
    NSDate *anchorDate = [calendar dateFromComponents:anchorComponents];
    HKStatisticsCollectionQuery *query = [[HKStatisticsCollectionQuery alloc] initWithQuantityType:type
                                                                           quantitySamplePredicate:nil
                                                                                           options:HKStatisticsOptionCumulativeSum
                                                                                        anchorDate:anchorDate
                                                                                intervalComponents:interval];
    query.initialResultsHandler =
    ^(HKStatisticsCollectionQuery *query, HKStatisticsCollection *results, NSError *error) {
        
        if (error) {
            NSError * error = [RCTFitness createErrorWithCode:ErrorNoEvents andDescription:RCT_ERROR_NO_EVENTS];
            [RCTFitness handleRejectBlock:reject error:error];
            return;
        }
        
        NSDate * sd = [RCTFitness dateFromTimeStamp: startDate / 1000];
        NSDate * ed = [RCTFitness dateFromTimeStamp: endDate   / 1000];
        
        NSMutableArray *data = [NSMutableArray arrayWithCapacity:1];
        [results
         enumerateStatisticsFromDate: sd
         toDate:ed
         withBlock:^(HKStatistics *result, BOOL *stop) {
            
            HKQuantity *quantity = result.sumQuantity;
            if (quantity) {
                NSDictionary *elem = @{
                    @"quantity" : @([quantity doubleValueForUnit:[HKUnit meterUnit]]),
                    @"startDate" : [RCTFitness ISO8601StringFromDate: result.startDate],
                    @"endDate" : [RCTFitness ISO8601StringFromDate: result.endDate],
                };
                [data addObject:elem];
            }
        }];
        dispatch_async(dispatch_get_main_queue(), ^{
            resolve(data);
        });
    };
    
    [self.healthStore executeQuery:query];
}

RCT_REMAP_METHOD(getCalories,
                 withStartDate: (double) startDate
                 andEndDate: (double) endDate
                 andInterval: (NSString *) customInterval
                 withCaloriesResolver:(RCTPromiseResolveBlock)resolve
                 andCaloriesRejecter:(RCTPromiseRejectBlock)reject){
    
    if(!startDate){
        NSError * error = [RCTFitness createErrorWithCode:ErrorDateNotCorrect andDescription:RCT_ERROR_DATE_NOT_CORRECT];
        [RCTFitness handleRejectBlock:reject error:error];
        return;
    }

    HKQuantityType *type =
    [HKObjectType quantityTypeForIdentifier: HKQuantityTypeIdentifierActiveEnergyBurned];
    NSCalendar *calendar = [NSCalendar currentCalendar];
    
    NSDateComponents *interval = [[NSDateComponents alloc] init];
    if([customInterval isEqual: @"minute"]){
        interval.minute = 1;
    }else if([customInterval isEqual: @"hour"]){
        interval.hour = 1;
    }else{
        interval.day = 1;
    }
    
    NSDateComponents *anchorComponents = [calendar components:NSCalendarUnitDay | NSCalendarUnitMonth | NSCalendarUnitYear
                                                     fromDate:[NSDate date]];
    anchorComponents.hour = 0;
    NSDate *anchorDate = [calendar dateFromComponents:anchorComponents];
    HKStatisticsCollectionQuery *query = [[HKStatisticsCollectionQuery alloc] initWithQuantityType:type
                                                                           quantitySamplePredicate:nil
                                                                                           options:HKStatisticsOptionCumulativeSum
                                                                                        anchorDate:anchorDate
                                                                                intervalComponents:interval];
    query.initialResultsHandler =
    ^(HKStatisticsCollectionQuery *query, HKStatisticsCollection *results, NSError *error) {
        
        if (error) {
            NSError * error = [RCTFitness createErrorWithCode:ErrorNoEvents andDescription:RCT_ERROR_NO_EVENTS];
            [RCTFitness handleRejectBlock:reject error:error];
            return;
        }
        
        NSDate * sd = [RCTFitness dateFromTimeStamp: startDate / 1000];
        NSDate * ed = [RCTFitness dateFromTimeStamp: endDate   / 1000];
        
        NSMutableArray *data = [NSMutableArray arrayWithCapacity:1];
        [results
         enumerateStatisticsFromDate: sd
         toDate:ed
         withBlock:^(HKStatistics *result, BOOL *stop) {
            HKQuantity *quantity = result.sumQuantity;
            if (quantity) {
                NSDictionary *elem = @{
                    @"quantity" : @([quantity doubleValueForUnit:[HKUnit kilocalorieUnit]]),
                    @"startDate" : [RCTFitness ISO8601StringFromDate: result.startDate],
                    @"endDate" : [RCTFitness ISO8601StringFromDate: result.endDate],
                };
                [data addObject:elem];
            }
        }];
        dispatch_async(dispatch_get_main_queue(), ^{
            resolve(data);
        });
    };
    
    [self.healthStore executeQuery:query];
}

RCT_REMAP_METHOD(getHeartRate,
                 withStartDate: (double) startDate
                 andEndDate: (double) endDate
                 andInterval: (NSString *) customInterval
                 withHeartRateResolver:(RCTPromiseResolveBlock)resolve
                 andHeartRateRejecter:(RCTPromiseRejectBlock)reject){
    
    if(!startDate){
        NSError * error = [RCTFitness createErrorWithCode:ErrorDateNotCorrect andDescription:RCT_ERROR_DATE_NOT_CORRECT];
        [RCTFitness handleRejectBlock:reject error:error];
        return;
    }
    HKQuantityType *type =
    [HKObjectType quantityTypeForIdentifier: HKQuantityTypeIdentifierHeartRate];
    NSCalendar *calendar = [NSCalendar currentCalendar];
   
    NSDateComponents *interval = [[NSDateComponents alloc] init];
    if([customInterval isEqual: @"minute"]){
        interval.minute = 1;
    }else if([customInterval isEqual: @"hour"]){
        interval.hour = 1;
    }else{
        interval.day = 1;
    }
    
    NSDateComponents *anchorComponents = [calendar components:NSCalendarUnitDay | NSCalendarUnitMonth | NSCalendarUnitYear
                                                     fromDate:[NSDate date]];
    anchorComponents.hour = 0;
    NSDate *anchorDate = [calendar dateFromComponents:anchorComponents];
    HKStatisticsCollectionQuery *query = [[HKStatisticsCollectionQuery alloc] initWithQuantityType:type
                                                                           quantitySamplePredicate:nil
                                                                                           options:HKStatisticsOptionDiscreteAverage
                                                                                        anchorDate:anchorDate
                                                                                intervalComponents:interval];
    query.initialResultsHandler =
    ^(HKStatisticsCollectionQuery *query, HKStatisticsCollection *results, NSError *error) {
        
        if (error) {
            NSError * error = [RCTFitness createErrorWithCode:ErrorNoEvents andDescription:RCT_ERROR_NO_EVENTS];
            [RCTFitness handleRejectBlock:reject error:error];
            return;
        }
        
        NSDate * sd = [RCTFitness dateFromTimeStamp: startDate / 1000];
        NSDate * ed = [RCTFitness dateFromTimeStamp: endDate   / 1000];
        
        NSMutableArray *data = [NSMutableArray arrayWithCapacity:1];
        [results
         enumerateStatisticsFromDate: sd
         toDate:ed
         withBlock:^(HKStatistics *result, BOOL *stop) {
            HKQuantity *quantity = result.averageQuantity;
            if (quantity) {
                NSDictionary *elem = @{
                    @"quantity" : @([quantity doubleValueForUnit:[[HKUnit countUnit] unitDividedByUnit:HKUnit.minuteUnit]]),
                    @"startDate" : [RCTFitness ISO8601StringFromDate: result.startDate],
                    @"endDate" : [RCTFitness ISO8601StringFromDate: result.endDate],
                };
                [data addObject:elem];
            }
        }];
        dispatch_async(dispatch_get_main_queue(), ^{
            resolve(data);
        });
    };
    
    [self.healthStore executeQuery:query];
}

RCT_REMAP_METHOD(getSleepAnalysis,
                 withStartDate: (double) startDate
                 andEndDate: (double) endDate
                 withSleepAnalysisResolver:(RCTPromiseResolveBlock)resolve
                 andSleepAnalysisRejecter:(RCTPromiseRejectBlock)reject){
    
    if(!startDate){
        NSError * error = [RCTFitness createErrorWithCode:ErrorDateNotCorrect andDescription:RCT_ERROR_DATE_NOT_CORRECT];
        [RCTFitness handleRejectBlock:reject error:error];
        return;
    }
    
    NSDate * sd = [RCTFitness dateFromTimeStamp: startDate / 1000];
    NSDate * ed = [RCTFitness dateFromTimeStamp: endDate   / 1000];

    HKSampleType *sampleType = [HKSampleType categoryTypeForIdentifier:HKCategoryTypeIdentifierSleepAnalysis];
    NSPredicate *predicate = [HKQuery predicateForSamplesWithStartDate:sd endDate:ed options:HKQueryOptionNone];

    HKSampleQuery *query = [[HKSampleQuery alloc] initWithSampleType:sampleType predicate:predicate limit:0 sortDescriptors:nil resultsHandler:^(HKSampleQuery *query, NSArray *results, NSError *error) {
        if (error) {
            NSError * error = [RCTFitness createErrorWithCode:ErrorNoEvents andDescription:RCT_ERROR_NO_EVENTS];
            [RCTFitness handleRejectBlock:reject error:error];
            return;
        }

        NSMutableArray *data = [NSMutableArray arrayWithCapacity:1];
        
        for (HKCategorySample *sample in results) {
            NSString *startDateString = [RCTFitness ISO8601StringFromDate: sample.startDate];
            NSString *endDateString = [RCTFitness ISO8601StringFromDate: sample.endDate];

            NSString *valueString;

            switch (sample.value) {
                case HKCategoryValueSleepAnalysisInBed:
                  valueString = @"INBED";
                break;
                case HKCategoryValueSleepAnalysisAsleep:
                  valueString = @"ASLEEP";
                break;
               default:
                  valueString = @"UNKNOWN";
               break;
            }
            
            NSDictionary *elem = @{
                    @"value" : valueString,
                    @"sourceName" : [[[sample sourceRevision] source] name],
                    @"sourceId" : [[[sample sourceRevision] source] bundleIdentifier],
                    @"startDate" : startDateString,
                    @"endDate" : endDateString,
            };

            [data addObject:elem];
        }
        
        dispatch_async(dispatch_get_main_queue(), ^{
            resolve(data);
        });
    }];
    
    [self.healthStore executeQuery:query];
}

@end
