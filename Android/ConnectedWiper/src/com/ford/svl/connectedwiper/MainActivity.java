package com.ford.svl.connectedwiper;

import com.openxc.VehicleManager;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.UnrecognizedMeasurementTypeException;
import com.openxc.measurements.WindshieldWiperStatus;
import com.openxc.measurements.Latitude;
import com.openxc.measurements.Longitude;
import com.openxc.remote.VehicleServiceException;

import android.text.format.Time;
import android.os.Bundle;

import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import android.app.Activity;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.ServiceConnection;
import android.content.SharedPreferences;

public class MainActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	public static final String TAG = MainActivity.class.getSimpleName();

	private MainActivity mainActivity;

	public static final String DEFAULT_PREFERENCE = "record_preference";
	public SharedPreferences settings;

	private static final String USER_DATA_DIR = "user_record.txt";
	private static final String DAILY_DATA_DIR = "daily_record.txt";

	private TextView status;
	private TextView btStatus;
	private TextView heavyHour;
	private TextView lightHour;
	private TextView dataLogging;

	private View switchLayout;

	private ImageView rainStatusImg;

	private Button clearButton;
	private Button recordButton;

	private Switch bt;

	private BluetoothConnection btConnection;

	private VehicleManager mVehicleManager;
	private TextView wiperStatus;
	private String vehicleLatitude;
	private String vehicleLongitude;

	private FileManager user_record = new FileManager(USER_DATA_DIR);
	private FileManager record = new FileManager(DAILY_DATA_DIR);

	boolean checked;

	int h_hrs = 0;
	int h_min = 0;
	int h_sec = 0;
	int l_hrs = 0;
	int l_min = 0;
	int l_sec = 0;

	Time rightnow;
	int time_record = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		status = (TextView) findViewById(R.id.status);
		heavyHour = (TextView) findViewById(R.id.heavyHour);
		lightHour = (TextView) findViewById(R.id.lightHour);
		wiperStatus = (TextView) findViewById(R.id.wiper);
		dataLogging = (TextView) findViewById(R.id.dataLogging);
		btStatus = (TextView) findViewById(R.id.btStatus);

		switchLayout = (View) findViewById(R.id.switch_layout);

		rainStatusImg = (ImageView) findViewById(R.id.rain_status_img);

		bt = (Switch) findViewById(R.id.bluetooth);
		bt.setOnCheckedChangeListener(this);

		clearButton = (Button) findViewById(R.id.clearButton);
		clearButton.setOnClickListener(this);

		recordButton = (Button) findViewById(R.id.recordButton);
		recordButton.setOnClickListener(this);

		settings = getSharedPreferences(DEFAULT_PREFERENCE, 0);
		h_hrs = settings.getInt("h_hrs", 0);
		h_min = settings.getInt("h_min", 0);
		h_sec = settings.getInt("h_sec", 0);
		l_hrs = settings.getInt("l_hrs", 0);
		l_min = settings.getInt("l_min", 0);
		l_sec = settings.getInt("l_sec", 0);
		time_record = settings.getInt("time_record", 0);
		String heavy_display = String.valueOf(h_hrs) + "h "
				+ String.valueOf(h_min) + "m " + String.valueOf(h_sec) + "s";
		String light_display = String.valueOf(l_hrs) + "h "
				+ String.valueOf(l_min) + "m " + String.valueOf(l_sec) + "s";
		heavyHour.setText(heavy_display);
		lightHour.setText(light_display);

		mainActivity = this;
		btConnection = new BluetoothConnection(getApplicationContext(),
				mainActivity);

		this.registerReceiver(receiver,
				IntentFilterForMainActivity.getIntentFilters());
		timeAdjustment();
		/*	*/

	}

	@Override
	public void onResume() {
		super.onResume();

		if (mVehicleManager == null) {
			Intent intent = new Intent(this, VehicleManager.class);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if (mVehicleManager != null) {
			Log.i(TAG, "Unbinding from Vehicle Manager");
			try {
				// Remember to remove your listeners, in typical Android
				// fashion.
				mVehicleManager.removeListener(WindshieldWiperStatus.class,
						wiperListener);
			} catch (VehicleServiceException e) {
				e.printStackTrace();
			}
			unbindService(mConnection);
			mVehicleManager = null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mVehicleManager != null) {
			Log.i("openxc", "Unbinding from VehicleManager");
			unregisterReceiver(receiver);
			unbindService(mConnection);
			mVehicleManager = null;
		}
	}

	/*
	 * Clear the current raining time record every 24 hours
	 * 
	 * at 0:00 the raining time record will be cleared and recorded in logs.
	 */
	void timeAdjustment() {
		rightnow = new Time(Time.getCurrentTimezone());
		rightnow.setToNow();
		if (rightnow.monthDay != time_record) {
			String dailyRecord = String.valueOf(rightnow.year) + "/"
					+ String.valueOf(rightnow.month) + "/"
					+ String.valueOf(time_record) + "   Heavy rain "
					+ String.valueOf(h_hrs) + "h " + String.valueOf(h_min)
					+ "m " + String.valueOf(h_sec) + "s, " + "Light rain "
					+ String.valueOf(l_hrs) + "h " + String.valueOf(l_min)
					+ "m " + String.valueOf(l_sec) + "s";
			record.writeToFile(dailyRecord + "\n");
			h_hrs = 0;
			h_min = 0;
			h_sec = 0;
			l_hrs = 0;
			l_min = 0;
			l_sec = 0;
			time_record = rightnow.monthDay;

			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("h_hrs", h_hrs).commit();
			editor.putInt("h_min", h_min).commit();
			editor.putInt("h_sec", h_sec).commit();
			editor.putInt("l_hrs", l_hrs).commit();
			editor.putInt("l_min", l_min).commit();
			editor.putInt("l_sec", l_sec).commit();
			editor.putInt("time_reocrd", time_record).commit();
			String heavy_display = String.valueOf(h_hrs) + "h "
					+ String.valueOf(h_min) + "m " + String.valueOf(h_sec)
					+ "s";
			String light_display = String.valueOf(l_hrs) + "h "
					+ String.valueOf(l_min) + "m " + String.valueOf(l_sec)
					+ "s";
			heavyHour.setText(heavy_display);
			lightHour.setText(light_display);
			dataLogging
					.setText("The raining status data log is being stored in the daily_record.txt file in Downloads");
		}
	}

	WindshieldWiperStatus.Listener wiperListener = new WindshieldWiperStatus.Listener() {
		boolean speed = false;

		public void receive(Measurement measurement) {
			// When we receive a new wiper status value from the car, we want to
			// update the UI to display the new value. First we cast the generic
			// Measurement back to the type we know it to be, an
			// WindshieldWiperStatus.
			final WindshieldWiperStatus wiper = (WindshieldWiperStatus) measurement;
			speed = wiper.getValue().booleanValue();

			// makes sure the code is run on the Main UI thread
			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					// Finally, we've got a new value and we're running on the
					// UI thread - we set the text of the WindshieldWiperStatus
					// view to
					// the latest value
					if (mVehicleManager != null) {
						// wiperStatus.setText(""+speed);
						if (speed)
							wiperStatus.setText("ON");
						else
							wiperStatus.setText("OFF");
					}
				}
			});
		}
	};

	Latitude.Listener latitudeListener = new Latitude.Listener() {
		double latitudeValue = 0.0;

		public void receive(Measurement measurement) {

			final Latitude latitude = (Latitude) measurement;
			latitudeValue = latitude.getValue().doubleValue();

			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					if (mVehicleManager != null) {
						vehicleLatitude = String.valueOf(latitudeValue);
					}
				}
			});
		}
	};

	Longitude.Listener longitudeListener = new Longitude.Listener() {
		double longitudeValue = 0.0;

		public void receive(Measurement measurement) {

			final Longitude longitude = (Longitude) measurement;
			longitudeValue = longitude.getValue().doubleValue();

			MainActivity.this.runOnUiThread(new Runnable() {
				public void run() {
					if (mVehicleManager != null) {
						vehicleLongitude = String.valueOf(longitudeValue);
					}
				}
			});
		}
	};

	private ServiceConnection mConnection = new ServiceConnection() {
		// Called when the connection with the VehicleManager service is
		// established, i.e. bound.
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i("openxc", "Bound to VehicleManager");
			// When the VehicleManager starts up, we store a reference to it
			// here in "mVehicleManager" so we can call functions on it
			// elsewhere in our code.
			mVehicleManager = ((VehicleManager.VehicleBinder) service)
					.getService();

			// We want to receive updates whenever the GearPosition and
			// EngineSpeed changes.
			try {
				mVehicleManager.addListener(WindshieldWiperStatus.class,
						wiperListener);
				mVehicleManager.addListener(Latitude.class, latitudeListener);
				mVehicleManager.addListener(Longitude.class, longitudeListener);
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

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.recordButton) {
			Log.i(TAG, "Record button clicked");
			rightnow = new Time(Time.getCurrentTimezone());
			rightnow.setToNow();
			String record = rightnow.toString() + "   Heavy rain "
					+ String.valueOf(h_hrs) + "h " + String.valueOf(h_min)
					+ "m " + String.valueOf(h_sec) + "s, " + "Light rain "
					+ String.valueOf(l_hrs) + "h " + String.valueOf(l_min)
					+ "m " + String.valueOf(l_sec) + "s";
			user_record.writeToFile(record + "\n");
			dataLogging.setText("The raining status data log is being stored in the user_record.txt file in Downloads");

		} else if (v.getId() == R.id.clearButton) {
			Log.i(TAG, "Clear");
			h_hrs = 0;
			h_min = 0;
			h_sec = 0;
			l_hrs = 0;
			l_min = 0;
			l_sec = 0;

			SharedPreferences.Editor editor = settings.edit();
			editor.putInt("h_hrs", h_hrs).commit();
			editor.putInt("h_min", h_min).commit();
			editor.putInt("h_sec", h_sec).commit();
			editor.putInt("l_hrs", l_hrs).commit();
			editor.putInt("l_min", l_min).commit();
			editor.putInt("l_sec", l_sec).commit();
			String heavy_display = String.valueOf(h_hrs) + "h "
					+ String.valueOf(h_min) + "m " + String.valueOf(h_sec)
					+ "s";
			String light_display = String.valueOf(l_hrs) + "h "
					+ String.valueOf(l_min) + "m " + String.valueOf(l_sec)
					+ "s";
			heavyHour.setText(heavy_display);
			lightHour.setText(light_display);
			dataLogging.setText("The raining status time has been cleared.");
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			checked = isChecked;
			try {

				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							btConnection.startBluetooth("RNBT-6DEC");
						} catch (Exception e) {
							Log.e(TAG, "" + e.getLocalizedMessage());
							MainActivity.this.runOnUiThread(new Runnable(){

								@Override
								public void run() {
									bt.setChecked(false);
									btStatus.setText("Bluetooth connection error");	
								}});
							
							e.printStackTrace();
						}
					}
				});
				thread.start();
				thread.join();
				btConnection.recieveData();
				Log.i(TAG, "BT connection");
			} catch (Exception ex) {
				Log.e(TAG, "" + ex.getLocalizedMessage());
			}
		} else {
			try {
				btConnection.disconnect();
			} catch (Exception ex) {
				Log.w(TAG, "" + ex.getLocalizedMessage());
			}
		}
	}

	public TextView getBtStatus() {
		return btStatus;
	}

	public TextView getStatus() {
		return status;
	}

	public TextView getHeavyHour() {
		return heavyHour;
	}

	public TextView getLightHour() {
		return lightHour;
	}

	public TextView getDataLogging() {
		return dataLogging;
	}

	public ImageView getRainStatusImg() {
		return rainStatusImg;
	}
	
	public String getLatitude() {
		return vehicleLatitude;
	}
	
	public String getLongitude() {
		return vehicleLongitude;
	}

	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
				btStatus.setText("Connected");
				switchLayout.setBackgroundResource(R.color.green_color);
			} else if (intent.getAction().equals(
					BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)) {
				btStatus.setText("Disconnecting...");
			} else if (intent.getAction().equals(
					BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
				btStatus.setText("Disconnected");
				switchLayout.setBackgroundResource(R.color.black_color);
			} else if (intent.getAction().equals(
					BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
				btStatus.setText("Pairing status changing...");
			} else if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
				btStatus.setText("Discovered device...");
			} else if (intent.getAction().equals(
					BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
				btStatus.setText("Searching for devices...");
			} else if (intent.getAction().equals(
					BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
				btStatus.setText("Finished earching for devices...");

			}
		}
	};
}