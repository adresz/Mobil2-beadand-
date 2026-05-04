package com.example.konyv_nyilvantarto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.konyv_nyilvantarto.model.BookItemHome;

import java.util.List;

public class BookAdapterHome extends RecyclerView.Adapter<BookAdapterHome.BookViewHolder>{
    private List<BookItemHome> bookList;
    private OnBookClickListener listener;

    public interface OnBookClickListener{
        void onBookClick(BookItemHome book);
    }

    public BookAdapterHome(List<BookItemHome> bookList, OnBookClickListener listener) {
        this.bookList = bookList;
        this.listener = listener;
    }

    public BookAdapterHome(List<BookItemHome> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmain, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookItemHome book = bookList.get(position);

        holder.tvTitle.setText(book.getBook_name());
        holder.tvAuthor.setText("Szerző: " + book.getBook_author());
        if (book.getRelease_year() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy", java.util.Locale.getDefault());
            String yearOnly = sdf.format(book.getRelease_year());
            holder.tvYear.setText("Kiadási év: " + yearOnly);
        } else {
            holder.tvYear.setText("Kiadási év: Ismeretlen");
        }

        // ProgressBar beállítása
        holder.pbProgress.setMax(book.getMax_pages());
        holder.pbProgress.setProgress(book.getCurrent_page());

        Glide.with(holder.itemView.getContext())
                .load(book.getCover())
                .into(holder.ivBookCover);

        holder.itemView.setOnClickListener(v -> {
            listener.onBookClick(book);
        });
    }

    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvYear;
        ProgressBar pbProgress;
        ImageView ivBookCover;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvYear = itemView.findViewById(R.id.tvYear);
            pbProgress = itemView.findViewById(R.id.pbProgress);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
        }
    }
}
