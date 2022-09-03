package com.heroku.birthdayreminder.container;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.UiThread;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.heroku.birthdayreminder.models.RegisterErrorsMessage;
import com.heroku.birthdayreminder.services.BirthdatesHttpService;
import com.heroku.birthdayreminder.strategy.registerError.EmailAlreadyUsed;
import com.heroku.birthdayreminder.strategy.registerError.RegisterErrorStrategy;
import com.heroku.birthdayreminder.strategy.registerError.UsernameAlreadyUsed;
import com.heroku.birthdayreminder.utils.Constants.ConstantsUrl;
import com.heroku.birthdayreminder.utils.Serializer.LocalDateDeserializer;
import com.heroku.birthdayreminder.utils.Serializer.LocalDateSerializer;
import com.heroku.birthdayreminder.utils.Util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BirthdayReminderApplication extends Application {

    private Retrofit retrofit;
    private BirthdatesHttpService birthdatesHttpService;
    private SharedPreferences sharedPreferencesApp;
    private Collection<RegisterErrorStrategy> collectionRegisterErrorStrategies = new ArrayList<>();
    private Gson gson;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        this.sharedPreferencesApp = getApplicationContext().getSharedPreferences("com.heroku.birthdayReminder.sharedPerferences", 0);
        this.setRegisterStrategies();
        this.context = this;
        this.gson = getGsonWithLocalDateSerializer();
    }

    @UiThread
    public BirthdatesHttpService getBirthdatesHttpService() {

        this.initializeBirthdateRetrofitClient();

        return birthdatesHttpService;
    }

    @UiThread
    public Retrofit getRetrofit() {

        this.initializeBirthdateRetrofitClient();

        return retrofit;
    }

    private void initializeBirthdateRetrofitClient() {
        if(retrofit == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    if (originalRequest.url().encodedPath().startsWith("/api/auth/") && originalRequest.method().equalsIgnoreCase("POST")) {
                        Log.d("TAG", "Pas besoin authorization: "+originalRequest.url().encodedPath());
                        return  chain.proceed(originalRequest);
                    }

                    Request.Builder newRequestBuilder = originalRequest.newBuilder().header("Authorization", "Bearer "+Util.getAccessToken(getSharedPreferencesApp()));
                    Request newRequest = newRequestBuilder.build();
                    Log.d("TAG", "Ajout authorization:  "+Util.getAccessToken(getSharedPreferencesApp()));
                    Log.d("TAG", "Ajout authorization:  "+Util.getRefreshToken(getSharedPreferencesApp()));
                    Response response = chain.proceed(newRequest);

                    int tryCount = 0;
                    while (!response.isSuccessful() && tryCount<3){
                        if(response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            response.close();

                            Util.silentAuthentication(getBirthdatesHttpService(),getSharedPreferencesApp(),context);

                            newRequestBuilder = originalRequest.newBuilder().header("Authorization","Bearer "+Util.getAccessToken(getSharedPreferencesApp()));
                            newRequest = newRequestBuilder.build();

                            response = chain.proceed(newRequest);
                            tryCount++;
                        } else  {
                            break;
                        }

                    }

                    return response;
                }
            });
            retrofit = new Retrofit.Builder()
                    .baseUrl(ConstantsUrl.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(getGsonWithLocalDateSerializer()))
                    .client(clientBuilder.build())
                    .build();
            birthdatesHttpService = retrofit.create(BirthdatesHttpService.class);
        }
    }

    public SharedPreferences getSharedPreferencesApp() {
        return sharedPreferencesApp;
    }
    private void setRegisterStrategies() {
        UsernameAlreadyUsed usernameAlreadyUsed = new UsernameAlreadyUsed();
        EmailAlreadyUsed emailAlreadyUsed = new EmailAlreadyUsed();

        collectionRegisterErrorStrategies.add(usernameAlreadyUsed);
        collectionRegisterErrorStrategies.add(emailAlreadyUsed);
    }

    public Collection<RegisterErrorStrategy> getCollectionRegisterErrorStrategies() {
        return collectionRegisterErrorStrategies;
    }

    public Gson getGsonWithLocalDateSerializer() {
        if(this.gson != null)
            return this.gson;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer());

        this.gson = gsonBuilder.setPrettyPrinting().create();

        return this.gson;
    }
}
