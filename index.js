
import { NativeModules, Platform } from 'react-native';

const { RNReactNativeGetMusicFiles } = NativeModules;

const MusicFiles = {
    getAll(options){

        return new Promise((reject, resolve) => {

            if(Platform.OS === "android"){
                RNReactNativeGetMusicFiles.getAll(options,(response) => {
                    resolve(response);
                },(error) => {
                    reject(error);
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
