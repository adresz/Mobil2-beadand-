package com.example.konyv_nyilvantarto;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kotlin.collections.ArrayDeque;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.all_books);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.clMainAll), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        /*
        RecyclerView recyclerView = findViewById(R.id.rvMyBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<BookHome> bookList = new ArrayList<>();

        bookList.add(new BookHome("Én, a kétarcú", "Ambrózy Áron György", 2026, 90, R.drawable.ketarcu));
        bookList.add(new BookHome("Egri Csillagok", "Gárdonyi Géza", 1899, 45, R.drawable.egricsillagok));
        bookList.add(new BookHome("A Pál utcai fiúk", "Molnár Ferenc", 1906, 100, R.drawable.apalutcaifiuk));

        BookAdapterHome adapter = new BookAdapterHome(bookList);
        recyclerView.setAdapter(adapter);
        */

    }
}