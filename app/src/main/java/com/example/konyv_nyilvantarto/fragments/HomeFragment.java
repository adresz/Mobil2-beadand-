package com.example.konyv_nyilvantarto.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.konyv_nyilvantarto.BookAdapterHome;
import com.example.konyv_nyilvantarto.R;
import com.example.konyv_nyilvantarto.model.BookItemHome;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private List<BookItemHome> bookList;

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

        bookList = new ArrayList<>();
        bookAdapterHome = new BookAdapterHome(bookList);
        rvMyBooksHome.setAdapter(bookAdapterHome);

        loadBooksFromSupabase();
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

                        bookList.clear();

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

                            bookList.add(item);
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
                    bookAdapterHome.notifyDataSetChanged();
                }
            });
        }
    }
}