package boomer.com.howl.Services;

import java.io.File;

/**
 * Created by raniredd on 1/12/2016.
 */
public abstract class AlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);
}
