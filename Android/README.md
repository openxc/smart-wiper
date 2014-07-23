**Android project for the Smart Wiper**
======================================

Update 07/21/2014

1. The updated application will display three status based on the wiping frequency: "Heavy Rain", "Light Rain" and "No Rain".

2. The raining time of heavy rain and light rain will display separately on the screen. The real-time data will be automatically recorded in *wiper_data.txt*, and the total time will be automatically recorded once a day in *daily_record.txt*.

3. Users can record the current raining time data in *user_record.txt* by pressing *RECORD* button, and can also clear the current data in screen by pressing *CLEAR* button.

______________________________________

The primary purpose of the Android application is to establish a Bluetooth connection with a previously paired Bluetooth-module, then listening for data being sent from the Smart-Wiper Box. As soon as the Bluetooth connection is established with the Smart-Wiper Box, it sends calculated wiper position data to the Android device. 

Note: In order to use the application,  you first need to pair the Smart-Wiper Box to the Android device. This can be done through the main Bluetooth Setting on the Android device.

Once the application is launched, toggle the *Bluetooth* button and wait until the Bluetooth status says *Bluetooth Opened*. In order to check that a successful bluetooth connection was made, look for a green light on the Smart-Wiper Box. Once all connections are established Wiper Status data shows up in large text on the Android application. 

Note: If the Bluetooth status says *Bluetooth Device Found*, the android device has been paired to the device but a connection was not made.

In order to close the Bluetooth connection, press the *Disconnect Bluetooth* button. This stops all Bluetooth communication with the box. 

In order to view the logs that were saved, navigate to your device's *Downloads* folder. The data will be stored in a file called *wiper_data.txt*
