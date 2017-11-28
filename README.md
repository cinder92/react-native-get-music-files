
# react-native-reat-native-get-music-files

## Getting started

`$ npm install react-native-reat-native-get-music-files --save`

### Mostly automatic installation

`$ react-native link react-native-reat-native-get-music-files`

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
  	include ':react-native-reat-native-get-music-files'
  	project(':react-native-reat-native-get-music-files').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-reat-native-get-music-files/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-reat-native-get-music-files')
  	```

#### Windows
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `RNReatNativeGetMusicFiles.sln` in `node_modules/react-native-reat-native-get-music-files/windows/RNReatNativeGetMusicFiles.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Reat.Native.Get.Music.Files.RNReatNativeGetMusicFiles;` to the usings at the top of the file
  - Add `new RNReatNativeGetMusicFilesPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import RNReatNativeGetMusicFiles from 'react-native-reat-native-get-music-files';

// TODO: What to do with the module?
RNReatNativeGetMusicFiles;
```
  