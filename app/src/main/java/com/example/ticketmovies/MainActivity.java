package com.example.ticketmovies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    public SharedPreferences sharedPreferences;
    public boolean isLogin ;
    public boolean isLoginFB;
    public String url_pic_fb;
    public String account_name_fb;
    public String email_fb;

    public static CallbackManager callbackManager;
    public static GoogleSignInClient mGoogleSignInClient;
    public static final int REQ_CODE_SIGNIN = 9001;

    public FragmentManager mFragmentManager;
    public LoginFragment loginFragment;
    public static MoviesInfoFragment moviesInfoFragment;
    public static MovieInfoAdapter movieInfoAdapter;
    public ArrayList<MainActivity.Movie> movies;

    private Button main_showinfo_btn;
    private Button main_login_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //load local data user
        sharedPreferences = getSharedPreferences("data_user_login",MODE_PRIVATE);
        isLogin = sharedPreferences.getBoolean("isLogin",false);
        isLoginFB = sharedPreferences.getBoolean("isLoginFB",false);
        url_pic_fb = sharedPreferences.getString("url_pic_fb","");
        account_name_fb = sharedPreferences.getString("account_name_fb","");
        email_fb =  sharedPreferences.getString("email_fb","");

        //use api to load info film and show
        movies = new ArrayList<MainActivity.Movie>();
        new LoadMoviesInfo().execute("https://api.androidhive.info/json/movies_2017.json");
        movieInfoAdapter = new MovieInfoAdapter(this,movies);

        // create fragment
        loginFragment = new LoginFragment(this);
        moviesInfoFragment = new MoviesInfoFragment();
        // change fragment, show info movies
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_frag_content,moviesInfoFragment,"info");
        fragmentTransaction.commit();

        //set up login in facebook
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {}
                    @Override
                    public void onCancel() {}
                    @Override
                    public void onError(FacebookException exception) {exception.printStackTrace(); }
                });
        //set up login in facebook
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //load user everytime - include fb use
        updateUI(account);


        main_showinfo_btn = (Button)this.findViewById(R.id.main_showinfo_btn);
        main_showinfo_btn.setOnClickListener(this);
        main_showinfo_btn.setEnabled(false);
        main_login_btn = (Button)this.findViewById(R.id.main_login_btn);
        main_login_btn.setOnClickListener(this);
        main_login_btn.setEnabled(true);
    }

    private void updateUI(GoogleSignInAccount account) {
        Log.e(TAG, "updateUI: " + " alo alo");
        //if log google success
        if(account!=null){
            (new LoadImageUser(account.getDisplayName(),account.getEmail()))
                    .execute(account.getPhotoUrl().toString());
            isLogin = true;
            isLoginFB = false;
            url_pic_fb = "";
            account_name_fb= "";
            email_fb = "";
            loginFragment.setupButton();
        }else {//if log google fail
            emptyUser();
        }
    }
    public void signIn() {
        Log.e(TAG, "signIn: " + (loginFragment.accountName==null));
        if(loginFragment.accountName==null){
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, REQ_CODE_SIGNIN);
        }else {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            emptyUser();
                        }
                    });
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_showinfo_btn:{
                //just for sure about they dont click this button again
                main_showinfo_btn.setEnabled(false);
                main_login_btn.setEnabled(true);
                // back to movies view
                mFragmentManager.popBackStack();
                break;
            }
            case R.id.main_login_btn:{
                //just for sure about they dont click this button again
                main_login_btn.setEnabled(false);
                main_showinfo_btn.setEnabled(true);
                //get into login view
                FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.main_frag_content,loginFragment,"login");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            }
        }
    }

    public void emptyUser(){
        if(isLoginFB){// đây là trường hợp fb uesr đóng app và vào lại
            (new LoadImageUser( account_name_fb,email_fb)).execute(url_pic_fb);
        }else {
            //clear user data
            isLogin = false;
            isLoginFB = false;
            loginFragment.pic = null;
            loginFragment.accountName = null;
            loginFragment.email = null;
            url_pic_fb = "";
            account_name_fb= "";
            email_fb = "";
        }
        //reset ui
        loginFragment.setupButton();
        loginFragment.setDataUser();
        Log.e(TAG, "emptyUser: " + "ở đây nó clear data");
        //save data
        saveData();
    }

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken==null){//empty user
                isLogin = false;
                isLoginFB = false;
                emptyUser();
            }else{// we got 1 facebook user
                isLogin = true;
                isLoginFB = true;
                loadUserFB(currentAccessToken);
            }
        }
    };
    private void loadUserFB(AccessToken accessToken){
        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    // get user data
                    url_pic_fb = " https://graph.facebook.com/" + object.getString("id") + "/picture?width=500&height=500";
                    account_name_fb = object.getString("first_name") + " " + object.getString("last_name");
                    email_fb = object.getString("email");
                    (new LoadImageUser( account_name_fb,email_fb)).execute(url_pic_fb);
                    loginFragment.setupButton();
                    //save user data
                    saveData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle bundle = new Bundle();
        bundle.putString("fields","first_name,last_name,email,id");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();

    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //facebook callback
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        //result google request
        if (requestCode == REQ_CODE_SIGNIN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    public class Movie{
        String title;
        String ticketPrice;
        Bitmap pic;
        public Movie(String title,String ticketPrice,Bitmap pic){
            this.title = title;
            this.ticketPrice = ticketPrice;
            this.pic = pic;
        };
        public void setTitle(String title){
            this.title = title;
        }
        public String getTitle(){
            return this.title;
        }
        public void setTicketPrice(String ticketPrice){
            this.ticketPrice = ticketPrice;
        }
        public String getTicketPrice(){
            return this.ticketPrice;
        }
        public void setPic(Bitmap pic ){
            this.pic = pic;
        }
        public Bitmap getPic(){
            return this.pic;
        }
    }
    //get json api about movies
    public class LoadMoviesInfo extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(final String... strings) {
            StringBuilder content = new StringBuilder();
            try {
                URL url = new URL(strings[0]);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
                String line = "";
                while ((line=bufferedReader.readLine())!=null){
                    content.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return content.toString().trim();
        }

        @Override
        protected void onPostExecute(String s) {
            new LoadImageMovies().execute(s);
            super.onPostExecute(s);
        }
    }
    //load image movies and handle to show in UI
    public class LoadImageMovies extends AsyncTask<String,Movie,String>{
        @Override
        protected String doInBackground(String... strings) {
            try {
                JSONArray jsonArray = new JSONArray(strings[0]);
                for (int i = 0; i < jsonArray.length() ; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Bitmap bitmap = BitmapFactory.decodeStream(new java.net.URL(jsonObject.getString("image")).openStream());
                    //show every image we was been loaded
                    publishProgress(new Movie(
                            jsonObject.getString("title"),
                            jsonObject.getString("price"),
                            bitmap
                    ));
                }
            }catch (JSONException | MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Movie... moviesPar) {
            movies.add(moviesPar[0]);
            movieInfoAdapter.notifyDataSetChanged();
            super.onProgressUpdate(moviesPar);
        }
    }
    //load image user and handle to show in UI
    public class LoadImageUser extends AsyncTask<String,Void,Bitmap>{
        String acccountNameUser;
        String emailUser;
        public LoadImageUser(String acccountNameUser,String emailUser){
            this.acccountNameUser = acccountNameUser;
            this.emailUser = emailUser;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap pic = null;
            try {
                URL url = new URL(strings[0]);
                pic = BitmapFactory.decodeStream(new java.net.URL(strings[0]).openStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return pic;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            loginFragment.pic = bitmap;
            loginFragment.accountName = this.acccountNameUser;
            loginFragment.email = this.emailUser;
            loginFragment.setDataUser();
        }
    }

    public void saveData(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLogin",isLogin);
        editor.putBoolean("isLoginFB",isLoginFB);
        editor.putString("url_pic_fb",url_pic_fb);
        editor.putString("account_name_fb",account_name_fb);
        editor.putString("email_fb",email_fb);
        editor.commit();
    }
//    public void logsomething(){
//        Log.e(TAG, "logsomething: isLogin " + sharedPreferences.getBoolean("isLogin",false));
//        Log.e(TAG, "logsomething: isLoginFB " + sharedPreferences.getBoolean("isLoginFB",false));
//        Log.e(TAG, "logsomething: url_pic_fb " + sharedPreferences.getString("url_pic_fb","empty"));
//        Log.e(TAG, "logsomething: account_name_fb " + sharedPreferences.getString("account_name_fb","empty"));
//        Log.e(TAG, "logsomething: email_fb " + sharedPreferences.getString("email_fb","empty"));
//    }
    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy: App đóng");
        super.onDestroy();
    }
}
