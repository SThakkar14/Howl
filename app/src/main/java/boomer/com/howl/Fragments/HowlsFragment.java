package boomer.com.howl.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;

import java.util.List;

import boomer.com.howl.Activities.HowlThread;
import boomer.com.howl.Constants;
import boomer.com.howl.HTTPCodes;
import boomer.com.howl.HowlApiClient;
import boomer.com.howl.Objects.Howl;
import boomer.com.howl.Objects.UserProfile;
import boomer.com.howl.R;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class HowlsFragment extends Fragment {
    UserProfile userProfile;
    List<Howl> howls;
    HowlApiClient api;
    RecyclerView.Adapter adapter;

    public HowlsFragment() {
        // Required empty public constructor
    }

    public static HowlsFragment newInstance(UserProfile userProfile) {
        HowlsFragment fragment = new HowlsFragment();
        Bundle args = new Bundle();
        args.putSerializable("HOWLS", userProfile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userProfile = (UserProfile) getArguments().getSerializable("HOWLS");
            if (userProfile != null) {
                howls = userProfile.getHowls();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_howls, container, false);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.howl_fragment_recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new howl_adapter();
        recyclerView.setAdapter(adapter);

        return v;
    }

    public void updateHowls() {
        if (api == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            api = retrofit.create(HowlApiClient.class);
        }

        String accessToken = AccessToken.getCurrentAccessToken().getToken();
        api.get_howls(accessToken).enqueue(new Callback<List<Howl>>() {
            @Override
            public void onResponse(Response<List<Howl>> response, Retrofit retrofit) {
                if (response.code() == HTTPCodes.OK) {
                    howls = response.body();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

    }

    public class howl_adapter extends RecyclerView.Adapter<howl_adapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.howl_card_layout, parent, false);
            return new howl_adapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Howl howl = howls.get(position);
            holder.example.setText(howl.getAttributes().getMessage());
            //Picasso.with(getContext()).load("https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png").into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return howls.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView example;
            ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);
                example = (TextView) itemView.findViewById(R.id.howl_card_message);
                imageView = (ImageView) itemView.findViewById(R.id.howl_card_image);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Howl howl = howls.get(getAdapterPosition());

                        Intent intent = new Intent(v.getContext(), HowlThread.class);
                        intent.putExtra("user_id", userProfile.getId());
                        intent.putExtra("id", howl.getId());
                        startActivity(intent);
                    }
                });
            }
        }
    }

}
