package com.heroku.birthdayreminder.container;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.UiThread;

import com.heroku.birthdayreminder.models.RegisterErrorsMessage;
import com.heroku.birthdayreminder.services.BirthdatesHttpService;
import com.heroku.birthdayreminder.strategy.registerError.EmailAlreadyUsed;
import com.heroku.birthdayreminder.strategy.registerError.RegisterErrorStrategy;
import com.heroku.birthdayreminder.strategy.registerError.UsernameAlreadyUsed;
import com.heroku.birthdayreminder.utils.Constants.ConstantsUrl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

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

    @Override
    public void onCreate() {
        super.onCreate();
        this.sharedPreferencesApp = getApplicationContext().getSharedPreferences("com.heroku.birthdayReminder.sharedPerferences", 0);
        this.setRegisterStrategies();
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
                    if (originalRequest.url().encodedPath().equalsIgnoreCase("/api/auth/signin")
                            || (originalRequest.url().encodedPath().equalsIgnoreCase("/api/auth/signup")
                            || (originalRequest.url().encodedPath().equalsIgnoreCase("/api/auth/refreshtoken")
                            && originalRequest.method().equalsIgnoreCase("POST")))) {
                        return  chain.proceed(originalRequest);
                    }

                    Request.Builder newRequestBuilder = originalRequest.newBuilder().header("Authorization",getSharedPreferencesApp().getString("access_token",null));
                    Request newRequest = newRequestBuilder.build();

                    Response response = chain.proceed(newRequest);

                    return response;
                }
            });
            retrofit = new Retrofit.Builder()
                    .baseUrl(ConstantsUrl.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
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
}
