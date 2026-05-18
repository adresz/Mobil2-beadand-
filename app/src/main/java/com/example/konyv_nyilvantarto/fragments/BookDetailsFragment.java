package com.example.konyv_nyilvantarto.fragments;

import android.media.Image;
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
import android.widget.RatingBar;

import com.example.konyv_nyilvantarto.R;

import java.security.PrivateKey;

public class BookDetailsFragment extends Fragment {

    private EditText etBookTitleD;
    private EditText etAuthorD;
    private EditText etReleaseD;
    private EditText etPagesD;
    private EditText etReadPagesD;
    private EditText etMaxPagesD;
    private RatingBar rbStarRatingD;
    private EditText etGenreD;
    private ImageView ivBookCoverD;
    private EditText etEndDateD;
    private EditText etStartDateD;
    private EditText etOpinionD;

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
        etMaxPagesD = view.findViewById(R.id.etMaxPagesD);
        rbStarRatingD = view.findViewById(R.id.rbStarRatingD);
        etReadPagesD = view.findViewById(R.id.etReadPagesD);
        etReadPagesD = view.findViewById(R.id.etReadPagesD);
        ivBookCoverD = view.findViewById(R.id.ivBookCoverD);
        etStartDateD = view.findViewById(R.id.etStartDateD);
        etEndDateD = view.findViewById(R.id.etEndDateD);
        etOpinionD = view.findViewById(R.id.etOpinionD);

        etBookTitleD.setText(getArguments().getString("title"));
        etAuthorD.setText(getArguments().getString("author"));
        etReleaseD.setText(getArguments().getString("year"));
        etGenreD.setText(getArguments().getString("genre"));
        etPagesD.setText(String.valueOf(getArguments().getInt("maxPages")));
        etReadPagesD.setText(String.valueOf(getArguments().get("currentPages")));
        rbStarRatingD.setRating(getArguments().getFloat("rating", 0f));
        etStartDateD.setText(getArguments().getString("start_date"));
        etEndDateD.setText(getArguments().getString("finish_date"));
        etOpinionD.setText(getArguments().getString("note"));
        etMaxPagesD.setText(etPagesD.getText());

        String imageUrl = getArguments().getString("cover");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            com.bumptech.glide.Glide.with(this)
                    .load(imageUrl)
                    .into(ivBookCoverD);
        }

    }
}