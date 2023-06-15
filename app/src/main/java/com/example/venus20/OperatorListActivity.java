package com.example.venus20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class OperatorListActivity extends AppCompatActivity {

    DatabaseReference databaseReference;

    RecyclerView recyclerView;
    ArrayList<Operator> operatorArrayList;
    OperatorRecyclerAdapter adapter;

    Button buttonAddOpr;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator_list);

        //Objects.requireNonNull(getSupportActionBar()).hide();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        operatorArrayList = new ArrayList<>();

        buttonAddOpr = findViewById(R.id.buttonAddOpr);
        buttonAddOpr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewDialogAdd viewDialogAdd = new ViewDialogAdd();
                viewDialogAdd.showDialog(OperatorListActivity.this);
            }
        });

        readData();
    }

    private void readData() {

        databaseReference.child("USERS").orderByChild("nomeOperador").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                operatorArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Operator operators = dataSnapshot.getValue(Operator.class);
                    operatorArrayList.add(operators);
                }
                adapter = new OperatorRecyclerAdapter(OperatorListActivity.this, operatorArrayList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public class ViewDialogAdd{
        public void showDialog(Context context){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_add_opr);

            EditText textName = dialog.findViewById(R.id.textAddName);
            EditText textEmail = dialog.findViewById(R.id.textAddEmail);
            EditText textUser = dialog.findViewById(R.id.textAddUser);
            EditText textPassword = dialog.findViewById(R.id.textAddPassword);

            Button buttonDialogAddOpr = dialog.findViewById(R.id.buttonDialogAddOpr);
            Button buttonDialogCancelOpr = dialog.findViewById(R.id.buttonDialogCancelOpr);

            /*buttonAddOpr.setText("Adicionado");*/
            buttonDialogCancelOpr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                dialog.dismiss();
                }
            });

            buttonDialogAddOpr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = "operador" + new Date().getTime();
                    String name = textName.getText().toString();
                    String email = textEmail.getText().toString();
                    String user = textUser.getText().toString();
                    String password = textPassword.getText().toString();

                    if(name.isEmpty() || email.isEmpty() || user.isEmpty() || password.isEmpty()){
                        Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    } else {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    FirebaseUser newUser = mAuth.getCurrentUser();
                                    Toast.makeText(context, "Cadastro concluído!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    if (newUser != null){
                                        String operatorID = newUser.getUid();
                                        DatabaseReference operatorUserRef = databaseReference.child("USERS").child(operatorID);
                                        Operator operator = new Operator(operatorID, name, user, email, password, false);
                                        operatorUserRef.setValue(operator);
                                    }
                                } else{
                                    Toast.makeText(context, "Erro ao cadastrar operador" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }
}