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
    ImageView login_pic_imgview;
    TextView login_account_txt;
    TextView login_email_txt;

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

        login_pic_imgview = (ImageView)fragmentView.findViewById(R.id.login_pic_imgview);
        login_account_txt = (TextView)fragmentView.findViewById(R.id.login_account_txt);
        login_email_txt = (TextView)fragmentView.findViewById(R.id.login_email_txt);
        //set event and permission for login facebook btn
        loginButton = fragmentView.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email","public_profile"));
        //too for login google btn
        signInButton = (SignInButton)fragmentView.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.signIn();
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
        if(login_pic_imgview == null) return;

        if(pic == null){
            login_pic_imgview.setVisibility(View.GONE);
            login_account_txt.setVisibility(View.GONE);
            login_email_txt.setVisibility(View.GONE);
            return;
        }
        login_pic_imgview.setVisibility(View.VISIBLE);
        login_account_txt.setVisibility(View.VISIBLE);
        login_email_txt.setVisibility(View.VISIBLE);
        login_pic_imgview.setImageBitmap(pic);
        login_account_txt.setText(accountName);
        login_email_txt.setText(email);
    }

    public void setupButton(){
        if(login_pic_imgview == null) return;
        if(mainActivity.isLogin){
            if(mainActivity.isLoginFB){
                loginButton.setVisibility(View.VISIBLE);
                signInButton.setVisibility(View.GONE);
                signOutButton.setVisibility(View.GONE);
            }else {
                loginButton.setVisibility(View.GONE);
                signInButton.setVisibility(View.GONE);
                signOutButton.setVisibility(View.VISIBLE);
            }
        }else{
            loginButton.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
        }
    }
}
