package com.example.venus20;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.venus20.databinding.LoginActivityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    LoginActivityBinding binding;
    private FirebaseAuth mAuth;
    private  DatabaseReference databaseReference;
    String userID;
    boolean isAdmin;
    TextView recCred;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);


        databaseReference = FirebaseDatabase.getInstance().getReference();

        binding = LoginActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        /*verificarAdminInicial();*/

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    loginEmailSenha(binding.editEmail.getText().toString(), binding.editPassword.getText().toString());
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), "Credenciais inválidas", Toast.LENGTH_LONG).show();
                }
            }
        });
        recCred = findViewById(R.id.recCredLogin);
        recCred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewDialogRecCred viewDialogRecCred = new ViewDialogRecCred();
                viewDialogRecCred.sowDialog(LoginActivity.this);
            }
        });
    }
    //MÉTODO PARA CRIAR O PRIMEIRO ADMIN NO BANCO DE DADOS
    /*private void verificarAdminInicial(){
        DatabaseReference adminRef = databaseReference.child("USER").child("user");
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    mAuth.createUserWithEmailAndPassword("admin@admin.com", "admin123").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser newUser = mAuth.getCurrentUser();
                                if (newUser != null) {
                                    String adminId = newUser.getUid();
                                    DatabaseReference adminUserRef = databaseReference.child("USERS").child(adminId);
                                    Admin admin = new Admin(adminId, "Admin", "admin123", "admin@admin.com", "admin123", true);
                                    adminUserRef.setValue(admin).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(LoginActivity.this, "Admin Cadastrado", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Falha ao cadastrar Admin", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                Exception exception = task.getException();
                                if (exception != null) {
                                    String errorMessage = exception.getMessage();
                                    Toast.makeText(LoginActivity.this, "Erro ao criar o admin: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                } else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Erro ao verificar Admin inicial", Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    private void loginEmailSenha(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInWithCustomToken:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    limparCampos();
                    if (user != null) {
                        String userID = user.getUid();
                        DatabaseReference userRef = databaseReference.child("USERS").child(user.getUid());
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Map<String, Object> userData = (Map<String, Object>) snapshot.getValue();
                                    if (userData != null) {
                                        boolean isAdmin = (boolean) userData.get("admin");
                                        if (isAdmin) {
                                            Toast.makeText(LoginActivity.this, "Entrando como Administrador", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Entrando como Operador", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, OperatorActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(LoginActivity.this, "Erro ao acessar o banco de dados", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                    //updateUI(user);
                } else {
                    //Login Falho: mostrar erro ao usuário
                    Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                    Toast.makeText(getApplicationContext(), "Credenciais erradas", Toast.LENGTH_LONG).show();
                    //updateUI(null);
                }
            }

            private void limparCampos() {
                binding.editEmail.setText("");
                binding.editPassword.setText("");
            }
        });
    }


    public class ViewDialogRecCred{
        public void sowDialog(Context context){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_rec_credencial);

            EditText textRecName = dialog.findViewById(R.id.textRecName);
            EditText textRecEmail = dialog.findViewById(R.id.textRecEmail);

            Button buttonRecEnviar = dialog.findViewById(R.id.buttonRecEnviar);

            buttonRecEnviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Email enviado! Admin entrará em contato.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}