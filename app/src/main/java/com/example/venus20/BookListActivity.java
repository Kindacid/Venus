package com.example.venus20;

import static android.content.ContentValues.TAG;

import static java.nio.file.Paths.get;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
//classes necessarias para interagir com o Firebase

public class BookListActivity extends AppCompatActivity {
    //declara a classe Book... que estende a classe App... para criar uma atividade do android

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    String userID;
    RecyclerView recyclerViewBook;
    ArrayList<Book> bookArrayList;
    BookRecyclerAdapter adapter;
    Button buttonAddBook;
    //declaram as variaveis usadas na classe, incluindo a referencia do banco de dados.
    //tambem inclui o botão para adicionar os livros dentro do aplicativo.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list);
        //o método onCreate é sempre chamado quando a atividade é criada.
        //ele serve para configurar o layout da atividade para o arquivo xml.

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            userID = user.getUid();
            DatabaseReference userRef = databaseReference.child("USERS").child(userID);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        Map<String,Object> userData = (Map<String, Object>) snapshot.getValue();
                        if (userData != null){
                            boolean isAdmin = (boolean) userData.get("admin");
                            if(isAdmin){
                                fetchAllBook();
                            } else{
                                fetchAllBook();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(BookListActivity.this, "Erro ao acessar o banco de dados", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Query query = databaseReference.child("LIVROS").orderByChild("operatorID").equalTo(userID);

        recyclerViewBook = findViewById(R.id.recyclerViewBook);
        recyclerViewBook.setHasFixedSize(true);
        recyclerViewBook.setLayoutManager(new LinearLayoutManager(this));
        bookArrayList = new ArrayList<>();
        //faz referencia ao RecyclerView no layout e configuram seu layoutManager como um LinearLayoutManager para exibir os itens em uma lista vertical

        adapter = new BookRecyclerAdapter(BookListActivity.this, bookArrayList);
        recyclerViewBook.setAdapter(adapter);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Book book = snapshot.getValue(Book.class);
                    bookArrayList.add(book);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttonAddBook = findViewById(R.id.buttonAddBook);
        buttonAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewDialogAdd viewDialogAdd = new ViewDialogAdd();
                viewDialogAdd.showDialog(BookListActivity.this);
            }
        });

        readData();
    }

    private void readData() {

        databaseReference.child("LIVROS").orderByChild("LIVRO").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Book books = dataSnapshot.getValue(Book.class);
                    bookArrayList.add(books);

                }
                adapter = new BookRecyclerAdapter(BookListActivity.this, bookArrayList);
                recyclerViewBook.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public class ViewDialogAdd {
        public void showDialog(Context context) {
            Log.d(TAG, "DIALOG ADD");
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_add_book);

            EditText texBookName = dialog.findViewById(R.id.textBookNameDialog);
            EditText textAutor = dialog.findViewById(R.id.textAutorDialog);
            EditText textEditora = dialog.findViewById(R.id.textEditoraDialog);
            EditText textGenero = dialog.findViewById(R.id.textGeneroDialog);
            EditText textOndeEnc = dialog.findViewById(R.id.textOndeEncDialog);

            Button buttonDialogAddBook = dialog.findViewById(R.id.buttonDialogAddBook);
            Button buttonDialogCancelBook = dialog.findViewById(R.id.buttonDialogCancelBook);

           /* buttonDialogAddBook.setText("Adicionado");*/

            buttonDialogCancelBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            buttonDialogAddBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String bookId = "Livro" + new Date().getTime();
                    String titulo = texBookName.getText().toString();
                    String autor = textAutor.getText().toString();
                    String editora = textEditora.getText().toString();
                    String genero = textGenero.getText().toString();
                    String ondeEnc = textOndeEnc.getText().toString();
                    Intent intent;

                    if (titulo.isEmpty() || autor.isEmpty() || editora.isEmpty()) {
                        Toast.makeText(context, "Preencha os campos obrigatórios (*)", Toast.LENGTH_SHORT).show();
                    } else {
                        String operatorID = userID;
                        DatabaseReference livroRef = databaseReference.child("LIVROS").child(bookId);
                        livroRef.setValue(new Book(bookId, titulo, autor, editora, genero, ondeEnc, operatorID)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    DatabaseReference userRef = databaseReference.child("USERS").child(userID);
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                Map<String, Object> userData = (Map<String, Object>) snapshot.getValue();
                                                if (userData != null) {
                                                    boolean isAdmin = (boolean) userData.get("admin");
                                                    if (isAdmin) {
                                                        fetchAllBook();
                                                    } else {
                                                        fetchAllBook();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(context, "Erro acessando banco de dados", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }else{
                                    Toast.makeText(context, "Erro adicionando o livro", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        });
                    }

                }
            });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

    }
    /*private void fetchOperatorBooks(String operatorID){
        Query query = databaseReference.child("LIVROS").orderByChild("operatorID").equalTo(operatorID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookArrayList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Book book = snapshot.getValue(Book.class);
                    bookArrayList.add(book);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/
    private void fetchAllBook(){
        DatabaseReference bookRef = databaseReference.child("LIVROS");
        bookRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Book book = snapshot.getValue(Book.class);
                    bookArrayList.add(book);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}