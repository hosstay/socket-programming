package com.taylorhoss.androidClient;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * @author Taylor Hoss
 * Date: 10/26/2017
 **/

public class MainActivity extends AppCompatActivity {

    EditText address;
    Button buttonConnect;
    short port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        address = (EditText) findViewById(R.id.addressEditText);
        buttonConnect = (Button) findViewById(R.id.connectButton);
        port = 8080;

        Log.i("MainActivity", Environment.getExternalStorageDirectory().getAbsolutePath());

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                intent.putExtra("ip", address.getText().toString());
                Log.i("MainActivity", "Starting MenuActivity");
                startActivity(intent);
            }
        });
    }
}
