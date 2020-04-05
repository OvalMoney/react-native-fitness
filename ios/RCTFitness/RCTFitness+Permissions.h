#import "RCTFitness.h"

@class HKObjectType;

typedef NS_ENUM(NSInteger, RCTFitnessPermissionKind)
{
    STEP = 0,
    DISTANCE,
    ACTIVITY,
    CALORIES,
    HEART_RATE,
    SLEEP_ANALYSIS,
};

typedef NS_ENUM(NSInteger, RCTFitnessPermissionAccess)
{
    READ = 0,
    WRITE,
};

NS_ASSUME_NONNULL_BEGIN

@interface RCTFitness(Permissions)
+(HKObjectType*)getQuantityType:(RCTFitnessPermissionKind)code;
@end

NS_ASSUME_NONNULL_END
