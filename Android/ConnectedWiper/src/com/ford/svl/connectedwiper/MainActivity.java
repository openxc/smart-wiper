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
import android.text.format.Time;
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
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.os.Build;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;

public class MainActivity extends Activity {
	static final String TAG = "Main Activity";
	TextView status;
	TextView btStatus;
	TextView heavyRain;
	TextView heavyHour;
	TextView lightRain;
	TextView lightHour;
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
    FileManager user_record = new FileManager("user_record");
    FileManager record = new FileManager("daily_record");
    boolean checked = false;
    Button clearButton;
    Button recordButton;
    int h_hrs = 0;
    int h_min = 0;
    int h_sec = 0;
    int l_hrs = 0;
    int l_min = 0;
    int l_sec = 0;
    public static final String pref = "record";
    Time rightnow;
    int time_record = 0;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.fragment_main);
        status = (TextView) findViewById(R.id.status);
        wiperStatus = (TextView) findViewById(R.id.wiper);
    	btStatus = (TextView) findViewById(R.id.btStatus);
    	bt = (Switch)  findViewById(R.id.bluetooth); 
    	clearButton = (Button) findViewById(R.id.clearButton);
    	recordButton = (Button) findViewById(R.id.recordButton);
    	heavyRain = (TextView) findViewById(R.id.heavyRain);
    	heavyHour = (TextView) findViewById(R.id.heavyHour);
    	lightRain = (TextView) findViewById(R.id.lightRain);
    	lightHour = (TextView) findViewById(R.id.lightHour);    	
    	SharedPreferences settings = getSharedPreferences(pref, 0);
    	h_hrs = settings.getInt("h_hrs", 0);
    	h_min = settings.getInt("h_min", 0);
    	h_sec = settings.getInt("h_sec", 0);
    	l_hrs = settings.getInt("l_hrs", 0);
    	l_min = settings.getInt("l_min", 0);
    	l_sec = settings.getInt("l_sec", 0); 
    	time_record = settings.getInt("time_record", 0);
    	String heavy_display = String.valueOf(h_hrs) + "h " + String.valueOf(h_min) + "m " + String.valueOf(h_sec) + "s";
    	String light_display = String.valueOf(l_hrs) + "h " + String.valueOf(l_min) + "m " + String.valueOf(l_sec) + "s";
		heavyHour.setText(heavy_display);
		lightHour.setText(light_display);
		
		timeAdjustment();
		   	
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
        
    	recordButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {    			
    			rightnow = new Time(Time.getCurrentTimezone());
    			rightnow.setToNow();
    			String dailyRecord =  rightnow.toString() + "   Heavy rain "+ String.valueOf(h_hrs) + "h " + String.valueOf(h_min) 
            			+ "m " + String.valueOf(h_sec) + "s, " + "Light rain " + String.valueOf(l_hrs) + "h " 
    					+ String.valueOf(l_min) + "m " + String.valueOf(l_sec) + "s";
    			user_record.writeToFile(dailyRecord +"\n");
    		}
    	});
    	
    	clearButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			h_hrs = 0;
    			h_min = 0;
    			h_sec = 0;
    			l_hrs = 0;
    			l_min = 0;
    			l_sec = 0;
    			SharedPreferences settings = getSharedPreferences(pref, 0);
    			SharedPreferences.Editor editor = settings.edit();
    			editor.putInt("h_hrs", h_hrs).commit();
    			editor.putInt("h_min", h_min).commit();
    			editor.putInt("h_sec", h_sec).commit();
    			editor.putInt("l_hrs", l_hrs).commit();
    			editor.putInt("l_min", l_min).commit();
    			editor.putInt("l_sec", l_sec).commit();
    			String heavy_display = String.valueOf(h_hrs) + "h " + String.valueOf(h_min) 
    					+ "m " + String.valueOf(h_sec) + "s";
    			String light_display = String.valueOf(l_hrs) + "h " + String.valueOf(l_min) 
    					+ "m " + String.valueOf(l_sec) + "s";
    			heavyHour.setText(heavy_display);
    			lightHour.setText(light_display);
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
    
    /* Clear the current raining time record every 24 hours
     * 
     * at 0:00 the raining time record will be cleared and recorded in logs.
     * 
     */
    void timeAdjustment() {
    	rightnow = new Time(Time.getCurrentTimezone());
		rightnow.setToNow();
		if (rightnow.monthDay != time_record) {
			String dailyRecord =  String.valueOf(rightnow.year) + "/" + String.valueOf(rightnow.month) + "/"
					+ String.valueOf(time_record) + "   Heavy rain "+ String.valueOf(h_hrs) + "h " + String.valueOf(h_min) 
        			+ "m " + String.valueOf(h_sec) + "s, " + "Light rain " + String.valueOf(l_hrs) + "h " 
					+ String.valueOf(l_min) + "m " + String.valueOf(l_sec) + "s";
			record.writeToFile(dailyRecord + "\n");
			h_hrs = 0;
        	h_min = 0;
        	h_sec = 0;
        	l_hrs = 0;
        	l_min = 0;
        	l_sec = 0;
        	time_record = rightnow.monthDay;
        	SharedPreferences settings = getSharedPreferences(pref, 0);
     		SharedPreferences.Editor editor = settings.edit();
     		editor.putInt("h_hrs", h_hrs).commit();
     		editor.putInt("h_min", h_min).commit();
     		editor.putInt("h_sec", h_sec).commit();
     		editor.putInt("l_hrs", l_hrs).commit();
     		editor.putInt("l_min", l_min).commit();
     		editor.putInt("l_sec", l_sec).commit();
     		editor.putInt("time_reocrd", time_record).commit();
        	String heavy_display = String.valueOf(h_hrs) + "h " + String.valueOf(h_min) 
        			+ "m " + String.valueOf(h_sec) + "s";
        	String light_display = String.valueOf(l_hrs) + "h " + String.valueOf(l_min) 
        			+ "m " + String.valueOf(l_sec) + "s";
     		heavyHour.setText(heavy_display);
     		lightHour.setText(light_display);
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
            btStatus.setText("No bluetooth adapter available");
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
        btStatus.setText("Device paired, not connected");
    	
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
        
        
        btStatus.setText("Bluetooth Opened");
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
                                    Time now = new Time(Time.getCurrentTimezone());
                            		now.setToNow();
                                    fileManager.writeToFile(now.toString() + "   " + data +"\n");
                                    readBufferPosition = 0;
                                    
                                    handler.post(new Runnable() {
                                        public void run() {                  
                                        	status.setText(data);
                                        	CharSequence cs = status.getText();
                                        	// the signal is "Heavy Rain"
                                        	if (cs.charAt(0) == 'H') {
                                        		h_sec += 5;
                                        		if (h_sec == 60) {
                                        			h_min++;
                                        			h_sec = 0;
                                        			if (h_min == 60) {
                                        				h_hrs++;
                                        				h_min = 0;
                                        			}
                                        		}
                                        		String heavy_display = String.valueOf(h_hrs) + "h " + 
                                        						String.valueOf(h_min) + "m " + 
                                        						String.valueOf(h_sec) + "s";
                                        		heavyHour.setText(heavy_display);
                                        		SharedPreferences settings = getSharedPreferences(pref, 0);
                                        		SharedPreferences.Editor editor = settings.edit();
                                        		editor.putInt("h_hrs", h_hrs).commit();
                                        		editor.putInt("h_min", h_min).commit();
                                        		editor.putInt("h_sec", h_sec).commit();
                                        	} 
                                        	// the signal is "Light Rain"
                                        	else if (cs.charAt(0) == 'L') {
                                        		l_sec += 5;
                                        		if (l_sec == 60) {
                                        			l_min++;
                                        			l_sec = 0;
                                        			if (l_min == 60) {
                                        				l_hrs++;
                                        				l_min = 0;
                                        			}
                                        		}
                                        		String light_display = String.valueOf(l_hrs) + "h " + 
                                        						String.valueOf(l_min) + "m " + 
                                        						String.valueOf(l_sec) + "s";
                                        		lightHour.setText(light_display);
                                        		SharedPreferences settings = getSharedPreferences(pref, 0);
                                        		SharedPreferences.Editor editor = settings.edit();
                                        		editor.putInt("l_hrs", l_hrs).commit();
                                        		editor.putInt("l_min", l_min).commit();
                                        		editor.putInt("l_sec", l_sec).commit();
                                        	}
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
        btStatus.setText("Bluetooth Closed");
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
                		wiperStatus.setText(""+speed);
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