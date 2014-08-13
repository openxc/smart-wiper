package com.ford.svl.connectedwiper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothManager {

	private static final String TAG = BluetoothManager.class.getSimpleName();
	
	private static final int CONNECTION_ATTEMPT = 3;
	
	private static final UUID RFCOMM_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
	
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private BluetoothSocket mSocket;
	private List<String> pairedDevices = new ArrayList<String>();
	private String macAddress;
	
	private InputStream mInStream;
    private OutputStream mOutStream;
    private boolean isConnected;
	
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}
	public List<String> getPairedDevices(){
		try{
			Set<BluetoothDevice> prdDevices = mBluetoothAdapter.getBondedDevices();
			for(BluetoothDevice device : prdDevices) {
            	pairedDevices.add(device.getName() + "\t" + device.getAddress());
            }
		}catch(Exception e){
			Log.e(TAG, "Error: getting paired devices");
		}
		return pairedDevices;
		
	}
	
	public void initiateConnection(){
		if (mBluetoothAdapter == null) {
			Log.w(TAG,"Device does not support Bluetooth");
		}
	}
	
	public boolean connect(){
		// Check if the mac address format conforms to the standard mac address format
		if(macAddress.matches("^([0-9A-F]{2}[:-]){5}([0-9A-F]{2})$")) {
			final BluetoothDevice mDevice = mBluetoothAdapter.getRemoteDevice(macAddress);
			for(int i=0;i<CONNECTION_ATTEMPT;i++){
				try {
					mSocket = mDevice.createRfcommSocketToServiceRecord(RFCOMM_UUID);
					mSocket.connect();
					isConnected = true;
					Log.d(TAG,"Connection made");
					break;
				} catch (IOException e) {
					try {
						mSocket.close();
					} catch (IOException e1) {
						Log.d(TAG,"Unable to end the connection",e1);
					}
					isConnected = false;
					Log.d(TAG,"Socket creation failed. Re-attempting connection.",e);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e2) {
						Log.d(TAG,"Unable to pause thread",e2);
					}
				}
			}
			
		}
		return isConnected;
	}
	
	public boolean disconnect(){
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				if(mInStream != null) {
			        try {
						mInStream.close();
				        mInStream = null;
						Log.d(TAG,"Disconnected from the InStream");
					} catch (IOException e) {
						Log.w(TAG,"Unable to close the input stream",e);
					}
		        }
				
				if(mOutStream != null) {
			        try {
						mOutStream.close();
				        mOutStream = null;
						Log.d(TAG,"Disconnected from the OutStream");
					} catch (IOException e) {
						Log.w(TAG,"Unable to close the output stream",e);
					}
		        }
				
				if(mSocket != null) {
		            try {
		                mSocket.close();
		                mSocket = null;
		                isConnected = false;
		                Log.d(TAG,"Disconnected from the socket");
		            } catch(IOException e) {
		                Log.w(TAG,"Unable to close the socket",e);
		            }
		        }
			}});
		thread.start();
		try {
			thread.join();
			return isConnected;
		} catch (InterruptedException e) {
			Log.i(TAG, "Error: Disconnectiong "+e.getLocalizedMessage());
			return isConnected;
		}
	}

}
