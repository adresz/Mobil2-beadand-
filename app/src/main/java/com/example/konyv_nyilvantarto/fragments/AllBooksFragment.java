package com.example.konyv_nyilvantarto.fragments;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.konyv_nyilvantarto.OpenLibraryResponse;
import com.example.konyv_nyilvantarto.R;
import com.example.konyv_nyilvantarto.BookAll;
import com.example.konyv_nyilvantarto.BookAdapterAll;
import com.example.konyv_nyilvantarto.RetrofitClient;
import com.example.konyv_nyilvantarto.BookApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllBooksFragment extends Fragment {

    private BookAdapterAll adapter;
    private List<BookAll> bookList = new ArrayList<>();
    private SearchView searchView;
    private Spinner filterSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_books, container, false);

        // Initialize UI components directly
        RecyclerView recyclerView = view.findViewById(R.id.rvAllBooks);
        searchView = view.findViewById(R.id.svAll);
        filterSpinner = view.findViewById(R.id.spFilteringAll);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookAdapterAll(bookList);
        recyclerView.setAdapter(adapter);

        setupSpinner();
        setupSearch();

        fetchBooksFromOpenLibrary("subject:fiction");
        return view;
    }

    private void fetchBooksFromOpenLibrary(String query) {
        // Cleaning up: Move API creation logic if possible, or keep it simple here
        BookApi api = RetrofitClient.getClient("https://openlibrary.org/").create(BookApi.class);

        api.searchBooks(query).enqueue(new Callback<OpenLibraryResponse>() {
            @Override
            public void onResponse(Call<OpenLibraryResponse> call, Response<OpenLibraryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bookList.clear();
                    List<BookAll> fetchedBooks = response.body().getDocs();
                    if (fetchedBooks != null) {
                        bookList.addAll(fetchedBooks);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<OpenLibraryResponse> call, Throwable t) {
                if (isAdded()) { // Prevents crash if user leaves fragment during load
                    Toast.makeText(getContext(), "Network Failure!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.HomeSpinner, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    fetchBooksFromOpenLibrary(query);
                    searchView.clearFocus(); // Closes the keyboard
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) { return false; }
        });
    }
}