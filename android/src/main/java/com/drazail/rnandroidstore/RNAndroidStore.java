
package com.drazail.rnandroidstore;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.io.Console;
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
import androidx.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import com.facebook.react.modules.core.DeviceEventManagerModule;

public class RNAndroidStore extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    private boolean getArtistFromSong = false;
    private boolean getDurationFromSong = true;
    private boolean getTitleFromSong = true;
    private boolean getIDFromSong = false;
    private boolean getCoversFromSongs = true;
    private String coversFolder = "/";
    private double coversResizeRatio = 1;
    private boolean getIcons = false;
    private int iconsSize = 125;
    private int coversSize = 0;

    private boolean getCoverFromSong = true;
    private String coverFolder = "/";
    private double coverResizeRatio = 1;
    private boolean getIcon = false;
    private int iconSize = 125;
    private int coverSize = 0;

    private int delay = 100;

    private boolean getGenreFromSong = false;
    private boolean getAlbumFromSong = true;
    private int minimumSongDuration = 0;
    private int songsPerIteration = 0;
    private int version = Build.VERSION.SDK_INT;

    public RNAndroidStore(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNAndroidStore";
    }

    @ReactMethod
    public void getSongByPath(ReadableMap options, final Callback successCallback, final Callback errorCallback) {

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

        WritableArray jsonArray = new WritableNativeArray();
        WritableMap item = new WritableNativeMap();
        if (options.hasKey("songUri")) {
            String songUri = options.getString("songUri");
            String title = "";
            String artist = "";
            long id = -1;
            String album = "";
            String duration = "";

            String[] proj = { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA };
            Uri musicUris = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            Cursor cursor = getCurrentActivity().getContentResolver().query(musicUris, proj,
                    MediaStore.Audio.Media.DATA + " like ? ", new String[] { songUri }, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (cursor.getColumnIndex(MediaStore.Audio.Media.TITLE) != -1) {
                    title = cursor.getString(0);
                    artist = cursor.getString(1);
                    id = cursor.getLong(4);
                    album = cursor.getString(2);
                    duration = cursor.getString(3);
                    songUri = cursor.getString(5);
                    if (getCoverFromSong) {
                        getCoverByPath(coverFolder, coverResizeRatio, getIcon, iconSize, coverSize,
                                songUri, id, item);
                    }
                }
            } else {
                String msg = "cursor is either null or empty ";
                Log.e("Musica", msg);
            }

            item.putString("id", String.valueOf(id));
            item.putString("path", String.valueOf(songUri));
            item.putString("title", String.valueOf(title));
            item.putString("author", String.valueOf(artist));
            item.putString("album", String.valueOf(album));
            item.putString("duration", String.valueOf(duration));
            jsonArray.pushMap(item);

            successCallback.invoke(jsonArray);
        } else {

            Log.e("musica", "no ID");
            errorCallback.invoke("No song Uri");
        }
    }

    /////

    @ReactMethod
    public void getAlbums(ReadableMap options, final Callback successCallback, final Callback errorCallback) {

        WritableArray jsonArray = new WritableNativeArray();

        if (options.hasKey("artist")) {
            String[] projection = new String[] { MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM,
                    MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Albums.ALBUM_ART,
                    MediaStore.Audio.Albums.NUMBER_OF_SONGS };
            String searchParam = "%" + options.getString("artist") + "%";
            Cursor cursor = getCurrentActivity().getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection,
                    MediaStore.Audio.Albums.ARTIST + " Like ?", new String[] { searchParam }, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    WritableMap item = new WritableNativeMap();
                    item.putString("id", String.valueOf(cursor.getLong(0)));
                    item.putString("album", String.valueOf(cursor.getString(1)));
                    item.putString("author", String.valueOf(cursor.getString(2)));
                    item.putString("cover",  "file://" +String.valueOf(cursor.getString(3)));
                    item.putString("numberOfSongs", String.valueOf(cursor.getString(4)));
                    jsonArray.pushMap(item);
                } while (cursor.moveToNext());
            } else {
                String msg = "cursor is either null or empty ";
                Log.e("Musica", msg);
            }
            Log.e("MusicaAlbums", String.valueOf(jsonArray));
            cursor.close();
            successCallback.invoke(jsonArray);
        } else {
            String[] projection = new String[] { MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM,
                    MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Albums.ALBUM_ART,
                    MediaStore.Audio.Albums.NUMBER_OF_SONGS };

            Cursor cursor = getCurrentActivity().getContentResolver()
                    .query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    WritableMap item = new WritableNativeMap();
                    item.putString("id", String.valueOf(cursor.getLong(0)));
                    item.putString("album", String.valueOf(cursor.getString(1)));
                    item.putString("author", String.valueOf(cursor.getString(2)));
                    item.putString("cover", "file://"+String.valueOf(cursor.getString(3)));
                    item.putString("numberOfSongs", String.valueOf(cursor.getString(4)));
                    jsonArray.pushMap(item);
                } while (cursor.moveToNext());
            } else {
                String msg = "cursor is either null or empty ";
                Log.e("Musica", msg);
            }
            Log.e("MusicaAlbums", String.valueOf(jsonArray));
            cursor.close();
            successCallback.invoke(jsonArray);
        }

    }

    ///////
    @ReactMethod
    public void getArtists(ReadableMap options, final Callback successCallback, final Callback errorCallback) {
        
        WritableArray jsonArray = new WritableNativeArray();

        String[] projection = new String[] { MediaStore.Audio.Artists.ARTIST_KEY, MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_ALBUMS, MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
                MediaStore.Audio.Artists._ID };
        Cursor cursor = getCurrentActivity().getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                projection, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                WritableMap item = new WritableNativeMap();
                item.putString("key", String.valueOf(cursor.getString(0)));
                item.putString("artist", String.valueOf(cursor.getString(1)));
                item.putString("numberOfAlbums", String.valueOf(cursor.getString(2)));
                item.putString("numberOfSongs", String.valueOf(cursor.getString(3)));
                item.putString("id", String.valueOf(cursor.getString(4)));
                jsonArray.pushMap(item);
            } while (cursor.moveToNext());
        } else {
            String msg = "cursor is either null or empty ";
            Log.e("Musica", msg);
        }
        Log.e("MusicaAlbums", String.valueOf(jsonArray));
        cursor.close();
        successCallback.invoke(jsonArray);

    }

    @ReactMethod
    public void getGenres(ReadableMap options, final Callback successCallback, final Callback errorCallback) {

        WritableArray jsonArray = new WritableNativeArray();
        if (options.hasKey("genre")) {
            int index;
            String GenreName;
            long genreId;
            Uri uri;
            Cursor genrecursor;
            Cursor tempcursor;
            String[] genreProjection = { MediaStore.Audio.Genres.NAME, MediaStore.Audio.Genres._ID };
            String[] projection = new String[] { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media._ID };
            String Selection = MediaStore.Audio.Genres.NAME + " Like ?";
            genrecursor = getCurrentActivity().getContentResolver().query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                    genreProjection, Selection, new String[] { "%"+options.getString("genre")+"%" }, null);
            if (genrecursor != null && genrecursor.getCount() > 0) {
                genrecursor.moveToFirst();

                do {
                    GenreName = String.valueOf(genrecursor.getString(0));
                    Log.e("Tag-Genre name", GenreName);
                    index = genrecursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID);
                    genreId = Long.parseLong(genrecursor.getString(index));
                    uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId);

                    tempcursor = getCurrentActivity().getContentResolver().query(uri, projection, null, null, null);
                    if (tempcursor.moveToFirst()) {

                        do {
                            WritableMap item = new WritableNativeMap();
                            item.putString("genre", GenreName);
                            item.putString("title", String.valueOf(tempcursor.getString(0)));
                            item.putString("artist", String.valueOf(tempcursor.getString(1)));
                            item.putString("album", String.valueOf(tempcursor.getString(2)));
                            item.putString("duration", String.valueOf(tempcursor.getString(3)));
                            item.putString("path", String.valueOf(tempcursor.getString(4)));
                            item.putString("id", String.valueOf(tempcursor.getString(5)));
                            jsonArray.pushMap(item);
                        } while (tempcursor.moveToNext());
                    }
                    tempcursor.close();
                } while (genrecursor.moveToNext());
            } else {
                String msg = "cursor is either null or empty ";
                Log.e("Musica", msg);
            }
            Log.e("MusicaGenres", String.valueOf(jsonArray));
            genrecursor.close();
            successCallback.invoke(jsonArray);
        } else {
            String[] projection = new String[] { MediaStore.Audio.Genres.NAME };
            Cursor cursor = getCurrentActivity().getContentResolver()
                    .query(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, projection, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    WritableMap item = new WritableNativeMap();
                    item.putString("name", String.valueOf(cursor.getString(0)));
                    jsonArray.pushMap(item);
                } while (cursor.moveToNext());
            } else {
                String msg = "cursor is either null or empty ";
                Log.e("Musica", msg);
            }
            Log.e("MusicaGenre", String.valueOf(jsonArray));
            cursor.close();
            successCallback.invoke(jsonArray);
        }

    }

    ///////
    @ReactMethod
    public void search(ReadableMap options, final Callback successCallback, final Callback errorCallback) {
        WritableArray jsonArray = new WritableNativeArray();
        String[] projection = new String[] { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media._ID };

        String searchParam = "%" + options.getString("searchParam") + "%";

        String Selection = MediaStore.Audio.Albums.ARTIST + " Like ? OR " + MediaStore.Audio.Albums.ALBUM
                + " Like ? OR " + MediaStore.Audio.Media.TITLE + " Like ? OR " + MediaStore.Audio.Media.DATA
                + " Like ?";
        Cursor cursor = getCurrentActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection, Selection, new String[] { searchParam, searchParam, searchParam, searchParam }, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                WritableMap item = new WritableNativeMap();
                item.putString("title", String.valueOf(cursor.getString(0)));
                item.putString("artist", String.valueOf(cursor.getString(1)));
                item.putString("album", String.valueOf(cursor.getString(2)));
                item.putString("duration", String.valueOf(cursor.getString(3)));
                item.putString("path", String.valueOf(cursor.getString(4)));
                item.putString("id", String.valueOf(cursor.getString(5)));
                jsonArray.pushMap(item);
            } while (cursor.moveToNext());
        } else {
            String msg = "cursor is either null or empty ";
            Log.e("Musica", msg);
        }
        Log.e("MusicaAlbums", String.valueOf(jsonArray));
        cursor.close();
        successCallback.invoke(jsonArray);
    }

    @ReactMethod
    public void getSong(ReadableMap options, final Callback successCallback, final Callback errorCallback) {

        WritableArray jsonArray = new WritableNativeArray();

        if (options.hasKey("artist") && !options.hasKey("album")) {
            String[] projection = new String[] { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media._ID };

            Cursor cursor = getCurrentActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection, MediaStore.Audio.Albums.ARTIST + " Like ?",
                    new String[] { "%" + options.getString("artist") + "%" }, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    WritableMap item = new WritableNativeMap();
                    item.putString("title", String.valueOf(cursor.getString(0)));
                    item.putString("artist", String.valueOf(cursor.getString(1)));
                    item.putString("album", String.valueOf(cursor.getString(2)));
                    item.putString("duration", String.valueOf(cursor.getString(3)));
                    item.putString("path", String.valueOf(cursor.getString(4)));
                    item.putString("id", String.valueOf(cursor.getString(5)));
                    jsonArray.pushMap(item);
                } while (cursor.moveToNext());
            } else {
                String msg = "cursor is either null or empty ";
                Log.e("Musica", msg);
            }
            Log.e("MusicaAlbums", String.valueOf(jsonArray));
            cursor.close();
            successCallback.invoke(jsonArray);
        }

        if (!options.hasKey("artist") && options.hasKey("album")) {
            String[] projection = new String[] { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media._ID };

            Cursor cursor = getCurrentActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection, MediaStore.Audio.Albums.ALBUM + " Like ?", new String[] { "%" + options.getString("album") + "%" },
                    null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    WritableMap item = new WritableNativeMap();
                    item.putString("title", String.valueOf(cursor.getString(0)));
                    item.putString("artist", String.valueOf(cursor.getString(1)));
                    item.putString("album", String.valueOf(cursor.getString(2)));
                    item.putString("duration", String.valueOf(cursor.getString(3)));
                    item.putString("path", String.valueOf(cursor.getString(4)));
                    item.putString("id", String.valueOf(cursor.getString(5)));
                    jsonArray.pushMap(item);
                } while (cursor.moveToNext());
            } else {
                String msg = "cursor is either null or empty ";
                Log.e("Musica", msg);
            }
            Log.e("MusicaAlbums", String.valueOf(jsonArray));
            cursor.close();
            successCallback.invoke(jsonArray);
        }

        if (!options.hasKey("artist") && !options.hasKey("album")) {
            String[] projection = new String[] { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media._ID };

            Cursor cursor = getCurrentActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection, MediaStore.Audio.Media.IS_MUSIC + "!= 0", null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    WritableMap item = new WritableNativeMap();
                    item.putString("title", String.valueOf(cursor.getString(0)));
                    item.putString("artist", String.valueOf(cursor.getString(1)));
                    item.putString("album", String.valueOf(cursor.getString(2)));
                    item.putString("duration", String.valueOf(cursor.getString(3)));
                    item.putString("path", String.valueOf(cursor.getString(4)));
                    item.putString("id", String.valueOf(cursor.getString(5)));
                    jsonArray.pushMap(item);
                } while (cursor.moveToNext());
            } else {
                String msg = "cursor is either null or empty ";
                Log.e("Musica", msg);
            }
            Log.e("MusicaAlbums", String.valueOf(jsonArray));
            cursor.close();
            successCallback.invoke(jsonArray);
        }

        if (options.hasKey("artist") && options.hasKey("album")) {
            String[] projection = new String[] { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media._ID };

            Cursor cursor = getCurrentActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    MediaStore.Audio.Albums.ARTIST + " Like ? AND " + MediaStore.Audio.Albums.ALBUM + " Like ?",
                    new String[] { "%" + options.getString("artist") + "%", "%" + options.getString("album") + "%"}, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    WritableMap item = new WritableNativeMap();
                    item.putString("title", String.valueOf(cursor.getString(0)));
                    item.putString("artist", String.valueOf(cursor.getString(1)));
                    item.putString("album", String.valueOf(cursor.getString(2)));
                    item.putString("duration", String.valueOf(cursor.getString(3)));
                    item.putString("path", String.valueOf(cursor.getString(4)));
                    item.putString("id", String.valueOf(cursor.getString(5)));
                    jsonArray.pushMap(item);
                } while (cursor.moveToNext());
            } else {
                String msg = "cursor is either null or empty ";
                Log.e("Musica", msg);
            }
            Log.e("MusicaAlbums", String.valueOf(jsonArray));
            cursor.close();
            successCallback.invoke(jsonArray);
        }
    }

    ///////

    @ReactMethod
    public void getAll(final ReadableMap options, final Callback successCallback, final Callback errorCallback) {

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
            coversFolder = options.getString("coverFolder");
        }

        if (options.hasKey("cover")) {
            getCoversFromSongs = options.getBoolean("cover");
        }

        if (options.hasKey("coverResizeRatio")) {
            coversResizeRatio = options.getDouble("coverResizeRatio");
        }

        if (options.hasKey("icon")) {
            getIcons = options.getBoolean("icon");
        }
        if (options.hasKey("iconSize")) {
            iconsSize = options.getInt("iconSize");
        }

        if (options.hasKey("coverSize")) {
            coversSize = options.getInt("coverSize");
        }

        if (options.hasKey("genre")) {
            getGenreFromSong = options.getBoolean("genre");
        }

        if (options.hasKey("album")) {
            getAlbumFromSong = options.getBoolean("album");
        }

        if (options.hasKey("delay")) {
            delay = options.getInt("delay");
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
            getSongs(options, successCallback, errorCallback);
        } else {
            Thread bgThread = new Thread(null, new Runnable() {
                @Override
                public void run() {
                    getSongs(options, successCallback, errorCallback);
                }
            }, "asyncTask", 1024);
            bgThread.start();
        }
    }

    private void getSongs(ReadableMap options, final Callback successCallback, final Callback errorCallback) {
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
        int mapSize = 0;
        if (musicCursor != null) {
            sendEvent(reactContext, "NoMusicFilesFound", null);
        }

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

                                if (getCoversFromSongs) {
                                    getCoverByPath(coversFolder, coversResizeRatio, getIcons,
                                            iconsSize, coversSize, songPath, songId, items);

                                }

                                jsonArray.pushMap(items);
                                mapSize++;
                                if (songsPerIteration > 0) {

                                    if (songsPerIteration > musicCursor.getCount()) {

                                        if (pointer == (musicCursor.getCount() - 1)) {
                                            WritableMap params = Arguments.createMap();
                                            params.putArray("batch", jsonArray);
                                            sendEvent(reactContext, "onBatchReceived", params);
                                            sendEvent(reactContext, "onLastBatchReceived", null);
                                        }
                                    } else {

                                        if (songsPerIteration == mapSize) {
                                            WritableMap params = Arguments.createMap();
                                            params.putArray("batch", jsonArray);
                                            sendEvent(reactContext, "onBatchReceived", params);
                                            jsonArray = new WritableNativeArray();
                                            mapSize = 0;
                                            Thread.sleep(delay);
                                        } else if (pointer == (musicCursor.getCount() - 1)) {
                                            WritableMap params = Arguments.createMap();
                                            params.putArray("batch", jsonArray);
                                            sendEvent(reactContext, "onBatchReceived", params);
                                            sendEvent(reactContext, "onLastBatchReceived", null);
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

    public void getCoverByPath(String coverFolder, Double coverResizeRatio, Boolean getIcon,
            int iconSize, int coverSize, String songPath, long songId, WritableMap items) {

        MediaMetadataRetriever mmrr = new MediaMetadataRetriever();
        ReactNativeFileManager fcm = new ReactNativeFileManager();
        String encoded = "";
        try {
            mmrr.setDataSource(songPath);
            byte[] albumImageData = mmrr.getEmbeddedPicture();

            if (albumImageData != null) {
                Bitmap songImage = BitmapFactory.decodeByteArray(albumImageData, 0, albumImageData.length);
                Bitmap resized = songImage;
                if (coverResizeRatio != 1 && coverSize == 0) {
                    resized = Bitmap.createScaledBitmap(songImage, (int) (songImage.getWidth() * coverResizeRatio),
                            (int) (songImage.getHeight() * coverResizeRatio), true);
                }

                if (coverSize != 0) {
                    resized = Bitmap.createScaledBitmap(songImage, coverSize, coverSize, true);
                }

                try {
                    File covers = new File(Environment.getExternalStorageDirectory() + File.separator + coverFolder
                            + File.separator + ".covers");
                    boolean success = true;
                    if (!covers.exists()) {
                        success = covers.mkdirs();
                    }
                    if (success) {
                        String pathToImg = covers.getAbsolutePath() + "/covers" + songId + ".jpg";
                        encoded = fcm.saveImageToStorageAndGetPath(pathToImg, resized);
                        items.putString("cover", "file://" + encoded);
                    } else {
                        // Do something else on failure
                    }

                } catch (Exception e) {
                    // Just let images empty
                    Log.e("error in image", e.getMessage());
                }

                if (getIcon) {
                    try {
                        File icons = new File(Environment.getExternalStorageDirectory() + File.separator + coverFolder
                                + File.separator + "icons");
                        boolean success = true;
                        if (!icons.exists()) {
                            success = icons.mkdirs();
                        }
                        if (success) {
                            Bitmap icon = Bitmap.createScaledBitmap(songImage, iconSize, iconSize, true);
                            String pathToIcon = icons.getAbsolutePath() + "/icons" + songId + "-icon.jpg";
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

    private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }
}
