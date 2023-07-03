package com.nn.jwt_java;

import com.nn.jwt_java.model.AuthResponse;
import com.nn.jwt_java.model.UpdateRequest;
import com.nn.jwt_java.model.UpdateResponse;
import com.nn.jwt_java.model.UploadRequest;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface LinkAPI {

    @FormUrlEncoded
    @POST("auth/realms/enterprise/protocol/openid-connect/token")
    Call<AuthResponse> login(@FieldMap Map<String,String> fields);

    @POST("device/v1/api/config/update")
    Call<UpdateResponse> update(@HeaderMap Map<String,String> headers, @Body UpdateRequest updateRequest);

    @GET("device/v1/api/config/file")
    Call<ResponseBody> downloadFiles(@HeaderMap Map<String,String> headers,
                                     @Query("deviceSn") String deviceSn,
                                     @Query("modelId") String modelId,
                                     @Query("compress") Boolean compress,
                                     @Query("configFiles") Integer[] configFiles
                                     );

    @Multipart
    @POST("device/v1/api/device-file")
    Call<ResponseBody> uploadFile(@HeaderMap Map<String,String> headers,
                                  @PartMap Map<String,RequestBody> data,
                                  @Part MultipartBody.Part log
                                  );



}
