package edu.therealbranik.therealflower.homescreen.social.nearby;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.bluetooth.BluetoothConnectionService;
import edu.therealbranik.therealflower.user.FriendRequest;
import edu.therealbranik.therealflower.utility.Permissons;

/**
 * A simple {@link Fragment} subclass.
 */

public class NearbyFragment extends Fragment {

    private static final String TAG = "BluetoothConnectionServ";
    public static final String BROADCAST_ON_BLUETOOTH_STATE_CHANGE = ".homescreen.social.nearby.BROADCAST_ON_BLUETOOTH_STATE_CHANGE";
    private static int REQUEST_ENABLE_BT = 9305;

    private HashMap<Integer, BluetoothDevice> indexToDevice = new HashMap<>();
    private List<String> mListDevices = new ArrayList<>();

    FirebaseAuth mAuth;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBluetoothDevice;
    BluetoothConnectionService mBluetoothConnectionService;


    private Switch switchBluetooth;
    private Button buttonDiscoverable;
    private Button buttonStartDiscovery;
    private Button buttonConnect;
    private Button buttonSend;
    private ListView listViewAvailableDevices;


    public NearbyFragment() {
        // Required empty public constructor
    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListenerBluetooth = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            if (mBluetoothAdapter == null)
//                return;
//
//            if (isChecked) {
//                mBluetoothAdapter.enable();
//            } else {
//                mBluetoothAdapter.disable();
//            }
            enableDisableBT();
        }
    };

//    View.OnClickListener onClickListenerDiscoverable = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
//            startActivity(discoverableIntent);
//            btnEnableDisable_Discoverable();
//        }
//    };




//    View.OnClickListener onClickListenerStartDiscovery = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (mBluetoothAdapter != null) {
//                mBluetoothAdapter.startDiscovery();
//            }
//        }
//    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = (View) inflater.inflate(R.layout.fragment_nearby, container, false);

        mAuth = FirebaseAuth.getInstance();



        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothConnectionService = new BluetoothConnectionService(getContext());

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(mBroadcastReceiver4, filter);


        buttonConnect = (Button) rootView.findViewById(R.id.buttonConnect);
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConnection();
            }
        });
        buttonSend = (Button) rootView.findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendRequest friendRequest = new FriendRequest();
                friendRequest.id = mAuth.getCurrentUser().getUid();
                mBluetoothConnectionService.sendFriendRequest(friendRequest);
            }
        });


        switchBluetooth = (Switch) rootView.findViewById(R.id.switchBluetooth);
        buttonDiscoverable = (Button) rootView.findViewById(R.id.buttonDiscoverable);
        buttonDiscoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonDiscoverable();
            }
        });
        buttonStartDiscovery = (Button) rootView.findViewById(R.id.buttonStartDiscovery);
        buttonStartDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonStartDiscovery();
            }
        });
        listViewAvailableDevices = (ListView) rootView.findViewById(R.id.listViewAvailableDevices);
        switchBluetooth.setOnCheckedChangeListener(onCheckedChangeListenerBluetooth);
//        buttonStartDiscovery.setOnClickListener(onClickListenerStartDiscovery);
//        buttonDiscoverable.setOnClickListener(onClickListenerDiscoverable);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        getActivity().registerReceiver(receiverOnBluetoothStateChange, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
//        getActivity().registerReceiver(receiverBTActionFound, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {
//            if (!mBluetoothAdapter.isEnabled()) {
//                visibilityBluetooth(false);
//                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(intent, REQUEST_ENABLE_BT);
//            } else {
//                visibilityBluetooth(true);
//                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//
//
//                mListDevices.clear();
//                getBondedDevices();
//                refreshDevicesList();
//            }
        }

        listViewAvailableDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BluetoothDevice btd = indexToDevice.get(position);

                //first cancel discovery because its very memory intensive.
                mBluetoothAdapter.cancelDiscovery();

                Log.d(TAG, "onItemClick: You Clicked on a device.");
                String deviceName = btd.getName();
                String deviceAddress = btd.getAddress();

                Log.d(TAG, "onItemClick: deviceName = " + deviceName);
                Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

//                //create the bond.
//                //NOTE: Requires API 17+? I think this is JellyBean
//                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
//
//                }

                Log.d(TAG, "Trying to pair with " + deviceName);
                btd.createBond();

                mBluetoothDevice = btd;



//                if (btd == null) {
//                    Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                switch (btd.getBondState()) {
//                    case BluetoothDevice.BOND_BONDED:
//                        Toast.makeText(getActivity(), "BOND_BONDED", Toast.LENGTH_SHORT).show();
//                        ExecutorService executorService = Executors.newSingleThreadExecutor();
//                        executorService.submit(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(btd.getAddress());
//                                    mBluetoothSocket = device.createRfcommSocketToServiceRecord(ParcelUuid.fromString("0000112f-0000-1000-8000-00805f9b34fb").getUuid());
//                                    mBluetoothSocket.connect();
//                                    OutputStream outputStream = mBluetoothSocket.getOutputStream();
//                                    FriendRequest friendRequest = new FriendRequest();
//                                    friendRequest.username = "ASDASD";
//                                    friendRequest.name = "TESTER";
//                                    friendRequest.id = FirebaseAuth.getInstance().getCurrentUser().getUid();
//                                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
//                                    objectOutputStream.writeObject(friendRequest);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });


