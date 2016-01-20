package com.pbasolutions.android.utils;

/**
 * Created by pbadell on 10/23/15.
 */
import java.io.File;

public abstract class AlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);
}