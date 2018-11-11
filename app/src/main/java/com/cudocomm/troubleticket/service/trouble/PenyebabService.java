package com.cudocomm.troubleticket.service.trouble;

import com.cudocomm.troubleticket.model.penyebab.PenyebabNew;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;

public interface PenyebabService {
    int TYPE_GSON = 1;
    int TYPE_STRING = 2;

    /*-------GET METHOD------*/
    String BASE_URL = "http://tt.scm.co.id/";

    @GET("api/getpenyebab")
    Observable<PenyebabNew> getPenyebab();

    class Factory {
        static PenyebabService penyebabService;

        static Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        public static PenyebabService createPenyebabService(int type) {
            if (penyebabService == null) {
                Retrofit retrofit = null;
                if (type == TYPE_GSON) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();
                } else if (type == TYPE_STRING) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();

                }
                assert retrofit != null;
                penyebabService = retrofit.create(PenyebabService.class);
            }


            return penyebabService;
        }
    }
}
