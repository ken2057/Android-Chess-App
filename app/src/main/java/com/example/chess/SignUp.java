package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chess.Data.Account;
import com.example.chess.Data.DbContext;
import com.example.chess.Data.DbHelper;


public class SignUp extends AppCompatActivity {
    DbContext db;
    EditText etxtAccId, etxtAccPassword, etxtEmail;
    TextView tBtnSignIn;
    Button btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        //region Declaration
        tBtnSignIn = findViewById(R.id.sign_up_to_sign_in);
        btnSignUp = findViewById(R.id.sign_up);
        etxtAccId = findViewById(R.id.account_name);
        etxtAccPassword = findViewById(R.id.account_password);
        etxtEmail = findViewById(R.id.account_email);
        db = new DbContext(this);
        //endregion



        tBtnSignIn.setOnClickListener(view -> {
            Intent goToSignIn = new Intent(SignUp.this,SignIn.class);
            startActivity(goToSignIn);
            finish();
        });

        btnSignUp.setOnClickListener(view -> {
            db.add(new Account(
                    "" + etxtAccId.getText(),
                    "" + etxtAccPassword.getText(),
                    "" + etxtEmail.getText()
            ));

            Intent goForSignIn = new Intent(SignUp.this,SignIn.class);
            startActivity(goForSignIn);
            finish();
        });
    }
}
