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
import java.util.Date;

public class OperatorRecyclerAdapter extends RecyclerView.Adapter<OperatorRecyclerAdapter.ViewHolder>{

    Context context;
    ArrayList<Operator> operatorArrayList;
    DatabaseReference databaseReference;

    public OperatorRecyclerAdapter(Context context, ArrayList<Operator> operatorArrayList) {
        this.context = context;
        this.operatorArrayList = operatorArrayList;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.operator_item,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

      Operator operators = operatorArrayList.get(position);

      holder.textName.setText("Nome: " + operators.getNome());
      holder.textEmail.setText("Email: " + operators.getEmail());
      holder.textUser.setText("Usuário: " + operators.getUser());
      holder.textPassword.setText("Senha: " + operators.getPassword());

      holder.buttonUpdate.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              ViewDialogAddUpdate viewDialogAddUpdate = new ViewDialogAddUpdate();
              viewDialogAddUpdate.showDialog(context, operators.getId(), operators.getNome(),operators.getEmail(), operators.getUser(), operators.getPassword());
          }
      });

      holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              ViewDialogConfirmDelete viewDialogConfirmDelete = new ViewDialogConfirmDelete();
              viewDialogConfirmDelete.showDialog(context, operators.getId());
          }
      });
    }

    @Override
    public int getItemCount() {
        return operatorArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textName;
        TextView textEmail;
        TextView textUser;
        TextView textPassword;

        Button buttonDelete;
        Button buttonUpdate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.textName);
            textEmail = itemView.findViewById(R.id.textEmail);
            textUser = itemView.findViewById(R.id.textUser);
            textPassword = itemView.findViewById(R.id.textPassword);

            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonUpdate = itemView.findViewById(R.id.buttonUpdate);
        }
    }

    public class ViewDialogAddUpdate{
        public void showDialog(Context context, String id, String name, String email, String user, String password){
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_add_opr);

            EditText textName = dialog.findViewById(R.id.textAddName);
            EditText textEmail = dialog.findViewById(R.id.textAddEmail);
            EditText textUser = dialog.findViewById(R.id.textAddUser);
            EditText textPassword = dialog.findViewById(R.id.textAddPassword);

            textName.setText(name);
            textEmail.setText(email);
            textUser.setText(user);
            textPassword.setText(password);

            Button buttonUpdate = dialog.findViewById(R.id.buttonDialogAddOpr);
            Button buttonDialogCancelOpr = dialog.findViewById(R.id.buttonDialogCancelOpr);

            buttonUpdate.setText("Atualizar");
            buttonDialogCancelOpr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                dialog.dismiss();
                }
            });

            buttonUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newName = textName.getText().toString();
                    String newEmail = textEmail.getText().toString();
                    String newUser = textUser.getText().toString();
                    String newPassword = textPassword.getText().toString();

                    if(newName.isEmpty() && newEmail.isEmpty() && newUser.isEmpty() && newPassword.isEmpty()){
                        Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    } else {

                        if(newName.equals(name) && newEmail.equals(email) && newUser.equals(user) && newPassword.equals(password)){
                            Toast.makeText(context, "Sem informações para atualizar", Toast.LENGTH_SHORT).show();
                        }else{
                            databaseReference.child("USERS").child(id).setValue(new Operator(id, newName, newEmail, newUser, newPassword, false));
                            Toast.makeText(context, "Atualizado!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                    }
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }

    public class ViewDialogConfirmDelete{
        public void showDialog(Context context, String id){
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

                    databaseReference.child("USERS").child(id).removeValue();
                    Toast.makeText(context, "Operador Removido", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }
    }
}