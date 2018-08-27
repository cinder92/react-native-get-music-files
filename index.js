/**
 * @typedef {Object} Song
 * @property {string} id
 * @property {string} songUri
 * @property {string} title
 * @property {string} artist
 * @property {string} album
 * @property {string} duration
 */

/**
 * @typedef {Object} Album
 * @property {string} id
 * @property {string} album
 * @property {string} author
 * @property {string} cover
 * @property {string} numberOfSongs
 */

/**
 * @typedef {Object} Artist
 * @property {string} key
 * @property {string} artist
 * @property {string} numberOfAlbums
 * @property {string} numberOfSongs
 * @property {string} id
 */

import { NativeModules, Platform } from "react-native";

const { RNReactNativeGetMusicFiles } = NativeModules;

const MusicFiles = {
  /**
   * @function
   * @async
   * @param {Object} options
   * @param {boolean} [options.blured]
   * @param {boolean} [options.artist]
   * @param {boolean} [options.duration]
   * @param {boolean} [options.title]
   * @param {boolean} [options.id]
   * @param {string} [options.coverFolder]
   * @param {boolean} [options.cover]
   * @param {number} [options.coverResizeRatio]
   * @param {boolean} [options.icon]
   * @param {number} [options.iconSize]
   * @param {number} [options.coverSize]
   * @param {boolean} [options.genre]
   * @param {boolean} [options.album]
   * @param {number} [options.batchNumber]
   * @param {number} [options.minimumSongDuration]
   */
  getAll(options) {
    return new Promise((resolve, reject) => {
      if (Platform.OS === "android") {
        RNReactNativeGetMusicFiles.getAll(
          options,
          tracks => {
            resolve(tracks);
          },
          error => {
            resolve(error);
          }
        );
      } else {
        RNReactNativeGetMusicFiles.getAll(options, tracks => {
          if (tracks.length > 0) {
            resolve(tracks);
          } else {
            resolve("Error, you don't have any tracks");
          }
        });
      }
    });
  },

  /**
   * @function
   * @async
   * @param {Object} options
   * @param {string} options.songUri
   * @param {string} [options.coverFolder]
   * @param {boolean} [options.cover]
   * @param {number} [options.coverResizeRatio]
   * @param {boolean} [options.icon]
   * @param {number} [options.iconSize]
   * @param {number} [options.coverSize]
   * @param {boolean} [options.blured]
   * @returns {Promise<Array<Song>>}
   */
  getSongByPath(options) {
    return new Promise((resolve, reject) => {
      if (Platform.OS === "android") {
        RNReactNativeGetMusicFiles.getSongByPath(
          options,
          tracks => {
            resolve(tracks);
          },
          error => {
            resolve(error);
          }
        );
      } else {
      }
    });
  },

  /**
   * @function
   * @async
   * @param {Object} options
   * @param {string} [options.artist]
   * @returns {Promise<Array<Album>>}
   */

  getAlbums(options) {
    return new Promise((resolve, reject) => {
      if (Platform.OS === "android") {
        RNReactNativeGetMusicFiles.getAlbums(
          options,
          albums => {
            resolve(albums);
          },
          error => {
            resolve(error);
          }
        );
      } else {
      }
    });
  },

  /**
   * @function
   * @async
   * @param {Object} options
   * @returns {Promise<Array<Artist>>}
   */

  getArtists(options) {
    return new Promise((resolve, reject) => {
      if (Platform.OS === "android") {
        RNReactNativeGetMusicFiles.getArtists(
          options,
          albums => {
            resolve(albums);
          },
          error => {
            resolve(error);
          }
        );
      } else {
      }
    });
  },

  /**
   * @function
   * @async
   * @param {Object} options
   * @param {string} [options.artist]
   * @param {string} [options.album]
   * @returns {Promise<Array<Song>>}
   */
  getSongs(options) {
    return new Promise((resolve, reject) => {
      if (Platform.OS === "android") {
        RNReactNativeGetMusicFiles.getSong(
          options,
          albums => {
            resolve(albums);
          },
          error => {
            resolve(error);
          }
        );
      } else {
      }
    });
  },

  /**
   * @async
   * @function
   * @param {Object} options
   * @param {string} options.searchParam
   * @returns {Promise<Array<Song>>}
   */
  search(options) {
    return new Promise((resolve, reject) => {
      if (Platform.OS === "android") {
        RNReactNativeGetMusicFiles.search(
          options,
          results => {
            resolve(results);
          },
          error => {
            resolve(error);
          }
        );
      } else {
      }
    });
  },

  /**
   * @async
   * @param {Object} options
   * @param {string} [options.genre]
   * @returns {Promise<Array<Song>>} -- in case of options === {} returns Promise<Array<String>>
   */
  getSongsByGenres(options) {
    return new Promise((resolve, reject) => {
      if (Platform.OS === "android") {
        RNReactNativeGetMusicFiles.getGenres(
          options,
          results => {
            resolve(results);
          },
          error => {
            resolve(error);
          }
        );
      } else {
      }
    });
  }
};
export default MusicFiles;
