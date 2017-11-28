package com.reactlibrary;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by dantecervantes on 28/11/17.
 */

public class ReactNativeFileManager extends ReactNativeBlurImage{
    public String saveImageToStorageAndGetPath(String pathToImg, Bitmap songImage) throws IOException {
        if (songImage != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            songImage.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            if(byteArray != null) {
                this.saveToStorage(pathToImg, byteArray);

                return pathToImg;
            }
        }

        return null;
    }



    public void saveToStorage(String pathToImg, byte[] imageBytes) throws IOException {
        FileOutputStream fos = null;
        try {
            File filePath = new File(pathToImg);
            fos = new FileOutputStream(filePath, true);
            fos.write(imageBytes);
        } finally {
            if (fos != null) {
                fos.flush();
                fos.close();
            }
        }
    }
}
