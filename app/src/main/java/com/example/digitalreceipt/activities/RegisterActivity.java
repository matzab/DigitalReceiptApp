package com.example.digitalreceipt.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.digitalreceipt.DatabaseHelper;
import com.example.digitalreceipt.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity {
    private EditText firstName;
    private EditText lastName;
    private EditText dateOfBirth;
    private EditText email;
    private EditText conEmail;
    private EditText pwd;
    private EditText conPwd;

    private View registerView;
    private View progressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupActionBar("Sign up");

        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        dateOfBirth = (EditText) findViewById(R.id.date_of_birth);
        email = (EditText) findViewById(R.id.email);
        conEmail = (EditText) findViewById(R.id.confirm_email);
        pwd = (EditText) findViewById(R.id.password);
        conPwd = (EditText) findViewById(R.id.confirm_password);

        registerView = findViewById(R.id.register_view);
        progressView = findViewById(R.id.register_progress);

        Button signUp = (Button) findViewById(R.id.sign_up_button);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSignUp();
            }
        });
    }

    private void setupActionBar(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(title);
        }
    }

    private void attemptSignUp(){
        firstName.setError(null);
        lastName.setError(null);
        dateOfBirth.setError(null);
        email.setError(null);
        conEmail.setError(null);
        pwd.setError(null);
        conPwd.setError(null);

        final String firstName = this.firstName.getText().toString();
        final String lastName = this.lastName.getText().toString();
        final String dateOfBirth = this.dateOfBirth.getText().toString();
        final String email = this.email.getText().toString();
        final String conEmail = this.conEmail.getText().toString();
        final String pwd = this.pwd.getText().toString();
        final String conPwd = this.conPwd.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(firstName)) {
            this.firstName.setError("Entering your first name is required");
            focusView = this.firstName;
            cancel = true;
        }

        if (TextUtils.isEmpty(lastName)) {
            this.lastName.setError("Entering your last name is required");
            focusView = this.lastName;
            cancel = true;
        }

        if (TextUtils.isEmpty(dateOfBirth)) {
            this.dateOfBirth.setError("Entering your date of birth is required");
            focusView = this.dateOfBirth;
            cancel = true;
        }
        if (!isValidDate(dateOfBirth,"dd/MM/yyyy")) {
            this.dateOfBirth.setError("Incorrect date of birth format");
            focusView = this.dateOfBirth;
            cancel = true;
        }

        if (TextUtils.isEmpty(pwd) ) {
            this.pwd.setError("Entering password is required");
            focusView = this.pwd;
            cancel = true;
        }

        if (!conPwd.equals(pwd) ) {
            this.pwd.setError("Passwords do not match");
            focusView = this.pwd;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            this.email.setError("Entering email is required");
            focusView = this.email;
            cancel = true;
        }


        if (!isEmailValid(email)) {
            this.email.setError("Invalid email format");
            focusView = this.email;
            cancel = true;
        }

        if (!email.equals(conEmail)) {
            this.email.setError("Emails do not match");
            focusView = this.email;
            cancel = true;
        }

        if(cancel){
            focusView.requestFocus();
        }else {
            showProgress(true);

            DatabaseHelper db = new DatabaseHelper();

            db.addUser(email,pwd,firstName,lastName,dateOfBirth);

            Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
            login.putExtra("successful", true);
            startActivity(login);

            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            registerView.setVisibility(show ? View.GONE : View.VISIBLE);
            registerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    registerView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            registerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public boolean isValidDate(String dateToValidate, String dateFormat){

        if(dateToValidate == null){
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);

        try {

            //if not valid, it will throw ParseException
            Date date = sdf.parse(dateToValidate);
            System.out.println(date);

        } catch (ParseException e) {

            e.printStackTrace();
            return false;
        }

        return true;
    }
}
