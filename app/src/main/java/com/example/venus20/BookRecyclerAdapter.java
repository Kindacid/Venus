package com.example.venus20;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

    public class BookRecyclerAdapter extends RecyclerView.Adapter<BookRecyclerAdapter.ViewHolder> {

    Context context;
    ArrayList<Book> bookArrayList;
    DatabaseReference databaseReference;

    public BookRecyclerAdapter(Context context, ArrayList<Book> bookArrayList) {
        this.context = context;
        this.bookArrayList = bookArrayList;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.book_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Book books = bookArrayList.get(position);

        holder.textBookName.setText("Titulo: " + books.getTitulo());
        holder.textAutor.setText("Autor(a): " + books.getAuthor());
        holder.textEditora.setText("Editora: " + books.getEditor());
        holder.textGenero.setText("Gênero: " + books.getGenero());
        holder.textOndeEnc.setText("Onde encontrar: " + books.getOndeEnc());
        holder.buttonUpdateBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewDialogAddUpdate viewDialogAddUpdate = new ViewDialogAddUpdate();
                viewDialogAddUpdate.showDialog(context, books.getBookID(), books.getTitulo(), books.getAuthor(), books.getEditor(), books.getGenero(), books.getOndeEnc());
            }
        });

        holder.buttonDeleteBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewDialogConfirmDelete viewDialogConfirmDelete = new ViewDialogConfirmDelete();
                viewDialogConfirmDelete.showDialog(context, books.getBookID());
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textBookName;
        TextView textAutor;
        TextView textEditora;
        TextView textGenero;
        TextView textOndeEnc;

        Button buttonDeleteBook;
        Button buttonUpdateBook;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textBookName = itemView.findViewById(R.id.textBookName);
            textAutor = itemView.findViewById(R.id.textAutor);
            textEditora = itemView.findViewById(R.id.textEditora);
            textGenero = itemView.findViewById(R.id.textGenero);
            textOndeEnc = itemView.findViewById(R.id.textOndeEnc);


            buttonDeleteBook = itemView.findViewById(R.id.buttonDeleteBook);
            buttonUpdateBook = itemView.findViewById(R.id.buttonUpdateBook);
        }
    }

    public class ViewDialogAddUpdate {
        public void showDialog(Context context, String bookId, String titulo, String autor, String editora, String genero, String ondeEnc) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_add_book);

            EditText textBookName = dialog.findViewById(R.id.textBookName);
            EditText textAutor = dialog.findViewById(R.id.textAutor);
            EditText textEditora = dialog.findViewById(R.id.textEditora);
            EditText textGenero = dialog.findViewById(R.id.textGenero);
            EditText textOndeEnc = dialog.findViewById(R.id.textOndeEnc);


            textBookName.setText(titulo);
            textAutor.setText(autor);
            textEditora.setText(editora);
            textGenero.setText(genero);
            textOndeEnc.setText(ondeEnc);

            Button buttonDialogAddBook = dialog.findViewById(R.id.buttonDialogAddBook);
            Button buttonDialogCancelBook = dialog.findViewById(R.id.buttonDialogCancelBook);

            buttonDialogAddBook.setText("Atualizado");
            buttonDialogCancelBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            buttonDialogAddBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String novoTitulo = textBookName.getText().toString();
                    String novoAutor = textAutor.getText().toString();
                    String novoEditora = textEditora.getText().toString();
                    String novoGenero = textGenero.getText().toString();
                    String novoOndeEnc = textOndeEnc.getText().toString();

                    if (novoTitulo.isEmpty() || novoAutor.isEmpty() || novoEditora.isEmpty()) {
                        Toast.makeText(context, "Preencha os campos obrigatórios (*)", Toast.LENGTH_SHORT).show();
                    } else {
                        if (novoTitulo.equals(titulo) && novoAutor.equals(autor) && novoEditora.equals(editora) && novoGenero.equals(genero) && novoOndeEnc.equals(ondeEnc)) {
                            Toast.makeText(context, "Sem informações para atualizar", Toast.LENGTH_SHORT).show();
                        } else {
                            databaseReference.child("LIVROS").child(bookId).setValue(new Book(bookId, titulo, autor, editora, genero, ondeEnc));
                            Toast.makeText(context, "Atualizado!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                    }
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

        public class ViewDialogConfirmDelete {
            public void showDialog(Context context, String bookId) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.view_dialog_confirm_delete);

                Button buttonConfirmDelete = dialog.findViewById(R.id.buttonConfirmDelete);
                Button buttonConfirmCancel = dialog.findViewById(R.id.buttonConfirmCancel);

                buttonConfirmCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                buttonConfirmDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseReference.child("LIVROS").child(bookId).removeValue();
                        Toast.makeText(context, "Livro Removido", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        }
    }

    public class ViewDialogConfirmDelete{
        public void showDialog(Context context, String bookId){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.view_dialog_confirm_delete);

            Button buttonConfirmDelete = dialog.findViewById(R.id.buttonConfirmDelete);
            Button buttonConfirmCancel = dialog.findViewById(R.id.buttonConfirmCancel);

            buttonConfirmCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            buttonConfirmDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    databaseReference.child("LIVROS").child(bookId).removeValue();
                    Toast.makeText(context, "Livro Removido", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }
}