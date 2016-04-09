package com.devon_dickson.apps.orgspace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    private String authURL = "http://devon-dickson.com/auth";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    public String jwt;
    public Intent mainActivityIntent;
    public LoginResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        Log.d("test","Pass");

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.authButton);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                result = loginResult;

                new getAuth().execute();

            }

            @Override
            public void onCancel() {
                Log.d("Facebook Login Status", "Canceled!");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("Facebook Login Status", "Failed!");
            }


        });

    }

    public class getAuth extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                storeJWT(result);
            }catch(Exception e) {
                Log.d("Intent Extra", "Failed. " + e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            startActivity(mainActivityIntent);
        }
    }

    private void storeJWT(LoginResult loginResult) throws Exception{
        String facebook_id = loginResult.getAccessToken().getUserId();
        String token = loginResult.getAccessToken().getToken();
        Request request = new Request.Builder().url(authURL + "?id=" + facebook_id + "&token=" + token).build();
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful()) {
            Log.d("Test","failed");
            throw new IOException("Unexpected code " + response);
        }
        Log.d("Test","Success!");
        String resp = response.body().string();
        Log.d("jwt", resp);

        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("jwt", resp);
        editor.commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
