
package com.cinder92.musicfiles;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.io.File;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
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
import com.cinder92.musicfiles.ReactNativeFileManager;

import org.farng.mp3.MP3File;

import java.io.File;

public class RNReactNativeGetMusicFilesModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private boolean getBluredImages = false;
    private boolean getArtistFromSong = false;
    private boolean getDurationFromSong = true;
    private boolean getTitleFromSong = true;
    private boolean getIDFromSong = false;
    private boolean getCoverFromSong = false;
    private String coverFolder = "/";
    private double coverResizeRatio = 1;
    private boolean getIcon = false;
    private int iconSize = 125;
    private int coverSize = 0;
    private boolean getGenreFromSong = false;
    private boolean getAlbumFromSong = true;
    private boolean getDateFromSong = false;
    private boolean getCommentsFromSong = false;
    private boolean getLyricsFromSong = false;
    private int minimumSongDuration = 0;
    private int songsPerIteration = 0;
    private int version = Build.VERSION.SDK_INT;
    private String[] STAR = { "*" };

    public RNReactNativeGetMusicFilesModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNReactNativeGetMusicFiles";
    }

    @ReactMethod
    public void getSongById(ReadableMap options, final Callback successCallback, final Callback errorCallback) {
        WritableArray jsonArray = new WritableNativeArray();
        WritableMap item = new WritableNativeMap();
        if (options.hasKey("songUri")) {
            Uri songUri = Uri.parse( "content://"+(options.getString("songUri")));
            String scheme = songUri.getScheme();
            String title = "";
            String artist = "";
            String id = "";
            String album = "";
            String duration = "";
            if (scheme.equals("content")) {
                String[] proj = { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media._ID, };
                Cursor cursor = getCurrentActivity().getContentResolver().query(songUri, proj, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    if (cursor.getColumnIndex(MediaStore.Audio.Media.TITLE) != -1) {
                        title = cursor.getString(0);
                        artist = cursor.getString(1);
                        id = cursor.getString(4);
                        album = cursor.getString(2);
                        duration = cursor.getString(3);
                    }
                }
            } else {
                title = "e";
                artist = "e";
                id = "e";
                album = "e";
                duration = "e";
            }
            item.putString("id", String.valueOf(id));
            item.putString("title", String.valueOf(title));
            item.putString("artist", String.valueOf(artist));
            item.putString("album", String.valueOf(album));
            item.putString("duration", String.valueOf(duration));
            jsonArray.pushMap(item);
            successCallback.invoke(jsonArray);
            Log.e("musica", String.valueOf(title));
        } else {
            errorCallback.invoke("No song Uri");
            Log.e("musica", "no ID");
        }
    }

    @ReactMethod
    public void getAll(ReadableMap options, final Callback successCallback, final Callback errorCallback) {

        if (options.hasKey("blured")) {
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

        if (options.hasKey("coverFolder")) {
            coverFolder = options.getString("coverFolder");
        }

        if (options.hasKey("cover")) {
            getCoverFromSong = options.getBoolean("cover");
        }

        if (options.hasKey("coverResizeRatio")) {
            coverResizeRatio = options.getDouble("coverResizeRatio");
        }

        if (options.hasKey("icon")) {
            getIcon = options.getBoolean("icon");
        }
        if (options.hasKey("iconSize")) {
            iconSize = options.getInt("iconSize");
        }

        if (options.hasKey("coverSize")) {
            coverSize = options.getInt("coverSize");
        }

        if (options.hasKey("genre")) {
            getGenreFromSong = options.getBoolean("genre");
        }

        if (options.hasKey("album")) {
            getAlbumFromSong = options.getBoolean("album");
        }

        /*
         * if (options.hasKey("date")) { getDateFromSong = options.getBoolean("date"); }
         * 
         * if (options.hasKey("comments")) { getCommentsFromSong =
         * options.getBoolean("comments"); }
         * 
         * if (options.hasKey("lyrics")) { getLyricsFromSong =
         * options.getBoolean("lyrics"); }
         */

        if (options.hasKey("batchNumber")) {
            songsPerIteration = options.getInt("batchNumber");
        }

        if (options.hasKey("minimumSongDuration") && options.getInt("minimumSongDuration") > 0) {
            minimumSongDuration = options.getInt("minimumSongDuration");
        } else {
            minimumSongDuration = 0;
        }

        if (version <= 19) {
            getSongs(successCallback, errorCallback);
        } else {
            Thread bgThread = new Thread(null, new Runnable() {
                @Override
                public void run() {
                    getSongs(successCallback, errorCallback);
                }
            }, "asyncTask", 1024);
            bgThread.start();
        }
    }

    private void getSongs(final Callback successCallback, final Callback errorCallback) {
        ContentResolver musicResolver = getCurrentActivity().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";

        if (minimumSongDuration > 0) {
            selection += " AND " + MediaStore.Audio.Media.DURATION + " >= " + minimumSongDuration;
        }

        String[] projection = { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media._ID, };

        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor musicCursor = musicResolver.query(musicUri, projection, selection, null, sortOrder);

        // Log.i("Tienes => ",Integer.toString(musicCursor.getCount()));

        int pointer = 0;

        if (musicCursor != null && musicCursor.moveToFirst()) {

            if (musicCursor.getCount() > 0) {
                WritableArray jsonArray = new WritableNativeArray();
                WritableMap items;

                // FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();

                int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);

                try {
                    do {
                        try {
                            items = new WritableNativeMap();

                            long songId = musicCursor.getLong(idColumn);

                            if (getIDFromSong) {
                                String str = musicCursor.getString(5);
                                items.putString("id", str);
                            }

                            String songPath = musicCursor.getString(4);
                            // MP3File mp3file = new MP3File(songPath);

                            Log.e("musica", songPath);

                            if (songPath != null && songPath != "") {

                                String fileName = songPath.substring(songPath.lastIndexOf("/") + 1);

                                // by default, always return path and fileName
                                items.putString("path", songPath);
                                items.putString("fileName", fileName);

                                // String songTimeDuration =
                                // mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
                                String songTimeDuration = musicCursor.getString(3);
                                int songIntDuration = Integer.parseInt(songTimeDuration);

                                if (getAlbumFromSong) {
                                    String songAlbum = musicCursor.getString(2);

                                    // String songAlbum =
                                    // mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
                                    items.putString("album", songAlbum);
                                }

                                if (getArtistFromSong) {
                                    String songArtist = musicCursor.getString(1);
                                    // String songArtist =
                                    // mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
                                    items.putString("author", songArtist);
                                }

                                if (getTitleFromSong) {
                                    String songTitle = musicCursor.getString(0);
                                    // String songTitle =
                                    // mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE);
                                    items.putString("title", songTitle);
                                }

                                if (getGenreFromSong) {
                                    String songGenre = mmr.extractMetadata(mmr.METADATA_KEY_GENRE);
                                    // String songGenre =
                                    // mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_GENRE);
                                    items.putString("genre", songGenre);
                                }

                                if (getDurationFromSong) {
                                    items.putString("duration", songTimeDuration);
                                }

                                /*
                                 * if (getCommentsFromSong) { items.putString("comments",
                                 * mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_COMMENT)); }
                                 * 
                                 * if (getDateFromSong) { items.putString("date",
                                 * mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DATE)); }
                                 * 
                                 * if (getLyricsFromSong) { //String lyrics =
                                 * mp3file.getID3v2Tag().getSongLyric(); //items.putString("lyrics", lyrics); }
                                 */

                                if (getCoverFromSong) {
                                    mmr.setDataSource(songPath);
                                    ReactNativeFileManager fcm = new ReactNativeFileManager();

                                    String encoded = "";
                                    String blurred = "";
                                    try {
                                        byte[] albumImageData = mmr.getEmbeddedPicture();

                                        if (albumImageData != null) {
                                            Bitmap songImage = BitmapFactory.decodeByteArray(albumImageData, 0,
                                                    albumImageData.length);
                                            Bitmap resized = songImage;
                                            if (coverResizeRatio != 1 && coverSize == 0) {
                                                resized = Bitmap.createScaledBitmap(songImage,
                                                        (int) (songImage.getWidth() * coverResizeRatio),
                                                        (int) (songImage.getHeight() * coverResizeRatio), true);
                                            }

                                            if (coverSize != 0) {
                                                resized = Bitmap.createScaledBitmap(songImage, coverSize, coverSize,
                                                        true);
                                            }

                                            try {
                                                File covers = new File(Environment.getExternalStorageDirectory()
                                                        + File.separator + coverFolder + File.separator + "covers");
                                                boolean success = true;
                                                if (!covers.exists()) {
                                                    success = covers.mkdirs();
                                                }
                                                if (success) {
                                                    String pathToImg = covers.getAbsolutePath() + "/covers" + songId
                                                            + ".jpg";
                                                    encoded = fcm.saveImageToStorageAndGetPath(pathToImg, resized);
                                                    items.putString("cover", "file://" + encoded);
                                                } else {
                                                    // Do something else on failure
                                                }

                                            } catch (Exception e) {
                                                // Just let images empty
                                                Log.e("error in image", e.getMessage());
                                            }

                                            if (getBluredImages) {
                                                try {
                                                    File blured = new File(
                                                            Environment.getExternalStorageDirectory() + File.separator
                                                                    + coverFolder + File.separator + "blurred");
                                                    boolean success = true;
                                                    if (!blured.exists()) {
                                                        success = blured.mkdirs();
                                                    }
                                                    if (success) {
                                                        String pathToImg = blured.getAbsolutePath() + "/blurred"
                                                                + songId + "-blur.jpg";
                                                        blurred = fcm.saveBlurImageToStorageAndGetPath(pathToImg,
                                                                songImage);
                                                        items.putString("blur", "file://" + blurred);
                                                    } else {
                                                        // Do something else on failure
                                                    }

                                                } catch (Exception e) {
                                                    // Just let images empty
                                                    Log.e("error in image-blured", e.getMessage());
                                                }
                                            }
                                            if (getIcon) {
                                                try {
                                                    File icons = new File(Environment.getExternalStorageDirectory()
                                                            + File.separator + coverFolder + File.separator + "icons");
                                                    boolean success = true;
                                                    if (!icons.exists()) {
                                                        success = icons.mkdirs();
                                                    }
                                                    if (success) {
                                                        Bitmap icon = Bitmap.createScaledBitmap(songImage, iconSize,
                                                                iconSize, true);
                                                        String pathToIcon = icons.getAbsolutePath() + "/icons" + songId
                                                                + "-icon.jpg";
                                                        encoded = fcm.saveImageToStorageAndGetPath(pathToIcon, icon);
                                                        items.putString("icon", "file://" + encoded);
                                                    } else {
                                                        // Do something else on failure
                                                    }
                                                } catch (Exception e) {
                                                    // Just let images empty
                                                    Log.e("error in icon", e.getMessage());
                                                }

                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e("embedImage", "No embed image");
                                    }
                                }

                                jsonArray.pushMap(items);

                                if (songsPerIteration > 0) {

                                    if (songsPerIteration > musicCursor.getCount()) {
                                        if (pointer == (musicCursor.getCount() - 1)) {
                                            WritableMap params = Arguments.createMap();
                                            params.putArray("batch", jsonArray);
                                            sendEvent(reactContext, "onBatchReceived", params);
                                        }
                                    } else {
                                        if (songsPerIteration == jsonArray.size()) {
                                            WritableMap params = Arguments.createMap();
                                            params.putArray("batch", jsonArray);
                                            sendEvent(reactContext, "onBatchReceived", params);
                                            jsonArray = new WritableNativeArray();
                                        } else if (pointer == (musicCursor.getCount() - 1)) {
                                            WritableMap params = Arguments.createMap();
                                            params.putArray("batch", jsonArray);
                                            sendEvent(reactContext, "onBatchReceived", params);
                                        }
                                    }

                                    pointer++;
                                }
                            }

                        } catch (Exception e) {
                            // An error in one message should not prevent from getting the rest
                            // There are cases when a corrupted file can't be read and a RuntimeException is
                            // raised

                            // Let's discuss how to deal with these kind of exceptions
                            // This song will be ignored, and incremented the pointer in order to this
                            // plugin work
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
                    errorCallback.invoke(e.getMessage());
                } finally {
                    mmr.release();
                }
            } else {
                Log.i("com.tests", "Error, you dont' have any songs");
                successCallback.invoke("Error, you dont' have any songs");
            }
        } else {
            Log.i("com.tests", "Something get wrong with musicCursor");
            errorCallback.invoke("Something get wrong with musicCursor");
        }
    }

    private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }
}
