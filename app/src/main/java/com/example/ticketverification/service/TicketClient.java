package com.example.ticketverification.service;

import com.example.ticketverification.model.AccessToken;
import com.example.ticketverification.model.Ticket;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TicketClient {

    @Headers("Accept: application/json")
    @POST("events/oauth/token")
    @FormUrlEncoded
    Call<AccessToken> getAcccesstoken(

            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("grant_type") String grantType,
            @Field("username") String username,
            @Field("password") String password
//        @Field("scope")String  scope

//        @Field("code")String  code
    );

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("events/oauth/token")
    Call<AccessToken> login(@FieldMap Map<String, String> options);
    @GET
    Call<ResponseBody> getInfo(@Path("user") String client);

    @Headers("Content-Type: application/json")
    @POST("events/api/mark_ticket")
    @FormUrlEncoded
    Call<ResponseBody> markTicket(@Field("ticket_no") String ticket_no);


    @Headers("Accept: application/json")
    @POST(" events/api/check")
    @FormUrlEncoded
    Call<Ticket> checkTicket(@Field("ticket_no") String ticket_no);

}
