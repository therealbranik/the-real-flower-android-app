package edu.therealbranik.therealflower.homescreen.social.nearby;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import edu.therealbranik.therealflower.R;
import edu.therealbranik.therealflower.homescreen.social.friend_request.FriendRequest;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragment extends Fragment {

    private HashMap<Integer, BluetoothDevice> indexToDevice = new HashMap<>();

    FirebaseAuth mAuth;
    BluetoothAdapter mBluetoothAdapter;

    ListView listViewAvailableDevices;


    public NearbyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = (View) inflater.inflate(R.layout.fragment_nearby, container, false);

        mAuth = FirebaseAuth.getInstance();

        listViewAvailableDevices = (ListView) rootView.findViewById(R.id.listViewAvailableDevices);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 1);
            } else {
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                List<String> s = new ArrayList<>();

                int i = 0;
                for(BluetoothDevice bt :  pairedDevices) {
                    s.add(bt.getName());
                    indexToDevice.put(i, bt);
                    i++;
                }

                ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, s);

                listViewAvailableDevices.setAdapter(itemsAdapter);

            }
        }

        listViewAvailableDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice btd = indexToDevice.get(position);
                Toast.makeText(getActivity(), btd.getUuids()[0].getUuid().toString(), Toast.LENGTH_SHORT).show();

                FirebaseUser user = mAuth.getCurrentUser();
//                FriendRequest friendRequest = new FriendRequest(user.getUid(), )

            }
        });



        return rootView;
    }


}
