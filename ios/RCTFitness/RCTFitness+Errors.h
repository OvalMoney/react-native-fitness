//
//  RCTFitness+Errors.h
//  RCTFitness
//
//  Created by Francesco Voto on 20/12/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import "RCTFitness.h"

extern NSString * const RCT_ERROR_HK_NOT_AVAILABLE;
extern NSString * const RCT_ERROR_METHOD_NOT_AVAILABLE;
extern NSString * const RCT_ERROR_DATE_NOT_CORRECT;
extern NSString * const RCT_ERROR_NO_EVENTS;

typedef NS_ENUM(NSInteger, RCTFitnessError)
{
    ErrorHKNotAvailable   = -100,
    ErrorMethodNotAvailable,
    ErrorDateNotCorrect,
    ErrorNoEvents
};

NS_ASSUME_NONNULL_BEGIN

@interface RCTFitness(Errors)
+(NSError*)createErrorWithCode:(NSInteger)code andDescription:(NSString*)description;
+(void)handleRejectBlock:(RCTPromiseRejectBlock)reject error:(NSError*)error;
@end

NS_ASSUME_NONNULL_END

