package com.example.controledecontas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Pagas extends AppCompatActivity {

    private ArrayList<String> itemList;
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;
    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagas);

        //Toolbar
        Toolbar toolbar3 = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar3);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }



        ListView listView = findViewById(R.id.listView3);
        itemList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList);
        listView.setAdapter(adapter);


        //shared
        sharedPreferences = getSharedPreferences("PagasPrefs", MODE_PRIVATE);
        Set<String> contasPrefs = sharedPreferences.getStringSet("contas", new HashSet<>());
        itemList.addAll(contasPrefs);
        adapter.notifyDataSetChanged();
        registerForContextMenu(listView);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.icon_adicionar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_calendar) {
            showDialogToAddItem();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu_pagas, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        selectedPosition = info.position;

        if (item.getItemId() == R.id.edit_pagas) {
            showDialogToEditItem(selectedPosition);
            return true;
        } else if (item.getItemId() == R.id.delete_pagas) {
            showDeleteDialog(selectedPosition);
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }



    private void showDialogToAddItem() {
        showDialog(null, -1);
    }

    private void showDialogToEditItem(int position) {
        String item = itemList.get(position);
        showDialog(item, position);
    }

    private void showDialog(String existingItem, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_pagas, null);
        builder.setView(dialogView);

        EditText editTextNomeConta = dialogView.findViewById(R.id.editTextNomeConta);
        EditText editTextDataPagamento = dialogView.findViewById(R.id.editTextDataPagamento);
        Button buttonAdd = dialogView.findViewById(R.id.buttonAdd);

        if (existingItem != null) {
            String[] parts = existingItem.split(", Data: ");
            if (parts.length == 2) {
                editTextNomeConta.setText(parts[0].replace("Conta: ", ""));
                editTextDataPagamento.setText(parts[1]);
                buttonAdd.setText("Editar");
            }
        }

        AlertDialog dialog = builder.create();



        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomeConta = editTextNomeConta.getText().toString().trim();
                String dataPagamento = editTextDataPagamento.getText().toString().trim();

                if (!nomeConta.isEmpty() && !dataPagamento.isEmpty()) {
                    String newItem = "Conta: " + nomeConta + ", Data: " + dataPagamento;
                    if (position >= 0) {

                        itemList.set(position, newItem);
                        Toast.makeText(Pagas.this, "Pagamento editado", Toast.LENGTH_LONG).show();
                    } else {

                        itemList.add(newItem);
                        Toast.makeText(Pagas.this, "Pagamento adicionado", Toast.LENGTH_LONG).show();
                    }
                    adapter.notifyDataSetChanged();

                    //altera Shared
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Set<String> contasPrefs = new HashSet<>(itemList);
                    editor.putStringSet("contas", contasPrefs);
                    editor.apply();

                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Apagar pagamento?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        itemList.remove(position);
                        adapter.notifyDataSetChanged();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Set<String> contasPrefs = new HashSet<>(itemList);
                        editor.putStringSet("contas", contasPrefs);
                        editor.apply();

                        // Toast
                        Toast.makeText(Pagas.this, "Removido com sucesso", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }
}
