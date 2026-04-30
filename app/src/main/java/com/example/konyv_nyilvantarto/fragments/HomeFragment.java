package com.example.konyv_nyilvantarto.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.konyv_nyilvantarto.BookAdapterHome;
import com.example.konyv_nyilvantarto.R;
import com.example.konyv_nyilvantarto.model.BookItemHome;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView rvMyBooksHome;
    private BookAdapterHome bookAdapterHome;
    private List<BookItemHome> fullBookList;
    private List<BookItemHome> displayBookList;
    private Spinner spSortHome;
    private SearchView swSearchHome;

    private TextView tvEmptySearchHome;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMyBooksHome = view.findViewById(R.id.rvMyBooksHome);
        rvMyBooksHome.setLayoutManager(new LinearLayoutManager(requireContext()));

        fullBookList = new ArrayList<>();
        displayBookList = new ArrayList<>();

        bookAdapterHome = new BookAdapterHome(displayBookList);
        rvMyBooksHome.setAdapter(bookAdapterHome);

        loadBooksFromSupabase();

        spSortHome = view.findViewById(R.id.spSortHome);

        spSortHome.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortBooks(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        swSearchHome = view.findViewById(R.id.swSearchHome);
        tvEmptySearchHome = view.findViewById(R.id.tvEmptySearchHome);

        swSearchHome.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Nem csinálunk semmit az Enter lenyomásakor
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Minden gépelt karakternél lefut a szűrés
                filterBooks(newText);
                return true;
            }
        });


    }

    private void loadBooksFromSupabase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();

                    String supabaseUrl = "https://plohvgiccntmsgzyszrl.supabase.co/rest/v1/Book_Details?select=id,book_name,book_author,release_year,max_pages,cover,Book_Progress(current_page)";
                    String apiKey = "sb_publishable_cuiKHkTKKdAHSnMkBcwghQ_rKE7skpG";

                    Request request = new Request.Builder()
                            .url(supabaseUrl)
                            .addHeader("apikey", apiKey)
                            .addHeader("Authorization", "Bearer " + apiKey)
                            .build();

                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful() && response.body() != null) {
                        String responseData = response.body().string();

                        JSONArray jsonArray = new JSONArray(responseData);

                        fullBookList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject bookObj = jsonArray.getJSONObject(i);

                            BookItemHome item = new BookItemHome();
                            item.setBook_name(bookObj.optString("book_name", "Ismeretlen cím"));
                            item.setBook_author(bookObj.optString("book_author", "Ismeretlen szerző"));

                            String dateStr = bookObj.optString("release_year", "");
                            if (!dateStr.isEmpty()) {
                                try {
                                    java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                                    java.util.Date dateObj = format.parse(dateStr);
                                    item.setRelease_year(dateObj);
                                } catch (java.text.ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            item.setMax_pages(bookObj.optInt("max_pages", 100));
                            item.setCover(bookObj.optString("cover", ""));

                            JSONArray progressArray = bookObj.optJSONArray("Book_Progress");
                            if (progressArray != null && progressArray.length() > 0) {
                                JSONObject progressObj = progressArray.getJSONObject(0);
                                item.setCurrent_page(progressObj.optInt("current_page", 0));
                            } else {
                                item.setCurrent_page(0);
                            }

                            fullBookList.add(item);
                        }
                        updateUI();
                    } else {
                        System.out.println("Supabase hiba: " + response.code() + " " + response.message());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateUI() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String currentQuery = swSearchHome != null ? swSearchHome.getQuery().toString() : "";
                    filterBooks(currentQuery);
                }
            });
        }
    }

    private void sortBooks(int sortOptionPosition) {
        if (displayBookList == null || displayBookList.isEmpty()) {
            return;
        }

        switch (sortOptionPosition) {
            case 0: // Cím ↑
                Collections.sort(displayBookList, new Comparator<BookItemHome>() {
                    @Override
                    public int compare(BookItemHome b1, BookItemHome b2) {
                        return b1.getBook_name().compareToIgnoreCase(b2.getBook_name());
                    }
                });
                break;
            case 1: // Cím ↓
                Collections.sort(displayBookList, new Comparator<BookItemHome>() {
                    @Override
                    public int compare(BookItemHome b1, BookItemHome b2) {
                        return b2.getBook_name().compareToIgnoreCase(b1.getBook_name());
                    }
                });
                break;
            case 2: // Szerző ↑
                Collections.sort(displayBookList, new Comparator<BookItemHome>() {
                    @Override
                    public int compare(BookItemHome b1, BookItemHome b2) {
                        return b1.getBook_author().compareToIgnoreCase(b2.getBook_author());
                    }
                });
                break;
            case 3: // Szerző ↓
                Collections.sort(displayBookList, new Comparator<BookItemHome>() {
                    @Override
                    public int compare(BookItemHome b1, BookItemHome b2) {
                        return b2.getBook_author().compareToIgnoreCase(b1.getBook_author());
                    }
                });
                break;
            case 4: // Haladás ↑
                Collections.sort(displayBookList, new Comparator<BookItemHome>() {
                    @Override
                    public int compare(BookItemHome b1, BookItemHome b2) {
                        float prog1 = b1.getMax_pages() > 0 ? (float) b1.getCurrent_page() / b1.getMax_pages() : 0f;
                        float prog2 = b2.getMax_pages() > 0 ? (float) b2.getCurrent_page() / b2.getMax_pages() : 0f;
                        return Float.compare(prog1, prog2);
                    }
                });
                break;
            case 5: // Haladás ↓
                Collections.sort(displayBookList, new Comparator<BookItemHome>() {
                    @Override
                    public int compare(BookItemHome b1, BookItemHome b2) {
                        float prog1 = b1.getMax_pages() > 0 ? (float) b1.getCurrent_page() / b1.getMax_pages() : 0f;
                        float prog2 = b2.getMax_pages() > 0 ? (float) b2.getCurrent_page() / b2.getMax_pages() : 0f;
                        return Float.compare(prog2, prog1);
                    }
                });
                break;
        }

        if (bookAdapterHome != null) {
            bookAdapterHome.notifyDataSetChanged();
        }
    }

    private void filterBooks (String query) {
        displayBookList.clear();

        if (query == null || query.trim().isEmpty()) {
            displayBookList.addAll(fullBookList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();

            for (BookItemHome book : fullBookList) {
                if (book.getBook_name() != null && book.getBook_name().toLowerCase().contains(lowerCaseQuery)) {
                    displayBookList.add(book);
                }
            }
        }

        if (spSortHome != null) {
            sortBooks(spSortHome.getSelectedItemPosition());
        } else if (bookAdapterHome != null) {
            bookAdapterHome.notifyDataSetChanged();
        }

        if (displayBookList.isEmpty()) {
            tvEmptySearchHome.setVisibility(View.VISIBLE);
            rvMyBooksHome.setVisibility(View.GONE);
        } else {
            tvEmptySearchHome.setVisibility(View.GONE);
            rvMyBooksHome.setVisibility(View.VISIBLE);
        }
    }
}