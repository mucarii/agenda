package com.example.agenda;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.agenda.R;

public class TelaSMSRecebidos extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_READ_SMS = 1;
    private TextView textViewSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_sms_recebidos);

        textViewSMS = findViewById(R.id.textView_sms);

        // Verifique se a permissão READ_SMS já foi concedida
        if (checkSelfPermission(android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            // A permissão já foi concedida, chame o método para ler e exibir os SMS recebidos
            lerESExibirSMSRecebidos();
        } else {
            // A permissão não foi concedida, solicite a permissão ao usuário
            requestPermissions(new String[]{android.Manifest.permission.READ_SMS}, PERMISSION_REQUEST_READ_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // A permissão foi concedida, chame o método para ler e exibir os SMS recebidos
                lerESExibirSMSRecebidos();
            } else {
                // A permissão foi negada, exiba uma mensagem ao usuário ou encerre a tela
                Toast.makeText(this, "A permissão para ler SMS foi negada.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void lerESExibirSMSRecebidos() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            StringBuilder smsBuilder = new StringBuilder();

            do {
                String address = cursor.getString(cursor.getColumnIndex("address"));
                String body = cursor.getString(cursor.getColumnIndex("body"));

                // Verifica se o número está salvo no banco de dados
                boolean isNumberSaved = BancoDados.isNumeroCadastrado(address, this);

                if (isNumberSaved) {
                    smsBuilder.append("From: ").append(address).append("\n");
                    smsBuilder.append("Message: ").append(body).append("\n\n");
                }
            } while (cursor.moveToNext());

            cursor.close();

            textViewSMS.setText(smsBuilder.toString());
        } else {
            textViewSMS.setText("Nenhum SMS recebido.");
        }
    }

}
