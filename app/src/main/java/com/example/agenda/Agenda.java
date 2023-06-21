package com.example.agenda;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Agenda extends AppCompatActivity {

    EditText et_instituicao, et_endereco, et_nome, et_telefone;
    Button btn_salvar, btn_consultar;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        et_nome = findViewById(R.id.et_nome);
        et_endereco = findViewById(R.id.et_endereco);
        et_instituicao = findViewById(R.id.et_instituicao);
        et_telefone = findViewById(R.id.et_telefone);
        btn_consultar = findViewById(R.id.btn_consultar);
        btn_salvar = findViewById(R.id.btn_salvar);


        BancoDados.abrirBanco(this);
        BancoDados.abrirOuCriarTabela(this);
        BancoDados.fecharDB();
    }



    public void inserirRegistro(View view) {
        String st_nome, st_fone, st_instituicao, st_endereco;
        st_nome = et_nome.getText().toString();
        st_fone = et_telefone.getText().toString();
        st_instituicao = et_instituicao.getText().toString();
        st_endereco = et_endereco.getText().toString();
        if (st_instituicao.equals("") || st_endereco.equals("") || st_nome.equals("") || st_fone.equals("")){
            CxMsg.mostrar("Campos não podem estar vazio",this);
            return;
        }

        BancoDados.inserirRegistro(st_instituicao,st_endereco,st_nome,st_fone, this);

        et_instituicao.setText(null);
        et_endereco.setText(null);
        et_telefone.setText("+55");
        et_nome.setText(null);
    }

    public void abrir_tela_consulta(View view) {

        Intent it_tela_conslta = new Intent(this, TelaConsulta.class);
        startActivity(it_tela_conslta);
    }



    public void fechar_tela(View view) {
        this.finish();
    }



}