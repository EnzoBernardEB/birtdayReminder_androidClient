package com.heroku.birthdayreminder.services;

import com.heroku.birthdayreminder.DTO.Authentication.Request.SignInRequestDTO;
import com.heroku.birthdayreminder.DTO.Authentication.Request.SignUpRequestDTO;
import com.heroku.birthdayreminder.DTO.Authentication.Response.SignInResponseDTO;
import com.heroku.birthdayreminder.DTO.Authentication.Response.SignUpResponseDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BirthdatesHttpService {

    @POST("auth/signin")
    Call<SignInResponseDTO>authentication(@Body SignInRequestDTO signInRequestDTO);

    @POST("auth/signup")
    Call<SignUpResponseDTO>register(@Body SignUpRequestDTO signUpRequestDTO);
}
