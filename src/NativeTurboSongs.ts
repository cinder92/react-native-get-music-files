import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Song {
  url: string;
  title: string;
  album: string;
  artist: string;
  duration: number;
  genre: string;
  cover: string;
}

export interface SongOptions {
  limit?: number;
  offset?: number;
  coverQuality?: number;
  minSongDuration?: number;
  searchBy?: string;
}

export interface Album {
  url: string;
  album: string;
  artist: string;
  numberOfSongs: string;
  cover: string;
}
export interface AlbumOptions {
  limit?: number;
  offset?: number;
  coverQuality?: number;
  artist: string;
}

export interface Spec extends TurboModule {
  getAll(options?: SongOptions): Promise<Song[] | string>;
  getAlbums(options?: AlbumOptions): Promise<Album[] | string>;
  searchSongs(options?: SongOptions): Promise<Song[] | string>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('TurboSongs');
