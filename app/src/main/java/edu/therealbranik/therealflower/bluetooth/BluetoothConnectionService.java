package edu.therealbranik.therealflower.bluetooth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.user.FriendRequest;
import edu.therealbranik.therealflower.user.FriendRequestReceiver;

public class BluetoothConnectionService {

    private static final String TAG = "BluetoothConnectionServ";
    private static final String appName = "MYAPP";
    public static final UUID MY_UUID_INSECURE = UUID.fromString("b19bdea7-27f6-4625-995f-64dc9c20225c");
//    public static final UUID MY_UUID_INSECURE = ParcelUuid.fromString("0000112f-0000-1000-8000-00805f9b34fb").getUuid();

    //    private static final UUID  = UUID.fromString("edd5ee16-3fb5-4572-8440-e562414f2d2d");

    private  Context mContext;
    private final BluetoothAdapter mBluetoothAdapter;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    private AlertDialog mAlertDialog;


    public BluetoothConnectionService(Context ctx) {
        this.mContext = ctx;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(false); // if you want user to wait for some process to finish,
        builder.setView(R.layout.layout_loading_dialog);
        mAlertDialog = builder.create();
        start();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmBluetoothServerSocket;

        public AcceptThread () {
            BluetoothServerSocket tmp = null;

            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(appName, MY_UUID_INSECURE);
                Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
            }

            mmBluetoothServerSocket = tmp;
        }

        public void run () {
            BluetoothSocket socket = null;

            try {
                Log.d(TAG, "run: RFCOM server socket start.....");
                socket = mmBluetoothServerSocket.accept();
                Log.d(TAG, "run: RFCOM server socket accepted connection.");
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage() );
            }

            if (socket != null) {
                connected(socket, mmDevice);
            }

            Log.i(TAG, "END mAcceptThread ");
        }

        public void cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.");
            try {
                mmBluetoothServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage() );
            }
        }
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmBluetoothSocket;


        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
            deviceUUID = uuid;
        }

        public void run () {
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN mConnectThread ");

            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: " +MY_UUID_INSECURE );
                tmp = mmDevice.createRfcommSocketToServiceRecord(deviceUUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }

            mmBluetoothSocket = tmp;

            mBluetoothAdapter.cancelDiscovery();

            try {
                mmBluetoothSocket.connect();
                Log.d(TAG, "run: ConnectThread connected.");
            } catch (IOException e) {
                try {
                    mmBluetoothSocket.close();
                    Log.d(TAG, "run: Socket closed.");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + MY_UUID_INSECURE );
            }

            connected(mmBluetoothSocket, mmDevice);
        }

        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmBluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
            }
        }
    }


    public synchronized void start () {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }

    public void startClient(BluetoothDevice device,UUID uuid){
        Log.d(TAG, "startClient: Started.");

        //initprogress dialog
//        mProgressDialog = ProgressDialog.show(mContext,"Connecting Bluetooth"
//                ,"Please Wait...",true);
        mAlertDialog.show();

        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmBluetoothSocket;
        private final InputStream mmInputStream;
        private final OutputStream mmOutputStream;

        public ConnectedThread (BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: starting.");
            mmBluetoothSocket = socket;
            InputStream tmpInputStream = null;
            OutputStream tmpOutputStream = null;

            mAlertDialog.dismiss();

            try {
                tmpInputStream = mmBluetoothSocket.getInputStream();
                tmpOutputStream = mmBluetoothSocket.getOutputStream();
            } catch (IOException e) {

            }

            mmInputStream = tmpInputStream;
            mmOutputStream = tmpOutputStream;
        }

        public void run () {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            while (true) {
//                try {
//                    if (mmInputStream.available() > 0) {
                        try {
                            ObjectInputStream objectInputStream = new ObjectInputStream(mmInputStream);
//                            bytes = mmInputStream.read(buffer);
                            FriendRequest friendRequest = (FriendRequest) objectInputStream.readObject();
                            Intent i = new Intent(FriendRequestReceiver.BROADCAST_FRIEND_REQUEST);
                            i.putExtra("id", friendRequest.id);
                            mContext.sendBroadcast(i);
//                            String incomingMessage = new String(buffer, 0, bytes);
                            Log.d(TAG, "InputStream: " + "Friend req from: " + friendRequest.id);

                        } catch (IOException e) {
                            Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage() );
                            break;
                        } catch (ClassNotFoundException e) {
                            Log.e(TAG, "write: Error reading Input Stream Objext. " + e.getMessage() );
                        }
//                    }
//                    else SystemClock.sleep(100);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

            }
        }

        //Call this from the main activity to send data to the remote device
        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                mmOutputStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage() );
            }
        }

        public void sendFriendRequest (FriendRequest friendRequest) {
            Log.d(TAG, "write: Sending friend request: " + friendRequest.id);
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(mmOutputStream);
                objectOutputStream.writeObject(friendRequest);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage() );
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmBluetoothSocket.close();
            } catch (IOException e) { }
        }
    }

    private void connected(BluetoothSocket mmBluetoothSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Starting.");

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmBluetoothSocket);
        mConnectedThread.start();
    }

    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;

        // Synchronize a copy of the ConnectedThread
        Log.d(TAG, "write: Write Called.");
        //perform the write
        mConnectedThread.write(out);
    }

    public void sendFriendRequest (FriendRequest friendRequest) {
        // Synchronize a copy of the ConnectedThread
        Log.d(TAG, "write: sendFriendRequest Called.");
        //perform the write
        mConnectedThread.sendFriendRequest(friendRequest);
    }
}
