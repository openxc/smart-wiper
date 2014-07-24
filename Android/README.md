**Android project for the Smart Wiper**
======================================

Update 07/21/2014

The updated application displays three raining status: "Heavy Rain", "Light Rain" and "No Rain" in the center.

The aggregated raining time of heavy rain and light rain displays separately on the screen. The raining time data for each status will be automatically recorded daily in "daily_record.txt" file in *Downloads* folder.

Users can record the current raining status and time data by pressing *RECORD* button. The data will be stored in "user_record.txt" file in *Downloads* folder.

Users can also clear the current raining time in screen by pressing *CLEAR* button.

______________________________________

The primary purpose of the Android application is to establish a Bluetooth connection with a previously paired Bluetooth-module, then listening for data being sent from the Smart-Wiper Box. As soon as the Bluetooth connection is established with the Smart-Wiper Box, it sends calculated wiper position data to the Android device. 

Note: In order to use the application,  you first need to pair the Smart-Wiper Box to the Android device. This can be done through the main Bluetooth Setting on the Android device.

Once the application is launched, toggle the *Bluetooth* switch and wait until the Bluetooth status says *Bluetooth Opened*. In order to check that a successful bluetooth connection was made, look for a green light on the Smart-Wiper Box. Once all connections are established Wiper Status data shows up in large text on the Android application. 

Note: If the Bluetooth status says *Bluetooth Device Found*, the android device has been paired to the device but a connection was not made.

In order to close the Bluetooth connection, turn off the *Bluetooth* switch. This stops all Bluetooth communication with the box. 

In order to view the logs that were saved, navigate to your device's *Downloads* folder. The data will be stored in a file called *wiper_data.txt*

![android](https://github.com/openxc/smart-wiper/raw/master/Android/Docs/android.png)
