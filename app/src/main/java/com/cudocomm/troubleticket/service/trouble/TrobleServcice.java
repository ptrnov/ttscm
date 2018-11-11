package com.cudocomm.troubleticket.service.trouble;

import com.cudocomm.troubleticket.model.penyebab.PenyebabNew;
import com.cudocomm.troubleticket.model.program.ProgramNew;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;

public interface TrobleServcice {
    int TYPE_GSON = 1;
    int TYPE_STRING = 2;

    /*-------GET METHOD------*/
    String BASE_URL = "http://tt.scm.co.id/";

    /*-----------------GET PROGRAM REQUEST------------------*/
    @GET("api/getprogramacara")
    Observable<ProgramNew> getProgram();



    class Factory {
        static TrobleServcice programService;


        static Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        public static TrobleServcice createTroubleService(int type) {
            if (programService == null) {
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
                programService = retrofit.create(TrobleServcice.class);
            }


            return programService;
        }
    }
}