//                        return;
//                    case BluetoothDevice.BOND_BONDING:
//                        Toast.makeText(getActivity(), "BOND_BONDING", Toast.LENGTH_SHORT).show();
//                        return;
//                    case BluetoothDevice.BOND_NONE:
//                        Toast.makeText(getActivity(), "BOND_NONE", Toast.LENGTH_SHORT).show();
//                        return;
//                }

//                Toast.makeText(getActivity(), btd.getUuids()[0].getUuid().toString(), Toast.LENGTH_SHORT).show();
//                FirebaseUser user = mAuth.getCurrentUser();
//                FriendRequest friendRequest = new FriendRequest(user.getUid(), )

            }
        });



        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                //TODO
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //TODO
            }
        }

        //        super.onActivityResult(requestCode, resultCode, data);
    }



//    private void visibilityBluetooth (boolean on) {
//        if (on) {
//            switchBluetooth.setChecked(true);
//            buttonDiscoverable.setVisibility(View.VISIBLE);
//            buttonStartDiscovery.setVisibility(View.VISIBLE);
//            listViewAvailableDevices.setVisibility(View.VISIBLE);
//        } else {
//            switchBluetooth.setChecked(false);
//            buttonDiscoverable.setVisibility(View.GONE);
//            buttonStartDiscovery.setVisibility(View.GONE);
//            listViewAvailableDevices.setVisibility(View.GONE);
//        }
//    }

//    private final BroadcastReceiver receiverOnBluetoothStateChange = new BroadcastReceiver() {
//        public void onReceive (Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
//                if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
//                    visibilityBluetooth(false);
//                } else  if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
//                    clearDataStructures();
//                    getBondedDevices();
//                    refreshDevicesList();
//                    visibilityBluetooth(true);
//                }
//
//            }
//
//        }
//
//    };

//    private final BroadcastReceiver receiverBTActionFound = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                // Discovery has found a device. Get the BluetoothDevice
//                // object and its info from the Intent.
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address
//                Toast.makeText(getActivity(), deviceName, Toast.LENGTH_SHORT).show();
//                mListDevices.add(deviceName);
//                indexToDevice.put(mListDevices.size() - 1, device);
//                refreshDevicesList();
//            }
//        }
//    };

    @Override
    public void onDestroy() {
//        getActivity().unregisterReceiver(receiverOnBluetoothStateChange);
//        getActivity().unregisterReceiver(receiverBTActionFound);
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver1);
        getActivity().unregisterReceiver(mBroadcastReceiver2);
        getActivity().unregisterReceiver(mBroadcastReceiver3);
        getActivity().unregisterReceiver(mBroadcastReceiver4);
    }

    private void clearDataStructures () {
        mListDevices.clear();
        indexToDevice.clear();
    }

    private void refreshDevicesList () {
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, mListDevices);
        listViewAvailableDevices.setAdapter(itemsAdapter);
    }

    private void getBondedDevices () {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        int i = mListDevices.size();
        for(BluetoothDevice bt :  pairedDevices) {
            mListDevices.add(bt.getName() + " (paired)");
            indexToDevice.put(i, bt);
            i++;
        }
    }

    public void startBTConnection (BluetoothDevice device, UUID uuid) {
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");

        mBluetoothConnectionService.startClient(device, uuid);
    }

    //create method for starting connection
    //***remember the conncction will fail and app will crash if you haven't paired first
    public void startConnection(){
        startBTConnection(mBluetoothDevice, BluetoothConnectionService.MY_UUID_INSECURE);
    }



    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            getActivity().registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            getActivity().registerReceiver(mBroadcastReceiver1, BTIntent);
        }

    }


    public void buttonDiscoverable() {
        Log.d(TAG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        getActivity().registerReceiver(mBroadcastReceiver2,intentFilter);

    }

    public void buttonStartDiscovery() {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getActivity().registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            getActivity().registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            if (Permissons.Check_FINE_LOCATION(getActivity()) && Permissons.Check_COARSE_LOCATION(getActivity())) {
                Permissons.Request_FINE_LOCATION(getActivity(), 1001);
                Permissons.Request_COARSE_LOCATION(getActivity(), 1001);
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
//                        visibilityBluetooth(false);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
//                        visibilityBluetooth(true);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };




    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                indexToDevice.put(mListDevices.size(), device);
                mListDevices.add(device.getName());
//                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                refreshDevicesList();
            }
        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    //inside BroadcastReceiver4
                    mBluetoothDevice = mDevice;
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };

}
