package com.example.agenda;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

    EditText et_instiuicao,et_nome,et_endereco,et_fone;
    Button btn_prox, btn_voltar, btn_anterior, btn_lermsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_consulta);

        et_nome=findViewById(R.id.et_nome_consulta);
        et_instiuicao=findViewById(R.id.et_instituicao_consulta);
        et_endereco=findViewById(R.id.et_endereco_consulta);
        et_fone=findViewById(R.id.et_telefone_consulta);
        btn_anterior=findViewById(R.id.btn_anterior_consulta);
        btn_prox=findViewById(R.id.btn_prox_consulta);
        btn_voltar=findViewById(R.id.btn_voltar_consulta);
        btn_lermsg=findViewById(R.id.btn_lermsg);

        cursor=BancoDados.buscarDados(this);
        if (cursor.getCount() != 0) {
            mostarDados();
        } else {
            CxMsg.mostrar("Nenhum registro encontrado", this);
        }
    }

    public void fechar_tela(View view){
        this.finish();
    }

    public void abrirBanco() {
        try {
            db = openOrCreateDatabase("agendadb", MODE_PRIVATE, null);
        } catch (Exception ex) {
            CxMsg.mostrar("Erro ao abrir ou criar banco de dados",this);
        }
    }

    public void fecharDB() {
        db.close();
    }


    public void proximoRegistro(View view) {
        try {
            cursor.moveToNext();
            mostarDados();
        }catch(Exception ex){
            if(cursor.isAfterLast()){
                CxMsg.mostrar("não existem mais registros",this);
            }else {
                CxMsg.mostrar("erro ao navegar pelos registros",this);
            }
        }
    }

    public void registroAnterior(View view){
        try {
            cursor.moveToPrevious();
            mostarDados();
        }catch(Exception ex){
        if(cursor.isBeforeFirst()){
            CxMsg.mostrar("não existem mais registros",this);
        }else {
            CxMsg.mostrar("erro ao navegar pelos registros",this);
        }
    }
    }


    @SuppressLint("Range")
    public void mostarDados(){
        et_instiuicao.setText(cursor.getString(cursor.getColumnIndex("instituicao")));
        et_endereco.setText(cursor.getString(cursor.getColumnIndex("endereco")));
        et_nome.setText(cursor.getString(cursor.getColumnIndex("nome")));
        et_fone.setText(cursor.getString(cursor.getColumnIndex("fone")));
    }


    public void abrir_tela_mesagem(View view) {

        Intent it_tela_sms_recebidos = new Intent(this, TelaSMSRecebidos.class);
        startActivity(it_tela_sms_recebidos);
    }

    public void abrir_tela_enviar(View view) {

        Intent it_tela_sms_send = new Intent(this, smsSend.class);
        startActivity(it_tela_sms_send);
    }



}