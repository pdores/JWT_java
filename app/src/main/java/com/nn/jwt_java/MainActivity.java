package com.nn.jwt_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nn.jwt_java.model.AuthRequest;
import com.nn.jwt_java.model.AuthResponse;
import com.nn.jwt_java.model.ConfigFileUpdates;
import com.nn.jwt_java.model.ConfigFiles;
import com.nn.jwt_java.model.DownloadRequest;
import com.nn.jwt_java.model.Event;
import com.nn.jwt_java.model.EventRequest;
import com.nn.jwt_java.model.EventShiftStart;
import com.nn.jwt_java.model.UpdateRequest;
import com.nn.jwt_java.model.UpdateResponse;
import com.nn.jwt_java.model.UploadRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
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

        //create okhttp
        OkHttpClient.Builder okBuilder= new OkHttpClient.Builder();
        HttpLoggingInterceptor logging= new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okBuilder.addInterceptor(logging);

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("https://frontend.endpoints.hf-gcp-dev.cloud.goog/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okBuilder.build())
                .build();

        linkAPI=retrofit.create(LinkAPI.class);
        //request("update");
        //request("upload");
        request("event");

        textViewResult.append("Start: request \n");
    }

    private void request(String ctx){
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
                    textViewResult.append("Login failed: "+response.code());
                    return;
                }

                AuthResponse authResponse=response.body();
                String content="";
//                content+="Token: "+authResponse.getAccess_token()+"\n";
//                content+="Expires in: "+authResponse.getExpires_in()+"\n";
//                content+="Token_type: "+authResponse.getToken_type()+"\n";
                Calendar calendar = Calendar.getInstance();
                Date currentTime = calendar.getTime();
                calendar.add(Calendar.SECOND, authResponse.getExpires_in());
                SimpleDateFormat simpleDateFormat =new  SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                content= simpleDateFormat.format(calendar.getTime());

                textViewResult.append("token expires on: "+content+"\n");

                switch (ctx){
                    case "update":
                        update(authResponse);
                        break;
                    case "upload":
                        uploadFile(authResponse);
                        break;
                    case "event":
                        sendEvent(authResponse,EventType.ShiftStart);
                        break;
                    default:
                        break;
                }



            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                textViewResult.append(t.getMessage());
            }
        });
    }

    private void sendEvent(AuthResponse authResponse,Integer eventType) {
        //add autentication
        Map<String,String> header=new HashMap<>();
        header.put("Authorization","Bearer "+authResponse.getAccess_token());

        //date format
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");


        switch(eventType)
        {
            case EventType.ShiftStart:

                EventShiftStart eventShiftStart=new EventShiftStart(
                        EventType.ShiftStart,
                        format.format(currentTime),
                        "123456_12345_202307041600_1",
                        "id-operator-hf",
                        "12345"
                );

                String data=new Gson().toJson(eventShiftStart);

                Log.d("NN",data);

                ArrayList<Event> eventArrayList= new ArrayList<>();



                Event event=new Event(
                        EventType.ShiftStart,
                        format.format(currentTime),
                        "id-operator-hf",
                        data
                );
                eventArrayList.add(event);



                EventRequest eventRequest= new EventRequest(
                        "123456",
                        "Wayfarer6",
                        eventArrayList
                );

                Call<ResponseBody> call= linkAPI.sendEvent(header,eventRequest);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            textViewResult.append("evento registrado: "+response.code()+"\n");


                        }
                        else {
                            textViewResult.append("resp error: "+response.code()+"\n");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        textViewResult.append("resp error: "+t.getMessage()+"\n");
                    }
                });


            break;
            default:
            break;

        }





    }

    private void uploadFile(AuthResponse authResponse) {
        //add autentication
        Map<String,String> header=new HashMap<>();
        header.put("Authorization","Bearer "+authResponse.getAccess_token());

        //date format
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");


        String filename="";
        String fileDate="";
        byte[] file=null;

        //get files to upload
        String path=context.getFilesDir() + File.separator +"Upload"+ File.separator;
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files.length>0) {
            Log.d("NN", "" + files.length);
            Log.d("NN", files[0].getName());

            filename = files[0].getName();
            fileDate=format.format(currentTime);

           // textViewResult.append("Date:"+fileDate);
        }
        else{
            textViewResult.append("No files to upload:");
            return;
        }



        File file1= new File(path+filename);

        Uri fileUri= FileUtils.getUri(file1);



        UploadRequest uploadRequest= new UploadRequest(
                "123456",
                "Wayfarer6",
                FileType.LOG,
                filename,
                fileDate,
                fileUri
        );




        Call<ResponseBody> call= linkAPI.uploadFile(header,
                createPartFromString(uploadRequest.getDeviceSn()),
                createPartFromString(uploadRequest.getModelId()),
                uploadRequest.getFileType(),
                createPartFromString(uploadRequest.getFileName()),
                createPartFromString(uploadRequest.getFileDate()),
                prepareFilePart("file",uploadRequest.getFile()));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    textViewResult.append("upload done code:"+response.code()+"\n");
                    Boolean deleted= file1.delete();
                    textViewResult.append("file deleted: "+deleted+"\n");
                }
                else {
                    textViewResult.append("resp error: "+response.code()+"\n");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                textViewResult.append("resp error: "+t.getMessage()+"\n");
            }
        });



    }

    private RequestBody createPartFromString(String descriptionString){
        return RequestBody.create(MultipartBody.FORM,descriptionString);
    }

    private MultipartBody.Part prepareFilePart(String partName,Uri fileUri){
        File file=FileUtils.getFile(this,fileUri);

        RequestBody requestFile=RequestBody.create(MediaType.parse("plain/text"),file);

        return MultipartBody.Part.createFormData(partName,file.getName(),requestFile);
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
                    textViewResult.append("Code: "+response.code()+"\n");
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