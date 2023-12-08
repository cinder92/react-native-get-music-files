import { NativeModules, Platform } from 'react-native';
import type {
  Album,
  AlbumOptions,
  Song,
  SongOptions,
} from './NativeTurboSongs';

const LINKING_ERROR =
  `The package 'react-native-music-files' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

// @ts-expect-error
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const TurboSongsModule = isTurboModuleEnabled
  ? require('./NativeTurboSongs').default
  : NativeModules.TurboSongs;

const TurboSongs = TurboSongsModule
  ? TurboSongsModule
  : new Proxy(
    {},
    {
      get() {
        throw new Error(LINKING_ERROR);
      },
    }
  );

enum SortSongOrder {
  ASC = 'ASC',
  DESC = 'DESC',
}

enum SortSongFields {
  TITLE = 'TITLE',
  DURATION = 'DURATION',
  ARTIST = 'ARTIST',
  GENRE = 'GENRE',
  ALBUM = 'ALBUM',
}

const getAll = async (options?: SongOptions): Promise<Song[] | string> => {
  try {
    return await TurboSongs.getAll(options);
  } catch (e) {
    return `${e}`;
  }
};

const getAlbums = async (options?: AlbumOptions): Promise<Album[] | string> => {
  try {
    return await TurboSongs.getAlbums(options);
  } catch (e) {
    return `${e}`;
  }
};

const searchSongs = async (options?: SongOptions): Promise<Song[] | string> => {
  try {
    return await TurboSongs.search(options);
  } catch (e) {
    return `${e}`;
  }
};

export { getAll, getAlbums, searchSongs, SortSongFields, SortSongOrder };
