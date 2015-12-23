package boomer.com.howl.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.login.LoginManager;

import boomer.com.howl.Activities.SplashSpreen;
import boomer.com.howl.R;

public class OtherFragment extends Fragment {


    public OtherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_other, container, false);

        Button button = (Button) v.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(getActivity(), SplashSpreen.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return v;
    }

}
