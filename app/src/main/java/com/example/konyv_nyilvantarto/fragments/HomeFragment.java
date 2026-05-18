package com.example.konyv_nyilvantarto.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintSet;
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
import android.widget.Toast;

import com.example.konyv_nyilvantarto.BookAdapterHome;
import com.example.konyv_nyilvantarto.fragments.BookDetailsFragment;
import com.example.konyv_nyilvantarto.R;
import com.example.konyv_nyilvantarto.model.BookItemHome;
import com.example.konyv_nyilvantarto.utils.Constants;

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

        bookAdapterHome = new BookAdapterHome(displayBookList, new BookAdapterHome.OnBookClickListener() {
            @Override
            public void onBookClick(BookItemHome book) {
                Bundle bundle = new Bundle();

                bundle.putLong("id", book.getId());
                bundle.putString("title", book.getBook_name());
                bundle.putString("author", book.getBook_author());
                bundle.putString("cover", book.getCover());
                bundle.putString("genre", book.getGenre());
                bundle.putFloat("rating", book.getRating());
                bundle.putString("note",book.getNote());
                bundle.putString("book_cover",book.getCover());

                if(book.getRelease_year() != null){
                    java.text.SimpleDateFormat sdf =
                            new java.text.SimpleDateFormat("yyyy", java.util.Locale.getDefault());

                    bundle.putString("year", sdf.format(book.getRelease_year()));
                }

                bundle.putInt("maxPages", book.getMax_pages());
                bundle.putInt("currentPages", book.getCurrent_page());

                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());

                if (book.getStart_date() != null) {
                    bundle.putString("start_date", sdf.format(book.getStart_date()));
                }
                if (book.getFinish_date() != null) {
                    bundle.putString("finish_date", sdf.format(book.getFinish_date()));
                }


                BookDetailsFragment fragment = new BookDetailsFragment();
                fragment.setArguments(bundle);

                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fcvContent, fragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onDeleteClick(BookItemHome book, int position) {
                android.graphics.drawable.Drawable alertIcon = androidx.core.content.ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_dialog_alert);

                if (alertIcon != null) {
                    alertIcon.mutate().setTint(android.graphics.Color.RED);
                }

                new AlertDialog.Builder(requireContext())
                        .setTitle("Törlés megerősítése")
                        .setIcon(alertIcon)
                        .setMessage("Biztosan törölni szeretnéd a(z) " + book.getBook_name() + " című könyvet? Törlés esetén a hozzá tartozó összes adat elveszik.")
                        .setPositiveButton("Törlés", (dialog, which) -> {
                            deleteBook(book, position);
                        })
                        .setNegativeButton("Mégse", null)
                        .show();
            }
        });

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

                    String supabaseUrl = Constants.SUPABASE_BASE_URL + "Book_Details?select=id,book_name,book_author,genre,note,start_date,finish_date,release_year,max_pages,cover,rating,Book_Progress(current_page)";

                    Request request = new Request.Builder()
                            .url(supabaseUrl)
                            .addHeader("apikey", Constants.SUPABASE_API_KEY)
                            .addHeader("Authorization", "Bearer " + Constants.SUPABASE_API_KEY)
                            .build();

                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful() && response.body() != null) {
                        String responseData = response.body().string();

                        JSONArray jsonArray = new JSONArray(responseData);

                        fullBookList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject bookObj = jsonArray.getJSONObject(i);
                            BookItemHome item = new BookItemHome();
                            item.setId(bookObj.optLong("id"));
                            item.setBook_name(bookObj.optString("book_name", "Ismeretlen cím"));
                            item.setBook_author(bookObj.optString("book_author", "Ismeretlen szerző"));
                            item.setGenre(bookObj.optString("genre","Ismeretlen"));
                            item.setNote(bookObj.optString("note","Nincs"));

                            item.setRating((float) bookObj.optDouble("rating",0.0));
                            String startDate = bookObj.optString("start_date", "");
                            if (!startDate.isEmpty() && !startDate.equals("null")) {
                                try {
                                    java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                                    item.setStart_date(format.parse(startDate));
                                } catch (java.text.ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            String finishDate = bookObj.optString("finish_date", "");
                            if (!finishDate.isEmpty() && !finishDate.equals("null")) {
                                try {
                                    java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                                    item.setFinish_date(format.parse(finishDate));
                                } catch (java.text.ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            String releaseYearStr = bookObj.optString("release_year", "");
                            if (!releaseYearStr.isEmpty() && !releaseYearStr.equals("null")) {
                                try {
                                    java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                                    item.setRelease_year(format.parse(releaseYearStr));
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

    private void deleteBook(BookItemHome book, int position){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();

                    String supabaseUrl = Constants.SUPABASE_BASE_URL + "Book_Details?id=eq." + book.getId();

                    Request request = new Request.Builder()
                            .url(supabaseUrl)
                            .addHeader("apikey", Constants.SUPABASE_API_KEY)
                            .addHeader("Authorization", "Bearer" + Constants.SUPABASE_API_KEY)
                            .delete()
                            .build();

                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    fullBookList.remove(book);
                                    displayBookList.remove(position);
                                    bookAdapterHome.notifyItemRemoved(position);
                                    bookAdapterHome.notifyItemRangeChanged(position, displayBookList.size());

                                    Toast.makeText(requireContext(), "Könyv sikeresen törölve!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(requireContext(), "Sikertelen törlés!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}