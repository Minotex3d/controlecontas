package com.example.controledecontas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Anotacoes extends AppCompatActivity {

    private ArrayList<String> anotacoesList;
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;
    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anotacoes);

        // Toolbar
        Toolbar toolbar4 = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar4);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // Fim da toolbar

        FloatingActionButton fab4 = findViewById(R.id.fab4);

        ListView listView4 = findViewById(R.id.listView4);
        anotacoesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, anotacoesList);
        listView4.setAdapter(adapter);

        //SharedPreferences
        sharedPreferences = getSharedPreferences("AnotacoesPrefs", MODE_PRIVATE);
        Set<String> anotacoesPrefs = sharedPreferences.getStringSet("anotacoes", new HashSet<>());
        anotacoesList.addAll(anotacoesPrefs);
        adapter.notifyDataSetChanged();
        registerForContextMenu(listView4);




        // botão floating
        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogToAddAnotacao();
            }
        });
        }

    //menu contexto
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu4, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        selectedPosition = info.position;

        if (item.getItemId() == R.id.edit4) {
            showDialogToEditAnotacao(selectedPosition);
            return true;
        } else if (item.getItemId() == R.id.delete4) {
            showDeleteDialog(selectedPosition);
            return true;
        }

        return super.onContextItemSelected(item);
    }


    //Dialog
    private void showDialogToAddAnotacao() {
        showDialog(null, -1);
    }

    private void showDialogToEditAnotacao(int position) {
        String anotacao = anotacoesList.get(position);
        showDialog(anotacao, position);
    }

    // Dialog adicionar/editar
    private void showDialog(String existingAnotacao, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_anotacoes, null);
        builder.setView(dialogView);

        EditText editTextAnotacao = dialogView.findViewById(R.id.editTextAnotacao);
        Button buttonAdd = dialogView.findViewById(R.id.buttonAdd);

        if (existingAnotacao != null) {
            editTextAnotacao.setText(existingAnotacao);
            buttonAdd.setText("Editar");
        }

        AlertDialog dialog = builder.create();
        buttonAdd.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String anotacao = editTextAnotacao.getText().toString().trim();

                if (!anotacao.isEmpty()) {
                    if (position >= 0) {

                        anotacoesList.set(position, anotacao);
                        Toast.makeText(Anotacoes.this, "Anotação editada", Toast.LENGTH_LONG).show();
                    } else {

                        anotacoesList.add(anotacao);
                        Toast.makeText(Anotacoes.this, "Anotação adicionada", Toast.LENGTH_LONG).show();
                    }
                    adapter.notifyDataSetChanged();

                    //Salvar
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Set<String> anotacoesPrefs = new HashSet<>(anotacoesList);
                    editor.putStringSet("anotacoes", anotacoesPrefs);
                    editor.apply();

                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Apagar anotação")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        anotacoesList.remove(position);
                        adapter.notifyDataSetChanged();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Set<String> anotacoesPrefs = new HashSet<>(anotacoesList);
                        editor.putStringSet("anotacoes", anotacoesPrefs);
                        editor.apply();

                        // Toast
                        Toast.makeText(Anotacoes.this, "Anotação removida", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calendario, menu);
        return true;
    }


}
