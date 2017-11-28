using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Reat.Native.Get.Music.Files.RNReatNativeGetMusicFiles
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNReatNativeGetMusicFilesModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNReatNativeGetMusicFilesModule"/>.
        /// </summary>
        internal RNReatNativeGetMusicFilesModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNReatNativeGetMusicFiles";
            }
        }
    }
}
