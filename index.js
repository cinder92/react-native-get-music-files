
import { NativeModules } from 'react-native';

const { RNReactNativeGetMusicFiles } = NativeModules;

const MusicFiles = {
    getAll(options,successCallBack,errorCallBack){
        RNReactNativeGetMusicFiles.getAll(options,(response) => {
            successCallBack(response);
        },(error) => {
            errorCallBack(error);
        });
    }
}
export default MusicFiles;
