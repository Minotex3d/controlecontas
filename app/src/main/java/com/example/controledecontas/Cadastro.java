package com.example.controledecontas;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

public class Cadastro extends AppCompatActivity {

    private SQLiteDatabase bancoDados;
    private EditText editTextNome, editTextPreco, editTextDiaPagamento, editTextMes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        editTextNome = findViewById(R.id.editTextNome);
        editTextPreco = findViewById(R.id.editTextPreco);
        editTextDiaPagamento = findViewById(R.id.editTextDiaPagamento);
        editTextMes = findViewById(R.id.editTextMes);

        Button botaoCadastrar = findViewById(R.id.buttonCadastrar);
        botaoCadastrar.setOnClickListener(v -> cadastrarConta(v));
    }

    private void cadastrarConta(View view) {
        try {
            bancoDados = openOrCreateDatabase("contas_db", MODE_PRIVATE, null);
            ContentValues valores = new ContentValues();
            valores.put("nome", editTextNome.getText().toString());
            valores.put("preco", Integer.parseInt(editTextPreco.getText().toString()));
            valores.put("dia_pagamento", Integer.parseInt(editTextDiaPagamento.getText().toString()));
            valores.put("mes", editTextMes.getText().toString());
            bancoDados.insert("contas", null, valores);
            bancoDados.close();

            //Snackbar
            Snackbar.make(view, "Conta cadastrada com sucesso!", Snackbar.LENGTH_LONG).show();
            new Handler().postDelayed(() -> finish(), 2000);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}