package com.ford.svl.connectedwiper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import com.openxc.VehicleManager;
import com.openxc.measurements.EngineSpeed;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.UnrecognizedMeasurementTypeException;
import com.openxc.measurements.WindshieldWiperStatus;
import com.openxc.remote.VehicleServiceException;
import com.openxc.units.Boolean;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.Build;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

public class MainActivity extends Activity {
	static final String TAG = "Main Activity";
	TextView status;
	TextView btStatus;
	Switch bt;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket socket;
    BluetoothDevice device;
    OutputStream outputStream;
    InputStream inputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
	private VehicleManager mVehicleManager;
	TextView wiperStatus;
	String filename = "wiper_data";
    FileManager fileManager = new FileManager("wiper_data");
    boolean checked = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.fragment_main);
        status = (TextView) findViewById(R.id.status);
        wiperStatus = (TextView) findViewById(R.id.wiper);
    	btStatus = (TextView) findViewById(R.id.btStatus);
    	bt = (Switch)  findViewById(R.id.bluetooth); 

    	
    	bt.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    	    	if(isChecked) {
    	    			checked = isChecked;
    	    			 try {
    	                     connectBluetooth(); //connect to bluetooth device
    	                     startBluetooth(); //start recieving data from device
    	                     Log.i(TAG, "BT connection");
    	                 } catch (Exception ex) { }
    	             } else {
    	            	 try {
    	            		 disconnectBluetooth(); //disconnect from bluetooth
                	
    	            	 	}
    	            	 catch (IOException ex) { }
    	            }
    	    	}
    	});
        
    	
        	
    }
    
    @Override
    public void onResume() { 
    	super.onResume();
    	/*if(mVehicleManager == null) {
			bindService(new Intent(this, VehicleManager.class), mConnection, Context.BIND_AUTO_CREATE);
		}*/
    	if(mVehicleManager == null) {
            Intent intent = new Intent(this, VehicleManager.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	
    	/*if(mVehicleManager != null) {
    		Log.i("openxc", "Unbinding from VehicleManager");
    		unbindService(mConnection);
    		mVehicleManager = null;
    	}*/
    	
        if(mVehicleManager != null) {
            Log.i(TAG, "Unbinding from Vehicle Manager");
            try {
                // Remember to remove your listeners, in typical Android
                // fashion.
                mVehicleManager.removeListener(WindshieldWiperStatus.class, wiperListener);
            } catch (VehicleServiceException e) {
                e.printStackTrace();
            }
            unbindService(mConnection);
            mVehicleManager = null;
        }
    }
    
    @Override 
    public void onDestroy(){
    	super.onDestroy();
    	if(mVehicleManager != null) {
    		Log.i("openxc", "Unbinding from VehicleManager");
    		unbindService(mConnection);
    		mVehicleManager = null;
    	}
    }
    
    
    /*Connects to bluetooth device
     * 
     * Checks to see if android device is BT compatible
     * Enables Bluetooth on android device
     * Connects to a previously paired device with the specified name
     * 
     */
    void connectBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
       
        //Check to see if device is bluetooth capable
        if(mBluetoothAdapter == null) {
            btStatus.setText("Bluetooth Status: No bluetooth adapter available");
        }
        
        //Enable bluetooth if it is not already enabled
        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }
        
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
        	
            for(BluetoothDevice device : pairedDevices) {
            	
                if(device.getName().equals("RNBT-6DEC")) //This is the name of the bluetooth device you want to connect to 
                {
                    this.device = device;
                    break;
                }
            }
        }
        btStatus.setText("Bluetooth Status: Device paired, not connected");
    	if(bt.isChecked()) {
    		bt.setChecked(!checked);
    	};

    }
    
    
    /*Starts bluetooth connection
     * 
     * Creates a bluetooth socket, then connects to the device specified using that socket.
     * Starts recieving data
     * 
     */
    @SuppressLint("NewApi") void startBluetooth() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        socket = device.createRfcommSocketToServiceRecord(uuid); //Create the socket 
        Log.i(TAG, "Socket: " + socket);
        socket.connect();
        Log.i(TAG, "Connected? " + socket.isConnected());
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
        
        recieveData();
        
        
        btStatus.setText("Bluetooth Status: Bluetooth Opened");
    }
    
    /*Handles received data
     * 
     * Fills a buffer with the incoming bytes from the arduino, convert the bytes into a string
     * Set the textview above the input box to display the incoming data
     * 
     */
    void recieveData()
    {
        final Handler handler = new Handler(); 
        final byte delimiter = 10; //This is the ASCII code for a newline character
        
        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024]; //size of the buffer
        workerThread = new Thread(new Runnable()  {
            public void run() {  
            // sniff for data being exchanged on a seperate thread
               while(!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = inputStream.available();                        
                        if(bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inputStream.read(packetBytes); //read the incoming bytes
                            for(int i=0;i<bytesAvailable;i++) {
                                byte b = packetBytes[i];
                                if(b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition]; //save the recieved data in an array
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length); 
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    fileManager.writeToFile(data+"\n");
                                    readBufferPosition = 0;
                                    
                                    handler.post(new Runnable() {
                                        public void run() {
                                            status.setText("Wiper Status: " +data); //display data
                                            Log.i(TAG, data);
                                        }
                                    });
                                }
                                else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
               }
            }
        });
        workerThread.start();
    }
    
    /*Sends data over bluetooth
     * 
     * Converts the message from the input box into a string then into a byte
     * Puts the bytes in a buffer
     * Sends the data over the output stream
     * 
     
    void sendData() throws IOException {
    	String msg = infoBox.getText().toString(); //Get the input from the box
        Log.i(TAG, "text form box");
        msg += "\n";
        Log.i(TAG, msg);
        byte [] buffer = msg.getBytes(); //make the message into bytes to send 
        Log.i(TAG, "data " + outputStream);
        outputStream.write(buffer); //send the data
        Log.i(TAG, "data sent ");
        status.setText("Data Sent");
    }
    */
    
    
    /*Closes all of the connections and disconnect from the device
     * 
     * Stops the worker thread that is sniffing for data
     * Closes the I/O streams and the socket
     *      
     */
    void disconnectBluetooth() throws IOException {
        stopWorker = true; 
        if(socket.isConnected()) {
	        outputStream.close();
	        inputStream.close();
	        socket.close();
       }
        btStatus.setText("Bluetooth Status: Bluetooth Closed");
    }
    
    WindshieldWiperStatus.Listener wiperListener = new WindshieldWiperStatus.Listener() {
	    boolean speed  = false;
        public void receive(Measurement measurement) {
            // When we receive a new wiper status value from the car, we want to
            // update the UI to display the new value. First we cast the generic
            // Measurement back to the type we know it to be, an WindshieldWiperStatus.
            final WindshieldWiperStatus wiper = (WindshieldWiperStatus) measurement;
            speed = wiper.getValue().booleanValue();
            
            // In order to modify the UI, we have to make sure the code is
            // running on the "UI thread" - Google around for this, it's an
            // important concept in Android.
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    // Finally, we've got a new value and we're running on the
                    // UI thread - we set the text of the WindshieldWiperStatus view to
                    // the latest value
                	if(mVehicleManager != null) {
                		wiperStatus.setText("Wiper Status:"  + speed);
                	}
                }
            });
        }
    };
    
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the VehicleManager service is established, i.e. bound.
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i("openxc", "Bound to VehicleManager");
            // When the VehicleManager starts up, we store a reference to it
            // here in "mVehicleManager" so we can call functions on it
            // elsewhere in our code.
            mVehicleManager = ((VehicleManager.VehicleBinder) service).getService();
            
            // We want to receive updates whenever the GearPosition and EngineSpeed changes.
            try {
                mVehicleManager.addListener(WindshieldWiperStatus.class, wiperListener);
                
            } catch (VehicleServiceException e) {
                e.printStackTrace();
            } catch (UnrecognizedMeasurementTypeException e) {
                e.printStackTrace();
            }
        }
        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.w("openxc", "VehicleManager Service disconnected unexpectedly");
            mVehicleManager = null;
        }
    };
}