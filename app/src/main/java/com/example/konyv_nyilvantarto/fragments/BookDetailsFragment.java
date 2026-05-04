package com.example.konyv_nyilvantarto.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.konyv_nyilvantarto.R;

public class BookDetailsFragment extends Fragment {

    private EditText etBookTitleD;
    private EditText etAuthorD;
    private EditText etReleaseD;
    private EditText etPagesD;
    private EditText etReadPagesD;
    private EditText etMaxPagesD;

    private EditText etGenreD;
    private ImageView ivBookCoverD;

    public BookDetailsFragment() {
        // Required empty public constructor
    }
    public static BookDetailsFragment newInstance(String param1, String param2) {
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        etBookTitleD = view.findViewById(R.id.etBookTitleD);
        etAuthorD = view.findViewById(R.id.etAuthorD);
        etReleaseD = view.findViewById(R.id.etReleaseD);
        etPagesD = view.findViewById(R.id.etPagesD);
        etReadPagesD = view.findViewById(R.id.etReadPagesD);
        etMaxPagesD = view.findViewById(R.id.etMaxPagesD);
        etGenreD = view.findViewById(R.id.etGenreD);

        ivBookCoverD = view.findViewById(R.id.ivBookCoverD);

        etBookTitleD.setText("Cím: " + getArguments().getString("title"));
        etAuthorD.setText(getArguments().getString("author"));
        etReleaseD.setText(getArguments().getString("year"));
        etGenreD.setText(getArguments().getString("genre"));
        etPagesD.setText(String.valueOf(getArguments().getInt("maxPages")));
    }
}