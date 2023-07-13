package com.sasy.nontag;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    public static final String SERVICE_ID = "00001101-0000-1000-8000-00805f9b34fb"; //SPP UUID
    public String SERVICE_ADDRESS = "00:18:E4:35:20:DB"; // HC-05 BT ADDRESS
    public Button button;
    public Button btnset;
    public Button btnget;
    public Button btnsel;
    public Button btnthr;
    public Button btnscan;

    public Button btnGDATE;
    public Button btnGTIME;

    public Button btnSetrange;
    public Button btnGetrange;

    public Button btnsrid;
    public Button btngrid;

    public Button btnsynrtc;
    public Button btngrtc;

    public Button btndeviceinfo;
    public Button btnclearlog;
    public Button btnclose;

    public EditText editText;
    public TextView txtreceive;
    public TextView txtaval;
    public TextView txtpair;
    public BluetoothAdapter btAdapter;
    public BluetoothDevice btDevice;
    public BluetoothSocket btSocket = null;
    public ListView devicelist;
    private Context mContext;
    private ConnectedThread mConnectedThread;
    BluetoothDevice mBTDevice;
    private ListView lvscan;
    private ListView lv;
    private String btnname = "";


    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
//    public Device_adapter mDeviceListAdapter;

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
//            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                if (device.getBondState() != BluetoothDevice.BOND_BONDED  ) {
                mBTDevices.add(device);
                List<String> availbledev = new ArrayList<String>();
//                    String namelen = "";
//                    String namelen1 = "";
                for (BluetoothDevice bt : mBTDevices)
//                        namelen = bt.getName() ;
//                        namelen1 = btAdapter.getAddress();
                    availbledev.add((bt.getName() + "          ").substring(0, 10) + "   " + bt.getAddress());
                ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, availbledev);
                lvscan.setAdapter(arrayAdapter3);

//                }

//              ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, mBTDevices);
                //mDeviceListAdapter = new Device_adapter(context, R.layout.content_act_devicelist, mBTDevices);

//               lvscan.setAdapter(arrayAdapter1);

            }
        }
    };


    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
//                    showToast("Paired");
                    Toast.makeText(getApplicationContext(), "Paired", Toast.LENGTH_LONG).show();

                    Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

                    List<String> availbledev = new ArrayList<String>();
                    for (BluetoothDevice bt : pairedDevices)
                        availbledev.add((bt.getName() + "          ").substring(0, 10) + "   " + bt.getAddress()); //+ " | " + bt.getName()
                    ArrayAdapter<String> arrayAdapter5 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, availbledev);
                    lv.setAdapter(arrayAdapter5);

//                    BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.BOND_BONDING);
//                    if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
//                        mBTDevices.add(device);
//                        List<String> availbledev = new ArrayList<String>();
//                        for(BluetoothDevice bt : mBTDevices)
//                            availbledev.add(bt.getAddress()  ); //+ " | " + bt.getName()
//                        ArrayAdapter<String> arrayAdapter5 = new ArrayAdapter<String> (getApplicationContext(), android.R.layout.simple_list_item_1, availbledev);
//                        lv.setAdapter(arrayAdapter5);
//                    }


                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
//                    showToast("Unpaired");
                }

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        lv = (ListView) findViewById(R.id.listdeviceshow);
        lvscan = (ListView) findViewById(R.id.listdevicesava);
//      final LinearLayout linardevice = findViewById(R.id.linardevice);
        btnclose = (Button) findViewById(R.id.btnclose);
        final LinearLayout liner2 = findViewById(R.id.linre2);
//        final LinearLayout liner3 = findViewById(R.id.linre3);
        final LinearLayout liner2a = findViewById(R.id.liner2a);

        txtaval = (TextView) findViewById(R.id.textViewDev);
        txtpair = (TextView) findViewById(R.id.textViewPair);
        txtreceive = (TextView) findViewById(R.id.txtreceived);

        button = (Button) findViewById(R.id.button);
        // btnthr = (Button) findViewById(R.id.BTNTHR);
        btnset = (Button) findViewById(R.id.BTNSET);
        btnget = (Button) findViewById(R.id.BTNGET);
        btnscan = (Button) findViewById(R.id.BTNscan);

        btnGDATE = (Button) findViewById(R.id.BTNGETdt);
