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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    // UI views
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private EditText mEditTextPasswordConfirm;
    private ProgressBar mProgressBar;
    private Button btnSignUp;

    // Firebase API Clients
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        bindViews();

        btnSignUp.setOnClickListener(this);
    }

    private void bindViews() {
        mEditTextEmail = (EditText) findViewById(R.id.edit_text_email_sign_up);
        mEditTextPassword = (EditText) findViewById(R.id.edit_text_password_sign_up);
        mEditTextPasswordConfirm = (EditText) findViewById(R.id.edit_text_password_confirm_sign_up);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar_sign_up);
        btnSignUp = (Button) findViewById(R.id.btn_signup_up);
    }

    private boolean validateUserInputs(String email, String password, String confirmPassword){

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

        if(!password.equals(confirmPassword)){
            mEditTextPasswordConfirm.setError("password mismatch!");
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_signup_up:{
                final String email = mEditTextEmail.getText().toString().trim();
                final String password = mEditTextPassword.getText().toString().trim();
                final String confirmPassword = mEditTextPasswordConfirm.getText().toString().trim();

                if(!validateUserInputs(email, password, confirmPassword))
                    break;

                mProgressBar.setVisibility(View.VISIBLE);

                // try to signup user
                trySignUpUser(email, password);

                break;
            }default: break;
        }
    }

    private void trySignUpUser(final String email, final String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressBar.setVisibility(View.GONE);

                        if(task.isSuccessful()) {
                            startActivity(new Intent(SignUpActivity.this, FirstTimeLoginActivity.class));
                            finish();
                        }

                        if(!task.isSuccessful()) {
                            AppUtils.toastMessage("Authentication failure", SignUpActivity.this);
                        }
                    }
                });
    }
}
