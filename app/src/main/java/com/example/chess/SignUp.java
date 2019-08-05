package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class SignUp extends AppCompatActivity {

    TextView tBtnSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        tBtnSignIn=findViewById(R.id.sign_up_to_sign_in);
        tBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSignIn = new Intent(SignUp.this,SignIn.class);
                startActivity(goToSignIn);
                finish();
            }
        });
    }
}