//        btnGTIME = (Button) findViewById(R.id.BTNGETTM);

        btndeviceinfo = (Button) findViewById(R.id.BtnDeviceinfo);
        btnclearlog = (Button) findViewById(R.id.BTNclear);
        editText = (EditText) findViewById(R.id.editText);
//
        btnGetrange = (Button) findViewById(R.id.BTNgetrange);
        btnSetrange = (Button) findViewById(R.id.BTNsetrange);

        btngrid = (Button) findViewById(R.id.BTNGETdt);
        btnsrid = (Button) findViewById(R.id.BTNTHR);

        btnsynrtc = (Button) findViewById(R.id.BTNinfo);
        btngrtc = (Button) findViewById(R.id.BTNGETTM);

        //BTNinfo

        button.setVisibility(View.GONE);
//        button.setTextColor(Color.parseColor("#FFFF00"));
        //button.setBackgroundColor(Color.parseColor("#000000"));

        btnset.setTextColor(Color.parseColor("#FFFF00"));
        //btnset.setBackgroundColor(Color.parseColor("#000000"));

        btnget.setTextColor(Color.parseColor("#FFFF00"));
        // btnthr.setTextColor(Color.parseColor("#FFFF00"));
        /// btnGTIME.setTextColor(Color.parseColor("#FFFF00"));
        // btnGDATE.setTextColor(Color.parseColor("#FFFF00"));

//       btngrid.setTextColor(Color.parseColor("#FFFF00"));
        btnGetrange.setTextColor(Color.parseColor("#FFFF00"));
        btnSetrange.setTextColor(Color.parseColor("#FFFF00"));
        btngrid.setTextColor(Color.parseColor("#FFFF00"));
        btnsynrtc.setTextColor(Color.parseColor("#FFFF00"));
        btngrtc.setTextColor(Color.parseColor("#FFFF00"));
        btnclearlog.setTextColor(Color.parseColor("#FFFF00"));
        btnclose.setTextColor(Color.parseColor("#FFFF00"));
        btnsrid.setTextColor(Color.parseColor("#FFFF00"));
        btnscan.setTextColor(Color.parseColor("#FFFF00"));

        btnscan.setVisibility(View.GONE);
        txtaval.setVisibility(View.GONE);
        lvscan.setVisibility(View.GONE);
        ///btnget.setBackgroundColor(Color.parseColor("#000000"));

//        btnclose.setTextColor(Color.parseColor("#FFFF00"));
        btndeviceinfo.setTextColor(Color.parseColor("#FFFF00"));
        //btnclose.setBackgroundColor(Color.parseColor("#000000"));

        editText.setTextColor(Color.parseColor("#FFFF00"));
        //editText.setBackgroundColor(Color.parseColor("#000000"));

        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        //check bluettoth is found
        txtreceive.setVisibility(View.GONE);

        btDevice = btAdapter.getRemoteDevice(SERVICE_ADDRESS);


        if (btAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth not available", Toast.LENGTH_LONG).show();
        } else {

            if (!btAdapter.isEnabled()) {

                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, 3);
            } else {

            }
        }

        IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mPairReceiver, intent);


        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        List<String> s = new ArrayList<String>();
        for (BluetoothDevice bt : pairedDevices)
//            s.add(  (bt.getName() + "                         ").substring(0,25)  + "   " + bt.getAddress()  ); //+ " | " + bt.getName()
            s.add(bt.getAddress() + "     " + (bt.getName() + "                         ").substring(0, 25)); //+ " | " + bt.getName()

//        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, s) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Cast the list view each item as text view
                TextView item = (TextView) super.getView(position, convertView, parent);

                // Set the typeface/font for the current item
                //item.setTypeface(mTypeface);

                // Set the list view item's text color
                item.setTextColor(Color.parseColor("#FFFF00"));

                // Set the item text style to bold
                item.setTypeface(item.getTypeface(), Typeface.BOLD);

                // Change the item text size
                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                // return the view
                return item;
            }
        };
//
        List<String> av = new ArrayList<String>();
