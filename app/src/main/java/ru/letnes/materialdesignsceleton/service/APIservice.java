package ru.letnes.materialdesignsceleton.service;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.letnes.materialdesignsceleton.BuildConfig;
import ru.letnes.materialdesignsceleton.model.WeatherData;

public interface APIservice {

    @GET("weather")
    Call<WeatherData> weatherQuery(
            @Query("q") String city,
            @Query("units") String units,
            @Query("lang") String lang,
            @Query("APPID") String APPID);

    @GET("find")
    Call<ResponseBody> findCity(
            @Query("q") String city,
            @Query("type") String type,
            @Query("lang") String lang,
            @Query("sort") String sort,
            @Query("cnt")   String cnt,
            @Query("mode") String mode,
            @Query("APPID") String APPID);


    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request request = chain.request();
                    if (BuildConfig.DEBUG) {
                        Log.e(getClass().getName(), request.method() + " " + request.url());
                        Log.e(getClass().getName(), "Cookie: " + request.header("Cookie"));
                        RequestBody rb = request.body();
                        Buffer buffer = new Buffer();
                        if (rb != null)
                            rb.writeTo(buffer);
                    }

                    return chain.proceed(request);
                }
            })
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://api.openweathermap.org/data/2.5/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}



