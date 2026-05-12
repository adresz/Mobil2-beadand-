package com.example.konyv_nyilvantarto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
        void onDeleteClick(BookItemHome book, int position);
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
        int maxpages = book.getMax_pages();
        int currentPage = book.getCurrent_page();

        holder.pbProgress.setMax(maxpages);
        holder.pbProgress.setProgress(currentPage);

        if(maxpages > 0) {
            int percentage = (int) ((currentPage * 100.0f) / maxpages);
            holder.tvPercentage.setText(percentage + "%");
        } else {
            holder.tvPercentage.setText("0%");
        }

        Glide.with(holder.itemView.getContext())
                .load(book.getCover())
                .into(holder.ivBookCover);

        holder.itemView.setOnClickListener(v -> {
            listener.onBookClick(book);
        });

        holder.btnDelete.setOnClickListener(v -> {
            listener.onDeleteClick(book, position);
        });

    }

    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvYear, tvPercentage;
        ProgressBar pbProgress;
        ImageView ivBookCover;
        ImageButton btnDelete;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvYear = itemView.findViewById(R.id.tvYear);
            pbProgress = itemView.findViewById(R.id.pbProgress);
            ivBookCover = itemView.findViewById(R.id.ivBookCover);
            tvPercentage = itemView.findViewById(R.id.tv_Percentage);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
