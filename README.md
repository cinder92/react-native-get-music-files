# react-native-get-music-files
React Native package to get music files from local and sd for iOS and Android
# What does this package?

This package get all the sound files in your local and sd card for Androi and iOS, and retrive metadata from each file, also generate an blurred image from cover file.

* SongID
* Title
* Author
* Album
* Duration
* Path
* Cover
* Duration
* Genre
## Getting started

`$ npm install react-native-get-music-files --save`

### Mostly automatic installation

`$ react-native link react-native-get-music-files`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-reat-native-get-music-files` and add `RNReatNativeGetMusicFiles.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNReatNativeGetMusicFiles.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNReatNativeGetMusicFilesPackage;` to the imports at the top of the file
  - Add `new RNReatNativeGetMusicFilesPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-get-music-files'
  	project(':react-native-get-music-files').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-get-music-files/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-get-music-files')
  	```

## Usage
```
import MusicFiles from 'react-native-get-music-files';

MusicFiles.getAll({
    blured : true, // works only when 'cover' is set to true
    artist : true,
    duration : true, //default : true
    cover : false, //default : true,
    genre : true,
    title : true,
    cover : true,
    coverFolder: 'myApp' // Android only - will save covers, icons and blurred images to this folder - defaults to "/"
    icon: true, // Android only - generates icons from cover images
    iconSize: 50, // Android only - icon size, defaults to 125
    coverResizeRatio: 0.8, // Android only - defaults to 1
    coverSize: 200, // Android only - overrides coverResizeRatio with a fixed number 
    minimumSongDuration : 10000 // get songs bigger than 10000 miliseconds duration,
    fields : ['title','albumTitle','genre','lyrics','artwork','duration'] // for iOs Version
}).then(tracks => {
    // do your stuff...
}).catch(error => {
    // catch the error
})

//In order to get blocks of songs, for fix performance issues at least in Android, use next

componentWillMount() {
    DeviceEventEmitter.addListener(
        'onBatchReceived',
        (params) => {
            this.setState({songs : [
                ...this.state.songs,
                ...params.batch
            ]});
        }
    )
}

componentDidMount(){
        MusicFiles.getAll({
            id : true,
            blured : false,
            artist : true,
            duration : true, //default : true
            cover : true, //default : true,
            title : true,
            cover : true,
            batchNumber : 5, //get 5 songs per batch
            minimumSongDuration : 10000, //in miliseconds,
            fields : ['title','artwork','duration','artist','genre','lyrics','albumTitle']
        });
}

```

MusicFiles returns an array of objects where you can loop, something like this.

```
[
  {
    id : 1,
    title : "La danza del fuego",
    author : "Mago de Oz",
    album : "Finisterra",
    genre : "Folk",
    duration : 132132312321, // miliseconds
    cover : "file:///sdcard/0/123.png",
    blur : "file:///sdcard/0/123-blur.png", //Will come null if createBLur is set to false
    path : "/sdcard/0/la-danza-del-fuego.mp3"
  }
]
```

# Notes

- [] For android 5 and above, you may request permissions before to use this plugin check `https://github.com/yonahforst/react-native-permissions`

# Version Changes

# 2.1

- [x] Removed FFMPEG library, was causing unexpected errors with differents metatags
- [x] Comments, Lyrics and Date tags are not available anymore
- [x] Improvements for Android API <= 19 
- [x] Removed unnecesary comments
- [x] Cleanup code
- [x] Fixed javascript side
- [x] Fixed crash in Android KitKat and lower
- [x] MinimumSongDuration parameter is now working
- [x] Renamed library from `com.reactlibrary` to `com.cinder92.musicfiles`
- [x] Upgraded gradle to 4.4
- [x] Removed unnecesary logs
- [x] Usable for API <= 19 Android

PR are welcome!
