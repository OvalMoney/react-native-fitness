//
//  RCTFitness+Errors.m
//  RCTFitness
//
//  Created by Francesco Voto on 20/12/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import "RCTFitness+Errors.h"

NSString * const RCT_ERROR_HK_NOT_AVAILABLE     = @"Health Kit not available";
NSString * const RCT_ERROR_METHOD_NOT_AVAILABLE = @"Method not available";
NSString * const RCT_ERROR_DATE_NOT_CORRECT     = @"Date not correct";
NSString * const RCT_ERROR_NO_EVENTS            = @"No events found";

static NSString *const RCTErrorDomain = @"com.ovalmoney.fitness";

@implementation RCTFitness(Errors)

+(NSError*)createErrorWithCode:(NSInteger)code andDescription:(NSString*)description
{
    NSString *safeDescription = (description == nil) ? @"" : description;
    return [NSError errorWithDomain:RCTErrorDomain code:code userInfo:@{NSLocalizedDescriptionKey: safeDescription}];
}

+(void)handleRejectBlock:(RCTPromiseRejectBlock)reject error:(NSError*)error
{
    reject([NSString stringWithFormat: @"%ld", (long)error.code], error.localizedDescription, error);
}

@end


