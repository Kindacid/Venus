package com.example.venus20;

import static android.content.ContentValues.TAG;

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
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
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

public class BookListActivity extends AppCompatActivity {

    DatabaseReference databaseReference;

    RecyclerView recyclerViewBook;
    ArrayList<Book> bookArrayList;
    BookRecyclerAdapter adapter;
    Button buttonAddBook;
    /*FirebaseAuth mAuth;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list);

        //Objects.requireNonNull(getSupportActionBar()).hide();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        /*mAuth = FirebaseAuth.getInstance();*/

        recyclerViewBook = findViewById(R.id.recyclerViewBook);
        recyclerViewBook.setHasFixedSize(true);
        recyclerViewBook.setLayoutManager(new LinearLayoutManager(this));

        bookArrayList = new ArrayList<>();
        adapter = new BookRecyclerAdapter(BookListActivity.this, bookArrayList);
        recyclerViewBook.setAdapter(adapter);

        buttonAddBook = findViewById(R.id.buttonAddBook);
        buttonAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewDialogAdd viewDialogAdd = new ViewDialogAdd();
                viewDialogAdd.showDialog(getApplicationContext());
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

            EditText texBookName = dialog.findViewById(R.id.textBookName);
            EditText textAutor = dialog.findViewById(R.id.textAutor);
            EditText textEditora = dialog.findViewById(R.id.textEditora);
            EditText textGenero = dialog.findViewById(R.id.textGenero);
            EditText textOndeEnc = dialog.findViewById(R.id.textOndeEnc);

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

                    if (titulo.isEmpty() || autor.isEmpty() || editora.isEmpty()) {
                        Toast.makeText(context, "Preencha os campos obrigatórios (*)", Toast.LENGTH_SHORT).show();
                    } else {
                        databaseReference.child("LIVROS").child(bookId).setValue(new Book(bookId, titulo, autor, editora, genero, ondeEnc));


                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                    }
                }
            });
        }
    }
}