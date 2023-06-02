package com.example.venus20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button buttonOperator = findViewById(R.id.buttonOperator);
        buttonOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, OperatorListActivity.class);
                startActivity(intent);
            }
        });
        Button buttonBook = findViewById(R.id.buttonBook);
        buttonBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, BookListActivity.class);
                startActivity(intent);
            }
        });

        Button buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(intent);
            }
        });
    }
}