#import <Foundation/Foundation.h>

#import <Cordova/CDVPlugin.h>

@interface CordovaHttpPlugin : CDVPlugin
- (void)post:(CDVInvokedUrlCommand*)command;
- (void)get:(CDVInvokedUrlCommand*)command;
@end
