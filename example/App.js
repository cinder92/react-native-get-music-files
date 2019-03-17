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
  Button,
  TextInput
} from "react-native";
import MusicFiles, { RNAndroidAudioStore } from "react-native-get-music-files";

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
          this.setState({ ...this.state, tracks: f });
        })
        .catch(er => alert(JSON.stringify(error)));
    };

    this.getAlbums = (artist='') => {
      RNAndroidAudioStore.getAlbums({ artist })
        .then(f => {
          this.setState({ ...this.state, albums: f });
        })
        .catch(er => alert(JSON.stringify(error)));
    };

    this.getArtists = () => {
      RNAndroidAudioStore.getArtists({})
        .then(f => {
          this.setState({ ...this.state, artists: f });
        })
        .catch(er => alert(JSON.stringify(error)));
    };

    this.getSongs = (artist = '', album = '') => {
      RNAndroidAudioStore.getSongs({ artist, album })
        .then(f => {
          this.setState({ ...this.state, songs: f });
        })
        .catch(er => alert(JSON.stringify(error)));
    };

    this.search = searchParam => {
      RNAndroidAudioStore.search({ searchParam })
        .then(f => {
          this.setState({ ...this.state, search: f });
        })
        .catch(er => alert(JSON.stringify(error)));
    };

    this.state = {
      getAlbumsInput: "",
      getSongsInput: {},
      searchParam: "",
      tracks: [],
      artists: [],
      albums: [],
      songs: [],
      search: []
    };
  }

  componentDidMount() {
    this.requestPermission();
  }

  render() {
    return (
      <View style={styles.container}>
        <Button title="getAll" onPress={this.getMusicFiles} />
        <Text style={styles.instructions}>
          results : {JSON.stringify(this.state.tracks)}
        </Text>

        <Button title="getArtists" onPress={this.getArtists} />
        <Text style={styles.instructions}>
          results : {JSON.stringify(this.state.artists)}
        </Text>

        <TextInput
          placeholder="author"
          onChangeText={v =>
            this.setState({ ...this.state, getAlbumsInput: v })
          }
        />
        <Button
          title="getAlbums"
          onPress={() => this.getAlbums( this.state.getAlbumsInput )}
        />
        <Text style={styles.instructions}>
          results : {JSON.stringify(this.state.albums)}
        </Text>
        <TextInput
          placeholder="artist"
          onChangeText={v =>
            this.setState({
              ...this.state,
              getSongsInput: { ...this.state.getSongsInput, artist: v }
            })
          }
        />
        <TextInput
          placeholder="album"
          onChangeText={v =>
            this.setState({
              ...this.state,
              getSongsInput: { ...this.state.getSongsInput, album: v }
            })
          }
        />
        <Button
          title="getSongs"
          onPress={() =>
            this.getSongs(
              this.state.getSongsInput.artist,
              this.state.getSongsInput.album
            )
          }
        />
        <Text style={styles.instructions}>
          results : {JSON.stringify(this.state.songs)}
        </Text>

        <TextInput
          placeholder="search"
          onChangeText={v => this.setState({ ...this.state, searchParam: v })}
        />
        <Button
          title="search"
          onPress={() => this.search(this.state.searchParam)}
        />
        <Text style={styles.instructions}>
          results : {JSON.stringify(this.state.search)}
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
