package com.example.rubel.u2uchat.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.rubel.u2uchat.R;
import com.example.rubel.u2uchat.Util.AppUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener, FirebaseAuth.AuthStateListener {

    // UI views
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private ProgressBar mProgressBar;
    private Button btnSignIn;
    private Button btnSignUp;
    private Button btnForgetPassword;

    // Firebase API Clients
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in);

        bindViews();

        mAuth = FirebaseAuth.getInstance();

        btnSignIn.setOnClickListener(this);

        btnSignUp.setOnClickListener(this);

        btnForgetPassword.setOnClickListener(this);

        mAuth.addAuthStateListener(this);

    }

    private void bindViews() {
        mEditTextEmail = (EditText) findViewById(R.id.edit_text_email_sign_in);
        mEditTextPassword = (EditText) findViewById(R.id.edit_text_password_sign_in);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_sign_in);
        btnSignIn = (Button) findViewById(R.id.btn_signin_in);
        btnSignUp = (Button) findViewById(R.id.btn_signup_in);
        btnForgetPassword = (Button) findViewById(R.id.btn_forget_password_in);
    }

    private boolean validateUserInputs(final String email, final String password){

        if(TextUtils.isEmpty(email)){
            mEditTextEmail.setError("enter email address");
            return false;
        }

        if(!AppUtils.validateEmail(email)){
            mEditTextEmail.setError("Doesn't look like an email.");
            return false;
        }

        if(TextUtils.isEmpty(password)){
            mEditTextPassword.setError("password shouldn't be empty");
            return false;
        }


        if(password.length() < 6){
            mEditTextPassword.setError("minimum 6 characters password");
            return false;
        }

        return true;
    }


    private void trySignInUser(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressBar.setVisibility(View.GONE);

                        if(task.isSuccessful()) {
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            finish();
                        }

                        if(!task.isSuccessful()){
                            AppUtils.toastMessage("Login failed!", SignInActivity.this);
                        }

                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_signin_in: {
                final String email = mEditTextEmail.getText().toString().trim();
                final String password = mEditTextPassword.getText().toString().trim();

                if(!validateUserInputs(email, password)) break;

                mProgressBar.setVisibility(View.VISIBLE);

                // try to sign in user
                trySignInUser(email, password);
                break;
            }
            case R.id.btn_signup_in: {
                AppUtils.toastMessage("Sign Up", this);
                Intent intentSignUp = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intentSignUp);
                break;
            }
            case R.id.btn_forget_password_in: {
                Intent intentPassword = new Intent(SignInActivity.this, ForgetPasswordActivity.class);
                startActivity(intentPassword);
                break;
            } default: break;
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            // user already signed in
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }
    }
}