//        for(BluetoothDevice bt : pairedDevices)
//            av.add(bt.getAddress()  ); //+ " | " + bt.getName()
//
//
        final ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, av) {
            //
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Cast the list view each item as text view
                TextView item = (TextView) super.getView(position, convertView, parent);

                // Set the typeface/font for the current item
                //item.setTypeface(mTypeface);

                // Set the list view item's text color
                item.setTextColor(Color.parseColor("#FFFF00"));

                // Set the item text style to bold
                item.setTypeface(item.getTypeface(), Typeface.BOLD);

                // Change the item text size
                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

                // return the view
                return item;
            }
        };

//
//        // DataBind ListView with items from ArrayAdapter
        lv.setAdapter(arrayAdapter);
        lvscan.setAdapter(arrayAdapter1);
        lv.requestFocus();

        //check bluetooth

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                String value = (String) adapter.getItemAtPosition(position);
                //String[] valuestr = value.split("|");
                String[] arrOfStr = value.split(" ");

//                btAdapter = BluetoothAdapter.getDefaultAdapter();
                //Toast.makeText(getApplicationContext(), valuestr[1], Toast.LENGTH_LONG).show();
                SERVICE_ADDRESS = arrOfStr[0];
//                Toast.makeText(getApplicationContext(), SERVICE_ADDRESS, Toast.LENGTH_LONG).show();

                btDevice = btAdapter.getRemoteDevice(SERVICE_ADDRESS);

//                if (btSocket != null) {
//                    Toast.makeText(getApplicationContext(), "Bluetooth not available", Toast.LENGTH_LONG).show();
//                }

                if (btAdapter == null) {
                    Toast.makeText(getApplicationContext(), "Device not available", Toast.LENGTH_LONG).show();
                } else {

                    if (!btAdapter.isEnabled()) {
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, 3);
                    } else {


                        new Thread() {
                            public void run() {
                                boolean fail = false;
                                btAdapter.cancelDiscovery();

//                              BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                                try {
                                    btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(SERVICE_ID));

                                } catch (IOException e) {
                                    Log.e("TEST", "Can't connect to service");

                                }

                                // Establish the Bluetooth socket connection.
                                try {
                                    btSocket.connect();
                                } catch (IOException e) {
                                    try {
                                        fail = true;
                                        btSocket.close();
                                        Log.e("TEST", "CONNECTION ERROR");
//                                      mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
//                                      .sendToTarget();
                                    } catch (IOException e2) {
                                        //insert code to deal with this
//                                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                //                                            if(fail == false) {
//                                            if (btSocket != null) {
//                                                mConnectedThread = new ConnectedThread(btSocket);
//                                                mConnectedThread.start();
//                                            }
//
////                                                mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
////                                                        .sendToTarget();
//                                            }
                            }
                        }.start();

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (btSocket != null) {
                            mConnectedThread = new ConnectedThread(btSocket);
                            mConnectedThread.start();
                            lv.setVisibility(View.GONE);
                            txtaval.setVisibility(View.GONE);
                            txtpair.setVisibility(View.GONE);
                            lvscan.setVisibility(View.GONE);
                            btnscan.setVisibility(View.GONE);

                            liner2.setVisibility(View.VISIBLE);
                            liner2a.setVisibility(View.VISIBLE);
//                            liner3.setVisibility(View.VISIBLE);

                            // linardevice.setVisibility (0);
                            Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();

                        } else {
//                            try{
//                                btSocket.close();
//                            } catch (IOException e2) {
//                            //insert code to deal with this
                            Toast.makeText(getApplicationContext(), "Error In Communication !", Toast.LENGTH_SHORT).show();
                            // }

                            // Toast.makeText(getApplicationContext(), "Erro In Communication !", Toast.LENGTH_LONG).show();
                        }
                    }
                }

            }
        });


        //lvscan
        lvscan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                String value = (String) adapter.getItemAtPosition(position);
                String[] arrOfStr = value.split(" ");
//                String[] valuestr= value.split("|");
//                if (valuestr.length > 1){
//                    Toast.makeText(getApplicationContext(), arrOfStr[0], Toast.LENGTH_LONG).show();
                SERVICE_ADDRESS = arrOfStr[3];
