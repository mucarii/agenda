package com.example.agenda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TelaConsulta extends AppCompatActivity {

    SQLiteDatabase db = null;
    Cursor cursor;

    EditText et_instiuicao, et_nome, et_endereco, et_fone;
    Button btn_prox, btn_voltar, btn_anterior, btn_lermsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_consulta);

        et_nome = findViewById(R.id.et_nome_consulta);
        et_instiuicao = findViewById(R.id.et_instituicao_consulta);
        et_endereco = findViewById(R.id.et_endereco_consulta);
        et_fone = findViewById(R.id.et_telefone_consulta);
        btn_anterior = findViewById(R.id.btn_anterior_consulta);
        btn_prox = findViewById(R.id.btn_prox_consulta);
        btn_voltar = findViewById(R.id.btn_voltar_consulta);
        btn_lermsg = findViewById(R.id.btn_lermsg);

        cursor = BancoDados.buscarDados(this);
        if (cursor.getCount() != 0) {
            mostrarDados();
        } else {
            CxMsg.mostrar("Nenhum registro encontrado", this);
        }

        // Adicione o clique de listener para cada entrada
        btn_prox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proximoRegistro();
            }
        });

        btn_anterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registroAnterior();
            }
        });

        btn_lermsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = et_fone.getText().toString();
                abrirTelaMensagens(phoneNumber);
            }
        });
    }

    public void fecharTela(View view) {
        finish();
    }

    public void abrirBanco() {
        try {
            db = openOrCreateDatabase("agendadb", MODE_PRIVATE, null);
        } catch (Exception ex) {
            CxMsg.mostrar("Erro ao abrir ou criar banco de dados", this);
        }
    }

    public void fecharDB() {
        if (db != null) {
            db.close();
        }
    }

    public void proximoRegistro() {
        if (cursor.moveToNext()) {
            mostrarDados();
        } else {
            CxMsg.mostrar("Não existem mais registros", this);
        }
    }

    public void registroAnterior() {
        if (cursor.moveToPrevious()) {
            mostrarDados();
        } else {
            CxMsg.mostrar("Não existem mais registros", this);
        }
    }

    public void mostrarDados() {
        String nome = cursor.getString(cursor.getColumnIndex("nome"));
        String fone = cursor.getString(cursor.getColumnIndex("fone"));

        String nomeContato = getNomeContatoByNumero(fone, this);
        if (nomeContato != null) {
            et_nome.setText(nomeContato);
        } else {
            et_nome.setText(nome);
        }

        et_instiuicao.setText(cursor.getString(cursor.getColumnIndex("instituicao")));
        et_endereco.setText(cursor.getString(cursor.getColumnIndex("endereco")));
        et_fone.setText(fone);
    }

    public void abrirTelaMensagens(String phoneNumber) {
        Intent intent = new Intent(this, TelaSMSRecebidos.class);
        intent.putExtra("phoneNumber", phoneNumber);
        startActivity(intent);
    }

    public void abrirTelaEnviar(View view) {
        String phoneNumber = et_fone.getText().toString();

        Intent intent = new Intent(this, smsSend.class);
        intent.putExtra("fone", phoneNumber);
        startActivity(intent);
    }

    public static String getNomeContatoByNumero(String phoneNumber, Context context) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = context.openOrCreateDatabase("agendadb", Context.MODE_PRIVATE, null);
            String[] projection = {"nome"};
            String selection = "fone = ?";
            String[] selectionArgs = {phoneNumber};
            cursor = db.query("contatos", projection, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex("nome"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return null;
    }
}
