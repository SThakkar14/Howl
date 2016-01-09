package boomer.com.howl.Activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;

import java.io.IOException;
import java.util.List;

import boomer.com.howl.Constants;
import boomer.com.howl.Fragments.HowlsFragment;
import boomer.com.howl.Fragments.OtherFragment;
import boomer.com.howl.Fragments.ProfileFragment;
import boomer.com.howl.HTTPCodes;
import boomer.com.howl.HowlApiClient;
import boomer.com.howl.Objects.Howl;
import boomer.com.howl.Objects.ResponseStatus;
import boomer.com.howl.Objects.UserProfile;
import boomer.com.howl.R;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class LandingPage extends AppCompatActivity {

    UserProfile initialProfile;
    HowlApiClient api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initialProfile = (UserProfile) getIntent().getSerializableExtra("user_profile");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_landing_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_landing_page, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        Fragment[] fragments;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new Fragment[4];
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    if (fragments[0] == null) {
                        fragments[0] = HowlsFragment.newInstance(initialProfile.getHowls(),initialProfile.getId());
//                        fragments[0] = HowlsFragment.newInstance(initialProfile);
                    }
                    return fragments[0];
                case 1:
                    if (fragments[1] == null) {
//                        Retrofit retrofit = new Retrofit.Builder()
//                                .baseUrl(Constants.BASE_URL)
//                                .addConverterFactory(GsonConverterFactory.create())
//                                .build();
//
//                        api = retrofit.create(HowlApiClient.class);
//                        final String accessToken = AccessToken.getCurrentAccessToken().getToken();
//
//                        api.get_following_feeds(accessToken).enqueue(new Callback<List<Howl>>() {
//
//                            @Override
//                            public void onResponse(Response<List<Howl>> response, Retrofit retrofit) {
//                                if (response.code() == HTTPCodes.OK) {
//                                    List<Howl> howls = response.body();
//                                    Log.i("followedHowls" , ""+howls.size());
//                                    fragments[1] = HowlsFragment.newInstance(howls,initialProfile.getId());
//
//                                } else {
//                                    Log.e("updateComments()", String.valueOf(response.code()));
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Throwable t) {
//
//                            }
//
//                        });

                        fragments[1] = HowlsFragment.newInstance(initialProfile.getHowls(),initialProfile.getId());
                    }
                    Log.i("followedHowls" , ""+fragments[1]);
                    return fragments[1];


                case 2:
                    if (fragments[2] == null) {
                        fragments[2] = ProfileFragment.newInstance("asdf", "asdf");
                    }
                    return fragments[2];

                case 3:
                    if (fragments[3] == null) {
                        fragments[3] = new OtherFragment();
                    }
                    return fragments[3];
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Howls";
                case 1:
                    return "Following";
                case 2:
                    return "Profile";
                case 3:
                    return "Other";
            }
            return null;
        }
    }
}
