package io.evercam.androidapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;

import io.evercam.androidapp.ParentActivity;

public class EvercamFile
{
    private static final String TAG = "EvercamFile";
    public static final String SUFFIX_JPG = ".jpg";

    public static File getCacheFileRelative(Context context, String cameraId)
    {
        String cachePath = context.getCacheDir() + File.separator + cameraId + SUFFIX_JPG;
        return new File(cachePath);
    }

    public static File getExternalFile(Context context, String cameraId)
    {
        File externalFile;
        String extCachePath = context.getExternalFilesDir(null) + File.separator + cameraId +
                SUFFIX_JPG;
        externalFile = new File(extCachePath);
        return externalFile;
    }
}
