//
//  RCTFitness+Utils.h
//  RCTFitness
//
//  Created by Francesco Voto on 14/12/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import "RCTFitness.h"

NS_ASSUME_NONNULL_BEGIN

@interface RCTFitness(Utils)
+ (NSString *)ISO8601StringFromDate: (NSDate*) date;
+ (NSDate *)dateFromTimeStamp: (NSTimeInterval) timestamp;
@end

NS_ASSUME_NONNULL_END
