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
    
    id sortOrderValue = [options objectForKey:@"sortOrder"];

    NSString *sortOrder;
    if ([sortOrderValue isKindOfClass:[NSString class]]) {
        sortOrder = sortOrderValue;
    } else {
        sortOrder = @"ASC";
    }
    
    id sortByValue = [options objectForKey:@"sortBy"];
    
    NSString *sortBy;
    if ([sortByValue isKindOfClass:[NSString class]]) {
        sortBy = sortByValue;
    } else {
        sortBy = @"TITLE";
    }
    
    NSMutableArray *mutableSongsToSerialize = [NSMutableArray array];

    MPMediaQuery *mediaQuery = [MPMediaQuery songsQuery]; // run a query on song media type
    [mediaQuery addFilterPredicate:[MPMediaPropertyPredicate predicateWithValue:@(NO)
                       forProperty:MPMediaItemPropertyIsCloudItem]]; // ensure what we retrieve is on device

    NSArray *allMediaItems = [mediaQuery items];
    
    // Define the range for the limited subset
    NSRange range = NSMakeRange(offset, MIN(limit, allMediaItems.count - offset));
    
    // Get the subset of media items within the specified range
    NSArray<MPMediaItem *> *limitedMediaItems = [allMediaItems subarrayWithRange:range];
    
    // Sort items
    NSArray<MPMediaItem *> *sortedMediaItems = [self sortMediaItems:limitedMediaItems byKey: sortBy sortOrder: sortOrder];
    
    for (MPMediaItem *song in sortedMediaItems) {
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

            if (assetURL != nil && assetURL.absoluteString != nil) {
                [songDictionary setValue:[NSString stringWithString:assetURL.absoluteString] forKey:@"url"];
            }
            if (title != nil) {
                [songDictionary setValue:[NSString stringWithString:title] forKey:@"title"];
            }
            if (albumTitle != nil) {
                [songDictionary setValue:[NSString stringWithString:albumTitle] forKey:@"album"];
            }
            if (albumArtist != nil) {
                [songDictionary setValue:[NSString stringWithString:albumArtist] forKey:@"artist"];
            }
            [songDictionary setValue:[NSNumber numberWithInt:durationInt * 1000] forKey:@"duration"];
            if (genre != nil) {
                [songDictionary setValue:[NSString stringWithString:genre] forKey:@"genre"];
            }

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
    
    id sortOrderValue = [options objectForKey:@"sortOrder"];

    NSString *sortOrder;
    if ([sortOrderValue isKindOfClass:[NSString class]]) {
        sortOrder = sortOrderValue;
    } else {
        sortOrder = @"ASC";
    }
    
    id sortByValue = [options objectForKey:@"sortBy"];
    
    NSString *sortBy;
    if ([sortByValue isKindOfClass:[NSString class]]) {
        sortBy = sortByValue;
    } else {
        sortBy = @"TITLE";
    }
    
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
    
    // Sort items
    NSArray<MPMediaItem *> *sortedMediaItems = [self sortMediaItems:limitedMediaItems byKey: sortBy sortOrder: sortOrder];
  
    for (MPMediaItem *album in sortedMediaItems) {
        NSDictionary *songDictionary = [NSMutableDictionary dictionary];
        
        NSURL *assetURL = [album valueForProperty:MPMediaItemPropertyAssetURL];
        AVAsset *asset = [AVAsset assetWithURL:assetURL];

        if ([asset hasProtectedContent] == NO) {
            
            NSString *albumTitle = [album valueForProperty: MPMediaItemPropertyAlbumTitle]; // filterable
            NSString *albumArtist = [album valueForProperty: MPMediaItemPropertyAlbumArtist]; //
            NSString *numberOfSongs = [album valueForProperty: MPMediaItemPropertyAlbumTrackCount]; //
            MPMediaItemArtwork *artwork = [album valueForProperty: MPMediaItemPropertyArtwork];

            if (assetURL != nil && assetURL.absoluteString != nil) {
                [songDictionary setValue:[NSString stringWithString:assetURL.absoluteString] forKey:@"url"];
            }
            if (albumTitle != nil) {
                [songDictionary setValue:[NSString stringWithString:albumTitle] forKey:@"album"];
            }
            if (albumArtist != nil) {
                [songDictionary setValue:[NSString stringWithString:albumArtist] forKey:@"artist"];
            }
            if (numberOfSongs != nil) {
                [songDictionary setValue:[NSString stringWithString:numberOfSongs] forKey:@"numberOfSongs"];
            }

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
    
    id sortOrderValue = [options objectForKey:@"sortOrder"];

    NSString *sortOrder;
    if ([sortOrderValue isKindOfClass:[NSString class]]) {
        sortOrder = sortOrderValue;
    } else {
        sortOrder = @"ASC";
    }
    
    id sortByValue = [options objectForKey:@"sortBy"];
    
    NSString *sortBy;
    if ([sortByValue isKindOfClass:[NSString class]]) {
        sortBy = sortByValue;
    } else {
        sortBy = @"TITLE";
    }
    
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
    
    // Sort items
    NSArray<MPMediaItem *> *sortedMediaItems = [self sortMediaItems:limitedMediaItems byKey: sortBy sortOrder: sortOrder];
  
    for (MPMediaItem *song in sortedMediaItems) {
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

            if (assetURL != nil && assetURL.absoluteString != nil) {
                [songDictionary setValue:[NSString stringWithString:assetURL.absoluteString] forKey:@"url"];
            }
            [songDictionary setValue:[NSString stringWithString:assetURL.absoluteString] forKey:@"url"];
            if (title != nil) {
                [songDictionary setValue:[NSString stringWithString:title] forKey:@"title"];
            }
            if (albumTitle != nil) {
                [songDictionary setValue:[NSString stringWithString:albumTitle] forKey:@"album"];
            }
            if (albumArtist != nil) {
                [songDictionary setValue:[NSString stringWithString:albumArtist] forKey:@"artist"];
            }
            [songDictionary setValue:[NSNumber numberWithInt:durationInt * 1000] forKey:@"duration"];
            if (genre != nil) {
                [songDictionary setValue:[NSString stringWithString:genre] forKey:@"genre"];
            }

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

- (NSArray<MPMediaItem *> *)sortMediaItems:(NSArray<MPMediaItem *> *)mediaItems byKey:(NSString *)sortKey sortOrder:(NSString *)sortOrder {
    NSArray<MPMediaItem *> *sortedMediaItems;

    if ([sortKey isEqualToString:@"DURATION"]) {
        sortedMediaItems = [mediaItems sortedArrayUsingComparator:^NSComparisonResult(MPMediaItem *item1, MPMediaItem *item2) {
            NSNumber *duration1 = [item1 valueForProperty:MPMediaItemPropertyPlaybackDuration];
            NSNumber *duration2 = [item2 valueForProperty:MPMediaItemPropertyPlaybackDuration];
            NSComparisonResult result = [duration1 compare:duration2];
            
            if ([sortOrder isEqualToString:@"DESC"]) {
                return (result == NSOrderedAscending) ? NSOrderedDescending :
                       (result == NSOrderedDescending) ? NSOrderedAscending : NSOrderedSame;
            }
            
            return result;
        }];
    } else if ([sortKey isEqualToString:@"TITLE"]) {
        sortedMediaItems = [mediaItems sortedArrayUsingComparator:^NSComparisonResult(MPMediaItem *item1, MPMediaItem *item2) {
            NSString *title1 = [item1 valueForProperty:MPMediaItemPropertyTitle];
            NSString *title2 = [item2 valueForProperty:MPMediaItemPropertyTitle];
            NSComparisonResult result = [title1 compare:title2];
            
            if ([sortOrder isEqualToString:@"DESC"]) {
                return (result == NSOrderedAscending) ? NSOrderedDescending :
                       (result == NSOrderedDescending) ? NSOrderedAscending : NSOrderedSame;
            }
            
            return result;
        }];
    } else if ([sortKey isEqualToString:@"ARTIST"]) {
        sortedMediaItems = [mediaItems sortedArrayUsingComparator:^NSComparisonResult(MPMediaItem *item1, MPMediaItem *item2) {
            NSString *artist1 = [item1 valueForProperty:MPMediaItemPropertyArtist];
            NSString *artist2 = [item2 valueForProperty:MPMediaItemPropertyArtist];
            NSComparisonResult result = [artist1 compare:artist2];
            
            if ([sortOrder isEqualToString:@"DESC"]) {
                return (result == NSOrderedAscending) ? NSOrderedDescending :
                       (result == NSOrderedDescending) ? NSOrderedAscending : NSOrderedSame;
            }
            
            return result;
        }];
    } else if ([sortKey isEqualToString:@"ALBUM"]) {
        sortedMediaItems = [mediaItems sortedArrayUsingComparator:^NSComparisonResult(MPMediaItem *item1, MPMediaItem *item2) {
            NSString *album1 = [item1 valueForProperty:MPMediaItemPropertyAlbumTitle];
            NSString *album2 = [item2 valueForProperty:MPMediaItemPropertyAlbumTitle];
            NSComparisonResult result = [album1 compare:album2];
            
            if ([sortOrder isEqualToString:@"DESC"]) {
                return (result == NSOrderedAscending) ? NSOrderedDescending :
                       (result == NSOrderedDescending) ? NSOrderedAscending : NSOrderedSame;
            }
            
            return result;
        }];
    } else if ([sortKey isEqualToString:@"GENDER"]) {
        sortedMediaItems = [mediaItems sortedArrayUsingComparator:^NSComparisonResult(MPMediaItem *item1, MPMediaItem *item2) {
            NSString *gender1 = [item1 valueForProperty:MPMediaItemPropertyGenre];
            NSString *gender2 = [item2 valueForProperty:MPMediaItemPropertyGenre];
            NSComparisonResult result = [gender1 compare:gender2];
            
            if ([sortOrder isEqualToString:@"DESC"]) {
                return (result == NSOrderedAscending) ? NSOrderedDescending :
                       (result == NSOrderedDescending) ? NSOrderedAscending : NSOrderedSame;
            }
            
            return result;
        }];
    } else if ([sortKey isEqualToString:@"DATE_ADDED"]) {
        sortedMediaItems = [mediaItems sortedArrayUsingComparator:^NSComparisonResult(MPMediaItem *item1, MPMediaItem *item2) {
            NSString *dateAdded1 = [item1 valueForProperty:MPMediaItemPropertyDateAdded];
            NSString *dateAdded2 = [item2 valueForProperty:MPMediaItemPropertyDateAdded];
            NSComparisonResult result = [dateAdded1 compare:dateAdded2];
            
            if ([sortOrder isEqualToString:@"DESC"]) {
                return (result == NSOrderedAscending) ? NSOrderedDescending :
                       (result == NSOrderedDescending) ? NSOrderedAscending : NSOrderedSame;
            }
            
            return result;
        }];
    } else {
        // Default sorting (by title)
        sortedMediaItems = mediaItems;
    }

    return sortedMediaItems;
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