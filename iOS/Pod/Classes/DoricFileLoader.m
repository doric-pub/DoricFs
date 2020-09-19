//
//  DoricFileLoader.m
//  DoricFs
//
//  Created by pengfei.zhou on 2020/9/19.
//

#import "DoricFileLoader.h"

@implementation DoricFileLoader
- (BOOL)filter:(NSString *)source {
    return [source hasPrefix:@"file:///"] || [source hasPrefix:@"/"];
}

- (DoricAsyncResult <NSString *> *)request:(NSString *)source {
    DoricAsyncResult *ret = [DoricAsyncResult new];
    NSError *error;
    NSString *jsContent;
    if ([source hasPrefix:@"file:///"]) {
        NSURL *URL = [NSURL URLWithString:source];
        jsContent = [NSString stringWithContentsOfURL:URL encoding:NSUTF8StringEncoding error:&error];
    } else {
        NSData *data = [[NSFileManager defaultManager] contentsAtPath:source];
        jsContent = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    }
    if (error) {
        [ret setupError:[NSException new]];
    } else {
        [ret setupResult:jsContent];
    }
    return ret;
}


@end