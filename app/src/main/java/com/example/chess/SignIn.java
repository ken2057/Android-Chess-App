package com.example.chess;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.chess.Data.Account;
import com.example.chess.Data.DbContext;

public class SignIn extends AppCompatActivity {
    EditText etxtID, etxtPass;
    Button btnSignIn,btnSignUp;
    DbContext db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        //region Declaration
        etxtID = findViewById(R.id.sign_in_id);
        etxtPass = findViewById(R.id.sign_in_password);
        btnSignIn = findViewById(R.id.sign_in);
        btnSignUp = findViewById(R.id.sign_up);
        db = new DbContext(this);
        //endregion

        //Function
        btnSignIn.setOnClickListener(view -> {
            if (db.check(new Account(
                    "" + etxtID.getText(),
                    "" + etxtPass.getText(),
                    null
            ))) {
                Intent goForGame = new Intent(SignIn.this,MainActivity.class);
                startActivity(goForGame);
                finish();
            }
            else {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Alert message to be shown");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            }
        });


        btnSignUp.setOnClickListener(view -> {
            Intent goForSignUp = new Intent(SignIn.this,SignUp.class);
            startActivity(goForSignUp);
            finish();
        });
    }
}
