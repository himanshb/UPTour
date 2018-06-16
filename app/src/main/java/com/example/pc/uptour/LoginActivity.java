package com.example.pc.uptour;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.pc.uptour.classes.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;


public class LoginActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, Serializable
{
    //for User Status
    private FirebaseAuth mAuth;
    DatabaseReference myRef;
    CallbackManager callBackManager;

   //for google sign-in
   private GoogleApiClient mGoogleApiClient;

    String TAG="GoogleLoginActivity";
    int RC_SIGN_IN=123;

    public void signInmehtod(View view)
    {
        Intent signInIntent;
        signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser !=null)
        {

          Intent intent=new Intent(this,MainActivity.class);
          Log.i("####ONSTART###", currentUser.getUid()+"\n"+ currentUser.getDisplayName()+"\n"+ currentUser.getEmail()+ currentUser.getPhotoUrl());
          startActivity(intent);
          finish();
          //Toast.makeText(this, "User Logged in: "+ currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
        }
        else{}
            //Toast.makeText(this, "User is null", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //noinspection ConstantConditions
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase myDataBase = FirebaseDatabase.getInstance();
        myRef= myDataBase.getReference();

        //for facebook login
        //noinspection deprecation
        FacebookSdk.sdkInitialize(getApplicationContext());
        //noinspection deprecation
        AppEventsLogger.activateApp(this);

        callBackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callBackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                setFacebookdata(loginResult);
            }
            @Override
            public void onCancel()
            {
                Toast.makeText(LoginActivity.this, "User login attempt cancelled", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(FacebookException error)
            {

            }
        });

        LoginButton login_button = findViewById(R.id.login_button);
        login_button.setReadPermissions("email");

        //for google-sign-in
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("85994419044-888mncmrlhg2dr4pr0ihn0erk2cqjds9.apps.googleusercontent.com")
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, LoginActivity.this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //for animation
        Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.slide_up_animation);
        animation.setStartOffset(0);
        CardView cardView3=findViewById(R.id.cardView3);
        cardView3.startAnimation(animation);
    }

    //google sign-in
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle("User Authentication Failed!")
                .setMessage("The user authentication failed due to some technical issues." +
                        "\nPlease Try Again!").setIcon(R.drawable.error).setPositiveButton("OKAY",null).show();
    }


    private void setFacebookdata(LoginResult loginResult)
    {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            Log.i("Response", response.toString());

                            String email = response.getJSONObject().getString("email");
                            String firstName = response.getJSONObject().getString("first_name");
                            String lastName = response.getJSONObject().getString("last_name");
                            String gender = response.getJSONObject().getString("gender");


                            Profile profile = Profile.getCurrentProfile();
                            String link = profile.getLinkUri().toString();
                            Log.i("Link", link);
                            if (Profile.getCurrentProfile() != null)
                            {
                                Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                            }

                                Log.i("Login" + "Email", email);
                                Log.i("Login" + "FirstName", firstName);
                                Log.i("Login" + "LastName", lastName);
                                Log.i("Login" + "Gender", gender);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAsync();

        handleFacebookAccessToken(loginResult.getAccessToken());
    }

        //creating/signing in the user in firebase

    private void handleFacebookAccessToken(AccessToken token)
    {
        Log.d("", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            //noinspection ConstantConditions
                            Toast.makeText(LoginActivity.this, user.getPhotoUrl().toString(), Toast.LENGTH_SHORT).show();
                            User user1=new User();
                            user1.setUserName(user.getDisplayName());
                            user1.setUserEmail(user.getEmail());
                            user1.setPhotoUrl(user.getPhotoUrl().toString());
                            myRef.child("users").child(user.getUid()).setValue(user1);

                            //profilePic.setImageBitmap(getbmpfromURL(String.valueOf(user.getPhotoUrl())));
                            Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("UserID",user.getUid());
                            intent.putExtra("Email",user1.getUserName());
                            intent.putExtra("User Name",user1.getUserEmail());
                            intent.putExtra("photoUrl",user1.getPhotoUrl());

                            //       intent.putExtra("currentUser", (Serializable) user);
                            startActivity(intent);
                            finish();
                            Toast.makeText(LoginActivity.this, "User Logged in :"+ user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("User Authentication Failed!")
                                    .setMessage("The user authentication failed due to some technical issues." +
                                            "\nPlease Try Again!").setIcon(R.drawable.error).setPositiveButton("OKAY",null).show();

                        }

                    }
                });
    }
        //creating/signing in the user in firebase

    //for facebook login
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        //if google sign-in
        if (resultCode== RESULT_OK && data!=null)
        if (requestCode == RC_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        else
        {
        callBackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    //for facebook login

    //for google sign-in
    private void handleSignInResult(GoogleSignInResult result)
    {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess())
        {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //mStatusTextView.setText(acct.getDisplayName());
            //updateUI(mAuth.getCurrentUser());
            //Toast.makeText(this, mAuth.getCurrentUser().toString(), Toast.LENGTH_SHORT).show();
        } else
        {
            // Signed out, show unauthenticated UI.
            Toast.makeText(this, "Null user", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct)
    {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                            User user1=new User();
                            if (user != null) {
                                user1.setUserName(user.getDisplayName());

                            user1.setUserEmail(user.getEmail());
                                //noinspection ConstantConditions
                                user1.setPhotoUrl(user.getPhotoUrl().toString());
                            myRef.child("users").child(user.getUid()).setValue(user1);
                            intent.putExtra("UserID",user.getUid());
                            intent.putExtra("Email",user1.getUserName());
                            intent.putExtra("User Name",user1.getUserEmail());
                            intent.putExtra("photoUrl",user1.getPhotoUrl());
                            }

                            startActivity(intent);
                            finish();
                            if (user != null) {
                               // Toast.makeText(LoginActivity.this, user.getDisplayName(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, "Null user ", Toast.LENGTH_SHORT).show();
                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("User Authentication Failed!")
                                    .setMessage("The user authentication failed due to some technical issues." +
                                            "\nPlease Try Again!").setIcon(R.drawable.error).setPositiveButton("OKAY",null).show();
                        }

                                            }
                });
    }

    public void useEmail(View view)
    {
        Intent intent=new Intent(this, EmailLoginActivity.class);
        startActivity(intent);

    }

    public void skipButton(View view)
    {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
