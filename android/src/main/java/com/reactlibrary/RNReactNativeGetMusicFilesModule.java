
package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;


import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;


public class RNReactNativeGetMusicFilesModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private ReactNativeFileManager fcm;

  public RNReactNativeGetMusicFilesModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNReactNativeGetMusicFiles";
  }

  @ReactMethod
  public void getAll(ReadableMap options, Callback errorCallback, Callback successCallback){

      successCallback.invoke(options);

  }

  /*@ReactMethod
  public void getAll(Boolean createBlur, Callback errorCallback, Callback successCallback) {

        ContentResolver musicResolver = this.getCurrentActivity().getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {

            if (musicCursor.getCount() > 0) {
                WritableArray jsonArray = new WritableNativeArray();
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                WritableMap items = new WritableNativeMap();
                
                //metadata variables
                int titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
                int idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
                int artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST);

                try {
                    do {
                        try {
                            items = new WritableNativeMap();
                            byte[] art;

                            //without metadata
                            long thisId = musicCursor.getLong(idColumn);
                            String thisPath = musicCursor
                                    .getString(musicCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                            String thisTitle = musicCursor.getString(titleColumn);
                            String thisArtist = musicCursor.getString(artistColumn);
                            String Duration = musicCursor
                                    .getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION));

                            if (thisPath != null && thisPath != "") {
                                mmr.setDataSource(thisPath);
                                //with metadata
                                String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                                String author = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                                String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                                String genero = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
                                String fileName = thisPath.substring(thisPath.lastIndexOf("/") + 1);
                                String encoded = "";
                                String blurred = "";
                                String encodedImage = "";
                                String blurImage = "";

                                art = mmr.getEmbeddedPicture();

                                if (album == null) {
                                    album = thisArtist;
                                }

                                if (author == null) {
                                    author = thisArtist;
                                }

                                if (title == null) {
                                    title = thisTitle;
                                }

                                if (art != null) {
                                    Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);

                                    try {
                                        String pathToImg = Environment.getExternalStorageDirectory() + "/" + thisId + ".jpg";
                                        encoded = fcm.saveImageToStorageAndGetPath(pathToImg, songImage);
                                    } catch (Exception e) {
                                        // Just let images empty
                                    }

                                    if (createBlur) {
                                        try {
                                            String pathToImg = Environment.getExternalStorageDirectory() + "/" + thisId + "-blur.jpg";
                                            blurred = fcm.saveBlurImageToStorageAndGetPath(pathToImg, songImage);
                                        } catch (Exception e) {
    
                                        }
                                    }
                                }

                                String str = String.valueOf(thisId);
                                //convert string to long
                                thisId = Long.valueOf(str);

                                items.putString("id", str);
                                items.putString("album", album);
                                items.putString("author", author);
                                items.putString("title", title);
                                items.putString("genre", genero);

                                if (encoded == "") {
                                    items.putString("cover", "");
                                } else {
                                    items.putString("cover", "file://" + encoded);
                                }

                                if (blurred == "") {
                                    items.putString("blur", "");
                                } else {
                                    items.putString("blur", "file://" + blurred);
                                }

                                items.putString("duration", Duration);
                                items.putString("path", thisPath);
                                items.putString("fileName", fileName);

                                jsonArray.pushMap(items);
                            }
                        } catch (Exception e) {
                            // An error in one message should not prevent from getting the rest
                            // There are cases when a corrupted file can't be read and a RuntimeException is raised

                            // Let's discuss how to deal with these kind of exeptions

                            continue; // This is redundant, but adds meaning
                        }
                    } while (musicCursor.moveToNext());

                    successCallback.invoke(jsonArray);
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
    }*/
}