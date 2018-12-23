//
//  RCTFitness+Utils.m
//  RCTFitness
//
//  Created by Francesco Voto on 14/12/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import "RCTFitness+Utils.h"
#include <time.h>

@implementation RCTFitness(Utils)

+ (NSString *)ISO8601StringFromDate: (NSDate*) date {
    struct tm *timeinfo;
    char buffer[80];
    
    time_t rawtime = [date timeIntervalSince1970] - [[NSTimeZone localTimeZone] secondsFromGMT];
    timeinfo = localtime(&rawtime);
    
    strftime(buffer, 80, "%Y-%m-%dT%H:%M:%S%z", timeinfo);
    
    return [NSString stringWithCString:buffer encoding:NSUTF8StringEncoding];
}

+ (NSDate *)dateFromTimeStamp:(NSTimeInterval) timestamp {
    if (!timestamp) {
        return [NSDate date];
    }
    return [NSDate dateWithTimeIntervalSince1970:timestamp];
}

@end
