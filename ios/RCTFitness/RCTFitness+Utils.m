//
//  RCTFitness+Utils.m
//  RCTFitness
//
//  Created by Francesco Voto on 14/12/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RCTFitness+Utils.h"

@implementation RCTFitness(Utils)

+ (NSISO8601DateFormatter *)dateFormatter {
    static dispatch_once_t once;
    static NSISO8601DateFormatter *dateFormatter;
    dispatch_once(&once, ^{
        dateFormatter = [[NSISO8601DateFormatter alloc] init];
    });
    return dateFormatter;
}

+ (NSString *)ISO8601StringFromDate:(NSDate *)date {
    return [[self dateFormatter] stringFromDate:date];
}

+ (NSDate *)dateFromTimeStamp:(NSTimeInterval)timestamp {
    if (!timestamp) {
        return [NSDate date];
    }
    
    return [NSDate dateWithTimeIntervalSince1970:timestamp];
}

@end
