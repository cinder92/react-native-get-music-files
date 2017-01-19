'use strict';

import {SongsCollection} from 'NativeModules';

var MusicFiles = {
	get : function(success,error){
		SongsCollection.getAll((err) => {
			error(err)
		},(response) => {
			var songs = response.filter((item) => {
				//get files large than 10s
				return item.duration > 10000
			});
			success(songs);
		});
	}
}

module.exports = MusicFiles;
