//
//  DoricSQLitePlugin.m
//  Pods-DoricSQLite
//
//  Created by pengfei.zhou on 2020/9/18.
//

#import "DoricFsPlugin.h"

@interface DoricFsPlugin () <UIDocumentPickerDelegate, UIDocumentInteractionControllerDelegate>
@property(nonatomic, strong) DoricPromise *currentPromise;
@end

@implementation DoricFsPlugin

- (void)getDocumentsDir:(NSDictionary *)dic withPromise:(DoricPromise *)promise {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSString *docDir = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) firstObject];
        [promise resolve:docDir];
    });
}

- (void)exists:(NSString *)paramPath withPromise:(DoricPromise *)promise {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSString *path = paramPath;
        if ([path hasPrefix:@"assets"]) {
            path = [NSBundle.mainBundle.bundlePath stringByAppendingPathComponent:[path substringFromIndex:@"assets://".length]];
        }
        BOOL exists = [[NSFileManager defaultManager] fileExistsAtPath:path];
        [promise resolve:@(exists)];
    });
}


- (void)stat:(NSString *)paramPath withPromise:(DoricPromise *)promise {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSString *path = paramPath;
        if ([path hasPrefix:@"assets"]) {
            path = [NSBundle.mainBundle.bundlePath stringByAppendingPathComponent:[path substringFromIndex:@"assets://".length]];
        }
        NSError *error;
        NSDictionary *fileAttributes = [[NSFileManager defaultManager] attributesOfItemAtPath:path error:&error];
        if (error) {
            [promise reject:error.localizedDescription];
        } else {
            [promise resolve:fileAttributes];
        }
    });
}


- (void)isFile:(NSString *)paramPath withPromise:(DoricPromise *)promise {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSString *path = paramPath;
        if ([path hasPrefix:@"assets"]) {
            path = [NSBundle.mainBundle.bundlePath stringByAppendingPathComponent:[path substringFromIndex:@"assets://".length]];
        }
        BOOL isDirectory = NO;
        BOOL exists = [[NSFileManager defaultManager] fileExistsAtPath:path isDirectory:&isDirectory];
        if (exists) {
            [promise resolve:@(!isDirectory)];
        } else {
            [promise reject:[NSString stringWithFormat:@"File %@ does not exist", path]];
        }
    });
}

- (void)isDirectory:(NSString *)paramPath withPromise:(DoricPromise *)promise {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSString *path = paramPath;
        if ([path hasPrefix:@"assets"]) {
            path = [NSBundle.mainBundle.bundlePath stringByAppendingPathComponent:[path substringFromIndex:@"assets://".length]];
        }
        BOOL isDirectory = NO;
        BOOL exists = [[NSFileManager defaultManager] fileExistsAtPath:path isDirectory:&isDirectory];
        if (exists) {
            [promise resolve:@(isDirectory)];
        } else {
            [promise reject:[NSString stringWithFormat:@"File %@ does not exist", path]];
        }
    });
}

- (void)mkdir:(NSString *)path withPromise:(DoricPromise *)promise {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        BOOL result = [[NSFileManager defaultManager] createDirectoryAtPath:path withIntermediateDirectories:YES attributes:nil error:nil];
        [promise resolve:@(result)];
    });
}

- (void)readDir:(NSString *)paramPath withPromise:(DoricPromise *)promise {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSString *path = paramPath;
        if ([path hasPrefix:@"assets"]) {
            path = [NSBundle.mainBundle.bundlePath stringByAppendingPathComponent:[path substringFromIndex:@"assets://".length]];
        }
        BOOL isDirectory = NO;
        BOOL exists = [[NSFileManager defaultManager] fileExistsAtPath:path isDirectory:&isDirectory];
        if (exists) {
            if (isDirectory) {
                NSArray <NSString *> *dirArray = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:path error:nil];
                NSArray <NSString *> *result = [dirArray map:^id(NSString *obj) {
                    return [path stringByAppendingPathComponent:obj];
                }];
                [promise resolve:result];
            } else {
                [promise reject:[NSString stringWithFormat:@"File %@ is not directory", path]];
            }
        } else {
            [promise reject:[NSString stringWithFormat:@"File %@ does not exist", path]];
        }
    });
}


- (void)readFile:(NSString *)paramPath withPromise:(DoricPromise *)promise {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSString *path = paramPath;
        if ([path hasPrefix:@"file://"]) {
            NSError *error;
            NSURL *URL = [NSURL URLWithString:path];
            NSString *content = [NSString stringWithContentsOfURL:URL encoding:NSUTF8StringEncoding error:&error];
            if (error) {
                [promise reject:error.localizedDescription];
            } else {
                [promise resolve:content];
            }
        } else {
            if ([path hasPrefix:@"assets"]) {
                path = [NSBundle.mainBundle.bundlePath stringByAppendingPathComponent:[path substringFromIndex:@"assets://".length]];
            }
            BOOL isDirectory = NO;
            BOOL exists = [[NSFileManager defaultManager] fileExistsAtPath:path isDirectory:&isDirectory];

            if (exists) {
                if (!isDirectory) {
                    NSData *data = [[NSFileManager defaultManager] contentsAtPath:path];
                    [promise resolve:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
                } else {
                    [promise reject:[NSString stringWithFormat:@"File %@ is not file", path]];
                }
            } else {
                [promise reject:[NSString stringWithFormat:@"File %@ does not exist", path]];
            }
        }
    });
}