//                }

                btDevice = btAdapter.getRemoteDevice(SERVICE_ADDRESS);
                if (btSocket != null) {
                    Toast.makeText(getApplicationContext(), "Bluetooth not available", Toast.LENGTH_LONG).show();
                } else {

                    Method m = null;
                    try {
                        m = btDevice.getClass().getMethod("createBond", (Class[]) null);
                        m.invoke(btDevice, (Object[]) null);

                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                }


//              Toast.makeText(getApplicationContext(), SERVICE_ADDRESS, Toast.LENGTH_LONG).show();

                if (btAdapter == null) {
                    Toast.makeText(getApplicationContext(), "Device not available", Toast.LENGTH_LONG).show();
                } else {

                    if (!btAdapter.isEnabled()) {
//                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_BLUETOOTH_SETTINGS);
//                        startActivityForResult(enableIntent, 3);
                    } else {


                    }
                }

            }
        });


        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mBluetoothAdapter.cancelDiscovery();
                mConnectedThread.cancel();


                lv.setVisibility(View.VISIBLE);
                txtaval.setVisibility(View.VISIBLE);
                txtpair.setVisibility(View.VISIBLE);
                lvscan.setVisibility(View.VISIBLE);
                btnscan.setVisibility(View.VISIBLE);
                liner2.setVisibility(View.GONE);
                liner2a.setVisibility(View.GONE);

                //temp
                btnscan.setVisibility(View.GONE);
                txtaval.setVisibility(View.GONE);
                lvscan.setVisibility(View.GONE);
                //temp
//              liner3.setVisibility(View.GONE);
                btnname = "Close";


            }
        });


//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        btnget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSocket != null) {
                    txtreceive.setText("");
                    btnname = "GetDB";
//                    mConnectedThread = new ConnectedThread(btSocket);
                    mConnectedThread.write("GET DB" + "\r");
//                    SystemClock.sleep(200);


                    //popup
//                    LayoutInflater layoutInflater = (LayoutInflater) Act_devicelist.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    View customView = layoutInflater.inflate(R.layout.activity_popup,null);
//
//                    Button closePopupBtn = (Button) customView.findViewById(R.id.closePopupBtn);
//                    TextView Txtrec = (TextView) customView.findViewById(R.id.txtviewrec);
//
//                    Txtrec.setText("DB : " + txtreceive.getText() );
//                    //instantiate popup w indow
//                    final PopupWindow popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////                    final PopupWindow popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    //display the popup window
//                    popupWindow.showAtLocation( liner2 , Gravity.CENTER, 0, 0);
//
//
//                    //close the popup window on button click
//                    closePopupBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            popupWindow.dismiss();
//                        }
//                    });


                    SystemClock.sleep(100);

//                    Toast.makeText(getApplicationContext(), "DB : " + txtreceive.getText(), Toast.LENGTH_LONG).show();

                }
            }
        });


        btnsrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSocket != null) {
                    btnname = "SetRID";
                    txtreceive.setText("");
                    mConnectedThread.write("SET RID " + editText.getText().toString() + "\r");
                    SystemClock.sleep(100);
                    Toast.makeText(getApplicationContext(), "SET RID " + editText.getText().toString(), Toast.LENGTH_LONG).show();

                }
            }
        });

        btngrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSocket != null) {
                    txtreceive.setText("");
                    btnname = "GetRID";
                    mConnectedThread.write("GET SYSPARAM" + "\r");
//                    SystemClock.sleep(100);
//                    Toast.makeText(getApplicationContext(), "GET RID : " + txtreceive.getText(), Toast.LENGTH_LONG).show();

                }
            }
        });


        btngrtc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSocket != null) {
                    txtreceive.setText("");
                    btnname = "GetRTC";
                    mConnectedThread.write("GET RTC" + "\r");
//                    SystemClock.sleep(100);
//                    Toast.makeText(getApplicationContext(), "GET RTC : " + txtreceive.getText(), Toast.LENGTH_LONG).show();

                }
            }
        });


