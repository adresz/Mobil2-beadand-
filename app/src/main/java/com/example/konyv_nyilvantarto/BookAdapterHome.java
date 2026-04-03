package com.example.konyv_nyilvantarto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookAdapterHome extends RecyclerView.Adapter<BookAdapterHome.BookViewHolder>{
    private List<BookHome> bookList;

    public BookAdapterHome(List<BookHome> bookList) {
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
        BookHome currentBook = bookList.get(position);
        holder.tvTitle.setText(currentBook.getTitle());
        holder.tvAuthor.setText("Szerző: " + currentBook.getAuthor());
        holder.tvYear.setText("Kiadási év: " + currentBook.getYear());
        holder.pbProgress.setProgress(currentBook.getProgress());
        holder.ivBookCover.setImageResource(currentBook.getImageResId());
    }

    @Override
    public int getItemCount() {
        return bookList.size();
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
