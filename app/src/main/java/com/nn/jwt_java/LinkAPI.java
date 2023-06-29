package com.nn.jwt_java;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

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


}
