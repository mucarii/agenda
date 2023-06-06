package com.example.agenda;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class TelaSMSRecebidos extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_READ_SMS = 1;
    private static final int PERMISSION_REQUEST_READ_CONTACTS = 2;

    private TextView textViewSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_sms_recebidos);

        textViewSMS = findViewById(R.id.textView_sms);

        // Verifique se a permissão READ_SMS já foi concedida
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            // A permissão já foi concedida, verifique a permissão READ_CONTACTS
            checkReadContactsPermission();
        } else {
            // A permissão não foi concedida, solicite a permissão ao usuário
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, PERMISSION_REQUEST_READ_SMS);
        }
    }

    private void checkReadContactsPermission() {
        // Verifique se a permissão READ_CONTACTS já foi concedida
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            // Ambas as permissões foram concedidas, chame o método para ler e exibir os SMS recebidos
            lerESExibirSMSRecebidos();
        } else {
            // A permissão READ_CONTACTS não foi concedida, solicite a permissão ao usuário
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // A permissão READ_SMS foi concedida, verifique a permissão READ_CONTACTS
                checkReadContactsPermission();
            } else {
                // A permissão READ_SMS foi negada, exiba uma mensagem ao usuário ou encerre a tela
                Toast.makeText(this, "A permissão para ler SMS foi negada.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == PERMISSION_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Ambas as permissões foram concedidas, chame o método para ler e exibir os SMS recebidos
                lerESExibirSMSRecebidos();
            } else {
                // A permissão READ_CONTACTS foi negada, exiba uma mensagem ao usuário ou encerre a tela
                Toast.makeText(this, "A permissão para ler contatos foi negada.", Toast.LENGTH_SHORT).show();
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


    private String getContactByNumber(String phoneNumber) {
        String contactName = phoneNumber; // Se o nome do contato não for encontrado, use o número como nome padrão

        // Consulta o banco de dados de contatos para obter o nome com base no número
        ContentResolver contentResolver = getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            cursor.close();
        }

        return contactName;
    }
}
