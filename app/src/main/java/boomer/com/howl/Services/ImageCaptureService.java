package boomer.com.howl.Services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.facebook.AccessToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import boomer.com.howl.HowlApiClient;
import boomer.com.howl.Objects.ResponseStatus;
import boomer.com.howl.R;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by raniredd on 1/14/2016.
 */
public class ImageCaptureService extends IntentService {

    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int DEFAULT_ACTION_TAKE_PHOTO_B = 1;
    private String mCurrentPhotoPath;
    private static final String JPEG_FILE_PREFIX = "IMG_HOWL_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private static final String ACTION_RESP = "boomer.com.howl.Services.MESSAGE_PROCESSED";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */
    public ImageCaptureService() {
        super("ImageCaptureService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        Log.i("ImageCaptureService" , "service started");
        int actionCode = intent.getIntExtra("actionCode" ,DEFAULT_ACTION_TAKE_PHOTO_B);
        File f = null;
        switch(actionCode) {
            case ACTION_TAKE_PHOTO_B:
                try {
                    f = setUpPhotoFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCurrentPhotoPath = f.getAbsolutePath();

//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                break;

            default:
                break;
        } // switch
            Intent broadcastIntent  = new Intent();
            broadcastIntent.setAction(ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra("fileUri" , Uri.fromFile(f));
            broadcastIntent.putExtra("pathToPhoto" , mCurrentPhotoPath);
            broadcastIntent.putExtra("actionCode" , actionCode);
            sendBroadcast(broadcastIntent);

    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private String getAlbumName() {
        return "Howl";
    }



}
