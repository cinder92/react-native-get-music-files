
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNTurboSongsSpec.h"

@interface TurboSongs : NSObject <NativeTurboSongsSpec>
#else
#import <React/RCTBridgeModule.h>

@interface TurboSongs : NSObject <RCTBridgeModule>
#endif

@end
