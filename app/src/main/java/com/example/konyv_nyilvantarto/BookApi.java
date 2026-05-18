package com.example.konyv_nyilvantarto;
import com.example.konyv_nyilvantarto.fragments.FavoriteBook;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookApi {
    @GET("search.json")
    Call<OpenLibraryResponse> searchBooks(@Query("q") String query);

    @POST("rest/v1/Book_Details")
    Call<Void> saveBookToSupabase(
            @Header("apikey") String apiKey,         // MUST be all lowercase
            @Header("Authorization") String auth,    // Standard capitalization
            @Body FavoriteBook book
    );

    @Multipart
    @POST("storage/v1/object/book_covers/{profile_path}")
    Call<ResponseBody> uploadBookCover(
            @Header("apikey") String apiKey,
            @Header("Authorization") String auth,
            @Path("profile_path") String fileName,
            @Part MultipartBody.Part file
    );
}