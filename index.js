
import { NativeModules, Platform } from 'react-native';

const { RNReactNativeGetMusicFiles } = NativeModules;

const MusicFiles = {
    getAll(options){

        return new Promise((reject, resolve) => {

            if(Platform.OS === "android"){
                RNReactNativeGetMusicFiles.getAll(options,(error) => {
                    reject(error);
                },(response) => {
                    resolve(response);
                });
            }else{
                RNReactNativeGetMusicFiles.getAll(options, (tracks) => {
                    if(tracks.length > 0){
                        resolve(tracks);
                    }else{
                        reject(false);
                    }
                });
            }

        });

    }
}
export default MusicFiles;
