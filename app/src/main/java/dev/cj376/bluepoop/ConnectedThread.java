package dev.cj376.bluepoop;

import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;

public class ConnectedThread extends Thread {
    private Handler handler;
    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;

        while (true) {
            try {
                bytes = inputStream.read(buffer);
                Message message = this.handler.obtainMessage(1, bytes, 0, buffer.clone());
                this.handler.sendMessage(message);
                Arrays.fill(buffer, (byte)0);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void write(byte[] msg) {
        try {
            outputStream.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConnectedThread(BluetoothSocket socket1, Handler handler1){
        handler = handler1;
        socket = socket1;
        try{
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
