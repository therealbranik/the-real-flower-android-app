package edu.therealbranik.therealflower.homescreen.social.nearby;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
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

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.user.FriendRequest;
import edu.therealbranik.therealflower.user.FriendRequestService;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragment extends Fragment {


    public static final String BROADCAST_ON_BLUETOOTH_STATE_CHANGE = ".homescreen.social.nearby.BROADCAST_ON_BLUETOOTH_STATE_CHANGE";
    private static int REQUEST_ENABLE_BT = 9305;

    private HashMap<Integer, BluetoothDevice> indexToDevice = new HashMap<>();
    private List<String> mListDevices = new ArrayList<>();

    FirebaseAuth mAuth;
    BluetoothAdapter mBluetoothAdapter;

    BluetoothSocket mBluetoothSocket;


    private Switch switchBluetooth;
    private Button buttonDiscoverable;
    private Button buttonStartDiscovery;
    private ListView listViewAvailableDevices;


    public NearbyFragment() {
        // Required empty public constructor
    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListenerBluetooth = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (mBluetoothAdapter == null)
                return;

            if (isChecked) {
                mBluetoothAdapter.enable();
            } else {
                mBluetoothAdapter.disable();
            }
        }
    };

    View.OnClickListener onClickListenerDiscoverable = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    };




    View.OnClickListener onClickListenerStartDiscovery = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.startDiscovery();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = (View) inflater.inflate(R.layout.fragment_nearby, container, false);

        mAuth = FirebaseAuth.getInstance();

        BluetoothConfiguration config = new BluetoothConfiguration();
        config.context = getContext();
        config.bluetoothServiceClass = BluetoothClassicService .class;
        config.bufferSize = 1024;
        config.characterDelimiter = '\n';
        config.deviceName = "Your App Name";
        config.callListenersInMainThread = true;

        config.uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Required

        BluetoothService.init(config);

        BluetoothService service = BluetoothService.getDefaultInstance();
        service.setOnEventCallback(new BluetoothService.OnBluetoothEventCallback() {
            @Override
            public void onDataRead(byte[] buffer, int length) {
                Toast.makeText(getActivity(),"READ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChange(BluetoothStatus status) {
                Toast.makeText(getActivity(),"STATUS CHANGED", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeviceName(String deviceName) {
                Toast.makeText(getActivity(),deviceName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onToast(String message) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDataWrite(byte[] buffer) {
                Toast.makeText(getActivity(),"WRITE", Toast.LENGTH_SHORT).show();
            }
        });


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        switchBluetooth = (Switch) rootView.findViewById(R.id.switchBluetooth);
        buttonDiscoverable = (Button) rootView.findViewById(R.id.buttonDiscoverable);
        buttonStartDiscovery = (Button) rootView.findViewById(R.id.buttonStartDiscovery);
        listViewAvailableDevices = (ListView) rootView.findViewById(R.id.listViewAvailableDevices);
        switchBluetooth.setOnCheckedChangeListener(onCheckedChangeListenerBluetooth);
        buttonStartDiscovery.setOnClickListener(onClickListenerStartDiscovery);
        buttonDiscoverable.setOnClickListener(onClickListenerDiscoverable);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        getActivity().registerReceiver(receiverOnBluetoothStateChange, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        getActivity().registerReceiver(receiverBTActionFound, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                visibilityBluetooth(false);
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLE_BT);
            } else {
                visibilityBluetooth(true);
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();


                mListDevices.clear();
                getBondedDevices();
                refreshDevicesList();
            }
        }

        listViewAvailableDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BluetoothDevice btd = indexToDevice.get(position);

                if (btd == null) {
                    Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
                    return;
                }

                switch (btd.getBondState()) {
                    case BluetoothDevice.BOND_BONDED:
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



                        service.connect(mBluetoothAdapter.getRemoteDevice(btd.getAddress())); // See also service.disconnect();

                        return;
                    case BluetoothDevice.BOND_BONDING:
                        Toast.makeText(getActivity(), "BOND_BONDING", Toast.LENGTH_SHORT).show();
                        return;
                    case BluetoothDevice.BOND_NONE:
                        Toast.makeText(getActivity(), "BOND_NONE", Toast.LENGTH_SHORT).show();
                        return;
                }

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



    private void visibilityBluetooth (boolean on) {
        if (on) {
            switchBluetooth.setChecked(true);
            buttonDiscoverable.setVisibility(View.VISIBLE);
            buttonStartDiscovery.setVisibility(View.VISIBLE);
            listViewAvailableDevices.setVisibility(View.VISIBLE);
        } else {
            switchBluetooth.setChecked(false);
            buttonDiscoverable.setVisibility(View.GONE);
            buttonStartDiscovery.setVisibility(View.GONE);
            listViewAvailableDevices.setVisibility(View.GONE);
        }
    }

    private final BroadcastReceiver receiverOnBluetoothStateChange = new BroadcastReceiver() {
        public void onReceive (Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                    visibilityBluetooth(false);
                } else  if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                    mListDevices.clear();
                    getBondedDevices();
                    refreshDevicesList();
                    visibilityBluetooth(true);
                }

            }

        }

    };

    private final BroadcastReceiver receiverBTActionFound = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Toast.makeText(getActivity(), deviceName, Toast.LENGTH_SHORT).show();
                mListDevices.add(deviceName);
                indexToDevice.put(mListDevices.size() - 1, device);
                refreshDevicesList();
            }
        }
    };

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiverOnBluetoothStateChange);
        getActivity().unregisterReceiver(receiverBTActionFound);
        super.onDestroy();
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


}
