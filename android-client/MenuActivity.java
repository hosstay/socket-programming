package com.taylorhoss.androidClient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author Taylor Hoss
 * Date: 11/15/2017
 **/

public class MenuActivity extends AppCompatActivity {
    Button databaseButton, itemButton, weightButton;
    EditText itemEditText, weightEditText;
    TextView responseTextView;
    String ip;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MenuActivity", "In Menu Acitivity");
        setContentView(R.layout.menu);
        databaseButton = (Button) findViewById(R.id.databaseButton);
        itemButton = (Button) findViewById(R.id.itemButton);
        weightButton = (Button) findViewById(R.id.weightButton);
        itemEditText = (EditText) findViewById(R.id.itemEditText);
        weightEditText = (EditText) findViewById(R.id.weightEditText);
        responseTextView = (TextView) findViewById(R.id.responseTextView);
        ip = getIntent().getExtras().getString("ip");

        databaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Client myClient = new Client(MenuActivity.this, responseTextView, databaseButton);

                myClient.execute(
                        ip,
                        "NULL",
                        "Database~");
            }
        });

        itemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Client myClient = new Client(MenuActivity.this, responseTextView, itemButton);

                if(Integer.parseInt(itemEditText.getText().toString()) > 0 && !itemEditText.getText().toString().equals("")) {
                    myClient.execute(
                            ip,
                            itemEditText.getText().toString(),
                            "Item~");
                }else{
                    responseTextView.setText("Enter a number above 0");
                }
            }
        });

        weightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Client myClient = new Client(MenuActivity.this, responseTextView, weightButton);

                if(Integer.parseInt(weightEditText.getText().toString()) > 0 && !weightEditText.getText().toString().equals("")) {
                    myClient.execute(
                            ip,
                            weightEditText.getText().toString(),
                            "Weight~");
                }else{
                    responseTextView.setText("Enter a number above 0");
                }
            }
        });
    }
}
