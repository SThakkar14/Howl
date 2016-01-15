package boomer.com.howl.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import boomer.com.howl.Constants;
import boomer.com.howl.HTTPCodes;
import boomer.com.howl.HowlApiClient;
import boomer.com.howl.Objects.Howl;
import boomer.com.howl.Objects.HowlCommentBody;
import boomer.com.howl.Objects.HowlCommentResponse;
import boomer.com.howl.Objects.ResponseStatus;
import boomer.com.howl.R;
import boomer.com.howl.Services.AlbumStorageDirFactory;
import boomer.com.howl.Services.BaseAlbumDirFactory;
import boomer.com.howl.Services.FroyoAlbumDirFactory;
import boomer.com.howl.Services.ImageCaptureService;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class HowlThread extends AppCompatActivity {
    public static final String API_URL = "http://pethowl.com";
    String id;
    String user_id;
    HowlApiClient api;
    RecyclerView.Adapter adapter;
    List<Howl> comments;
    ProgressBar spinner;
    RecyclerView recyclerView;
    boolean following ;
    Menu currentMenuBar;
    private static final int ACTION_TAKE_PHOTO_B = 1;
    private String mCurrentPhotoPath;
    private static final String JPEG_FILE_PREFIX = "IMG_HOWL_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private ImageView mImageView;
    public  PhotoReadyReceiver receiver;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howl_thread);

        spinner = (ProgressBar) findViewById(R.id.howls_loading);
        spinner.setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.howl_thread_activity_recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HowlThread.this);
        recyclerView.setLayoutManager(layoutManager);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(HowlApiClient.class);
        final String accessToken = AccessToken.getCurrentAccessToken().getToken();

        Intent intent = getIntent();
        this.id = intent.getStringExtra("id");
        this.user_id = intent.getStringExtra("user_id");
        //this.following = (boolean) intent.getBooleanExtra("following" , false);

        ImageButton commentOnAHowl = (ImageButton) findViewById(R.id.commentOnAHowl);
        ImageButton takeAPicture = (ImageButton) findViewById(R.id.takePicture);
        mImageView = (ImageView) findViewById(R.id.imageView1);

        /**
         * Below is the code to register the photoreadyreceiver which would receive stuff
         * from ImageCaptureService on completion
         */
