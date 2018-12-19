#import <HealthKit/HealthKit.h>
#import "RCTFitness.h"
#import "RCTFitness+Utils.h"

#define ERROR_HEALTH_NOT_AVAILABLE @"HealthKit is not available";

@interface RCTFitness()
@property (nonatomic, strong) HKHealthStore *healthStore;
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

#pragma mark - constants export

- (NSDictionary *)constantsToExport{
    return @{
             @"Platform" : @"AppleHealth",
             };
}

RCT_REMAP_METHOD(requestPermissions,
                 withRequestResolver:(RCTPromiseResolveBlock)resolve
                 andRequestRejecter:(RCTPromiseRejectBlock)reject){

    if ([HKHealthStore isHealthDataAvailable]) {
        NSMutableSet *perms = [NSMutableSet setWithCapacity:1];
        [perms addObject:[HKObjectType quantityTypeForIdentifier: HKQuantityTypeIdentifierDistanceWalkingRunning]];
        [perms addObject:[HKObjectType quantityTypeForIdentifier: HKQuantityTypeIdentifierStepCount]];
        [self.healthStore requestAuthorizationToShareTypes:nil readTypes:perms completion:^(BOOL success, NSError *error) {
            if (!success) {
                reject(@"no_available", @"HealthKit is not available", error);
                return;
            }
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                    resolve(@YES);
                });
        }];
    }else{
        reject(@"no_available", @"HealthKit is not available", [NSError new]);
    }
}

RCT_REMAP_METHOD(isAuthorized,
                 withAuthorizedResolver:(RCTPromiseResolveBlock)resolve
                 andAuthorizedRejecter:(RCTPromiseRejectBlock)reject){
    if (@available(iOS 12.0, *)) {
        if ([HKHealthStore isHealthDataAvailable]) {
            NSMutableSet *perms = [NSMutableSet setWithCapacity:1];
            [perms addObject:[HKObjectType quantityTypeForIdentifier: HKQuantityTypeIdentifierDistanceWalkingRunning]];
            [perms addObject:[HKObjectType quantityTypeForIdentifier: HKQuantityTypeIdentifierStepCount]];

            [self.healthStore getRequestStatusForAuthorizationToShareTypes:[NSSet set] readTypes:perms completion:^(HKAuthorizationRequestStatus status, NSError *error) {
                dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                    resolve(status == HKAuthorizationStatusSharingAuthorized ? @YES : @NO);
                });
            }];
        }else{
            reject(@"no_available", @"HealthKit is not available", [NSError new]);
        }
    }else{
        reject(@"no_available", @"Method not available", [NSError new]);
    }

}

RCT_REMAP_METHOD(getSteps,
                 params:(NSDictionary *)params
                 withStepsResolver:(RCTPromiseResolveBlock)resolve
                 andStepsRejecter:(RCTPromiseRejectBlock)reject){

    NSString * startDateString = [params objectForKey:@"startDate"];
    NSString * endDateString = [params objectForKey:@"endDate"];
    if(startDateString == nil){
        reject(@"no_events", @"There were no Start Date", [NSError new]);
        return;
    }

    HKQuantityType *type =
    [HKObjectType quantityTypeForIdentifier:HKQuantityTypeIdentifierStepCount];

    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents *interval = [[NSDateComponents alloc] init];
    interval.day = 1;

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
             reject(@"no_events", @"There were no events", error);
            abort();
        }

        NSDate * sd = [RCTFitness dateFromISO8601String: startDateString];
                NSDate * ed = [RCTFitness dateFromISO8601String: endDateString];

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
                params:(NSDictionary *)params
                 withDistanceResolver:(RCTPromiseResolveBlock)resolve
                 andDistanceRejecter:(RCTPromiseRejectBlock)reject){

    NSString * startDateString = [params objectForKey:@"startDate"];
    NSString * endDateString = [params objectForKey:@"endDate"];
    if(startDateString == nil){
        reject(@"no_events", @"There were no Start Date", [NSError new]);
        return;
    }

    HKQuantityType *type =
    [HKObjectType quantityTypeForIdentifier:HKQuantityTypeIdentifierDistanceWalkingRunning];

    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents *interval = [[NSDateComponents alloc] init];
    interval.day = 1;

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
            reject(@"no_events", @"There were no events", error);
            abort();
        }

        NSMutableArray *data = [NSMutableArray arrayWithCapacity:1];
        [results
         enumerateStatisticsFromDate: [RCTFitness dateFromISO8601String: startDateString]
         toDate:[RCTFitness dateFromISO8601String: endDateString]
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

@end

