#import "TurboSongs.h"
#import <MediaPlayer/MediaPlayer.h>


@implementation TurboSongs
RCT_EXPORT_MODULE()

// Example method
// See // https://reactnative.dev/docs/native-modules-ios
RCT_EXPORT_METHOD(getAll:(NSDictionary *)options
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    
    if([MPMediaLibrary authorizationStatus] != MPMediaLibraryAuthorizationStatusAuthorized){
        reject(@"Permission denied",@"Permission denied",0);
        return;
    }
    
    NSInteger limit = [options objectForKey:@"limit"] ? [options[@"limit"] integerValue] : 20;
    NSInteger offset =  [options objectForKey:@"offset"] ? [options[@"offset"] integerValue] : 0;
    NSInteger coverQty = [options objectForKey:@"coverQuality"] ? [options[@"coverQuality"] integerValue] : 100;
    NSInteger minSongDuration = [options objectForKey:@"minSongDuration"] ? [options[@"minSongDuration"] integerValue] / 1000 : 100;
    
    NSMutableArray *mutableSongsToSerialize = [NSMutableArray array];

    MPMediaQuery *mediaQuery = [MPMediaQuery songsQuery]; // run a query on song media type
    [mediaQuery addFilterPredicate:[MPMediaPropertyPredicate predicateWithValue:@(NO)
                       forProperty:MPMediaItemPropertyIsCloudItem]]; // ensure what we retrieve is on device

    NSArray *allMediaItems = [mediaQuery items];
    
    // Define the range for the limited subset
    NSRange range = NSMakeRange(offset, MIN(limit, allMediaItems.count - offset));
    
    // Get the subset of media items within the specified range
    NSArray<MPMediaItem *> *limitedMediaItems = [allMediaItems subarrayWithRange:range];
  
    for (MPMediaItem *song in limitedMediaItems) {
        NSDictionary *songDictionary = [NSMutableDictionary dictionary];
        
        NSString *durationStr = [song valueForProperty: MPMediaItemPropertyPlaybackDuration];
        NSInteger durationInt = [durationStr integerValue];
        
        NSURL *assetURL = [song valueForProperty:MPMediaItemPropertyAssetURL];
        AVAsset *asset = [AVAsset assetWithURL:assetURL];

        if ([asset hasProtectedContent] == NO and durationInt >= minSongDuration) {
            
            NSString *title = [song valueForProperty: MPMediaItemPropertyTitle];
            NSString *albumTitle = [song valueForProperty: MPMediaItemPropertyAlbumTitle];
            NSString *albumArtist = [song valueForProperty: MPMediaItemPropertyAlbumArtist];
            NSString *genre = [song valueForProperty: MPMediaItemPropertyGenre]; // filterable
            
            MPMediaItemArtwork *artwork = [song valueForProperty: MPMediaItemPropertyArtwork];

            [songDictionary setValue:[NSString stringWithString:assetURL.absoluteString] forKey:@"url"];
            [songDictionary setValue:[NSString stringWithString:title] forKey:@"title"];
            [songDictionary setValue:[NSString stringWithString:albumTitle] forKey:@"album"];
            [songDictionary setValue:[NSString stringWithString:albumArtist] forKey:@"artist"];
            [songDictionary setValue:[NSNumber numberWithInt:durationInt * 1000] forKey:@"duration"];
            [songDictionary setValue:[NSString stringWithString:genre] forKey:@"genre"];
            
            if (artwork != nil) {
                UIImage *image = [artwork imageWithSize:CGSizeMake(coverQty, coverQty)];
                // http://www.12qw.ch/2014/12/tooltip-decoding-base64-images-with-chrome-data-url/
                // http://stackoverflow.com/a/510444/185771
                NSString *base64 = [NSString stringWithFormat:@"%@%@", @"data:image/jpeg;base64,", [self imageToNSString:image]];
                [songDictionary setValue:base64 forKey:@"cover"];
            } else {
                [songDictionary setValue:@"" forKey:@"cover"];
            }
            
            [mutableSongsToSerialize addObject:songDictionary];
        }
    }

    resolve(mutableSongsToSerialize);
}

