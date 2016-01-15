package boomer.com.howl.Services;

import android.os.Environment;

import java.io.File;

/**
 * Created by raniredd on 1/12/2016.
 */
public class BaseAlbumDirFactory extends AlbumStorageDirFactory {
    private static final String CAMERA_DIR = "/dcim/";

    @Override
    public File getAlbumStorageDir(String albumName) {
        return new File (
                Environment.getExternalStorageDirectory()
                        + CAMERA_DIR
                        + albumName
        );
    }
}
