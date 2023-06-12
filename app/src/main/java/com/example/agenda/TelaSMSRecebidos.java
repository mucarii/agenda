package com.example.agenda;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    private String phoneNumber;
    private int selectedMessagePosition; // Posição da mensagem selecionada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_sms_recebidos);

        textViewSMS = findViewById(R.id.textView_sms);

        // Obtém o número do telefone da intenção
        phoneNumber = getIntent().getStringExtra("phoneNumber");

        // Registrar o TextView para exibir o menu de contexto (opções de exclusão)
        registerForContextMenu(textViewSMS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Verifique as permissões necessárias
        checkPermissions();
    }

    private void checkPermissions() {
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

                if (address.equals(phoneNumber)) {
                    // Obtém o nome do contato com base no número
                    String contactName = getContactByNumber(address);

                    smsBuilder.append("From: ").append(contactName).append("\n");
                    smsBuilder.append("Message: ").append(body).append("\n\n");
                }
            } while (cursor.moveToNext());

            cursor.close();

            textViewSMS.setText(smsBuilder.toString());
        } else {
            textViewSMS.setText("Nenhum SMS recebido.");
        }

        // Definir um clique longo no TextView para exibir o menu de contexto
        textViewSMS.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Registrar a posição da mensagem selecionada
                selectedMessagePosition = textViewSMS.getSelectionStart();
                return false;
            }
        });
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_contextual, menu); // Inflar o layout do menu de contexto
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_excluir) {
            // Excluir a mensagem selecionada
            excluirMensagemSelecionada();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void excluirMensagemSelecionada() {
        String smsText = textViewSMS.getText().toString();
        int start = textViewSMS.getSelectionStart();
        int end = textViewSMS.getSelectionEnd();

        // Verificar se a posição da mensagem selecionada está dentro dos limites
        if (start >= 0 && end <= smsText.length()) {
            // Remover a mensagem selecionada do texto
            String updatedText = smsText.substring(0, start) + smsText.substring(end);

            // Atualizar o TextView com o texto modificado
            textViewSMS.setText(updatedText);

            // Exibir uma mensagem de confirmação
            Toast.makeText(this, "Mensagem excluída.", Toast.LENGTH_SHORT).show();
        }
    }
}