RCT_EXPORT_METHOD(getAlbums:(NSDictionary *)options
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    
    if([MPMediaLibrary authorizationStatus] != MPMediaLibraryAuthorizationStatusAuthorized){
        reject(@"Permission denied",@"Permission denied",0);
        return;
    }
    
    NSInteger limit = [options objectForKey:@"limit"] ? [options[@"limit"] integerValue] : 20;
    NSInteger offset =  [options objectForKey:@"offset"] ? [options[@"offset"] integerValue] : 0;
    NSInteger coverQty = [options objectForKey:@"coverQuality"] ? [options[@"coverQuality"] integerValue] : 100;
    NSString *artist = options[@"artist"];
    
    if(artist.length == 0){
        reject(@"Artist name must not be empty",@"Artist name must not be empty",0);
        return;
    }

    NSMutableArray *mutableSongsToSerialize = [NSMutableArray array];

    MPMediaQuery *mediaQuery = [MPMediaQuery albumsQuery]; // run a query on song media type
    [mediaQuery addFilterPredicate:[MPMediaPropertyPredicate predicateWithValue:@(NO)
                       forProperty:MPMediaItemPropertyIsCloudItem]]; // ensure what we retrieve is on device
    // this is returning all songs no matter what
    [mediaQuery addFilterPredicate:[
        MPMediaPropertyPredicate
        predicateWithValue:artist
        forProperty:MPMediaItemPropertyArtist
        comparisonType: MPMediaPredicateComparisonContains
    ]
    ];

    NSArray *allMediaItems = [mediaQuery items];
    
    // Define the range for the limited subset
    NSRange range = NSMakeRange(offset, MIN(limit, allMediaItems.count - offset));
    
    // Get the subset of media items within the specified range
    NSArray<MPMediaItem *> *limitedMediaItems = [allMediaItems subarrayWithRange:range];
  
    for (MPMediaItem *album in limitedMediaItems) {
        NSDictionary *songDictionary = [NSMutableDictionary dictionary];
        
        NSURL *assetURL = [album valueForProperty:MPMediaItemPropertyAssetURL];
        AVAsset *asset = [AVAsset assetWithURL:assetURL];

        if ([asset hasProtectedContent] == NO) {
            
            NSString *albumTitle = [album valueForProperty: MPMediaItemPropertyAlbumTitle]; // filterable
            NSString *albumArtist = [album valueForProperty: MPMediaItemPropertyAlbumArtist]; //
            NSString *numberOfSongs = [album valueForProperty: MPMediaItemPropertyAlbumTrackCount]; //
            MPMediaItemArtwork *artwork = [album valueForProperty: MPMediaItemPropertyArtwork];
       
            [songDictionary setValue:[NSString stringWithString:assetURL.absoluteString] forKey:@"url"];
            [songDictionary setValue:[NSString stringWithString:albumTitle] forKey:@"album"];
            [songDictionary setValue:[NSString stringWithString:albumArtist] forKey:@"artist"];
            [songDictionary setValue:[NSString stringWithString:numberOfSongs] forKey:@"numberOfSongs"];
            
            if (artwork != nil) {
                UIImage *image = [artwork imageWithSize:CGSizeMake(128, 128)];
                // http://www.12qw.ch/2014/12/tooltip-decoding-base64-images-with-chrome-data-url/
                // http://stackoverflow.com/a/510444/185771
                NSString *base64 = [NSString stringWithFormat:@"%@%@", @"data:image/jpeg;base64,", [self imageToNSString:image]];
                [songDictionary setValue:base64 forKey:@"cover"];
            } else {
                [songDictionary setValue:@"" forKey:@"cover"];
            }
            
            [mutableSongsToSerialize addObject:songDictionary];
        }
    }

    resolve(mutableSongsToSerialize);
}

