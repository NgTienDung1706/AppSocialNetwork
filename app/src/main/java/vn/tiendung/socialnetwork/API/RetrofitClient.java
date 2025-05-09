package vn.tiendung.socialnetwork.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    public static Retrofit getRetrofit(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    // đường dẫn API
                    .baseUrl("http://10.0.2.2:3001/")
                    //.baseUrl("https://socialnetwork-api-zbeb.onrender.com/")
                    .addConverterFactory (GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
