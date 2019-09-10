package spaak.supertec.vedioupload.api;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import spaak.supertec.vedioupload.model.ResponseModel;

public interface ApiInterface {

    @Multipart
    @POST("/upload/")
    Call<ResponseModel> uploadVideoToServer(@Part("user_id") RequestBody user_id, @Part MultipartBody.Part imageFile);


    @Headers("Content-Type: application/json")
    @GET("/api/countries")
    Call<ResponseModel> getAllVideos();
}
