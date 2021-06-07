package com.example.ticketverification;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import static android.Manifest.permission_group.CAMERA;

import com.example.ticketverification.model.AccessToken;
import com.example.ticketverification.model.Ticket;
import com.example.ticketverification.service.TicketClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.Result;

import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler  {

    // fields for getting the access token
//    private String clientId = "2";
//    private String clientSecret = "nGUu9OSbwU8FhEJtWmT2CNMyZVeaKdD6jUHasKDO";
//    private String redirectUri = " http://178.79.155.54/events/api/check";

    SharedPreferences prefs;
    public static final String MyPREFERENCES = "com.example.ticketverification.PREFERENCE_FILE_KEY" ;


    // Barcode
    private static final int REQUEST_CAMERA =1;
    private ZXingScannerView scannerView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_main);

            scannerView = new ZXingScannerView(this);
            setContentView(scannerView);

            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED){
                requestPermissions( new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
            }
        }


    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CAMERA:
                if(grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted){
                        Toast.makeText(this,"Permission Granted", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(this,"Permission Denied ",Toast.LENGTH_LONG).show();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if(shouldShowRequestPermissionRationale(CAMERA)){
                                displayAlertMessage("You need to allow both permissions", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                                    }
                                });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkPermission()){
                if(scannerView==null){
                    scannerView =new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }else
            {
                requestPermission();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener)
    {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK",listener)
                .setNegativeButton("CANCEL",null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        final String scanResult = result.getText();
        getUrlAccessToken(scanResult);
//        AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        builder.setTitle("Scan Result");
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                scannerView.resumeCameraPreview(MainActivity.this);
//            }
//        });
//        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                scannerView.resumeCameraPreview(MainActivity.this);
////                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
////                startActivity(intent);
//            }
//        });
//        builder.setMessage(scanResult);
//        AlertDialog alert = builder.create();
//        alert.show();
    }

    public void getUrlAccessToken(final String ticket_no){

        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("clientId","2");
        editor.putString("clientSecret","nGUu9OSbwU8FhEJtWmT2CNMyZVeaKdD6jUHasKDO");
        editor.putString("grantType","password");
        editor.putString("username","charles@deveint.com");
        editor.putString("password","deveint#");
        editor.apply();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://178.79.155.54/events/api/mark_ticket")
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        TicketClient client = retrofit.create(TicketClient.class);

        Call<AccessToken> accessTokenCall=client.getAcccesstoken(

                prefs.getString("clientId",null),
                prefs.getString("clientSecret",null),
                prefs.getString("grantType",null),
                prefs.getString("username",null),
                prefs.getString("password",null));

        accessTokenCall.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
//                Toast.makeText(MainActivity.this, "SUCCESSFUL", Toast.LENGTH_SHORT).show();
//                Log.v("ERROR---->",response.toString());
//                showAlert("\nSUCCESSFUL\n"+response.raw(),"GET ACCESS TOKEN RESULT");

                verifyTicket(response.body().getAccessToken(),ticket_no);
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Toast.makeText(MainActivity.this, "ERROR"+t, Toast.LENGTH_SHORT).show();
            }
        });


    }


    public void markTicket(final String accessToken,String ticket_no) {

        OkHttpClient.Builder client = new OkHttpClient.Builder();

        client.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                Request.Builder newRequest = request.newBuilder().header("Authorization", "Bearer "+accessToken);
                return chain.proceed(newRequest.build());
            }
        });


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client.build())
                .baseUrl(" http://178.79.155.54/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        TicketClient client_t = retrofit.create(TicketClient.class);

        Call<ResponseBody> markTicketPost=client_t.markTicket(ticket_no);

        markTicketPost.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                Toast.makeText(MainActivity.this, "POST SUCCESSUL", Toast.LENGTH_LONG).show();
                showAlert("\nPOST SUCCESSFUL\n","MARK TICKET RESULT");
                Log.e(this.toString(),response.body().toString());

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "POST ERROR", Toast.LENGTH_LONG).show();
                Log.e(this.toString(),t.toString());
            }
        });


    }


    public void verifyTicket(final String accessToken, final String ticket_no){
            final StringBuilder str=new StringBuilder();
        OkHttpClient.Builder client = new OkHttpClient.Builder();

        client.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                Request.Builder newRequest = request.newBuilder().header("Authorization", "Bearer "+accessToken);

                return chain.proceed(newRequest.build());
            }
        });


        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client.build())
                .baseUrl("http://178.79.155.54/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        TicketClient client_t = retrofit.create(TicketClient.class);
        Call<Ticket> markTicketPost=client_t.checkTicket(ticket_no);

        markTicketPost.enqueue(new Callback<Ticket>() {
            @Override
            public void onResponse(Call<Ticket> call, Response<Ticket> response) {
//                Toast.makeText(MainActivity.this, "CHECK SUCCESSUL", Toast.LENGTH_LONG).show();
                if(response.isSuccessful()) {
//                    "\nCHECK SUCCESSFUL\n" + response.raw()


                    if(response.body().getStatus_code().equals("200")) {
                        showAlert("This ticket has already been used", ticket_no);
                        return;
                    }if(response.body().getStatus_code().equals("404")){
                        showAlert("No ticket found for this number", ticket_no);
                        return;
                    }else{
                        markTicket(accessToken,ticket_no);
                    }

                }

            }

            @Override
            public void onFailure(Call<Ticket> call, Throwable t) {
                Toast.makeText(MainActivity.this, "CHECK ERROR", Toast.LENGTH_LONG).show();
                Log.e(this.toString(),t.toString());
            }
        });
    }

    public void showAlert(String str,String title){
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                scannerView.resumeCameraPreview(MainActivity.this);
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                scannerView.resumeCameraPreview(MainActivity.this);
            }
        });
        builder.setMessage(str);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
