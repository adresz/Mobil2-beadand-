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
    private List<BookAll> fullBookList = new ArrayList<>();
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
        BookApi api = RetrofitClient.getClient("https://openlibrary.org/").create(BookApi.class);

        api.searchBooks(query).enqueue(new Callback<OpenLibraryResponse>() {
            @Override
            public void onResponse(Call<OpenLibraryResponse> call, Response<OpenLibraryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BookAll> fetchedBooks = response.body().getDocs();
                    if (fetchedBooks != null) {
                        fullBookList.clear();
                        fullBookList.addAll(fetchedBooks);

                        bookList.clear();
                        bookList.addAll(fetchedBooks);

                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<OpenLibraryResponse> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Hiba történt!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.trim().isEmpty()) {
                    fetchBooksFromOpenLibrary(query);
                } else {
                    fetchBooksFromOpenLibrary("subject:fiction");
                }
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.trim().isEmpty()) {
                    fetchBooksFromOpenLibrary("subject:fiction");
                } else {
                    filterBooks(newText);
                }
                return true;
            }
        });
    }

    private void filterBooks(String query) {
        bookList.clear();
        if (query == null || query.trim().isEmpty()) {
            bookList.addAll(fullBookList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (BookAll book : fullBookList) {
                if (book.getTitle() != null && book.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                    bookList.add(book);
                }
            }
        }

        sortBooks(filterSpinner.getSelectedItemPosition());
    }

    private void sortBooks(int sortOptionPosition) {
        if (bookList == null || bookList.isEmpty()) return;

        switch (sortOptionPosition) {
            case 0: // Cím szerint növekvő
                bookList.sort((b1, b2) -> b1.getTitle().compareToIgnoreCase(b2.getTitle()));
                break;
            case 1: // Cím szerint csökkenő
                bookList.sort((b1, b2) -> b2.getTitle().compareToIgnoreCase(b1.getTitle()));
                break;
            case 2: // Szerző szerint növekvő
                bookList.sort((b1, b2) -> b1.getFirstAuthor().compareToIgnoreCase(b2.getFirstAuthor()));
                break;
            case 3: // Szerző szerint csökkenő
                bookList.sort((b1, b2) -> b2.getFirstAuthor().compareToIgnoreCase(b1.getFirstAuthor()));
                break;
        }
        adapter.notifyDataSetChanged();
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.AllBooksSortArray, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);

        filterSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                sortBooks(position);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }
}


