package com.example.ticketmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import java.util.Arrays;

public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getSimpleName();
    private final MainActivity mainActivity;

    public LoginButton loginButton;
    public SignInButton signInButton;
    public Button signOutButton;

    Bitmap pic;
    String accountName;
    String email;
    ImageView loginPicImgview;
    TextView loginAccountTxt;
    TextView loginEmailTxt;

    public LoginFragment(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        this.pic = null;
        this.accountName = null;
        this.email = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_login,container,false);

        loginPicImgview = (ImageView)fragmentView.findViewById(R.id.login_pic_imgview);
        loginAccountTxt = (TextView)fragmentView.findViewById(R.id.login_account_txt);
        loginEmailTxt = (TextView)fragmentView.findViewById(R.id.login_email_txt);
        //set event and permission for login facebook btn
        loginButton = fragmentView.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email","public_profile"));
        //too for login google btn
        signInButton = (SignInButton)fragmentView.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.signIn();
                //see what we got it!
                setDataUser();
            }
        });
        // and logout google btn, too!!!
        signOutButton =  (Button) fragmentView.findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.signIn();
                setDataUser();
            }
        });
        //reset UI
        setupButton();
        setDataUser();
        return fragmentView;
    }

    public void setDataUser(){
        //view was create? if not yet ui will empty and return
        if(loginPicImgview == null) return;

        if(pic == null){//if data user empty
            //nothing to show so invisibility for user view
            loginPicImgview.setVisibility(View.GONE);
            loginAccountTxt.setVisibility(View.GONE);
            loginEmailTxt.setVisibility(View.GONE);
            return;
        }
        //visibility data user view
        loginPicImgview.setVisibility(View.VISIBLE);
        loginAccountTxt.setVisibility(View.VISIBLE);
        loginEmailTxt.setVisibility(View.VISIBLE);
        //take data and give to view
        loginPicImgview.setImageBitmap(pic);
        loginAccountTxt.setText(accountName);
        loginEmailTxt.setText(email);
    }

    public void setupButton(){
        //view was create? if not yet ui will empty and return
        if(loginPicImgview == null) return;
        //if user is login
        if(mainActivity.isLogin){
            //if user is login with facebook
            if(mainActivity.isLoginFB){
                //able for fb login btn
                loginButton.setVisibility(View.VISIBLE);
                //sign in,out google btn will disable
                signInButton.setVisibility(View.GONE);
                signOutButton.setVisibility(View.GONE);
            }else {//else user login with google
                loginButton.setVisibility(View.GONE);
                signInButton.setVisibility(View.GONE);
                signOutButton.setVisibility(View.VISIBLE);
            }
        }else{//if user !login
            //visible both fb and google login
            loginButton.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.VISIBLE);
            //not for logout
            signOutButton.setVisibility(View.GONE);
        }
    }
}
