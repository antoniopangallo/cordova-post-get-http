#import "CordovaHttpPlugin.h"
#import <Foundation/Foundation.h>


@interface CordovaHttpPlugin()
@end


@implementation CordovaHttpPlugin {
}

- (void)post:(CDVInvokedUrlCommand*)command {
    [self makeCall:command typeCall:@"POST"];
}

- (void)get:(CDVInvokedUrlCommand*)command {
    [self makeCall:command typeCall:@"GET"];
}

- (void)makeCall:(CDVInvokedUrlCommand*)command typeCall:(NSString*)type {
    NSData *postData;
    if ([[command.arguments objectAtIndex:1] isKindOfClass:[NSString class]])
        postData = [[NSData alloc] initWithData:[[command.arguments objectAtIndex:1] dataUsingEncoding:NSUTF8StringEncoding]];
    else if  ([[command.arguments objectAtIndex:1] isKindOfClass:[NSDictionary class]]) {
        NSMutableString *result = [NSMutableString string];
        NSDictionary *dict = [command.arguments objectAtIndex:1];
        for (NSString *key in [dict allKeys]) {
            id value = dict[key];
            if (result.length) {
                [result appendString:@"&"];
            }
            [result appendFormat:@"%@=%@", key, [value description]];
        }
        postData = [[NSData alloc] initWithData:[result dataUsingEncoding:NSUTF8StringEncoding]];
    }
    
    NSLog(@"%@", postData);
    
    NSString *url = [command.arguments objectAtIndex:0];
    NSDictionary *headers = [command.arguments objectAtIndex:2];
    
    CordovaHttpPlugin* __weak weakSelf = self;
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]
                                                           cachePolicy:NSURLRequestUseProtocolCachePolicy
                                                       timeoutInterval:10.0];
    [request setHTTPMethod:type];
    [request setAllHTTPHeaderFields:headers];
    [request setHTTPBody:postData];
    
    NSURLSession *session = [NSURLSession sharedSession];
    NSURLSessionDataTask *dataTask = [session dataTaskWithRequest:request
                                                completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
                                                    if (error) {
                                                        NSLog(@"%@", error);
                                                        NSMutableDictionary *dictionary = [NSMutableDictionary dictionary];
                                                        
                                                        NSMutableString *result = [NSMutableString string];
                                                        NSDictionary *dict = error.userInfo;
                                                        
                                                        if ([dict[@"NSLocalizedDescription"] isKindOfClass:[NSString class]])
                                                            [result appendString:dict[@"NSLocalizedDescription"]];
                                                        else
                                                            [result appendString:@"Error"];
                                                        [dictionary setObject:result forKey:@"error"];
                                                        NSString *status = [@(error.code) stringValue];
                                                        [dictionary setValue:status forKey:@"status"];
                                                        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:dictionary];
                                                        [weakSelf.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                                                    } else {
                                                        NSMutableDictionary *dictionary = [NSMutableDictionary dictionary];
                                                        NSString *mydata = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
                                                        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *) response;
                                                        NSLog(@"%@", httpResponse);
                                                        NSString *status = [@(httpResponse.statusCode) stringValue];
                                                        [dictionary setValue:status forKey:@"status"];
                                                        [dictionary setObject:mydata forKey:@"data"];
                                                        [dictionary setObject:httpResponse.allHeaderFields forKey:@"headers"];
                                                        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:dictionary];
                                                        [weakSelf.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                                                    }
                                                }];
    [dataTask resume];
}
@end
