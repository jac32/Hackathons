
package com.example.bluetooth.le;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;


public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private BluetoothGattService mBluetoothReadService;
    private BluetoothGattService mBluetoothSendService;
    private BluetoothGattCharacteristic mBluetoothNotify;
    private BluetoothGattCharacteristic mBluetoothWrite;

    private MipWebSocketServer mwss;


    private boolean connected = false;

    static final String MOVE_FWD = "forward_move";
    static final String MOVE_BWD = "backward_move";
    static final String TURN_LEFT = "left_turn";
    static final String TURN_RIGHT = "right_turn";
    static final String SPEAK = "say";
    static final String MOOD = "mood";

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_RECEIVE_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_SEND_SERVICE = UUID.fromString("0000ffe5-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_NOTIFY_CHAR = UUID.fromString("0000ffe4-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_WRITE_CHAR = UUID.fromString("0000ffe9-0000-1000-8000-00805f9b34fb");

    static final int SERVER_PORT = 4321;

    static TextToSpeech textSpeech;



    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to MIP.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);


            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {

        if (UUID_NOTIFY_CHAR.equals(characteristic.getUuid())) {

            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                Log.d(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    public boolean initialize() {

        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        try {
            mwss.stop();
            mwss = null;
        }
        catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is enables the notification in the descriptor
        if (UUID_NOTIFY_CHAR.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }

    public void setupMipControls(){
        Log.d(TAG, "Setting up controls");

        mBluetoothReadService = mBluetoothGatt.getService(UUID_RECEIVE_SERVICE);
        mBluetoothSendService = mBluetoothGatt.getService(UUID_SEND_SERVICE);

        mBluetoothNotify = mBluetoothReadService.getCharacteristic(UUID_NOTIFY_CHAR);
        mBluetoothWrite = mBluetoothSendService.getCharacteristic(UUID_WRITE_CHAR);

        Log.d(TAG, "Write format: " + mBluetoothWrite.getWriteType() + ", permissions: " + mBluetoothWrite.getPermissions() + ", properties: " + mBluetoothWrite.getProperties());

        setCharacteristicNotification(mBluetoothNotify, true);

        mwss = new MipWebSocketServer( SERVER_PORT );

        mwss.start();

        broadcastUpdate( ACTION_DATA_AVAILABLE );
        Log.d(TAG, "Controls set up.");
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    private class MipWebSocketServer extends WebSocketServer{

        final String TAG = MipWebSocketServer.class.getSimpleName();

        public MipWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        public MipWebSocketServer( int port ){
            super( new InetSocketAddress( port ) );
        }

        @Override
        public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
            Log.d(TAG, "New connection from: " + clientHandshake.getResourceDescriptor());
        }

        @Override
        public void onClose(WebSocket webSocket, int i, String s, boolean b) {
            Log.d(TAG, "Connection closed: " + webSocket);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onMessage(WebSocket webSocket, String s) {
            Log.d(TAG, "Message: " + s);

            String[] parts = s.split(" ");

            if(parts.length < 2){
                Log.e(TAG, "Incorrect amount of data received, skipping.");
                return;
            }
            else if(parts.length >= 2){

            byte[] msg = null;

            switch(parts[0]) {
                case MOVE_FWD:
                    Log.d(TAG, "Forward");
                    msg = new byte[3];
                    msg[0] = 0x71;  //move forward
                    msg[1] = 0x30;  //speed 10
                    msg[2] = (byte) Integer.parseInt(parts[1]); //time
                    break;
                case MOVE_BWD:
                    Log.d(TAG, "Backward");
                    msg = new byte[3];
                    msg[0] = 0x72;  //move backwd
                    msg[1] = 0x30;  //speed 10
                    msg[2] = (byte) Integer.parseInt(parts[1]); //time
                    break;
                case TURN_LEFT:
                    Log.d(TAG, "Left turn");
                    msg = new byte[3];
                    msg[0] = 0x73;  //turn left
                    msg[1] = (byte) Integer.parseInt(parts[1]); //turn angles (per 5 degrees)
                    msg[2] = 0x24; //speed 12
                    break;
                case TURN_RIGHT:
                    Log.d(TAG, "Right turn");
                    msg = new byte[3];
                    msg[0] = 0x74; //turn right
                    msg[1] = (byte) Integer.parseInt(parts[1]); //turn angles per 5 degrees
                    msg[2] = 0x24; //speed 12
                    break;
                case SPEAK:
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < parts.length; i++) {
                        sb.append(parts[i] + " ");
                    }
                    Log.d(TAG, sb.toString());
                    textSpeech.speak(sb.toString(), TextToSpeech.QUEUE_FLUSH, null, null);

                    break;
                case MOOD:
                    msg = new byte[4];
                    msg[0] = (byte) 132;

                    Log.d(TAG, "Mood" + parts[1]);
                    switch(Integer.parseInt(parts[1])){
                        case 5:
                            msg[1] = 0x00;
                            msg[2] = (byte) 255;
                            msg[3] = 0x00;
                            break;
                        case 4:
                            msg[1] = (byte) 119;
                            msg[2] = (byte) 255;
                            msg[3] = (byte) 119;
                            break;
                        case 3:
                            msg[1] = (byte) 255;
                            msg[2] = (byte) 255;
                            msg[3] = (byte) 255;
                            break;
                        case 2:
                            msg[1] = (byte) 255;
                            msg[2] = (byte) 119;
                            msg[3] = (byte) 119;
                            break;
                        case 1:
                            msg[1] = (byte) 255;
                            msg[2] = 0x00;
                            msg[3] = 0x00;
                            break;
                        default:
                            Log.e(TAG, "MOOD CHANGE ERR");
                            break;
                    }
                    break;
                default:

                    //If incoherent message, just burp
                    msg = new byte[2];

                    msg[0] = 0x06;
                    msg[1] = 0x03;
                    break;

            }
            if(mBluetoothWrite != null) {

                mBluetoothWrite.setValue(msg);

                boolean isWritten = mBluetoothGatt.writeCharacteristic(mBluetoothWrite);

                Log.d(TAG, "Write status to mip: " + isWritten);
            }
            else{
                Log.e(TAG, "write char was null");
            }
        }


    }

        @Override
        public void onError(WebSocket webSocket, Exception e) {
            Log.e(TAG, "Error" + e.getMessage());
        }
    }
}