- (void)readBinaryFile:(NSString *)paramPath withPromise:(DoricPromise *)promise {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSString *path = paramPath;
        if ([path hasPrefix:@"file://"]) {
            NSError *error;
            NSURL *URL = [NSURL URLWithString:path];
            NSData *data = [NSData dataWithContentsOfURL:URL options:NSDataReadingUncached error:&error];
            if (error) {
                [promise reject:error.localizedDescription];
            } else {
                [promise resolve:data];
            }
        } else {
            if ([path hasPrefix:@"assets"]) {
                path = [NSBundle.mainBundle.bundlePath stringByAppendingPathComponent:[path substringFromIndex:@"assets://".length]];
            }
            BOOL isDirectory = NO;
            BOOL exists = [[NSFileManager defaultManager] fileExistsAtPath:path isDirectory:&isDirectory];

            if (exists) {
                if (!isDirectory) {
                    NSData *data = [[NSFileManager defaultManager] contentsAtPath:path];
                    [promise resolve:data];
                } else {
                    [promise reject:[NSString stringWithFormat:@"File %@ is not file", path]];
                }
            } else {
                [promise reject:[NSString stringWithFormat:@"File %@ does not exist", path]];
            }
        }
    });
}

- (void)writeFile:(NSDictionary *)params withPromise:(DoricPromise *)promise {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSError *error;
        NSString *path = params[@"path"];
        id content = params[@"content"];
        if ([content isKindOfClass: NSString.class]) {
            [(NSString *)content writeToFile:path atomically:YES encoding:NSUTF8StringEncoding error:&error];
        } else if ([content isKindOfClass: NSData.class]) {
            [(NSData *)content writeToFile:path atomically:YES];
        } else {
            [promise reject:@"Donot support class type"];
        }
        if (error) {
            [promise reject:error.localizedDescription];
        } else {
            [promise resolve:nil];
        }
    });
}

- (void)appendFile:(NSDictionary *)params withPromise:(DoricPromise *)promise {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSError *error;
        NSString *path = params[@"path"];
        id content = params[@"content"];
        BOOL isDirectory = NO;
        BOOL exists = [[NSFileManager defaultManager] fileExistsAtPath:path isDirectory:&isDirectory];
        if (exists) {
            if (!isDirectory) {
                NSData *stringData;
                if ([content isKindOfClass: NSString.class]) {
                    stringData = [content dataUsingEncoding:NSUTF8StringEncoding];
                } else if ([content isKindOfClass: NSData.class]){
                    stringData = content;
                } else {
                     [promise reject:error.localizedDescription];
                     return;
                }
                NSFileHandle *fileHandle = [NSFileHandle fileHandleForUpdatingAtPath:path];
                [fileHandle seekToEndOfFile];
                [fileHandle writeData:stringData];
                [fileHandle closeFile];
            } else {
                [promise reject:[NSString stringWithFormat:@"File %@ is directory", path]];
            }
        } else {
            [content writeToFile:path atomically:YES encoding:NSUTF8StringEncoding error:&error];
        }
        if (error) {
            [promise reject:error.localizedDescription];
        } else {
            [promise resolve:nil];
        }
    });
}

- (void)delete:(NSString *)path withPromise:(DoricPromise *)promise {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSError *error;
        BOOL isDelete = [[NSFileManager defaultManager] removeItemAtPath:path error:&error];
        if (error) {
            [promise reject:error.localizedDescription];
        } else {
            [promise resolve:@(isDelete)];
        }
    });
}

- (void)rename:(NSDictionary *)dic withPromise:(DoricPromise *)promise {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSError *error;
        NSString *src = dic[@"src"];
        NSString *dest = dic[@"dest"];
        BOOL isSuccess = [[NSFileManager defaultManager] moveItemAtPath:src toPath:dest error:&error];
        if (error) {
            [promise reject:error.localizedDescription];
        } else {
            [promise resolve:@(isSuccess)];
        }
    });
}

- (void)copy:(NSDictionary *)dic withPromise:(DoricPromise *)promise {
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        NSError *error;
        NSString *src = dic[@"src"];
        NSString *dest = dic[@"dest"];
        if ([src hasPrefix:@"assets"]) {
            src = [NSBundle.mainBundle.bundlePath stringByAppendingPathComponent:[src substringFromIndex:@"assets://".length]];
        }
        BOOL isSuccess = [[NSFileManager defaultManager] copyItemAtPath:src toPath:dest error:&error];
        if (error) {
            [promise reject:error.localizedDescription];
        } else {
            [promise resolve:@(isSuccess)];
        }
    });
}

- (void)choose:(NSDictionary *)dic withPromise:(DoricPromise *)promise {
    dispatch_async(dispatch_get_main_queue(), ^{
        self.currentPromise = promise;
        NSArray *uti = dic[@"uniformTypeIdentifiers"];
        UIDocumentPickerViewController *documentPickerViewController = [[UIDocumentPickerViewController alloc] initWithDocumentTypes:uti
                                                                                                                              inMode:UIDocumentPickerModeOpen];
        documentPickerViewController.delegate = self;
        [((UIViewController *) (self.doricContext.navigator)).navigationController presentViewController:documentPickerViewController
                                                                                                animated:YES
                                                                                              completion:nil];
    });
}

- (void)documentPicker:(UIDocumentPickerViewController *)controller didPickDocumentsAtURLs:(NSArray <NSURL *> *)urls {
    NSURL *url = urls.firstObject;
    NSFileCoordinator *fileCoordinator = [[NSFileCoordinator alloc] init];
    NSError *error = nil;
    [fileCoordinator coordinateReadingItemAtURL:url options:NSFileCoordinatorReadingWithoutChanges error:&error byAccessor:^(NSURL *newURL) {
        [self.currentPromise resolve:newURL.absoluteString];
    }];
}

- (void)documentPickerWasCancelled:(UIDocumentPickerViewController *)controller {
    [self.currentPromise reject:@"Cancelled"];
}


@end
