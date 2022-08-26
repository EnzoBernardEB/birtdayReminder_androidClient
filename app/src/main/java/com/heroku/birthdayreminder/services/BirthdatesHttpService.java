package com.heroku.birthdayreminder.services;

import com.heroku.birthdayreminder.DTO.Authentication.Request.SignInRequestDTO;
import com.heroku.birthdayreminder.DTO.Authentication.Request.SignUpRequestDTO;
import com.heroku.birthdayreminder.DTO.Authentication.Request.TokenRefreshRequestDTO;
import com.heroku.birthdayreminder.DTO.Authentication.Response.SignInResponseDTO;
import com.heroku.birthdayreminder.DTO.Authentication.Response.SignUpResponseDTO;
import com.heroku.birthdayreminder.DTO.Authentication.Response.TokenRefreshResponseDTO;
import com.heroku.birthdayreminder.DTO.Birthdates.BirthdateDTO;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BirthdatesHttpService {

    @POST("auth/signin")
    Call<SignInResponseDTO>authentication(@Body SignInRequestDTO signInRequestDTO);

    @POST("auth/signup")
    Call<SignUpResponseDTO>register(@Body SignUpRequestDTO signUpRequestDTO);

    @POST("auth/refreshtoken")
    Call<TokenRefreshResponseDTO>refreshToken(@Body TokenRefreshRequestDTO tokenRefreshRequestDTO);

    @GET("user/{userId}/birthdates")
    Call<List<BirthdateDTO>> getUserBirthdates(@Path("userId")UUID userId);

}
