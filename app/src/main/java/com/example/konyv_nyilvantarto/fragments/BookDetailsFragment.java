package com.example.konyv_nyilvantarto.fragments;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konyv_nyilvantarto.R;

import java.security.PrivateKey;

import okhttp3.OkHttpClient;

public class BookDetailsFragment extends Fragment {

    private EditText etBookTitleD;
    private EditText etAuthorD;
    private EditText etReleaseD;
    private EditText etPagesD;
    private EditText etReadPagesD;
    private TextView tvMaxPagesD;
    private RatingBar rbStarRatingD;
    private EditText etGenreD;
    private ImageView ivBookCoverD;
    private EditText etEndDateD;
    private EditText etStartDateD;
    private EditText etOpinionD;
    private TextView tvTitleD;
    private Button btnSaveD;
    private Spinner spStateD;

    private Button btnCancelD;
    private long bookId = -1L;

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
        etGenreD = view.findViewById(R.id.etGenreD);
        tvMaxPagesD = view.findViewById(R.id.tvMaxPagesD);
        rbStarRatingD = view.findViewById(R.id.rbStarRatingD);
        etReadPagesD = view.findViewById(R.id.etReadPagesD);
        etReadPagesD = view.findViewById(R.id.etReadPagesD);
        ivBookCoverD = view.findViewById(R.id.ivBookCoverD);
        etStartDateD = view.findViewById(R.id.etStartDateD);
        etEndDateD = view.findViewById(R.id.etEndDateD);
        etOpinionD = view.findViewById(R.id.etOpinionD);
        tvTitleD = view.findViewById(R.id.tvTitleD);
        btnSaveD = view.findViewById(R.id.btnSaveD);

        if (getArguments() != null) {
            bookId = getArguments().getLong("id", -1L);
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
            tvTitleD.setText(getArguments().getString("title"));

            tvMaxPagesD.setText(etPagesD.getText());

            String imageUrl = getArguments().getString("cover");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                com.bumptech.glide.Glide.with(this)
                        .load(imageUrl)
                        .into(ivBookCoverD);
            }
        }

        btnSaveD.setOnClickListener(v -> saveBookData());
    }

    private void saveBookData() {
        String title = etBookTitleD.getText().toString().trim();
        String author = etAuthorD.getText().toString().trim();
        String releaseYear = etReleaseD.getText().toString().trim();
        String genre = etGenreD.getText().toString().trim();
        float rating = rbStarRatingD.getRating();
        String startDate = etStartDateD.getText().toString().trim();
        String finishDate = etEndDateD.getText().toString().trim();
        String note = etOpinionD.getText().toString().trim();

        String releaseYearDate = releaseYear.isEmpty() ? null : releaseYear + "-01-01";

        int maxPages = 0;
        int currentPages = 0;

        try {
            String maxP = tvMaxPagesD.getText().toString().trim();
            String currP = etReadPagesD.getText().toString().trim();

            if (!maxP.isEmpty()) maxPages = Integer.parseInt(maxP);
            if (!currP.isEmpty()) currentPages = Integer.parseInt(currP);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Kérlek, érvényes számokat adj meg az oldalakhoz!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            OkHttpClient client = new OkHttpClient();

            org.json.JSONObject detailsJson = new org.json.JSONObject();
            detailsJson.put("book_name", title);
            detailsJson.put("book_author", author);

            if (releaseYearDate != null) {
                detailsJson.put("release_year", releaseYearDate);
            } else {
                detailsJson.put("release_year", org.json.JSONObject.NULL);
            }

            detailsJson.put("genre", genre);
            detailsJson.put("max_pages", maxPages);
            detailsJson.put("rating", rating);
            detailsJson.put("note", note);
            detailsJson.put("start_date", startDate.isEmpty() ? org.json.JSONObject.NULL : startDate);
            detailsJson.put("finish_date", finishDate.isEmpty() ? org.json.JSONObject.NULL : finishDate);

            okhttp3.RequestBody detailsBody = okhttp3.RequestBody.create(
                    detailsJson.toString(),
                    okhttp3.MediaType.parse("application/json; charset=utf-8")
            );

            String detailsUrl = com.example.konyv_nyilvantarto.utils.Constants.SUPABASE_BASE_URL + "Book_Details?id=eq." + bookId;

            okhttp3.Request detailsRequest = new okhttp3.Request.Builder()
                    .url(detailsUrl)
                    .patch(detailsBody)
                    .addHeader("apikey", com.example.konyv_nyilvantarto.utils.Constants.SUPABASE_API_KEY)
                    .addHeader("Authorization", "Bearer " + com.example.konyv_nyilvantarto.utils.Constants.SUPABASE_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            int finalCurrentPages = currentPages;
            client.newCall(detailsRequest).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull okhttp3.Call call, @NonNull java.io.IOException e) {
                    showToastOnUi("Hiba a mentés során: " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws java.io.IOException {
                    if (response.isSuccessful()) {
                        updateBookProgress(client, bookId, finalCurrentPages, title);
                    } else {
                        showToastOnUi("Supabase hiba (Details): " + response.code() + " " + response.message());
                    }
                    response.close();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Hiba a JSON összeállításánál!", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateBookProgress(OkHttpClient client, long bookId, int currentPages, String newTitle) {
        try {
            org.json.JSONObject progressJson = new org.json.JSONObject();
            progressJson.put("current_page", currentPages);

            okhttp3.RequestBody progressBody = okhttp3.RequestBody.create(
                    progressJson.toString(),
                    okhttp3.MediaType.parse("application/json; charset=utf-8")
            );

            String progressUrl = com.example.konyv_nyilvantarto.utils.Constants.SUPABASE_BASE_URL + "Book_Progress?book_id=eq." + bookId;

            okhttp3.Request progressRequest = new okhttp3.Request.Builder()
                    .url(progressUrl)
                    .patch(progressBody)
                    .addHeader("apikey", com.example.konyv_nyilvantarto.utils.Constants.SUPABASE_API_KEY)
                    .addHeader("Authorization", "Bearer " + com.example.konyv_nyilvantarto.utils.Constants.SUPABASE_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(progressRequest).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(@NonNull okhttp3.Call call, @NonNull java.io.IOException e) {
                    showToastOnUi("A könyv adatai mentve, de a haladás frissítése sikertelen.");
                }

                @Override
                public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws java.io.IOException {
                    if (response.isSuccessful()) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Minden módosítás sikeresen mentve!", Toast.LENGTH_SHORT).show();
                                tvTitleD.setText(newTitle);
                            });
                        }
                    } else {
                        showToastOnUi("Supabase hiba (Progress): " + response.code());
                    }
                    response.close();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showToastOnUi(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
        }
    }

}