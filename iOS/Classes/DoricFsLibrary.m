//
//  DoricSQLiteLibrary.m
//  DoricCore
//
//  Created by pengfei.zhou on 2020/9/18.
//

#import "DoricFsLibrary.h"
#import "DoricFsPlugin.h"
#import "DoricFileLoader.h"

@implementation DoricFsLibrary
- (void)load:(DoricRegistry *)registry {
    NSString *path = [[NSBundle mainBundle] bundlePath];
    NSString *fullPath = [path stringByAppendingPathComponent:@"bundle_fs.js"];
    NSString *jsContent = [NSString stringWithContentsOfFile:fullPath encoding:NSUTF8StringEncoding error:nil];
    [registry registerJSBundle:jsContent withName:@"doric-fs"];
    [Doric addJSLoader:[DoricFileLoader new]];
    [registry registerNativePlugin:DoricFsPlugin.class withName:@"fs"];
}
@end
