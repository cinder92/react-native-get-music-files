import {SongsCollection} from 'NativeModules';

let MusicFiles = {
	get : function(success,error){
		SongsCollection.getAll((err) => {
			error(err)
		},(response) => {
			let songs = response.filter((item) => {
				//get files large than 10s
				return item.duration > 10000
			});
			success(songs);
		});
	}
}

module.exports = MusicFiles;
