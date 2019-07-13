/**
 * @typedef {Object} Song
 * @property {string} id
 * @property {string} songUri
 * @property {string} title
 * @property {string} artist
 * @property {string} album
 * @property {string} duration
 * @property {string} path
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

const { RNAndroidStore } = NativeModules;

/**
 * @class RNAndroidAudioStore
 */
const RNAndroidAudioStore = {
  /**
   * @member
   * @function
   * @async
   * @param {Object} options
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
   * @param {number} [options.delay]
   */
  getAll(options) {
    return new Promise((resolve, reject) => {
      if (Platform.OS === "android") {
        RNAndroidStore.getAll(
          options,
          tracks => {
            resolve(tracks);
          },
          error => {
            resolve(error);
          }
        );
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
   * @returns {Promise<Array<Song>>}
   */
  getSongByPath(options) {
    return new Promise((resolve, reject) => {
      if (Platform.OS === "android") {
        RNAndroidStore.getSongByPath(
          options,
          tracks => {
            resolve(tracks);
          },
          error => {
            resolve(error);
          }
        );
    }});
  },

  /**
   * @function
   * @async
   * @param {Object} options
   * @param {string} [options.artist]
   * @returns {Promise<Array<Album>>}
   */

  getAlbums(options = {}) {
    return new Promise((resolve, reject) => {
      if (Platform.OS === "android") {
        RNAndroidStore.getAlbums(
          options,
          albums => {
            resolve(albums);
          },
          error => {
            resolve(error);
          }
        );
      }
    });
  },

  /**
   * @function
   * @async
   * @param {Object} options
   * @returns {Promise<Array<Artist>>}
   */

  getArtists(options = {}) {
    return new Promise((resolve, reject) => {
      if (Platform.OS === "android") {
        RNAndroidStore.getArtists(
          options,
          albums => {
            console.log(albums);
            resolve(albums);
          },
          error => {
            console.log(error)
            resolve(error);
          }
        );
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
  getSongs(options = {}) {
    return new Promise((resolve, reject) => {
      if (Platform.OS === "android") {
        RNAndroidStore.getSong(
          options,
          albums => {
            resolve(albums);
          },
          error => {
            resolve(error);
          }
        );
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
  search(options = {}) {
    return new Promise((resolve, reject) => {
      if (Platform.OS === "android") {
        RNAndroidStore.search(
          options,
          results => {
            resolve(results);
          },
          error => {
            resolve(error);
          }
        );
      } 
    });
  },

  /**
   * @async
   * @param {Object} options
   * @param {string} [options.genre]
   * @returns {Promise<Array<Song>>} -- in case of options === {} returns Promise<Array<String>>
   */
  getSongsByGenres(options = {}) {
    return new Promise((resolve, reject) => {
      if (Platform.OS === "android") {
        RNAndroidStore.getGenres(
          options,
          results => {
            resolve(results);
          },
          error => {
            resolve(error);
          }
        );
      } 
    });
  }
};



export default RNAndroidAudioStore
