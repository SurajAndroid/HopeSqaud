package com.hopesquad.API;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 *  Created by Suraj Shakya on 01/06/2018.
 */

public interface APIInterface {

    @Multipart
    @POST("uploadProfilePhoto")
    Call<ResponseBody> AddProfileImage(@Part("GymId") RequestBody GymId
            , @Part("UserId") RequestBody UserId
            , @Part("UserType") RequestBody UserType
            , @Part MultipartBody.Part file);

    @Multipart
    @POST("UploadUserGalleryImage")
    Call<ResponseBody> UploadUserGalleryImage(@Part("GymId") RequestBody GymId
            , @Part("UserID") RequestBody UserId
            , @Part MultipartBody.Part file);


    @Multipart
    @POST("UploadImagesVideosStatus")
    Call<ResponseBody> UpLoadStatusImage(@Part("GymId") RequestBody GymId
            , @Part("UserID") RequestBody UserId
            , @Part("FileType") RequestBody fileType
            , @Part("Status") RequestBody status
            , @Part MultipartBody.Part file);


    @Multipart
    @POST("InsertImageMessageInfo")
    Call<ResponseBody> InsertImageMessageInfo(@Part("GymId") RequestBody GymId
            , @Part("SenderID") RequestBody senderId
            , @Part("RecevierID") RequestBody recevierId
            , @Part("UserType") RequestBody userType
            , @Part MultipartBody.Part file);



}
