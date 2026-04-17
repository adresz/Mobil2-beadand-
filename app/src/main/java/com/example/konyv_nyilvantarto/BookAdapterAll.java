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
import java.util.List;

public class BookAdapterAll extends RecyclerView.Adapter<BookAdapterAll.BookViewHolder> {

    private List<BookAll> bookList;

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

        holder.tvTitle.setText(currentBook.getTitle());
        holder.tvAuthor.setText(currentBook.getAuthor());
        holder.ivCover.setImageResource(currentBook.getImageResource());

        holder.ibAdd.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), currentBook.getTitle() + " hozzáadva a kedvencekhez!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle, tvAuthor;
        ImageButton ibAdd;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.ivRowCover);
            tvTitle = itemView.findViewById(R.id.tvRowTitle);
            tvAuthor = itemView.findViewById(R.id.tvRowAuthor);
            ibAdd = itemView.findViewById(R.id.ibRowAddFavorite);
        }
    }
}