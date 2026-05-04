package com.example.konyv_nyilvantarto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OpenLibraryResponse {
    @SerializedName("docs")
    private List<BookAll> docs;

    public List<BookAll> getDocs() {
        return docs;
    }
}