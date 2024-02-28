package interface_pack;

import model.SearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

// Retrofit interface for making HTTP requests
public interface SearchService {
    @GET("customsearch/v1")
    Call<SearchResponse> search(@Query("q") String query, @Query("key") String apiKey, @Query("cx") String cx);
}
