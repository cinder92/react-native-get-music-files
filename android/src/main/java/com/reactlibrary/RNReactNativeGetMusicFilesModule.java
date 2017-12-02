
package com.reactlibrary;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import wseemann.media.FFmpegMediaMetadataRetriever;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.reactlibrary.ReactNativeFileManager;

import org.farng.mp3.MP3File;


public class RNReactNativeGetMusicFilesModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private boolean getBluredImages = false;
    private boolean getArtistFromSong = false;
    private boolean getDurationFromSong = true;
    private boolean getTitleFromSong = true;
    private boolean getIDFromSong = false;
    private boolean getCoverFromSong = false;
    private boolean getGenreFromSong = false;
    private boolean getAlbumFromSong = true;
    private boolean getDateFromSong = false;
    private boolean getCommentsFromSong = false;
    private boolean getLyricsFromSong = false;
    private int minimumSongDuration = 0;
    private int songsPerIteration = 0;

    public RNReactNativeGetMusicFilesModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNReactNativeGetMusicFiles";
    }

    @ReactMethod
    public void getAll(ReadableMap options, final Callback errorCallback, final Callback successCallback) {


        if (options.getBoolean("blured")) {
            getBluredImages = options.getBoolean("blured");
        }


        if (options.hasKey("artist")) {
            getArtistFromSong = options.getBoolean("artist");
        }

        if (options.hasKey("duration")) {
            getDurationFromSong = options.getBoolean("duration");
        }

        if (options.hasKey("title")) {
            getTitleFromSong = options.getBoolean("title");
        }

        if (options.hasKey("id")) {
            getIDFromSong = options.getBoolean("id");
        }

        if (options.hasKey("cover")) {
            getCoverFromSong = options.getBoolean("cover");
        }

        if (options.hasKey("genre")) {
            getGenreFromSong = options.getBoolean("genre");
        }

        if (options.hasKey("album")) {
            getAlbumFromSong = options.getBoolean("album");
        }

        if (options.hasKey("date")) {
            getDateFromSong = options.getBoolean("date");
        }

        if (options.hasKey("comments")) {
            getCommentsFromSong = options.getBoolean("comments");
        }

        if (options.hasKey("lyrics")) {
            getLyricsFromSong = options.getBoolean("lyrics");
        }

        if (options.hasKey("batchNumber")) {
            songsPerIteration = options.getInt("batchNumber");
        }

        int optionMinimumDuration = options.getInt("minimumSongDuration");

        if (optionMinimumDuration > 0) {
            minimumSongDuration = optionMinimumDuration;
        } else {
            minimumSongDuration = 10000;
        }

        Thread bgThread = new Thread(null,
                new Runnable() {
                    @Override
                    public void run() {

                        ContentResolver musicResolver = getCurrentActivity().getContentResolver();
                        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
                        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
                        Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, sortOrder);

                        int pointer = 0;

                        if (musicCursor != null && musicCursor.moveToFirst()) {

                            if (musicCursor.getCount() > 0) {
                                WritableArray jsonArray = new WritableNativeArray();
                                WritableMap items;
                                FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();

                                int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);

                                try {
                                    do {
                                        Log.i("musicPointer: - ", String.valueOf(pointer));
                                        try {
                                            items = new WritableNativeMap();

                                            long songId = musicCursor.getLong(idColumn);

                                            if (getIDFromSong) {
                                                String str = String.valueOf(songId);
                                                items.putString("id", str);
                                            }

                                            String songPath = musicCursor.getString(musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                                            MP3File mp3file = new MP3File(songPath);

                                            if (songPath != null && songPath != "") {

                                                String fileName = songPath.substring(songPath.lastIndexOf("/") + 1);

                                                //by default, always return path and fileName
                                                items.putString("path", songPath);
                                                items.putString("fileName", fileName);

                                                mmr.setDataSource(songPath);

                                                String songTimeDuration = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
                                                int songIntDuration = Integer.parseInt(songTimeDuration);

                                                //verify that is the minimum duration
                                                // if(songIntDuration >= minimumSongDuration) {


                                                if (getAlbumFromSong) {
                                                    //String songAlbum = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                                                    String songAlbum = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
                                                    items.putString("album", songAlbum);
                                                }

                                                if (getArtistFromSong) {
                                                    //String songArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                                                    String songArtist = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
                                                    items.putString("author", songArtist);
                                                }


                                                if (getTitleFromSong) {
                                                    //String songTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                                                    String songTitle = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE);
                                                    items.putString("title", songTitle);
                                                }

                                                if (getGenreFromSong) {
                                                    //String songGenre = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
                                                    String songGenre = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_GENRE);
                                                    items.putString("genre", songGenre);
                                                }

                                                if (getDurationFromSong) {
                                                    //musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                                                    items.putString("duration", songTimeDuration);
                                                }

                                                if (getCommentsFromSong) {
                                                    items.putString("comments", mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_COMMENT));
                                                }

                                                if (getDateFromSong) {
                                                    items.putString("date", mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DATE));
                                                }

                                                if (getLyricsFromSong) {
                                                    String lyrics = mp3file.getID3v2Tag().getSongLyric();
                                                    items.putString("lyrics", lyrics);
                                                }

                                                if (getCoverFromSong) {

                                                    ReactNativeFileManager fcm = new ReactNativeFileManager();

                                                    String encoded = "";
                                                    String blurred = "";
                                                    byte[] albumImageData = mmr.getEmbeddedPicture();

                                                    if (albumImageData != null) {
                                                        Bitmap songImage = BitmapFactory.decodeByteArray(albumImageData, 0, albumImageData.length);

                                                        try {
                                                            String pathToImg = Environment.getExternalStorageDirectory() + "/" + songId + ".jpg";
                                                            encoded = fcm.saveImageToStorageAndGetPath(pathToImg, songImage);
                                                            items.putString("cover", "file://" + encoded);
                                                        } catch (Exception e) {
                                                            // Just let images empty
                                                            Log.e("error in image", e.getMessage());
                                                        }

                                                        if (getBluredImages) {
                                                            try {
                                                                String pathToImg = Environment.getExternalStorageDirectory() + "/" + songId + "-blur.jpg";
                                                                blurred = fcm.saveBlurImageToStorageAndGetPath(pathToImg, songImage);
                                                                items.putString("blur", "file://" + blurred);
                                                            } catch (Exception e) {
                                                                Log.e("error in image-blured", e.getMessage());
                                                            }
                                                        }
                                                    }
                                                }


                                                jsonArray.pushMap(items);

                                                //si mi numero total de canciones es mayor a mi contador entra
                                                if (songsPerIteration > 0) {
                                                    Log.i("musicCursor", String.valueOf(musicCursor.getCount()));
                                                    Log.i("musicPointer", String.valueOf(pointer));


                                                    if (songsPerIteration > musicCursor.getCount()) {
                                                        if (pointer == (musicCursor.getCount() - 1)) {
                                                            //enviar todas
                                                            WritableMap params = Arguments.createMap();
                                                            params.putArray("batch", jsonArray);
                                                            sendEvent(reactContext, "onBatchReceived", params);
                                                        }
                                                    } else {
                                                        // 2 == 2
                                                        if (songsPerIteration == jsonArray.size()) {
                                                            //enviar por bloques
                                                            WritableMap params = Arguments.createMap();
                                                            params.putArray("batch", jsonArray);
                                                            sendEvent(reactContext, "onBatchReceived", params);
                                                            jsonArray = new WritableNativeArray();
                                                        } else if (pointer == (musicCursor.getCount() - 1)) {
                                                            //enviar las restantes
                                                            WritableMap params = Arguments.createMap();
                                                            params.putArray("batch", jsonArray);
                                                            sendEvent(reactContext, "onBatchReceived", params);
                                                        }
                                                    }

                                                    pointer++;
                                                    //Log.i("pointer",String.valueOf(pointer));
                                                }

                                                //}
                                            }

                                        } catch (Exception e) {
                                            // An error in one message should not prevent from getting the rest
                                            // There are cases when a corrupted file can't be read and a RuntimeException is raised

                                            // Let's discuss how to deal with these kind of exceptions
                                            // This song will be ignored, and incremented the pointer in order to this plugin work
                                            pointer++;

                                            continue; // This is redundant, but adds meaning
                                        }

                                    } while (musicCursor.moveToNext());

                                    if (songsPerIteration == 0) {
                                        successCallback.invoke(jsonArray);
                                    }

                                } catch (RuntimeException e) {
                                    errorCallback.invoke(e.toString());
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    errorCallback.invoke(e.getMessage());
                                } finally {
                                    mmr.release();
                                }
                            }
                        }

                    }
                }, "asyncTask", 1024
        );
        bgThread.start();
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}