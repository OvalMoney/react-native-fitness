//
//  RCTFitness+Errors.h
//  RCTFitness
//
//  Created by Francesco Voto on 20/12/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import "RCTFitness.h"

extern NSString * _Nonnull const RCT_ERROR_HK_NOT_AVAILABLE;
extern NSString * _Nonnull const RCT_ERROR_METHOD_NOT_AVAILABLE;
extern NSString * _Nonnull const RCT_ERROR_DATE_NOT_CORRECT;
extern NSString * _Nonnull const RCT_ERROR_NO_EVENTS;
extern NSString * _Nonnull const RCT_ERROR_EMPTY_PERMISSIONS;

typedef NS_ENUM(NSInteger, RCTFitnessError)
{
    ErrorHKNotAvailable   = -100,
    ErrorMethodNotAvailable,
    ErrorDateNotCorrect,
    ErrorNoEvents,
    ErrorEmptyPermissions,
};

NS_ASSUME_NONNULL_BEGIN

@interface RCTFitness(Errors)
+(NSError*)createErrorWithCode:(NSInteger)code andDescription:(NSString*)description;
+(void)handleRejectBlock:(RCTPromiseRejectBlock)reject error:(NSError*)error;
@end

NS_ASSUME_NONNULL_END

