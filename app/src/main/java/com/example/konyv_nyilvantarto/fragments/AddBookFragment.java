package com.example.konyv_nyilvantarto.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konyv_nyilvantarto.BookApi;
import com.example.konyv_nyilvantarto.R;
import com.example.konyv_nyilvantarto.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBookFragment extends Fragment {

    private String mParam1;
    private String mParam2;

    private Button btSaveAdd;
    private EditText etTitleAdd;
    private EditText etAuthorAdd;
    private EditText etReleaseAdd;
    private EditText etPagesAdd;
    private EditText etGenreAdd;

    private static final String SUPABASE_URL = "https://plohvgiccntmsgzyszrl.supabase.co/";
    private static final String API_KEY = "sb_publishable_cuiKHkTKKdAHSnMkBcwghQ_rKE7skpG";



    public AddBookFragment() {
        // Required empty public constructor
    }

    public static AddBookFragment newInstance(String param1, String param2) {
        AddBookFragment fragment = new AddBookFragment();
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
        return inflater.inflate(R.layout.fragment_add_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btSaveAdd = view.findViewById(R.id.btSaveAdd);
        etTitleAdd = view.findViewById(R.id.etTitleAdd);
        etAuthorAdd = view.findViewById(R.id.etAuthorAdd);
        etReleaseAdd = view.findViewById(R.id.etReleaseAdd);
        etPagesAdd = view.findViewById(R.id.etPagesAdd);
        etGenreAdd = view.findViewById(R.id.etGenreAdd);

        btSaveAdd.setOnClickListener(v -> {
            Boolean title = etTitleAdd.getText().toString().trim().isEmpty();
            Boolean author = etAuthorAdd.getText().toString().trim().isEmpty();
            Boolean release = etReleaseAdd.getText().toString().trim().isEmpty();
            Boolean pages = etPagesAdd.getText().toString().trim().isEmpty();
            Boolean genre = etGenreAdd.getText().toString().trim().isEmpty();

            if(title || author || release ||pages|| genre){
                Toast.makeText(getContext(), "Minden mező kitöltése kötelező", Toast.LENGTH_SHORT).show();
            }
            else{
                FavoriteBook book = new FavoriteBook(
                        etTitleAdd.getText().toString(),
                        etAuthorAdd.getText().toString(),
                        "https://plohvgiccntmsgzyszrl.supabase.co/storage/v1/object/public/book_covers/nopicture.png",
                        Integer.parseInt(etPagesAdd.getText().toString()),
                        etReleaseAdd.getText().toString(),
                        etGenreAdd.getText().toString()
                );

                BookApi api = RetrofitClient
                        .getClient(SUPABASE_URL)
                        .create(BookApi.class);

                api.saveBookToSupabase(
                        API_KEY,
                        "Bearer " + API_KEY,
                        book
                ).enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(Call<Void> call,
                                           Response<Void> response) {

                        if(response.isSuccessful()) {

                            Toast.makeText(getContext(),
                                    "Mentve!",
                                    Toast.LENGTH_SHORT).show();

                        } else {

                            Toast.makeText(getContext(),
                                    "Hiba: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call,
                                          Throwable t) {

                        Toast.makeText(getContext(),
                                "Hálózati hiba!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }
}