RCT_EXPORT_METHOD(search:(NSDictionary *)options
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    if([MPMediaLibrary authorizationStatus] != MPMediaLibraryAuthorizationStatusAuthorized){
        reject(@"Permission denied",@"Permission denied",0);
        return;
    }
    
    NSInteger limit = [options objectForKey:@"limit"] ? [options[@"limit"] integerValue] : 20;
    NSInteger offset =  [options objectForKey:@"offset"] ? [options[@"offset"] integerValue] : 0;
    NSInteger coverQty = [options objectForKey:@"coverQuality"] ? [options[@"coverQuality"] integerValue] : 100;
    NSString *searchBy = options[@"searchBy"];
    
    if(searchBy.length == 0){
        reject(@"Search param must not be empty",@"Search param must not be empty",0);
        return;
    }

    NSMutableArray *mutableSongsToSerialize = [NSMutableArray array];
    
    MPMediaQuery *mediaQuery = [MPMediaQuery songsQuery]; // run a query on song media type
    [mediaQuery addFilterPredicate:[MPMediaPropertyPredicate predicateWithValue:@(NO)
                       forProperty:MPMediaItemPropertyIsCloudItem]]; // ensure what we retrieve is on device
    
    NSPredicate *filters = [NSPredicate predicateWithFormat:@"title contains[cd] %@ OR albumTitle contains[cd] %@ OR artist contains[cd] %@", searchBy, searchBy, searchBy];
    
    NSArray *allMediaItems = [[mediaQuery items] filteredArrayUsingPredicate:filters];

    
    // Define the range for the limited subset
    NSRange range = NSMakeRange(offset, MIN(limit, allMediaItems.count - offset));
    
    // Get the subset of media items within the specified range
    NSArray<MPMediaItem *> *limitedMediaItems = [allMediaItems subarrayWithRange:range];
  
    for (MPMediaItem *song in limitedMediaItems) {
        NSDictionary *songDictionary = [NSMutableDictionary dictionary];
        
        NSURL *assetURL = [song valueForProperty:MPMediaItemPropertyAssetURL];
        AVAsset *asset = [AVAsset assetWithURL:assetURL];

        if ([asset hasProtectedContent] == NO) {
            
            NSString *durationStr = [song valueForProperty: MPMediaItemPropertyPlaybackDuration];
            NSInteger durationInt = [durationStr integerValue];
            
            NSString *title = [song valueForProperty: MPMediaItemPropertyTitle];
            NSString *albumTitle = [song valueForProperty: MPMediaItemPropertyAlbumTitle];
            NSString *albumArtist = [song valueForProperty: MPMediaItemPropertyAlbumArtist];
            NSString *genre = [song valueForProperty: MPMediaItemPropertyGenre]; // filterable
            
            MPMediaItemArtwork *artwork = [song valueForProperty: MPMediaItemPropertyArtwork];

            [songDictionary setValue:[NSString stringWithString:assetURL.absoluteString] forKey:@"url"];
            [songDictionary setValue:[NSString stringWithString:title] forKey:@"title"];
            [songDictionary setValue:[NSString stringWithString:albumTitle] forKey:@"album"];
            [songDictionary setValue:[NSString stringWithString:albumArtist] forKey:@"artist"];
            [songDictionary setValue:[NSNumber numberWithInt:durationInt * 1000] forKey:@"duration"];
            [songDictionary setValue:[NSString stringWithString:genre] forKey:@"genre"];
            
            if (artwork != nil) {
                UIImage *image = [artwork imageWithSize:CGSizeMake(coverQty, coverQty)];
                // http://www.12qw.ch/2014/12/tooltip-decoding-base64-images-with-chrome-data-url/
                // http://stackoverflow.com/a/510444/185771
                NSString *base64 = [NSString stringWithFormat:@"%@%@", @"data:image/jpeg;base64,", [self imageToNSString:image]];
                [songDictionary setValue:base64 forKey:@"cover"];
            } else {
                [songDictionary setValue:@"" forKey:@"cover"];
            }
            
            [mutableSongsToSerialize addObject:songDictionary];
        }
    }

    resolve(mutableSongsToSerialize);
}


// http://stackoverflow.com/questions/22243854/encode-image-to-base64-get-a-invalid-base64-string-ios-using-base64encodedstri
- (NSString *)imageToNSString:(UIImage *)image
{
    NSData *data = UIImagePNGRepresentation(image);
    
    return [data base64EncodedStringWithOptions:NSDataBase64EncodingEndLineWithLineFeed];
}

// Don't compile this code when we build for the old architecture.
#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeTurboSongsSpecJSI>(params);
}
#endif

@end
