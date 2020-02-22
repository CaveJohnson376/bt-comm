package dev.cj376.bluepoop;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
    BluetoothSocket bluetoothSocket;
    TextView textView;

    @SuppressLint("HandlerLeak")
    Handler getHandler(){
        return new Handler(){
            @Override
            public void handleMessage(Message msg){
                textView.append("\n" + new String((byte[])msg.obj));
            }
        };
    }

    private void connect() {
        try {
            BluetoothDevice device = bluetooth.getRemoteDevice(findViewById(R.id.editText).toString());
            Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            bluetoothSocket = (BluetoothSocket) m.invoke(device, 1);
            if (bluetoothSocket != null) {
                bluetoothSocket.connect();
            }
            Toast.makeText(MainActivity.this, "connect", Toast.LENGTH_SHORT).show();
            testsend();
            ConnectedThread connectedThread = new ConnectedThread(bluetoothSocket, this.getHandler());
            connectedThread.start();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | IOException e) {
            e.printStackTrace();
        }
    }

    public void testsend() {
        try {
            OutputStream os = bluetoothSocket.getOutputStream();
            os.write("hello".getBytes());
            Toast.makeText(MainActivity.this, "sent", Toast.LENGTH_SHORT).show();
            Log.d("bluetooth", "sent");
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "not sent", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        textView = findViewById(R.id.textView);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ;
                if (bluetooth != null) {
                    if (bluetooth.isEnabled()) {
                        String s = bluetooth.getAddress() + " : " + bluetooth.getName();

                        Snackbar.make(view, s, Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    } else {
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, 1);
                    }
                    connect();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
