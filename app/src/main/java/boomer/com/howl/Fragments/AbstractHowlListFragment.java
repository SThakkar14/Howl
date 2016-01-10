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

import java.util.List;

import boomer.com.howl.Activities.HowlThread;
import boomer.com.howl.Objects.Howl;
import boomer.com.howl.R;

abstract public class AbstractHowlListFragment extends Fragment {
    protected List<Howl> howls;
    protected String userId;
    protected RecyclerView.Adapter adapter;


    public AbstractHowlListFragment() {
        // Required empty public constructor
    }

    abstract public void getHowls();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_abstract_howl_list, container, false);

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.abstract_howl_list_recycler_view);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new howl_adapter();
        recyclerView.setAdapter(adapter);

        getHowls();

        return v;
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
            if (howls == null)
                return 0;
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
                        intent.putExtra("user_id", userId);
                        intent.putExtra("id", howl.getId());
                        startActivity(intent);
                    }
                });
            }
        }
    }

}
