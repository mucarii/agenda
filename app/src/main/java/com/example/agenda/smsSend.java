package com.example.agenda;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class smsSend extends AppCompatActivity {

    EditText smsFone, smsMsg;
    Button btnEnviar, btn_reset, btn_statusc, btn_status, btn_voltar;

    private static final int REQUEST_SEND_SMS_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_send);

        // Initialize views
        smsFone = findViewById(R.id.smsFone);
        smsMsg = findViewById(R.id.smsMsg);
        btnEnviar = findViewById(R.id.btnEnviar);
        btn_reset = findViewById(R.id.btn_reset);
        btn_statusc = findViewById(R.id.btn_statussc);
        btn_status = findViewById(R.id.btn_status);
        btn_voltar = findViewById(R.id.btn_voltar_sms);

        // Get phone number from the previous activity and set it in the phone number EditText
        String phoneNumber = getIntent().getStringExtra("fone");
        smsFone.setText(phoneNumber);

        // Set click listeners for buttons
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarSMS();
            }
        });

        btn_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsMsg.setText("Status");
            }
        });
        btn_statusc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsMsg.setText("Statusc");
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smsMsg.setText("Reset");
            }
        });

        btn_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fechar_tela(v);
            }
        });

        // Check if the SEND_SMS permission has been granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // The SEND_SMS permission has not been granted. Request the permission from the user.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_SEND_SMS_PERMISSION);
        }
    }

    // Close the current activity
    public void fechar_tela(View view) {
        this.finish();
    }

    // Send the SMS
    private void enviarSMS() {
        String numero = smsFone.getText().toString();
        String mensagem = smsMsg.getText().toString();

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numero, null, mensagem, null, null);
            Toast.makeText(this, "SMS enviado com sucesso.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Falha ao enviar SMS.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SEND_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // SEND_SMS permission granted by the user
                Toast.makeText(this, "Permissão para enviar SMS concedida.", Toast.LENGTH_SHORT).show();
            } else {
                // SEND_SMS permission denied by the user
                Toast.makeText(this, "Permissão para enviar SMS negada.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
