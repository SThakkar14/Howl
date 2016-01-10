package boomer.com.howl.Fragments;

import android.util.Log;

import com.facebook.AccessToken;

import java.util.List;

import boomer.com.howl.Constants;
import boomer.com.howl.HTTPCodes;
import boomer.com.howl.HowlApiClient;
import boomer.com.howl.Objects.Howl;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class HowlsFragment extends AbstractHowlListFragment {

    public HowlsFragment(String userId) {
        this.userId = userId;
    }

    @Override
    public void getHowls() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HowlApiClient api = retrofit.create(HowlApiClient.class);

        final String accessToken = AccessToken.getCurrentAccessToken().getToken();
        api.get_howls(accessToken).enqueue(new Callback<List<Howl>>() {
            @Override
            public void onResponse(Response<List<Howl>> response, Retrofit retrofit) {
                if (response.code() == HTTPCodes.OK) {
                    HowlsFragment.this.howls = response.body();
                    HowlsFragment.this.adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Failed", accessToken);
            }
        });
    }
}
