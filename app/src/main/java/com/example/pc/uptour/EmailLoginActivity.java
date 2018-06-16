package com.example.pc.uptour;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc.uptour.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmailLoginActivity extends AppCompatActivity implements View.OnClickListener
{

    private FirebaseAuth mAuth;

    String TAG="EmailLoginActivity";

    private EditText emailEditText,passwordEditText;
    private Button loginButton;
    private TextView textView;
    private  EditText nameEditText;


    private FirebaseDatabase myDataBase;
    DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        getSupportActionBar().setTitle("Log in/Create Account");
        mAuth=FirebaseAuth.getInstance();

        emailEditText= findViewById(R.id.emailTextView);
        passwordEditText= findViewById(R.id.passwordEditText);
        nameEditText=findViewById(R.id.nameEditText);
        textView= findViewById(R.id.errorTV);

        loginButton= findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);



    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }

    public void createAccount(final String email, final String password, final String name)
    {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            myDataBase=FirebaseDatabase.getInstance();
                            myRef=myDataBase.getReference();
                            User user1=new User();

                            user1.setUserName(name);
                            user1.setUserEmail(user.getEmail());
                            user1.setPhotoUrl("null");
                            myRef.child("users").child(user.getUid()).setValue(user1);


                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Email sent.");
                                            }
                                        }
                                    });
                            signIn(email, password);
                            updateUI(user);
                        }
                        else
                            {

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                Toast.makeText(EmailLoginActivity.this, "Redirecting to SignIN...", Toast.LENGTH_SHORT).show();
                                signIn(email, password);
                            }

                            else
                            {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(EmailLoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }


                                            }
                });
    }

    public void signIn(final String email, final String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(EmailLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            new AlertDialog.Builder(EmailLoginActivity.this)
                                    .setTitle("User Authentication Failed!")
                                    .setMessage("The user authentication failed." +
                                            "\nPlease Try Again!").setIcon(R.drawable.error).setPositiveButton("OKAY",null).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void updateUI(FirebaseUser currentUser)
    {
        if (currentUser!=null)
        {
            Intent intent=new Intent(EmailLoginActivity.this,MainActivity.class);
            intent.putExtra("currentUser",currentUser.getUid());
            startActivity(intent);
            Toast.makeText(this, "UI Update Request from currentUser "+ currentUser+ currentUser.getEmail()+currentUser.isEmailVerified(), Toast.LENGTH_SHORT).show();
    }}

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.login_button)
        {
            textView.setText("");
            if (!emailEditText.getText().toString().equals("")&& !passwordEditText.getText().toString().equals("")) {
                if (emailEditText.getText().toString().matches("(.*?)@(.*?).com")) {

                    if (nameEditText!=null)
                    //if (passwordEditText.getText().toString().matches("\"((?=.*\\\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})"))
                    {
                        createAccount(emailEditText.getText().toString(), passwordEditText.getText().toString(), nameEditText.getText().toString());
                    }//else
                    //    textView.setText("Password does not match the criteria\n 1 uppercase letter\n 1 lowercase letter\n 1 special symbol\n length>=4");
                } else
                    textView.setText("E-mail is in incorrect format");
            }else
                textView.setText("Email/password cannot be left blank");

                }
            }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.signOut();
    }
}