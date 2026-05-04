package com.example.konyv_nyilvantarto;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.konyv_nyilvantarto.fragments.FavoriteBook;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookAdapterAll extends RecyclerView.Adapter<BookAdapterAll.BookViewHolder> {

    private final List<BookAll> bookList;
    private static final String SUPABASE_URL = "https://plohvgiccntmsgzyszrl.supabase.co/";
    private static final String API_KEY = "sb_publishable_cuiKHkTKKdAHSnMkBcwghQ_rKE7skpG";

    public BookAdapterAll(List<BookAll> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_book_row, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookAll currentBook = bookList.get(position);

        holder.tvRowTitle.setText(currentBook.getTitle());
        holder.tvRowAuthor.setText(currentBook.getFirstAuthor());

        Glide.with(holder.ivRowCover.getContext())
                .load(currentBook.getCoverUrl())
                .placeholder(R.drawable.nopicture)
                .error(R.drawable.nopicture)
                .into(holder.ivRowCover);

        holder.ibRowAddFavorite.setOnClickListener(v -> {
            holder.ibRowAddFavorite.setEnabled(false);
            FavoriteBook fav = new FavoriteBook(
                    currentBook.getTitle(),
                    currentBook.getFirstAuthor(),
                    currentBook.getCoverUrl(),
                    currentBook.getPageCount(),
                    currentBook.getReleaseYear(),
                    currentBook.getGenre()
            );

            BookApi api = RetrofitClient.getClient(SUPABASE_URL).create(BookApi.class);
            api.saveBookToSupabase(API_KEY, "Bearer " + API_KEY, fav)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            holder.ibRowAddFavorite.setEnabled(true);
                            if (response.isSuccessful()) {
                                Toast.makeText(v.getContext(), "Sikeresen mentve a könyveidhez!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(v.getContext(), "Hiba: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            holder.ibRowAddFavorite.setEnabled(true);
                            Toast.makeText(v.getContext(), "Hálózati hiba!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return (bookList != null) ? bookList.size() : 0;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        final TextView tvRowTitle, tvRowAuthor;
        final ImageView ivRowCover;
        final ImageButton ibRowAddFavorite;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRowTitle = itemView.findViewById(R.id.tvRowTitle);
            tvRowAuthor = itemView.findViewById(R.id.tvRowAuthor);
            ivRowCover = itemView.findViewById(R.id.ivRowCover);
            ibRowAddFavorite = itemView.findViewById(R.id.ibRowAddFavorite);
        }
    }
}