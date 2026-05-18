package com.example.konyv_nyilvantarto.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.konyv_nyilvantarto.BookApi;
import com.example.konyv_nyilvantarto.R;
import com.example.konyv_nyilvantarto.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBookFragment extends Fragment {

    private String mParam1;
    private String mParam2;

    private Button btSaveAdd;
    private EditText etTitleAdd;
    private EditText etAuthorAdd;
    private EditText etReleaseAdd;
    private EditText etPagesAdd;
    private EditText etGenreAdd;
    private LinearLayout linlayUploadAdd;
    private TextView tvUploadAdd;

    private Uri imageUri = null;
    private String selectedFile = "";

    private static final String SUPABASE_URL = "https://plohvgiccntmsgzyszrl.supabase.co/";
    private static final String API_KEY = "sb_publishable_cuiKHkTKKdAHSnMkBcwghQ_rKE7skpG";

    private final ActivityResultLauncher<Intent> importImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedFileUri = result.getData().getData();
                    if (selectedFileUri != null) {
                        String fileName = getFileName(selectedFileUri);
                        if (tvUploadAdd != null) {
                            imageUri = selectedFileUri;
                            selectedFile = fileName;
                            tvUploadAdd.setText(fileName);
                        }
                    }
                }
            }
    );

    public AddBookFragment() {
        // Required empty public constructor
    }

    public static AddBookFragment newInstance(String param1, String param2) {
        AddBookFragment fragment = new AddBookFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btSaveAdd = view.findViewById(R.id.btSaveAdd);
        etTitleAdd = view.findViewById(R.id.etTitleAdd);
        etAuthorAdd = view.findViewById(R.id.etAuthorAdd);
        etReleaseAdd = view.findViewById(R.id.etReleaseAdd);
        etPagesAdd = view.findViewById(R.id.etPagesAdd);
        etGenreAdd = view.findViewById(R.id.etGenreAdd);

        linlayUploadAdd = view.findViewById(R.id.linlayUploadAdd);
        tvUploadAdd = view.findViewById(R.id.tvUploadAdd);

        View.OnClickListener linlayListener = v -> {
            int id = v.getId();
            if (id == R.id.linlayUploadAdd) {
                openImagePicker();
            }
        };

        linlayUploadAdd.setOnClickListener(linlayListener);

        btSaveAdd.setOnClickListener(v -> {
            Boolean title = etTitleAdd.getText().toString().trim().isEmpty();
            Boolean author = etAuthorAdd.getText().toString().trim().isEmpty();
            Boolean release = etReleaseAdd.getText().toString().trim().isEmpty();
            Boolean pages = etPagesAdd.getText().toString().trim().isEmpty();
            Boolean genre = etGenreAdd.getText().toString().trim().isEmpty();

            if(title || author || release || pages || genre){
                Toast.makeText(getContext(), "Minden mező kitöltése kötelező", Toast.LENGTH_SHORT).show();
            }
            else{
                if (imageUri != null) {
                    try {
                        java.io.InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                        java.io.ByteArrayOutputStream byteBuffer = new java.io.ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = inputStream.read(buffer)) != -1) {
                            byteBuffer.write(buffer, 0, len);
                        }
                        byte[] bytes = byteBuffer.toByteArray();

                        okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse(getContext().getContentResolver().getType(imageUri)), bytes);
                        okhttp3.MultipartBody.Part body = okhttp3.MultipartBody.Part.createFormData("file", selectedFile, requestFile);

                        String uniqueFileName = System.currentTimeMillis() + "_" + selectedFile;

                        BookApi api = RetrofitClient.getClient(SUPABASE_URL).create(BookApi.class);

                        api.uploadBookCover(API_KEY, "Bearer " + API_KEY, uniqueFileName, body).enqueue(new Callback<okhttp3.ResponseBody>() {
                            @Override
                            public void onResponse(@NonNull Call<okhttp3.ResponseBody> call, @NonNull Response<okhttp3.ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    String generatedImageUrl = SUPABASE_URL + "storage/v1/object/public/book_covers/" + uniqueFileName;

                                    // Mentés az adatbázisba a frissen feltöltött kép URL-jével
                                    FavoriteBook book = new FavoriteBook(
                                            etTitleAdd.getText().toString(),
                                            etAuthorAdd.getText().toString(),
                                            generatedImageUrl,
                                            Integer.parseInt(etPagesAdd.getText().toString()),
                                            etReleaseAdd.getText().toString(),
                                            etGenreAdd.getText().toString()
                                    );

                                    api.saveBookToSupabase(API_KEY, "Bearer " + API_KEY, book).enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> dbResponse) {
                                            if(dbResponse.isSuccessful()) {
                                                etTitleAdd.setText("");
                                                etAuthorAdd.setText("");
                                                etReleaseAdd.setText("");
                                                etPagesAdd.setText("");
                                                etGenreAdd.setText("");
                                                imageUri = null;
                                                selectedFile = "";
                                                if (tvUploadAdd != null) tvUploadAdd.setText("Kép hozzáadása");
                                                Toast.makeText(getContext(), "Mentve!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "Adatbázis hiba: " + dbResponse.code(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        @Override
                                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                            Toast.makeText(getContext(), "Adatbázis hálózati hiba!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(getContext(), "Kép feltöltési hiba: " + response.code(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<okhttp3.ResponseBody> call, @NonNull Throwable t) {
                                Toast.makeText(getContext(), "Kép feltöltési hálózati hiba!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Fájl beolvasási hiba", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Ha nincs kiválasztott kép, azonnal mentünk a nopicture.png-vel
                    FavoriteBook book = new FavoriteBook(
                            etTitleAdd.getText().toString(),
                            etAuthorAdd.getText().toString(),
                            "https://plohvgiccntmsgzyszrl.supabase.co/storage/v1/object/public/book_covers/nopicture.png",
                            Integer.parseInt(etPagesAdd.getText().toString()),
                            etReleaseAdd.getText().toString(),
                            etGenreAdd.getText().toString()
                    );

                    BookApi api = RetrofitClient.getClient(SUPABASE_URL).create(BookApi.class);

                    api.saveBookToSupabase(API_KEY, "Bearer " + API_KEY, book).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if(response.isSuccessful()) {
                                etTitleAdd.setText("");
                                etAuthorAdd.setText("");
                                etReleaseAdd.setText("");
                                etPagesAdd.setText("");
                                etGenreAdd.setText("");
                                if (tvUploadAdd != null) tvUploadAdd.setText("Kép hozzáadása");
                                Toast.makeText(getContext(), "Mentve!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Hiba: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Toast.makeText(getContext(), "Hálózati hiba!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        importImageLauncher.launch(intent);
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (getContext() != null && "content".equals(uri.getScheme())) {
            try (Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result != null ? result.lastIndexOf('/') : -1;
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result != null ? result : "kiválasztott_kep.jpg";
    }
}