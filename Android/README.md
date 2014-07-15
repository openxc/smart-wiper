** Android project for the Smart Wiper**
======================================

The primary purpose of the android application is to works by initially establishing a bluetooth connection with a previously paired device, then listening for data being sent from that device. The application can also send messages to the device over bluetooth. As soon as a bluetooth connection is opened with the device, all of the data received by the android device from the Ardruino. 

In order to use the application, first go into the bluetooth setting of your android device, and pair to the Arduino in the Smart Wiper Box. 

Next navigate to where the application is located in the Android's app drawer and launch the application. Once the application is launched, press the *Connect Bluetooth* button and wait until the dialog says *Bluetooth Opened*. In order to check that a successful bluetooth connection was made, look for a green light on the box. Once this occurs, look at the same dialog, as this is where the data will be displayed. 

If the dialog displays "Bluetooth Opened", then a successful connection was made. If the dialog displays "Bluetooth Device Found", the android device has been paired to the device but a connection was not made.

To send data, type the message into the text box and press *Send Data*. 

In order to close the bluetooth connection, press the *Disconnect Bluetooth* button. This stops all bluetooth communication with the box. 

In order to view the logs that were saved, navigate to your device's Downloads folder through either a file management application (i.e. OI File Manager) or the Downloads application. The data will be stored in a file called *wiper_data.txt*
