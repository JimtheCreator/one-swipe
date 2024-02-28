package api;

import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public interface RetrofitCallback {
        void onSuccess(Retrofit retrofit);
        void onFailure(Throwable throwable);
    }

    private static final String BASE_URL = "https://www.googleapis.com/";
    private static Retrofit retrofit;

    public static void getClient(RetrofitCallback callback) {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // Add logging interceptor for debug purposes
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(loggingInterceptor);

            // Add any other configurations as needed, such as connection pooling, timeouts, etc.
            httpClient.connectionPool(new ConnectionPool());
            httpClient.connectTimeout(30, TimeUnit.SECONDS);
            httpClient.readTimeout(30, TimeUnit.SECONDS);
            httpClient.writeTimeout(30, TimeUnit.SECONDS);

            OkHttpClient client = httpClient.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        // Check if Retrofit initialization was successful
        if (retrofit != null) {
            callback.onSuccess(retrofit);
        } else {
            callback.onFailure(new Exception("Retrofit initialization failed"));
        }
    }
}






