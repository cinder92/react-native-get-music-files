import * as React from 'react';

import { StyleSheet, View, Text, Platform, Image } from 'react-native';
import { getAll, getAlbums, searchSongs, SortSongFields, SortSongOrder } from 'react-native-get-music-files';
import type { Song } from '../../src/NativeTurboSongs';

import { check, PERMISSIONS, request, RESULTS, requestMultiple } from 'react-native-permissions';

export default function App() {
  const [result, setResult] = React.useState<Song[]>();

  const hasPermissions = async () => {
    if (Platform.OS === 'android') {
      let hasPermission =
        (await check(PERMISSIONS.ANDROID.READ_EXTERNAL_STORAGE)) ===
        RESULTS.GRANTED || (await check(PERMISSIONS.ANDROID.READ_MEDIA_AUDIO)) ===
        RESULTS.GRANTED;

      if (!hasPermission) {
        hasPermission = await requestMultiple([
          PERMISSIONS.ANDROID.READ_EXTERNAL_STORAGE,
          PERMISSIONS.ANDROID.READ_MEDIA_AUDIO,
        ]);
      }

      return hasPermission;
    }

    if (Platform.OS === 'ios') {
      let hasPermission =
        (await check(PERMISSIONS.IOS.MEDIA_LIBRARY)) === RESULTS.GRANTED;
      if (!hasPermission) {
        hasPermission =
          (await request(PERMISSIONS.IOS.MEDIA_LIBRARY)) === RESULTS.GRANTED;
      }

      return hasPermission;
    }

    return false;
  };

  const test = async () => {
    const permissions = await hasPermissions();
    if (permissions) {
      const songsResults = await getAll({
        limit: 20,
        offset: 0,
        coverQuality: 50,
        minSongDuration: 1000,
        sortOrder: SortSongOrder.DESC,
        sortBy: SortSongFields.TITLE,
      });
      if (typeof songsResults === 'string') {
        return;
      }
      setResult(songsResults);
      /*const albums = await getAlbums({
        limit: 10,
        offset: 0,
        coverQuality: 50,
        artist: 'Rihanna',
        sortOrder: SortSongOrder.DESC,
        sortBy: SortSongFields.ALBUM,
      });
      console.log(albums, 'Albums[]');*/
      /*const songsResults = await searchSongs({
        limit: 10,
        offset: 0,
        coverQuality: 50,
        searchBy: 'what',
        sortOrder: SortSongOrder.DESC,
        sortBy: SortSongFields.DURATION,
      });
      console.log(songsResults, 'SongResult[]');*/
    }
  };

  React.useEffect(() => {
    test();
  }, []);

  const render = () => {
    if (result?.length === 0) {
      return <Text>No items</Text>;
    }

    return result?.map((song) => (
      <View key={song.url}>
        <Image
          source={{
            uri: song.cover,
          }}
          resizeMode="cover"
          style={{
            width: 150,
            height: 150,
          }}
        />
        <Text style={styles.text}>Album: {song.album}</Text>
        <Text style={styles.text}>Artist: {song.artist}</Text>
        <Text style={styles.text}>Title: {song.title}</Text>
        <Text style={styles.text}>Duration(ms): {song.duration}</Text>
        <Text style={styles.text}>Genre: {song.genre}</Text>
        <Text style={styles.text}>FileUrl: {song.url}</Text>
      </View>
    ));
  };

  return <View style={styles.container}>{render()}</View>;
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
  text: {
    color: 'black',
  },
});
