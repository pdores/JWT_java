package com.nn.jwt_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView textViewResult;
    private LinkAPI linkAPI;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context=this;

        textViewResult=findViewById(R.id.text_view_result);

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://frontend.endpoints.hf-gcp-dev.cloud.goog/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        linkAPI=retrofit.create(LinkAPI.class);
        login();


    }

    private void login(){
        AuthRequest authRequest=new AuthRequest("client_credentials","y2wE8CZn8gvnbE77ZxxfwezRRbjllvW5","app-validator");

        Map<String,String> parameters=new HashMap<>();
        parameters.put("grant_type",authRequest.getGrant_type());
        parameters.put("client_secret",authRequest.getClient_secret());
        parameters.put("client_id",authRequest.getClient_id());


        Call<AuthResponse> call=linkAPI.login(parameters);
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if(!response.isSuccessful()){
                    textViewResult.setText("Code: "+response.code());
                    return;
                }

                AuthResponse authResponse=response.body();
                String content="";
                content+="Token: "+authResponse.getAccess_token()+"\n";
                content+="Expires in: "+authResponse.getExpires_in()+"\n";
                content+="Token_type: "+authResponse.getToken_type()+"\n";

                textViewResult.append(content);

                update(authResponse);

            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                textViewResult.append(t.getMessage());
            }
        });
    }

    private void update(AuthResponse authResponse) {

        Map<String,String> header=new HashMap<>();
        header.put("Authorization","Bearer "+authResponse.getAccess_token());

        ArrayList<ConfigFiles> configFiles= new ArrayList<ConfigFiles>();


        UpdateRequest updateRequest= new UpdateRequest(
                "123456",
                "Wayfarer6",
                "0b81724a-ad77-4515-a001-3e994f9cccd5",
                4,
                "id-operator-hf",
                "pt.nn.consolabordo",
                1,
                configFiles
        );


        Call<UpdateResponse> call= linkAPI.update(header,updateRequest);
        call.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                if(!response.isSuccessful()){
                    textViewResult.setText("Code: "+response.code());
                    return;
                }

                UpdateResponse updateResponse=response.body();

                String content="\n\n";
                content+="forceCleanConfig: "+updateResponse.isForceCleanConfig()+"\n";

                for(ConfigFileUpdates configFileUpdates: updateResponse.getConfigFileUpdates()){
                    content+= "id: "+configFileUpdates.getId()+"\n";
                    content+= "type: "+configFileUpdates.getType()+"\n";
                    content+= "name: "+configFileUpdates.getName()+"\n";
                    content+= "version: "+configFileUpdates.getVersion()+"\n";
                    content+= "size: "+configFileUpdates.getSize()+"\n";
                    content+= "hash: "+configFileUpdates.getHash()+"\n\n";
                }


                textViewResult.append(content);

                downloadFiles(authResponse,updateResponse);


            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                textViewResult.append(t.getMessage());
            }
        });


    }

    private void downloadFiles(AuthResponse authResponse, UpdateResponse updateResponse) {

        Map<String,String> header=new HashMap<>();
        header.put("Authorization","Bearer "+authResponse.getAccess_token());


        ArrayList<Integer> fileArray= new ArrayList<>();

        for(ConfigFileUpdates configFileUpdates: updateResponse.getConfigFileUpdates()){
            fileArray.add(configFileUpdates.getId());
        }


        DownloadRequest downloadRequest=new DownloadRequest(
                "123456",
                "Wayfarer6",
                true,
                fileArray
        );

//
        Call<ResponseBody> call=linkAPI.downloadFiles(header,
                downloadRequest.getDeviceSn(),
                downloadRequest.getModelId(),
                downloadRequest.isCompress(),
                downloadRequest.getConfigFiles().toArray(new Integer[0]));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful()){
                    Log.d("NN", "server contacted and has file");
                    boolean writtenToDisk = writeResponseBodyToDisk(response.body(),context.getFilesDir() + File.separator +"Download"+ File.separator + "package.zip");

                    if(writtenToDisk) {
                        textViewResult.append("download successful\n");

                        Zip zip=new Zip();

                        if(zip.unzipFile(context.getFilesDir() + File.separator+"Download"+ File.separator + "package.zip",context.getFilesDir() + File.separator+"cfg"))
                            textViewResult.append("unzip successful\n");
                        else
                            textViewResult.append("unzip error\n");

                        //zip dir test
                        File dir = new File(context.getFilesDir() + File.separator+"cfg");
                        String zipDirName = context.getFilesDir() + File.separator+"Upload"+File.separator+"dir.zip";

                        if(zip.zipAllFiles(dir,zipDirName))
                            textViewResult.append("zip dir successful\n");
                        else
                            textViewResult.append("zip error\n");
                    }
                    else
                        textViewResult.append("download error\n");

                }



            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                textViewResult.append(t.getMessage());
            }
        });


    }

    private boolean writeResponseBodyToDisk(ResponseBody body,String path) {
        try {
            // todo change the file location/name according to your needs
            //File zipFile = new File(getExternalFilesDir(null) + File.separator + "package.zip");
            File zipFile = new File(path);

            textViewResult.append(zipFile.getPath()+"\n\n");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(zipFile,false);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("NN", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }




}