'use strict';

import { NativeModules, DeviceEventEmitter } from 'react-native';
const SongsCollection = NativeModules.SongsCollection;

let MusicFiles = {
	get : function(success, error, createBlur = false){
		SongsCollection.getAll(createBlur, error, (response) => {
			let songs = response.filter((item) => {
				//get files large than 10s
				return item.duration > 10000
			});
			success(songs);
		});
	}
}

module.exports = MusicFiles;
