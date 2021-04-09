package com.mcbedrock.minecraftnews.realeseChangelog;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.mcbedrock.minecraftnews.R;

public class realeseRecyclerViewFragment extends Fragment {

    //ГЕНЕРАТОР КАРТОЧЕК

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    RecyclerView recview;
    bedrockRealeseAdapter adapter;
    bedrockRealeseBigCardAdapter adapterBC;

    private Boolean card_size;

    public realeseRecyclerViewFragment() {

    }

    public static realeseRecyclerViewFragment newInstance(String param1, String param2) {
        realeseRecyclerViewFragment fragment = new realeseRecyclerViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        LoadPrefs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_realese_recview, container, false);

        recview = (RecyclerView) view.findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<RealeseChangelogModel> options =
                new FirebaseRecyclerOptions.Builder<RealeseChangelogModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("realese_changelogs"), RealeseChangelogModel.class)
                .build();

        if (card_size) {
            adapter = new bedrockRealeseAdapter(options);
            recview.setAdapter(adapter);
        } else {
            adapterBC = new bedrockRealeseBigCardAdapter(options);
            recview.setAdapter(adapterBC);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (card_size) {
            adapter.startListening();
        } else {
            adapterBC.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (card_size) {
            adapter.startListening();
        } else {
            adapterBC.startListening();
        }
    }

    private void LoadPrefs() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        card_size = sharedPreferences.getBoolean("card_smallsize", true);
    }

}