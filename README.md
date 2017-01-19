# react-native-get-music-files
React Native package to get music files from local and sd for Android (only)

#What does this package?

This package get all the sound files in your local and sd card (Android only), and retrive metadata from each file, also generate an blurred image from cover file.

* SongID
* Title
* Author
* Album
* Duration
* Path
* Cover
* Duration


#How to install
`npm i -S react-native-get-music-files`

#How to use it

```
import MusicFiles from 'react-native-get-music-files';

MusicFiles.get(
  (success) => {
     //this.saveSongData(success)
  },
  (error) => {
       console.log(error)
  }
);
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
    blur : "file:///sdcard/0/123.png",
    path : "/sdcard/0/la-danza-del-fuego.mp3"
  }
]
```
