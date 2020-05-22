package de.zitruism.thl_todo_liste.injection;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.zitruism.thl_todo_liste.network.Webservice;
import de.zitruism.thl_todo_liste.network.interfaces.IWebService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class RequestModule {

    public RequestModule() {}

    @Provides
    @Singleton
    Retrofit provideRetrofit(){
        return new Retrofit.Builder()
                .baseUrl(Webservice.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    IWebService provideWebService(Retrofit retrofit){
        return retrofit.create(IWebService.class);
    }
}