package boomer.com.howl.Services;

import android.os.Environment;

import java.io.File;

/**
 * Created by raniredd on 1/12/2016.
 */
public class FroyoAlbumDirFactory extends AlbumStorageDirFactory {@Override
  public File getAlbumStorageDir(String albumName) {
    // TODO Auto-generated method stub
    return new File(
            Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
            ),
            albumName
    );
}

}
