package com.example.agenda;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static android.content.Context.MODE_PRIVATE;

public class BancoDados {

    static SQLiteDatabase db = null;
    static Cursor cursor;

    public static void abrirBanco(Activity act) {
        try {
            ContextWrapper cw = new ContextWrapper(act);
            db = cw.openOrCreateDatabase("agendadb", MODE_PRIVATE, null);
        } catch (Exception ex) {
            CxMsg.mostrar("Erro ao abrir ou criar banco de dados", act);
        }
    }

    public static void fecharDB() {
        db.close();
    }

    public static void abrirOuCriarTabela(Activity act) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS contatosbd (id INTEGER PRIMARY KEY, instituicao TEXT, endereco TEXT, nome TEXT, fone TEXT);");
        } catch (Exception ex) {
            CxMsg.mostrar("Erro ao criar tabela", act);
        }
    }

    public static void inserirRegistro(String instituicao, String endereco, String nome, String fone, Activity act) {
        abrirBanco(act);
        try {
            db.execSQL("INSERT INTO contatosbd (instituicao, endereco, nome, fone) VALUES ('" + instituicao + "','" + endereco + "','" + nome + "','" + fone + "')");
        } catch (Exception ex) {
            CxMsg.mostrar("Erro ao inserir Registro", act);
        } finally {
            CxMsg.mostrar("Registrado com sucesso", act);
        }
        fecharDB();
    }

    public static Cursor buscarDados(Activity act) {
        abrirBanco(act);
        cursor = db.query("contatosbd",
                new String[]{"instituicao", "endereco", "nome", "fone"},
                null,
                null,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        return cursor;
    }

    public static Cursor buscarMensagensPorNumero(String fone, Activity act) {
        abrirBanco(act);
        cursor = db.query("mensagens",
                new String[]{"mensagem"},
                "fone = ?",
                new String[]{fone},
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        return cursor;
    }

    public static boolean isNumeroCadastrado(String fone, Activity act) {
        abrirBanco(act);

        Cursor cursor = db.query("contatosbd",
                new String[]{"id"},
                "fone = ?",
                new String[]{fone},
                null,
                null,
                null,
                null
        );

        boolean isCadastrado = cursor != null && cursor.moveToFirst();

        if (cursor != null) {
            cursor.close();
        }

        fecharDB();

        return isCadastrado;
    }


    public static Cursor buscarMensagensPorContato(String contato, Activity act) {
        abrirBanco(act);

        // Verifica se o contato está salvo no banco de dados
        Cursor cursorContato = db.query("contatosbd",
                new String[]{"instituicao", "endereco", "nome", "fone"},
                "fone = ?",
                new String[]{contato},
                null,
                null,
                null,
                null
        );

        if (cursorContato.moveToFirst()) {
            // O contato está salvo no banco de dados, então busca suas mensagens
            cursor = db.query("mensagens",
                    new String[]{"mensagem"},
                    "contato = ?",
                    new String[]{contato},
                    null,
                    null,
                    null,
                    null
            );
        } else {
            // O contato não está salvo no banco de dados, retorna um cursor vazio
            cursor = null;
        }

        // Fecha o banco de dados e retorna o cursor com as mensagens (ou null)
        fecharDB();
        return cursor;
    }

    public static Cursor buscarMensagensDeContatos(Context context) {
        abrirBanco((Activity) context);

        String query = "SELECT m.sender, m.message " +
                "FROM SMSMessages m " +
                "JOIN Contatos c ON m.sender = c.fone";

        return db.rawQuery(query, null);
    }


}