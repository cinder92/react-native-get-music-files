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
or 
`$ npm install https://github.com/cinder92/react-native-get-music-files.git --save`

### Mostly automatic installation

`$ react-native link react-native-get-music-files`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-reat-native-get-music-files` and add `RNReatNativeGetMusicFiles.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNReatNativeGetMusicFiles.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `com.cinder92.musicfiles.RNReatNativeGetMusicFilesPackage;` to the imports at the top of the file
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

# RNAndroidStore

This class is essentially an extended version of getMusicFiles but only works on android > 5.0.



#### Manual installation

1. Open up `android/app/src/main/java/[...]/MainApplication.java`
  - Add `import com.drazail.rnandroidstore.RNAndroidStorePackage;` to the imports at the top of the file
  - Add `new RNAndroidStorePackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-get-music-files'
  	project(':react-native-get-music-files').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-get-music-files/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-get-music-files')
  	```
#### Import

  `import  { RNAndroidAudioStore } from "react-native-get-music-files";`
  
#### Return Types
* Album

    Type: Object
    
    | property 	| type 	| description 	|
    |---------------:	|:--------:	|-------------------------------	|
    | id  	| string 	| album id 	|
    | album 	| string 	| album name 	|
    | author 	| string 	| author 	|
    | cover 	| string 	| path to album cover 	|
    | numberOfSongs 	| string 	| number of songs in this album 	|

* Artist

    Type: Object
    
    | property 	| type 	| description 	|
    |---------------	|--------	|-------------------------------	|
    | key  	| string 	| album key 	|
    | artist 	| string 	| album name 	|
    | numberOfAlbums 	| string 	| number of albums 	|
    | numberOfSongs 	| string 	| number of songs 	|
    | id 	| string 	| album id 	|

* Track

  Type: Object

    | property 	| type 	| description 	|
    |---------------	|--------	|-------------------------------	|
    | id  	| string 	| song id	|
    | title 	| string 	| title	|
    | artist 	| string 	|  artist	|
    | album 	| string 	|  album name	|
    | duration 	| string 	|  duration in ms	|
    | path 	| string 	| path of the song 	|


#### Methods
* ##### getAll
    
    `(async, static) getAll(options) → {Promise.<Array.<AObject>>}`
    
    * options
    
        Type: Object
        
        | property 	| type 	| description 	|
        |---------------	|--------	|-------------------------------	|
        | blured  	| boolean 	| if true returns path to blured cover, works only when 'cover' is set to true , this is will add a performance hit 	|
        | artist 	| boolean 	| if true returns artist's name	|
        | duration 	| boolean 	|  if true returns duration	|
        | title 	| boolean 	|  if true returns title	|
        | id 	| boolean 	|  if true returns id	|
        | cover  	| boolean 	| if true returns path to cover  , this is will add a performance hit	|
        | coverFolder 	| string 	| path at which the cover images will be saved, defaults to  "covers"	|
        | coverResizeRatio 	| number 	| if set resizes the cover image, defaults to 1	|
        | coverSize 	| number 	| in pixels, if set resizes the cover image, overrides the	coverResizeRatio	|
        | icon 	| boolean 	|  if true returns the path to song's thumbnail	|
        | iconSize 	| number 	|  in pixels, if set resizes the icon	|
        | genre 	| boolean 	|  if true returns genre	|
        | album 	| boolean 	| if true returns album 	|
        | batchNumber 	| number 	| number of songs returned per batch, please refer to notes bellow |
        | delay 	| number 	|  in ms, defaults to 100, delay between each batch, only owrks if batchNumber is set, please refer to notes bellow	|
        | minimumSongDuration 	| number 	|  minimum duration of the songs returned	|
    * returns
    
        Type: Object
        
        | property 	| type 	| description 	|
        |---------------	|--------	|-------------------------------	|
        | icon 	| string 	| path to icon	|
        | title 	| string 	| title	|
        | author 	| string 	|  author	|
        | album 	| string 	|  album name	|
        | duration 	| string 	|  duration in ms	|
        | path 	| string 	| path of the song 	|
        | fileName 	| string 	| fileName	|
        | cover 	| string 	|  path to cover	|
    * ##### note : 
         * If `batchNumber` is set, this method will not return any value and instead will fire the following events: 
         
             | Type 	| payload.batch 	| description 	|
             |---------------	|--------	|-------------------------------	|
             | onBatchReceived 	| array of songs 	| number of songs per batch is equal to batchNumber	|
             | onLastBatchReceived 	| null 	| fires when the last batch has been sent	|
             
        * `delay` paramater determines how many ms should the native side wait before sending the next batch. This should help with UI thread performance on older devices.
        * event listeners should be used to catch these events ie:
          ```javascript
             componentDidMount() {
               DeviceEventEmitter.addListener(
                'onBatchReceived',
                (p) => {
                    this.setState({ ...this.state, tracks: [...this.state.tracks, p.batch] })
                  }
                )
             }

          ```
        
* ##### getSongByPath
    
    `(async, static) getSongByPath(options) → {Promise.<Track>>}`
  
    This method retrieves metadata directly from the file path.
    
    * options
    
        Type: Object
        
        | property 	| type 	| description 	|
        |---------------	|--------	|-------------------------------	|
        | songUri  	| string 	| path to the song, this is not optional	|
        | blured  	| boolean 	| if true returns path to blured cover, works only when 'cover' is set to true , this is will add a performance hit 	|
        | cover  	| boolean 	| if true returns path to cover  , this is will add a performance hit	|
        | coverFolder 	| string 	| path at which the cover images will be saved, defaults to  "covers"	|
        | coverResizeRatio 	| number 	| if set resizes the cover image, defaults to 1	|
        | coverSize 	| number 	| in pixels, if set resizes the cover image, overrides the	coverResizeRatio	|
        | icon 	| boolean 	|  if true returns the path to song's thumbnail	|
        | iconSize 	| number 	|  in pixels, if set resizes the icon	|
    * returns
    
        Type: Track

* ##### getAlbums
    
    `(async, static) getAlbums(options) → {Promise.<Array.<Album>>}`
    
    * options
    
        Type: Object
        
        | property 	| type 	| description 	|
        |---------------	|--------	|-------------------------------	|
        | artist  	| string 	| if provided, returns artist's albums, else return all the albums 	|
    * returns
    
        Type: Album
* ##### getArtists
    
    `(async, static) getArtists() → {Promise.<Array.<Artist>>}`
    * returns
  
        Type: Artist
* ##### getSongs
    
    `(async, static) getSongs(options) → {Promise.<Array.<Track>>}`
    
    * options
    
        Type: Object
        
        | property 	| type 	| description 	|
        |---------------	|--------	|-------------------------------	|
        | artist  	| string 	| optional 	|
        | album  	| string 	| optional 	|
    * returns
    
        Type: Track
* ##### search
    
    `(async, static) search(options) → {Promise.<Array.<Track>>}`
    
    * options
    
        Type: Object
        
        | property 	| type 	| description 	|
        |---------------	|--------	|-------------------------------	|
        | searchParam  	| string 	|  optional	|
    * returns
        Type: Track

## Usage:

[example app](https://github.com/cinder92/react-native-get-music-files/tree/master/example)

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
