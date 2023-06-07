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

    public class  BookRecyclerAdapter extends RecyclerView.Adapter<BookRecyclerAdapter.ViewHolder> {

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

        Book book = bookArrayList.get(position);

        holder.textBookNameItem.setText("Titulo: " + book.getTitulo());
        holder.textAutorItem.setText("Autor(a): " + book.getAuthor());
        holder.textEditoraItem.setText("Editora: " + book.getEditor());
        holder.textGeneroItem.setText("Gênero: " + book.getGenero());
        holder.textOndeEncItem.setText("Onde encontrar: " + book.getOndeEnc());
        holder.buttonUpdateBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewDialogAddUpdate viewDialogAddUpdate = new ViewDialogAddUpdate();
                viewDialogAddUpdate.showDialog(context, book.getBookID(), book.getTitulo(), book.getAuthor(), book.getEditor(), book.getGenero(), book.getOndeEnc());
            }
        });

        holder.buttonDeleteBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewDialogConfirmDelete viewDialogConfirmDelete = new ViewDialogConfirmDelete();
                viewDialogConfirmDelete.showDialog(context, book.getBookID());
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textBookNameItem;
        TextView textAutorItem;
        TextView textEditoraItem;
        TextView textGeneroItem;
        TextView textOndeEncItem;

        Button buttonDeleteBook;
        Button buttonUpdateBook;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textBookNameItem = itemView.findViewById(R.id.textBookNameItem);
            textAutorItem = itemView.findViewById(R.id.textAutorItem);
            textEditoraItem = itemView.findViewById(R.id.textEditoraItem);
            textGeneroItem = itemView.findViewById(R.id.textGeneroItem);
            textOndeEncItem = itemView.findViewById(R.id.textOndeEncItem);


            buttonDeleteBook = itemView.findViewById(R.id.buttonDeleteBook);
            buttonUpdateBook = itemView.findViewById(R.id.buttonUpdateBook);
        }
    }

    public class ViewDialogAddUpdate {
        public void showDialog(Context context, String bookId, String titulo, String autor, String editora, String genero, String ondeEnc) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_add_book);

            EditText textBookNameDialog = dialog.findViewById(R.id.textBookNameDialog);
            EditText textAutorDialog = dialog.findViewById(R.id.textAutorDialog);
            EditText textEditoraDialog = dialog.findViewById(R.id.textEditoraDialog);
            EditText textGeneroDialog = dialog.findViewById(R.id.textGeneroDialog);
            EditText textOndeEncDialog = dialog.findViewById(R.id.textOndeEncDialog);


            textBookNameDialog.setText(titulo);
            textAutorDialog.setText(autor);
            textEditoraDialog.setText(editora);
            textGeneroDialog.setText(genero);
            textOndeEncDialog.setText(ondeEnc);

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
                    String novoTitulo = textBookNameDialog.getText().toString();
                    String novoAutor = textAutorDialog.getText().toString();
                    String novoEditora = textEditoraDialog.getText().toString();
                    String novoGenero = textGeneroDialog.getText().toString();
                    String novoOndeEnc = textOndeEncDialog.getText().toString();

                    if (novoTitulo.isEmpty() || novoAutor.isEmpty() || novoEditora.isEmpty()) {
                        Toast.makeText(context, "Preencha os campos obrigatórios (*)", Toast.LENGTH_SHORT).show();
                    } else {
                        if (novoTitulo.equals(titulo) && novoAutor.equals(autor) && novoEditora.equals(editora) && novoGenero.equals(genero) && novoOndeEnc.equals(ondeEnc)) {
                            Toast.makeText(context, "Sem informações para atualizar", Toast.LENGTH_SHORT).show();
                        } else {
                            databaseReference.child("LIVROS").child(bookId).setValue(new Book(bookId, novoTitulo, novoAutor, novoAutor, novoGenero, novoOndeEnc));
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