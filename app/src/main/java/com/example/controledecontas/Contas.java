package com.example.controledecontas;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;

public class Contas extends AppCompatActivity {

    private SQLiteDatabase bancoDados;
    private ListView listView1;
    private ArrayList<Integer> arrayIds;
    private ArrayList<String> linhas;
    private ArrayAdapter<String> meuAdapter;
    private Integer idSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contas);

        Toolbar toolbar1 = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar1);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        FloatingActionButton fab1 = findViewById(R.id.fab1);
        listView1 = findViewById(R.id.listView1);


        arrayIds = new ArrayList<>();
        linhas = new ArrayList<>();
        meuAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                linhas
        );
        listView1.setAdapter(meuAdapter);


        criarBancoDados();
        listarDados();
        registerForContextMenu(listView1);

        //botão fab
        fab1.setOnClickListener(view -> {
            Intent intent = new Intent(Contas.this, Cadastro.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_compartilhar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compartilhar_list) {
            compartilharDados();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
    private void compartilharDados() {
        if (!linhas.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String linha : linhas) {
                sb.append(linha).append("\n");
            }
            String message = "Dados das contas:\n\n" + sb.toString();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, "Compartilhar");
            startActivity(shareIntent);
        } else {
            Toast.makeText(this, "Nenhum dado para compartilhar.", Toast.LENGTH_SHORT).show();
        }
    }

    //on resume lista
    @Override
    protected void onResume() {
        super.onResume();
        listarDados();
    }

    private void criarBancoDados() {
        try {
            bancoDados = openOrCreateDatabase("contas_db", MODE_PRIVATE, null);
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS contas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome VARCHAR, " +
                    "preco INTEGER, " +
                    "dia_pagamento INTEGER, " +
                    "mes VARCHAR)");
            bancoDados.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listarDados() {
        try {
            arrayIds.clear();
            linhas.clear();

            bancoDados = openOrCreateDatabase("contas_db", MODE_PRIVATE, null);
            Cursor meuCursor = bancoDados.rawQuery("SELECT id, nome, preco, dia_pagamento, mes FROM contas", null);

            if (meuCursor.moveToFirst()) {
                do {
                    String item = meuCursor.getString(1) + " - " + meuCursor.getInt(2) + " - " + meuCursor.getInt(3) + "/" + meuCursor.getString(4);
                    linhas.add(item);
                    arrayIds.add(meuCursor.getInt(0));
                } while (meuCursor.moveToNext());
            }

            meuCursor.close();
            bancoDados.close();

            meuAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu_contas, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        idSelecionado = arrayIds.get(position);

        if (item.getItemId() == R.id.editar_conta) {
            showDialogToEditItem(position);
            return true;
        } else if (item.getItemId() == R.id.deletar_conta) {
            confirmaExcluir();
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }


    private void showDialogToEditItem(int position) {
        String item = linhas.get(position);
        String[] parts = item.split(" - ");
        String nomeConta = parts[0];
        String valorConta = parts[1];
        String dataPagamento = parts[2];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_contas, null);
        builder.setView(dialogView);

        EditText editTextNomeConta = dialogView.findViewById(R.id.editTextNomeConta);
        EditText editTextValorConta = dialogView.findViewById(R.id.editTextValorConta);
        EditText editTextDataPagamento = dialogView.findViewById(R.id.editTextDataPagamento);
        Button buttonAdd = dialogView.findViewById(R.id.buttonAdd);


        editTextNomeConta.setText(nomeConta);
        editTextValorConta.setText(valorConta);
        editTextDataPagamento.setText(dataPagamento);
        buttonAdd.setText("Editar");

        AlertDialog dialog = builder.create();

        buttonAdd.setOnClickListener(v -> {
            String nome = editTextNomeConta.getText().toString().trim();
            String valor = editTextValorConta.getText().toString().trim();
            String data = editTextDataPagamento.getText().toString().trim();

            if (!nome.isEmpty() && !valor.isEmpty() && !data.isEmpty()) {
                atualizarConta(idSelecionado, nome, valor, data);
                dialog.dismiss();
            } else {
                Toast.makeText(Contas.this, "Preencha todos os campos", Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    private void atualizarConta(int id, String nome, String valor, String data) {
        try {
            bancoDados = openOrCreateDatabase("contas_db", MODE_PRIVATE, null);
            String sql = "UPDATE contas SET nome = ?, preco = ?, dia_pagamento = ?, mes = ? WHERE id = ?";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindString(1, nome);
            stmt.bindString(2, valor);
            stmt.bindString(3, data.split("/")[0]); // Dia
            stmt.bindString(4, data.split("/")[1]); // Mês
            stmt.bindLong(5, id);
            stmt.executeUpdateDelete();
            bancoDados.close();
            listarDados();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmaExcluir() {
        AlertDialog.Builder msgBox = new AlertDialog.Builder(Contas.this);
        msgBox.setTitle("Excluir");
        msgBox.setMessage("Excluir conta?");
        msgBox.setPositiveButton("Sim", (dialogInterface, i) -> {
            excluir();
            listarDados();
        });
        msgBox.setNegativeButton("Não", (dialogInterface, i) -> {});
        msgBox.show();
    }

    private void excluir() {
        try {
            bancoDados = openOrCreateDatabase("contas_db", MODE_PRIVATE, null);
            String sql = "DELETE FROM contas WHERE id = ?";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindLong(1, idSelecionado);
            stmt.executeUpdateDelete();
            bancoDados.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
