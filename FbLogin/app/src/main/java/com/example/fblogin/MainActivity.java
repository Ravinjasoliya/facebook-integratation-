package com.example.fblogin;

import android.content.Context;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Arrays;

import static android.R.attr.data;


public class MainActivity extends AppCompatActivity {


    LoginButton loginButton;
    CallbackManager callbackManager;
    String AuthToken,UserId,tag="login",fbuser,email,birthday;
    FacebookCallback<LoginResult> facebookCallback;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginButton=(LoginButton)findViewById(R.id.login);
    //    loginButton.setReadPermissions("user_friends");
      //  loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("email");
       // loginButton.setReadPermissions("user_birthday");
     //   loginButton.setReadPermissions(Arrays.asList("email", "public_profile","user_birthday"));
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile","user_birthday"));
      //  loginButton.setReadPermissions("email");
        textView=(TextView)findViewById(R.id.txt1);
        callbackManager =CallbackManager.Factory.create();

        AppEventsLogger appEventsLogger=AppEventsLogger.newLogger(this);
        appEventsLogger.logEvent("email");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }
            @Override
            public void onCancel() {

            }
            @Override
            public void onError(FacebookException error) {

            }
        });

        //changed token (user login change)
      /*  AccessTokenTracker accessTokenTracker=new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                AuthToken =currentAccessToken.getToken();
                UserId=currentAccessToken.getUserId();
                Log.e(tag,"user="+UserId);

            }
        };*/

        //get User Name Only

     /*   ProfileTracker profileTracker=new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                fbuser=currentProfile.getName();
                textView.setText(fbuser);

                Log.e(tag,"user="+fbuser);
            }
        };*/


        //get Graph Api data

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                textView.setText(fbuser);

                String[] requiredFields = new String[]{"name","email","birthday"};//fields in graph api
                Bundle parameters = new Bundle();
                parameters.putString("fields", TextUtils.join(",", requiredFields));

                GraphRequest requestEmail = new GraphRequest(loginResult.getAccessToken(), "me", parameters, null, new GraphRequest.Callback()
                {
                    @Override
                    public void onCompleted (GraphResponse response)
                    {
                        if (response != null)
                        {
                            GraphRequest.GraphJSONObjectCallback callbackEmail = new GraphRequest.GraphJSONObjectCallback()
                            {
                                @Override
                                public void onCompleted (JSONObject me, GraphResponse response)
                                {
                                    Log.e(tag,response.toString());
                                    if (response.getError() != null)
                                    {
                                        Log.d(tag, "FB: cannot parse email");
                                    }
                                    else
                                    {
                                        String email = me.optString("email");
                                        String bd = me.optString("birthday");
                                        String name = me.optString("name");

                                        textView.setText(email+" "+bd+" "+name);
                                        Log.e("emailss",email+"null"+bd+" nul"+name);
                                                                            }
                                }
                            };

                            callbackEmail.onCompleted(response.getJSONObject(), response);
                        }
                    }
                });
                requestEmail.executeAsync();

            }
            @Override
            public void onCancel() {

            }
            @Override
            public void onError(FacebookException error) {

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

}