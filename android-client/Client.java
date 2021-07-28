package com.taylorhoss.androidClient;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author Taylor Hoss
 * Date: 10/26/2017
 *
 * Base client code courtesy of:
 * http://androidsrc.net/android-client-server-using-sockets-client-implementation/
 *
 * Base file transfer code courtesy of:
 * http://android-er.blogspot.com/2015/01/file-transfer-via-socket-between.html
 */

public class Client extends AsyncTask<String, String, String> {

    Activity activity;
    Button button;
    TextView textResponse;
    IOException ioException;
    UnknownHostException unknownHostException;
    private static final String TAG = "HardwareClient";
    String port = "8080";

    Client(Activity activity, TextView textView, Button button) {
        super();
        this.activity = activity;
        this.textResponse = textView;
        this.button = button;
        this.ioException = null;
        this.unknownHostException = null;
    }

    @Override
    protected String doInBackground(String... params) {

        Socket socket = null;
        StringBuilder sb = new StringBuilder();
        String request = "";

        try {
            Log.i(TAG, "creating socket...");
            socket = new Socket(params[0], Integer.parseInt(port));

            if(params[2].compareTo("Item~") == 0 || params[2].compareTo("Weight~") == 0) {
                request = params[1] + " " + params[2];

                // send request through socket
                Log.i(TAG, "sending request through socket...");
                Log.i(TAG, "string sent: " + request);
                OutputStream out = socket.getOutputStream();
                out.write(request.getBytes());
                out.flush();

                // Create byte stream to dump read bytes into
                InputStream in = socket.getInputStream();

                int byteRead = 0;

                // Read from input stream. Note: inputStream.read() will block
                // if no data return
                Log.i(TAG, "Reading in response from socket...");
                while (byteRead != -1) {
                    byteRead = in.read();
                    if (byteRead == 126) {
                        byteRead = -1;
                    } else {
                        sb.append((char) byteRead);
                    }
                }
            }else if(params[2].compareTo("Database~") == 0) {
                request = "0 " + params[2];

                // send request through socket
                Log.i(TAG, "sending request through socket...");
                Log.i(TAG, "string sent: " + request);
                OutputStream out = socket.getOutputStream();
                out.write(request.getBytes());
                out.flush();

                //open file
                writeFileWrapper();
                File file = new File(Environment.getExternalStorageDirectory(), "database.txt");
                //will need to increase size of byte array if information exceeds 1024 bytes
                byte[] bytes = new byte[1024];
                InputStream in = socket.getInputStream();
                FileOutputStream fOut = new FileOutputStream(file);
                BufferedOutputStream bOut = new BufferedOutputStream(fOut);

                //read in from the socket input stream and write to file output stream
                int bytesRead = in.read(bytes, 0, bytes.length);
                bOut.write(bytes, 0, bytesRead);
                bOut.close();

                sb.append("Database received in /storage/sdcard");
            }

        } catch (UnknownHostException e) {
            this.unknownHostException = e;
            return "Error: unknownHostException";
        } catch (IOException e) {
            this.ioException = e;
            Log.i(TAG, "IOException...");
            return "Error: ioException";
        } finally {
            if (socket != null) {
                try {
                    Log.i(TAG, "closing socket");
                    socket.close();
                } catch (IOException e) {
                    this.ioException = e;
                    Log.i(TAG, "IOException when closing socket...");
                    return "Error: ioException";
                }
            }
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        if (this.ioException != null) {
            new AlertDialog.Builder(this.activity)
                    .setTitle("An error occurred")
                    .setMessage(this.ioException.toString())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else if (this.unknownHostException != null) {
            new AlertDialog.Builder(this.activity)
                    .setTitle("An error occurred")
                    .setMessage(this.unknownHostException.toString())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            Log.i(TAG, "Setting response text...");
            this.textResponse.setText(result);
        }
        super.onPostExecute(result);
    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 1;

    private void writeFileWrapper() {
        int hasStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
    }



}
