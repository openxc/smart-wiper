package com.ford.svl.connectedwiper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;

public class BluetoothConnection {
	
	private static final String TAG = BluetoothConnection.class.getSimpleName();
	
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothSocket socket;
	private BluetoothDevice device;
	
	private OutputStream outputStream;
	private InputStream inputStream;
	
	FileManager fileManager = new FileManager("wiper_data");
	
	private Context context;
	private MainActivity mainActivity;
	
	private volatile boolean stopWorker;
	private Thread workerThread;
	byte[] readBuffer;
    int readBufferPosition;
    int counter;
    
    private SharedPreferences settings;
    
	private int h_hrs; 
	private int h_min; 
	private int h_sec; 
	private int l_hrs; 
	private int l_min; 
	private int l_sec; 

    
	
	public BluetoothConnection(Context context, MainActivity mainActivity){
		this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		this.context = context;
		this.mainActivity = mainActivity;
		this.settings = context.getSharedPreferences(MainActivity.DEFAULT_PREFERENCE, 0);
		this.h_hrs = settings.getInt("h_hrs", 0);
		this.h_min = settings.getInt("h_min", 0);
		this.h_sec = settings.getInt("h_sec", 0);
		this.l_hrs = settings.getInt("l_hrs", 0);
		this.l_min = settings.getInt("l_min", 0);
		this.l_sec = settings.getInt("l_sec", 0);
		
	}
	
	private BluetoothDevice getASpecificBTDevice(String deviceName) throws Exception {

        //Check to see if device is Bluetooth capable
        if(mBluetoothAdapter == null) {
        	Log.i(TAG, "No bluetooth device available");
            mainActivity.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					mainActivity.getBtStatus().setText("No bluetooth adapter available");
				}});
        }
        
        //Enable Bluetooth if it is not already enabled
        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(enableBluetooth);
        }
        
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
        	
            for(BluetoothDevice myDevice : pairedDevices) {
            	
                if(myDevice.getName().equals(deviceName)) 
                {
                    return myDevice;
                }
            }
        }
		throw new Exception("Bluetooth may not have been enabled or the specified device may not be reachable");
    	
    }
	
	public BluetoothSocket startBluetooth(String deviceName) throws Exception{
		
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        device = getASpecificBTDevice(deviceName);
        
        try{
        	socket = device.createRfcommSocketToServiceRecord(uuid); 
        	socket.connect();
        	inputStream = socket.getInputStream();
        	outputStream = socket.getOutputStream();
        	return socket;
        }catch(Exception e){
        	throw new IOException("Opening bluetooth socket failed...");
        }
    }
	
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
                                    mainActivity.runOnUiThread(new Runnable(){
										@Override
										public void run() {
											mainActivity.getDataLogging().setText("The raining status data log is being stored in the wiper_data.txt file in Downloads");
										}});
                                    
                                    readBufferPosition = 0;
                                    
                                    handler.post(new Runnable() {
                                        public void run() {                  
                                        	mainActivity.getStatus().setText(data);
                                        	CharSequence cs = mainActivity.getStatus().getText();//status.getText();
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

                                        		mainActivity.getHeavyHour().setText(heavy_display);
                                        		mainActivity.getRainStatusImg().setBackgroundResource(R.drawable.heavy_rain);

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

                                        		mainActivity.getRainStatusImg().setBackgroundResource(R.drawable.light_rain);
                                        		mainActivity.getLightHour().setText(light_display);

                                        		SharedPreferences.Editor editor = settings.edit();
                                        		editor.putInt("l_hrs", l_hrs).commit();
                                        		editor.putInt("l_min", l_min).commit();
                                        		editor.putInt("l_sec", l_sec).commit();
                                        	}else{
                                        		mainActivity.getRainStatusImg().setBackgroundResource(R.drawable.no_rain);
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
	
	public void disconnect() throws Exception{
		stopWorker = true;
        if(socket.isConnected()) {
        	Thread thread = new Thread(new Runnable(){

				@Override
				public void run() {
					try {
						outputStream.close();
						inputStream.close();
				        socket.close();
					} catch (IOException e) {
						Log.e(TAG, ""+e.getLocalizedMessage());
						e.printStackTrace();
					}
				}});
        	thread.start();
        	thread.join();
	        return;
       }
        throw new Exception("Unable to disconnect safely");
	}

}
