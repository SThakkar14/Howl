package boomer.com.howl.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import boomer.com.howl.HTTPCodes;
import boomer.com.howl.HowlApiClient;
import boomer.com.howl.Objects.Howl;
import boomer.com.howl.Objects.HowlCommentBody;
import boomer.com.howl.Objects.HowlCommentResponse;
import boomer.com.howl.Objects.ResponseStatus;
import boomer.com.howl.R;
import boomer.howl.Constants;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class HowlThread extends AppCompatActivity {
    public static final String API_URL = "http://pethowl.com";
    String zipcode;
    String id;
    String user_id;
    HowlApiClient api;
    RecyclerView.Adapter adapter;
    List<Howl> comments;
    ProgressBar spinner;
    RecyclerView recyclerView;
    Menu howlMenu;

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
        this.zipcode = intent.getStringExtra("zipcode");
        this.user_id = intent.getStringExtra("user_id");


        //Button button = (Button) findViewById(R.id.submitButton);
        ImageButton commentOnAHowl = (ImageButton) findViewById(R.id.commentOnAHowl);

        commentOnAHowl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = (EditText) findViewById(R.id.editText);
                String text = editText.getText().toString();
                spinner.setVisibility(View.VISIBLE);

                HowlCommentBody hcb = new HowlCommentBody(zipcode, text);
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

                adapter = new howl_thread_adapter();
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(comments.size() - 1);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

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
        this.howlMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(api == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            api = retrofit.create(HowlApiClient.class);
        }

        final String accessToken = AccessToken.getCurrentAccessToken().getToken();
        //noinspection SimplifiableIfStatement
        if (id == R.id.follow) {
            api.follow_a_feed(accessToken, this.id).enqueue(new Callback<ResponseStatus>() {

                @Override
                public void onResponse(Response<ResponseStatus> response, Retrofit retrofit) {
                    if (response.code() == HTTPCodes.OK) {
                        //flipping the menuitem based on the response
                        Log.i("follow_response",String.valueOf(response.code()));
                        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                        MenuItem unfollowItem = howlMenu.findItem(R.id.unfollow);
                        unfollowItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    } else {
                        Log.e("onOptionsItemSelected()", String.valueOf(response.code()));
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
            return true;
        }
        if (id == R.id.unfollow) {
            api.unfollow_a_feed(accessToken, this.id).enqueue(new Callback<ResponseStatus>() {

                @Override
                public void onResponse(Response<ResponseStatus> response, Retrofit retrofit) {
                    if (response.code() == HTTPCodes.OK) {
                        //flipping the menuitem based on the response
                        Log.i("follow_response",String.valueOf(response.code()));
                        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                        MenuItem followItem = howlMenu.findItem(R.id.follow);
                        followItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    } else {
                        Log.e("onOptionsItemSelected()", String.valueOf(response.code()));
                    }
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
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
            //String thing = comments.get(position).toString();
            holder.card_view_textview.setText(thing);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView card_view_textview;

            public ViewHolder(View itemView) {
                super(itemView);
                card_view_textview = (TextView) itemView.findViewById(R.id.card_view_textview);
            }
        }
    }
}