//        IntentFilter filter = new IntentFilter(PhotoReadyReceiver.ACTION_RESP);
//        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new PhotoReadyReceiver();
//        registerReceiver(receiver,filter);

        setBtnListenerOrDisable(
                takeAPicture,
                mTakePicOnClickListener,
                MediaStore.ACTION_IMAGE_CAPTURE
        );


        commentOnAHowl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = (EditText) findViewById(R.id.editText);
                String text = editText.getText().toString();
                spinner.setVisibility(View.VISIBLE);

                HowlCommentBody hcb = new HowlCommentBody(null, text);
                Call<HowlCommentResponse> commentResponse = api.post_comment(accessToken, id, hcb);
                commentResponse.enqueue(new Callback<HowlCommentResponse>() {
                    @Override
                    public void onResponse(Response<HowlCommentResponse> response, Retrofit retrofit) {
                        spinner.setVisibility(View.GONE);
                        editText.setText("");
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        updateComments();
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });
            }
        });


        Call<List<Howl>> feed = api.get_feed(accessToken, id);

        feed.enqueue(new Callback<List<Howl>>() {
            @Override
            public void onResponse(Response<List<Howl>> response, Retrofit retrofit) {
                spinner.setVisibility(View.GONE);
                comments = response.body();

                sortComments(comments);
                //Getting the first howl
                if(comments.get(0) != null && comments.get(0).getParent() ==1){
                    Log.i("isFollowing","OnCreate() "+comments.get(0).isFollowing());
                    following = comments.get(0).isFollowing();
                    if (following) {
//                        Log.i("isFollowing" , "onCreateOptionsMenu() Its a full star");
                        currentMenuBar.findItem(R.id.follow).setIcon(R.drawable.ic_star_white_24dp);
                    } else {
//                        Log.i("isFollowing" , "onCreateOptionsMenu() Its a border star");
                        currentMenuBar.findItem(R.id.follow).setIcon(R.drawable.ic_star_border_white_24dp);
                    }
                }
                adapter = new howl_thread_adapter();
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(comments.size() - 1);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }

    Button.OnClickListener mTakePicOnClickListener =
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
                }
            };
    private void dispatchTakePictureIntent(int actionCode) {

        Log.i("ImageCaptureService" , "BEFORE THE service");
        //Intent takePicturImageCaptureServiceeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent imageCaptureService = new Intent(this, ImageCaptureService.class);
        imageCaptureService.putExtra("actionCode", ACTION_TAKE_PHOTO_B);
        startService(imageCaptureService);
    }

    public class PhotoReadyReceiver extends BroadcastReceiver{
        public static final String ACTION_RESP =
                "boomer.com.howl.Services.MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context context, Intent intent) {
            Uri photoUri = (Uri) intent.getExtras().get("fileUri");
            String pathToPhoto = intent.getStringExtra("pathToPhoto");
            int actionCode = (Integer) intent.getExtras().get("actionCode");
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            mCurrentPhotoPath = pathToPhoto;
            startActivityForResult(takePictureIntent, actionCode);
        }
    }



    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void setBtnListenerOrDisable(
            ImageButton btn,
            Button.OnClickListener onClickListener,
            String intentName
    ) {
        if (isIntentAvailable(this, intentName)) {
            btn.setOnClickListener(onClickListener);
        } else {
            Log.e("setBtnListenerOrDisable","fail");
            btn.setClickable(false);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            }
//            case ACTION_TAKE_PHOTO_S: {
//                if (resultCode == RESULT_OK) {
//                    handleSmallCameraPhoto(data);
//                }
//                break;
//            } // ACTION_TAKE_PHOTO_S
        } // switch
    }

    @Override
    protected void onStop() {
        if(receiver != null) {

            unregisterReceiver(receiver);
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(PhotoReadyReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filter);

    }

    private void handleBigCameraPhoto() {
        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }

    private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(HowlApiClient.class);
        final String accessToken = AccessToken.getCurrentAccessToken().getToken();
        String description = "hello, this is description speaking";
        File file = new File(mCurrentPhotoPath);
        RequestBody requestBody =
                RequestBody.create(MediaType.parse("image/jpeg"), file);
        Call<ResponseStatus> howlCommentWithAnAttachment = api.post_comment_with_attachment(accessToken,this.id , requestBody, description);
        howlCommentWithAnAttachment.enqueue(new Callback<ResponseStatus>() {
            @Override
            public void onResponse(Response<ResponseStatus> response, Retrofit retrofit) {
                Log.i("AttachPhoto" , String.valueOf(response.code()));
                updateComments();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("AttachPhoto" , t.getMessage());
            }
        });
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void updateComments() {

        if (api == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            api = retrofit.create(HowlApiClient.class);
        }
        String accessToken = AccessToken.getCurrentAccessToken().getToken();
        api.get_feed(accessToken, id).enqueue(new Callback<List<Howl>>() {

            @Override
            public void onResponse(Response<List<Howl>> response, Retrofit retrofit) {
                if (response.code() == HTTPCodes.OK) {
                    comments = response.body();

                    sortComments(comments);
//                    //Getting the first howl
//                    if(comments.get(0) != null && comments.get(0).getParent() ==1){
//                        following = comments.get(0).isFollowing();
//                    }
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(comments.size() - 1);
                } else {
                    Log.e("updateComments()", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }


    private void sortComments(List<Howl> comments) {
        Collections.sort(comments, new Comparator<Howl>() {
            @Override
            public int compare(Howl lhs, Howl rhs) {
                if (lhs.getCreated() > rhs.getCreated())
                    return 1;
                else if (lhs.getCreated() < rhs.getCreated())
                    return -1;
                else
                    return 0;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_howl_thread, menu);
        this.currentMenuBar = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (api == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            api = retrofit.create(HowlApiClient.class);
        }

        final String accessToken = AccessToken.getCurrentAccessToken().getToken();
        //noinspection SimplifiableIfStatement
        if (id == R.id.follow) {
            if (this.following) {
                api.unfollow_a_feed(accessToken, this.id).enqueue(new Callback<ResponseStatus>() {

                    @Override
                    public void onResponse(Response<ResponseStatus> response, Retrofit retrofit) {
                        if (response.code() == HTTPCodes.OK) {
                            //flipping the menuitem based on the response
//                            Log.i("follow_response", "unfollow "+String.valueOf(response.code()));
                            item.setIcon(R.drawable.ic_star_border_white_24dp);
                            following = false;
                        } else {
                            Log.e("onOptionsItemSelected()", String.valueOf(response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });
            } else {
                api.follow_a_feed(accessToken, this.id).enqueue(new Callback<ResponseStatus>() {

                    @Override
                    public void onResponse(Response<ResponseStatus> response, Retrofit retrofit) {
                        if (response.code() == HTTPCodes.OK) {
                            //flipping the menuitem based on the response

                            item.setIcon(R.drawable.ic_star_white_24dp);
                            following = true;
                        } else {
                            Log.e("onOptionsItemSelected()", String.valueOf(response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class howl_thread_adapter extends RecyclerView.Adapter<howl_thread_adapter.ViewHolder> {

        @Override
        public int getItemViewType(int position) {
            Howl howl = comments.get(position);
            return howl.getUser_id().equals(user_id) ? 0 : 1;
        }

        @Override
        public howl_thread_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.howl_thread_comment, parent, false);

            CardView cv = (CardView) view.findViewById(R.id.card_view);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.card_view_parent);

            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width_rl = (int) (displaymetrics.widthPixels * 0.2);

            if (viewType == 1) {
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                rl.setPadding(0, 0, width_rl, 0);
            } else {
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                rl.setPadding(width_rl, 0, 0, 0);
            }
            cv.setLayoutParams(lp);

            return new howl_thread_adapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(howl_thread_adapter.ViewHolder holder, int position) {
            String thing = comments.get(position).getAttributes().getMessage();
            String imageAttachmentUrl = comments.get(position).getAttributes().getImage();
            if(imageAttachmentUrl != null){
                holder.card_image_attachment.setVisibility(View.VISIBLE);
                Picasso.with(getBaseContext())
                        .load(imageAttachmentUrl)
                        .resize(200, 200)
                        .centerCrop()
                        .rotate(90)
                        .into(holder.card_image_attachment);
            }

            holder.card_view_textview.setText(thing);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView card_view_textview;
            ImageView card_image_attachment;

            public ViewHolder(View itemView) {
                super(itemView);
                card_view_textview = (TextView) itemView.findViewById(R.id.card_view_textview);
                card_image_attachment = (ImageView) itemView.findViewById(R.id.card_image_attachment);
            }
        }
    }
}
