//
//  RCTFitness+Utils.m
//  RCTFitness
//
//  Created by Francesco Voto on 14/12/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RCTFitness+Utils.h"
#include <time.h>

@implementation RCTFitness(Utils)
    
+ (NSString *)ISO8601StringFromDate: (NSDate*) date {
    // Cache the formatter in thread local storage the first
    // time it's created and then re-use it every other time.
    NSString *cachedISO8601DateFormatterKey = @"cachedNSISO8601DateFormatterKey";
    NSMutableDictionary *threadDictionary = [[NSThread currentThread] threadDictionary];
    NSISO8601DateFormatter *dateFormatter = threadDictionary[cachedISO8601DateFormatterKey];
    
    if (!dateFormatter) {
        dateFormatter = [[NSISO8601DateFormatter alloc] init];
        threadDictionary[cachedISO8601DateFormatterKey] = dateFormatter;
    }
    
    return [dateFormatter stringFromDate:date];
}


+ (NSDate *)dateFromTimeStamp:(NSTimeInterval) timestamp {
    if (!timestamp) {
        return [NSDate date];
    }
    return [NSDate dateWithTimeIntervalSince1970:timestamp];
}

@end