//        btngrtc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (btSocket != null) {
//                    txtreceive.setText("");
//                    mConnectedThread.write("GET TIME" + "\r" );
////                    SystemClock.sleep(1000);
////                    Toast.makeText(getApplicationContext(), "TIME : " + txtreceive.getText(), Toast.LENGTH_LONG).show();
//
//                }
//            }
//        });

        btndeviceinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSocket != null) {
                    txtreceive.setText("");
                    btnname = "GetSYS";
                    mConnectedThread.write("GET SYSPARAM" + "\r");
                    SystemClock.sleep(100);
//                    Toast.makeText(getApplicationContext(), "DATE : " + txtreceive.getText(), Toast.LENGTH_LONG).show();

                }
            }
        });


        btnclearlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSocket != null) {
                    txtreceive.setText("");
                    btnname = "Clear";
                    mConnectedThread.write("SET DEFLFILE" + "\r");
                    SystemClock.sleep(100);
                    Toast.makeText(getApplicationContext(), "Delete All : " + txtreceive.getText(), Toast.LENGTH_LONG).show();

                }
            }
        });

        btnGetrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSocket != null) {
                    txtreceive.setText("");
                    btnname = "GetXRANGE";
                    mConnectedThread.write("GET XRANGE" + "\r");
//                    SystemClock.sleep(100);
//                      Toast.makeText(getApplicationContext(), "GET XRANGE : " + txtreceive.getText(), Toast.LENGTH_LONG).show();

                }
            }
        });


        btnsynrtc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSocket != null) {
                    txtreceive.setText("");
                    btnname = "SetRTC";
//                    try {

                    //OutputStream out = btSocket.getOutputStream();
                    // InputStream In = btSocket.getInputStream();

//                    SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
//                    String currentDateandTime = sdf.format(new Date());
//                    //out.write(("SET ACT CAL " + currentDateandTime + "\r").getBytes());
//                    mConnectedThread.write("SET CAL " + currentDateandTime + "\r" );
//
//                    try {
//                        Thread.sleep(600);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                    SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMyyHHmm");
                    String currentDateandTime1 = sdf1.format(new Date());
                    //out.write(("SET TIME " +  currentDateandTime1 +"\r").getBytes());
                    mConnectedThread.write("SET RTC " + currentDateandTime1 + "\r");
                    //out.close();
                    SystemClock.sleep(100);
                    SimpleDateFormat strdate = new SimpleDateFormat("dd/MM/yy HH:mm");
                    String currentDateandTime3 = strdate.format(new Date());

                    Toast.makeText(getApplicationContext(), "SET RTC " + currentDateandTime3, Toast.LENGTH_LONG).show();


//                    }
//                    catch (IOException e) {
//                        Toast.makeText(getApplicationContext(), "Error Communication", Toast.LENGTH_LONG).show();
//                    }

                }
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSocket != null) {
                    txtreceive.setText("");
//                    btnname = "GetDB";
//                    try {
//                        OutputStream out = btSocket.getOutputStream();
//                        out.write((editText.getText().toString() + "\r").getBytes());
//
//                        out.close();

//                        mConnectedThread = new ConnectedThread(btSocket);
                    mConnectedThread.write(editText.getText().toString() + "\r");
//                    } catch (IOException e) {
//                        Toast.makeText(getApplicationContext(), "Error Communication", Toast.LENGTH_LONG).show();
//                    }

                }
            }
        });


        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Scanning Started", Toast.LENGTH_LONG).show();
                mBTDevices.clear();
                lvscan.setAdapter(null);
                txtreceive.setText("");
                mBluetoothAdapter.cancelDiscovery();
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
//                 Log.d(TAG, "btnDiscover: Canceling discovery.");
                    mBluetoothAdapter.startDiscovery();
                    IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
                }
                if (!mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.startDiscovery();
                    IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
                }

            }
        });


        btnset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSocket != null) {
                    txtreceive.setText("");
                    btnname = "SetDB";
//                    try {
//                        OutputStream out = btSocket.getOutputStream();
//                        out.write(( "SET DB " + editText.getText().toString() + "\r").getBytes());
//
//                        out.close();
//                    } catch (IOException e) {
//                        Toast.makeText(getApplicationContext(), "Error Communication", Toast.LENGTH_LONG).show();
//                    }
//                    mConnectedThread = new ConnectedThread(btSocket);
                    mConnectedThread.write("SET DB " + editText.getText().toString() + "\r");
                    SystemClock.sleep(100);
                    Toast.makeText(getApplicationContext(), "DB : " + editText.getText().toString(), Toast.LENGTH_LONG).show();

                }
            }
        });

        btnSetrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSocket != null) {
                    txtreceive.setText("");
                    btnname = "SetXRANGE";
//                    try {
//                        OutputStream out = btSocket.getOutputStream();
//                        out.write(( "SET DB " + editText.getText().toString() + "\r").getBytes());
//
//                        out.close();
//                    } catch (IOException e) {
//                        Toast.makeText(getApplicationContext(), "Error Communication", Toast.LENGTH_LONG).show();
//                    }
//                    mConnectedThread = new ConnectedThread(btSocket);
                    mConnectedThread.write("SET XRANGE " + editText.getText().toString() + "\r");
                    SystemClock.sleep(100);
                    Toast.makeText(getApplicationContext(), "Set Range : " + editText.getText().toString() + "m", Toast.LENGTH_LONG).show();

                }
            }
        });


    }


    /* It is called when an activity completes.*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //getActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>ActionBartitle </font>"));
        final ListView lv = (ListView) findViewById(R.id.listdeviceshow);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        List<String> s = new ArrayList<String>();
        for (BluetoothDevice bt : pairedDevices)
            s.add(bt.getAddress());
//
//        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, s);
//
//       // DataBind ListView with items from ArrayAdapter
        lv.setAdapter(arrayAdapter);
        lv.requestFocus();

    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
//            Toast.makeText(getApplicationContext(), "Class started", Toast.LENGTH_LONG).show();

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
//            mHandler.obtainMessage( 2,1,1)
//                    .sendToTarget();
//            txtreceive.setText("Check Data");
//            Toast.makeText(getApplicationContext(), "Start reading", Toast.LENGTH_LONG).show();
//            // Keep listening to the InputStream until an exception occurs
            while (true) {
//                Toast.makeText(getApplicationContext(), "Whileloop", Toast.LENGTH_LONG).show();
                try {
//                    // Read from the InputStream
                    bytes = mmInStream.available();


//                    mHandler.obtainMessage(2,  bytes, -1, buffer)
//                            .sendToTarget(); // Send the obtained bytes to the UI activity

//                    txtreceive.setText("Check Data");
                    if (bytes != 0) {
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read

//                        txtreceive.setText("");
//                        txtreceive.setText("dddate");
                        mHandler.obtainMessage(1, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity


                    }
                } catch (IOException e) {
                    e.printStackTrace();
//
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            byte[] writeBuf = (byte[]) msg.obj;
//            int begin = (int)msg.arg1;
//            int end = (int)msg.arg2;
//
//            switch(msg.what) {
//                case 1:
//                    String writeMessage = new String(writeBuf);
//                    writeMessage = writeMessage.substring(begin, end);
//                    Toast.makeText(getApplicationContext(), writeMessage, Toast.LENGTH_LONG).show();
//                    txtreceive.setText(writeMessage);
//
//                    break;
//            }
//        }
//    };

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                String readMessage = null;
                try {
                    readMessage = new String((byte[]) msg.obj, "UTF-8");

//                    Toast.makeText(getApplicationContext(), readMessage, Toast.LENGTH_LONG).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                txtreceive.setText("");
                String[] arrSplit_3 = readMessage.split("\\s");
//              readMessage.replace("OK","");
                if (arrSplit_3.length > 1) {

                    if (btnname == "GetXRANGE") {
                        txtreceive.setText("Range : " + arrSplit_3[0] + "m");
                        Toast.makeText(getApplicationContext(), "Range : " + arrSplit_3[0] + "m", Toast.LENGTH_LONG).show();
                    }
                    if (btnname == "GetRID") {
                        if (arrSplit_3[0].length() >= 15) {
                            txtreceive.setText("Get RID : " + arrSplit_3[0].substring(5, 15));
                            Toast.makeText(getApplicationContext(), "Get RID : " + arrSplit_3[0].substring(5, 15), Toast.LENGTH_LONG).show();
                        }

                    }

                    if (btnname == "GetDB") {
                        txtreceive.setText("DB : " + arrSplit_3[0]);
                        Toast.makeText(getApplicationContext(), "DB : " + arrSplit_3[0], Toast.LENGTH_LONG).show();
                    }
//                    if (  btnname == "SetDB"){
//                        txtreceive.setText("Set DB : " + arrSplit_3[0] );
//                        Toast.makeText(getApplicationContext(),  "Set DB : " + arrSplit_3[0] , Toast.LENGTH_LONG).show();
//                    }
                    if (btnname == "GetRTC") {

                        txtreceive.setText("Get RTC : " + arrSplit_3[0].substring(0, 2) + "/" + arrSplit_3[0].substring(2, 4) + "/" + arrSplit_3[0].substring(4, 6) + " " + arrSplit_3[0].substring(6, 8) + ":" + arrSplit_3[0].substring(8, 10) + ":" + arrSplit_3[0].substring(10, 12));
                        Toast.makeText(getApplicationContext(), "Get RTC : " + arrSplit_3[0].substring(0, 2) + "/" + arrSplit_3[0].substring(2, 4) + "/" + arrSplit_3[0].substring(4, 6) + " " + arrSplit_3[0].substring(6, 8) + ":" + arrSplit_3[0].substring(8, 10) + ":" + arrSplit_3[0].substring(10, 12), Toast.LENGTH_LONG).show();
                    }


//                    if (arrSplit_3[0].length() < 3 ){
////                        txtreceive.setText("DB : " + arrSplit_3[0]);
//                        if (arrSplit_3[0].equals("OK")) {
////                            Toast.makeText(getApplicationContext(),   arrSplit_3[0], Toast.LENGTH_LONG).show();
//                        }else{
////                            Toast.makeText(getApplicationContext(),  "DB : " + arrSplit_3[0], Toast.LENGTH_LONG).show();
//                        }
//
//                    }
//
//                    if (arrSplit_3[0].length() > 7){
//                        String[] arrSplit_4 = arrSplit_3[0].split(":");
////                        if (arrSplit_4.length > 2){
//                          txtreceive.setText("DateTime : " + arrSplit_3[0]);
//                            Toast.makeText(getApplicationContext(),  "DateTime : " + arrSplit_3[0], Toast.LENGTH_LONG).show();
////                        }
////                        String[] arrSplit_5 = arrSplit_3[0].split("/");
////                        if (arrSplit_5.length > 2) {
//////                            txtreceive.setText("Date : " + arrSplit_3[0]);
////                            Toast.makeText(getApplicationContext(),  "Date : " + arrSplit_3[0], Toast.LENGTH_LONG).show();
////                        }
//
//                    }

                    if (arrSplit_3[0] != "OK") {
                        txtreceive.setText(arrSplit_3[0]);
                    }


//                    Toast.makeText(getApplicationContext(),  txtreceive.getText(), Toast.LENGTH_LONG).show();

//                    LayoutInflater layoutInflater = (LayoutInflater) Act_devicelist.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    View customView = layoutInflater.inflate(R.layout.activity_popup,null);
//
//                    Button closePopupBtn = (Button) customView.findViewById(R.id.closePopupBtn);
//                    TextView Txtrec = (TextView) customView.findViewById(R.id.txtviewrec);
//
//                    Txtrec.setText( txtreceive.getText() );
//                    //instantiate popup w indow
//                    final PopupWindow popupWindow = new PopupWindow(customView,  500, 300);
////                    final PopupWindow popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    //display the popup window
////                    popupWindow.setHeight("1in");
////                    popupWindow.setWidth(ListPopupWindow.WRAP_CONTENT);
//
//                    popupWindow.setFocusable(true);
//                    popupWindow.showAtLocation( lv , Gravity.CENTER, 0, 0);
//
//                    //close the popup window on button click
//                    closePopupBtn.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            popupWindow.dismiss();
//                        }
//                    });


                    //poup
                } else {
                    txtreceive.setText(readMessage);
                }


            }

            if (msg.what == 2) {
                if (msg.arg1 == 1)
                    txtreceive.setText("Connected to Device: " + (String) (msg.obj));
                else
                    txtreceive.setText("Connection Failed");
            }
        }
    };


    public void onBackPressed() {

        finish();

    }


}
