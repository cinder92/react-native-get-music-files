# react-native-get-music-files
React Native package to get music files from local and sd for iOS and Android
# What does this package?

This package allow you to get music files from Android & iOS with following properties:

* Title
* Author
* Album
* Duration
* FilePath
* Cover
* Duration
* Genre
## Getting started

`$ yarn add react-native-get-music-files`
or 
`$ yarn add https://github.com/cinder92/react-native-get-music-files.git`

#### iOS

1. Add in `info.plist` following permission
```
<key>NSAppleMusicUsageDescription</key>
<string>This permission is not needed by the app, but it is required by an underlying API. If you see this dialog, contact us.</string>
``` 
2. Add MediaPlayer.framework under build settings in Xcode
3. Ensure all your music files are sync from a computer to a real iPhone device (this package does not work in simulators)

#### Android

1. Navigate to `android/app/src/main/AndroidManifest.xml` and ensure to add this permission
```
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO"/> <-- Add this for Android 13 and newer versions
```

## Before usage

As this package needs permissions from the device, please ensure that you asked for permissions before run any of this package functions.

## Usage
```js

import { getAll, getAlbums, searchSongs } from "react-native-get-music-files";


const songsOrError = await getAll({
    limit: 20,
    offset: 0,
    coverQuality: 50,
    minSongDuration: 1000,
});

// error 
if (typeof songsOrError === 'string') {
    // do something with the error
    return;
}

const albumsOrError = await getAlbums({
    limit: 10,
    offset: 0,
    coverQuality: 50,
    artist: 'Rihanna',
});

// error 
if (typeof albumsOrError === 'string') {
    // do something with the error
    return;
}

const resultsOrError = await searchSongs({
    limit: 10,
    offset: 0,
    coverQuality: 50,
    searchBy: '...',
});

// error 
if (typeof resultsOrError === 'string') {
    // do something with the error
    return;
}
```

MusicFiles returns an array of objects where you can loop, something like this.

```js
[
  {
    title : "La danza del fuego",
    author : "Mago de Oz",
    album : "Finisterra",
    genre : "Folk",
    duration : 209120,
    cover : "data:image/jpeg;base64, ....",
    url : "/sdcard/0/la-danza-del-fuego.mp3"
  }
]
```

  
#### Return Types
* Album

    Type: Object
    
    | property 	| type 	| description 	|
    |---------------:	|:--------:	|-------------------------------	|
    | album 	| string 	| album name 	|
    | artist 	| string 	| author 	|
    | cover 	| string 	| base64 of the artwork 	|
    | numberOfSongs 	| number 	| number of songs in this album 	|

* Song

    | property 	| type 	| description 	|
    |---------------	|--------	|-------------------------------	|
    | title 	| string 	| title	|
    | artist 	| string 	|  artist	|
    | album 	| string 	|  album name	|
    | duration 	| string 	|  duration in ms	|
    | genre 	| string 	|  genre	|
    | cover 	| string 	|  base64 of the artwork	|
    | url 	| string 	| path of the song 	|


#### Methods

* ##### getAlbums
    
    `async getAlbums(options) → {Promise<Album[] | string>}`

    * options
    
        Type: Object
        
        | property 	| type 	| description 	|
        |---------------	|--------	|-------------------------------	|
        | artist  	| string 	|  required	|
        | limit  	| number 	|  optional	|
        | offset  	| number 	|  required if limit set	|
        | coverQuality  	| number 	|  optional	|
    * returns
  
        Type: Albums
        Error: string
* ##### getAll
    
    `async getAll(options) → {Promise<Song[] | string>}`
    
    * options
    
        Type: Object
        
        | property 	| type 	| description 	|
        |---------------	|--------	|-------------------------------	|
        | limit  	| number 	|  optional	|
        | offset  	| number 	|  required if limit set	|
        | coverQuality  	| string 	|  optional	|
        | minSongDuration  	| number 	|  optional	|
    * returns
    
        Type: Song
        Error: string
* ##### searchSongs
    
    `async searchSongs(options) → { Promise<Song[] | string> }`
    
    * options
    
        Type: Object
        
        | property 	| type 	| description 	|
        |---------------	|--------	|-------------------------------	|
        | searchBy  	| string 	|  required	|
        | limit  	| number 	|  optional	|
        | offset  	| number 	|  required if limit set	|
        | coverQuality  	| number 	|  optional	|
   
    * returns
        Type: Song
        Error: string

## Usage:

[example app](https://github.com/cinder92/react-native-get-music-files/tree/master/example)

# Version Changes

# 2.2

- [x] Android & iOS compatible
- [x] Retro-compat turbo module
- [x] Limit & offset to paginate results
- [x] Compatible with `https://github.com/zoontek/react-native-permissions`

PR are welcome!
