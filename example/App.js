/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 * @lint-ignore-every XPLATJSCOPYRIGHT1
 */

import React, { Component } from "react";
import {
  StyleSheet,
  Text,
  View,
  PermissionsAndroid,
  Button
} from "react-native";
import { MusicFiles, RNAndroidAudioStore } from "react-native-get-music-files";

type Props = {};
export default class App extends Component<Props> {
  constructor() {
    super();

    this.requestPermission = async () => {
      try {
        const granted = await PermissionsAndroid.requestMultiple(
          [
            PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE,
            PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE
          ],
          {
            title: "Permission",
            message: "Storage access is requiered",
            buttonPositive: "OK"
          }
        );
        if (granted === PermissionsAndroid.RESULTS.GRANTED) {
          alert("You can use the package");
        } else {
          this.requestPermission();
        }
      } catch (err) {
        console.warn(err);
      }
    };

    this.getMusicFiles = () => {
      MusicFiles.getAll({
        blured: false, // works only when 'cover' is set to true
        artist: true,
        duration: true, //default : true
        cover: false, //default : true,
        genre: true,
        title: true,
        cover: true,
        minimumSongDuration: 10000, // get songs bigger than 10000 miliseconds duration,
        fields: [
          "title",
          "albumTitle",
          "genre",
          "lyrics",
          "artwork",
          "duration"
        ] // for iOs Version
      })
        .then(f => {
          this.setState({ tracks: f });
        })
        .catch(er => alert(JSON.stringify(error)));
    };

    this.getAlbums = () => {
      RNAndroidAudioStore.getAlbums({artist:'johnny cash'
      })
        .then(f => {
          this.setState({ tracks: f });
        })
        .catch(er => alert(JSON.stringify(error)));
    };

    this.getArtists = () => {
      RNAndroidAudioStore.getArtists({})
        .then(f => {
          this.setState({ tracks: f });
        })
        .catch(er => alert(JSON.stringify(error)));
    };

    this.getSongs = () => {
      RNAndroidAudioStore.getSongs({artist:'johnny cash', album:'American IV: The Man Comes Around'})
        .then(f => {
          this.setState({ tracks: f });
        })
        .catch(er => alert(JSON.stringify(error)));
    };

    this.state = {
      tracks: []
    };
  }

  componentDidMount() {
    this.requestPermission();
  }

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>Welcome to React Native!</Text>
        <Text style={styles.instructions}>To get started, edit App.js</Text>
        <Button title="getAll" onPress={this.getMusicFiles} />
        <Text style={styles.instructions}>
          {JSON.stringify(this.state.tracks)}
        </Text>
        <Button title="getArtists" onPress={this.getArtists} />
        <Text style={styles.instructions}>
          {JSON.stringify(this.state.tracks)}
        </Text>
        <Button title="getAlbums" onPress={this.getAlbums} />
        <Text style={styles.instructions}>
          {JSON.stringify(this.state.tracks)}
        </Text>
        <Button title="getSongs" onPress={this.getSongs} />
        <Text style={styles.instructions}>
          {JSON.stringify(this.state.tracks)}
        </Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#F5FCFF"
  },
  welcome: {
    fontSize: 20,
    textAlign: "center",
    margin: 10
  },
  instructions: {
    textAlign: "center",
    color: "#333333",
    marginBottom: 5
  }
});
