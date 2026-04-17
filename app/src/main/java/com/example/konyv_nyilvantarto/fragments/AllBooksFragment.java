package com.example.konyv_nyilvantarto.fragments;
import android.os.Bundle;
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

import com.example.konyv_nyilvantarto.R;
import com.example.konyv_nyilvantarto.BookAll;
import com.example.konyv_nyilvantarto.BookAdapterAll;

import java.util.ArrayList;
import java.util.List;

public class AllBooksFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookAdapterAll adapter;
    private List<BookAll> bookList;
    private SearchView searchView;
    private Spinner filterSpinner;

    public AllBooksFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_books, container, false);

        //Initialize UI components
        recyclerView = view.findViewById(R.id.rvAllBooks);
        searchView = view.findViewById(R.id.svAll);
        filterSpinner = view.findViewById(R.id.spFilteringAll);

        //Example data for the demo
        fillExampleData();

        //Set up RecyclerView with LayoutManager and Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookAdapterAll(bookList);
        recyclerView.setAdapter(adapter);

        //Setup the Spinner (Filter)
        setupSpinner();

        //Setup search functionality base
        setupSearch();

        return view;
    }

    private void fillExampleData() {
        bookList = new ArrayList<>();
        // Fontos: R.drawable.nopicture létezzen, vagy írd át arra, ami van!
        bookList.add(new BookAll("Egri Csillagok", "Gárdonyi Géza", R.drawable.egricsillagok));
        bookList.add(new BookAll("A Pál utcai fiúk", "Molnár Ferenc", R.drawable.apalutcaifiuk));
        bookList.add(new BookAll("Én, a kétarcú", "Ambrózy Áron György", R.drawable.ketarcu));
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.HomeSpinner, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(spinnerAdapter);
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Placeholder for future filtering
                return true;
            }
        });
    }
}