//
//  DoricSQLiteLibrary.m
//  DoricCore
//
//  Created by pengfei.zhou on 2020/9/18.
//

#import "DoricFsLibrary.h"
#import "DoricFsPlugin.h"

@implementation DoricFsLibrary
- (void)load:(DoricRegistry *)registry {
    [registry registerNativePlugin:DoricFsPlugin.class withName:@"fs"];
}
@end
