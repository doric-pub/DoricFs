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
    [[DoricJSLoaderManager instance] addJSLoader:[DoricFileLoader new];
    [registry registerNativePlugin:DoricFsPlugin.class withName:@"fs"];
}
@end
