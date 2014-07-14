**Smart Wiper**
===============

The Smart Wiper project provides a mean for collecting data on rain frequency and/or intensity. A 3D printed enclosure or 'box' houses an inertial measurement unit that can be attached to the windshield wiper. The device monitors the change in acceleration of the windshield wiper and use that as a surrogate for rain detection and intensity. 
The project contains:

* Electronics Layout: The circuit diagram and all necessary electronics for the box
* Arduino Firmware: The arduino firmware that converts acceleration data into a generic wiper movement data
* Android Project: That communicates with the electronics box over bluetooth and provides a simple UI to display that
* 3D printed enclosure: CAD files for the box, for 3D printing the box


## **Dependencies**

**Hardware**

* Bluetooth-compatible Android device (see list here)
* Smart Wiper Box (see 3D CAD files here)
* Electrical components (see BOM here)

**Software**

* Android SDK
* Android ADT/Eclipse
* Arduino IDE


## **Smart Wiper Hardware Assembly**

The Smart wiper box includes:
  - Arduino Pro Mini board 
  - Bluetooth module 
  - Triple axis accelerometer
  - Li-ion battery with battery USB charger 
  - 3D printed enclosure

The image below shows the overall circuit layout

**[[Insert Image]]**

In order to assemble the box, follow these steps: 

1) Follow the electrical circuit design above to solder and connect the electronic components. 

2) Place the bluetooth-module to the bottom of the box, the soldered joints are right below two holes of the box.
   **[[Insert Image]]**

3) Place the arduino pro mini above the bluetooth module and fix it to the slots.
   **[[Insert Image]]**

4) Next place the triple-axis accelerometer onto the two holes and fix it;   
   **[[Insert Image]]**

5) Once the accelerometer is firmly secured grab the insert plate and place it onto the arduino-pro-mini and cover it.
  **[[Insert Image]]**

6) Put the battery-USB-charger onto the insert plate and fix it, the USB interface of the charger should fit the interface hole of the box;
  **[[Insert Image]]**
  
7) Finally, put the Li-ion battery onto the internal side of lid, close the lid and fix it. Note: For a waterproof seal use the silicone adhesive around the lid.
  **[[Insert Image]]**
  
## **Arduino**
Once all the electronics are soldered and placed, we will upload the firmware on to the arduino. To upload the firmware you need a FTDI cable, that has a standard male USB on its other end.

 **[[Insert Image]]**
 
The Arduino firmware calculates and sends the information about current states of the wiper to Android devices over bluetooth. It receives raw data of acceleration rates in three axes from a triple axis accelerometer, calculates the variances of acceleration in all three axes and decides if the wiper is moving and at which speed mode based on the values of variances. From our tests, it works well for regular driving environment. In some cases, it could be misled by rapid turning and braking (this also means potentially it can be used for judging if a driver is driving dangerously). In the next version, new sensors might be added to the Wiper Box and could provide more data about wiper, and the data analysis would be more accurate by improving algorithm. 

**Algorithm Overview**

In order to recognize different states of the wiper (wiping/non-wiping, wiping speed), the algorithm makes use of the statistic differences of acceleration data collected from accelerometer. From the plot below, the variance of XY-Theta (defined as atan2(acceleration in X axis, acceleration in Y axis)) and the variance of acceleration in Z axis perform very unique patterns. 

Mathematically the variance can be calculated in two steps: firstly calculate the differences between single data and the average value, then square these differences to make it always positive. To get the average value without storing data in an array (it slows down the program), we use reference value as the average: we calculate the average values of 50 points at the beginning of each loop and assign them to reference variables (refX, refY and refZ). 

Basically the algorithm works in the following way: if any of the characteristic variances are smaller than the critical values (currently, we set these values 0.05 for XY-Theta and 20000 for Z axis) in a set period (10 seconds right now), the microcontroller will send OFF signal to Android application over bluetooth; Otherwise, the microcontroller will decide at which speed the wiper is wiping based on the frequency of variance peaks (currently, we set 1-6 for SLOW mode, 7-13 for REGULAR mode and 14 or larger for FAST mode in a period of 10 seconds) and send the corresponding signal to application over bluetooth.

In order to connect Arduino Pro Mini board to computer, you need a 3.3V USB FTDI. Notice that all pins must connect to corresponding pins of the board.

## **Android**

The primary purpose of the android application is to works by initially establishing a bluetooth connection with a previously paired device, then listening for data being sent from that device. The application can also send messages to the device over bluetooth. As soon as a bluetooth connection is opened with the device, all of the data received by the android device from the Ardruino. 

In order to use the application, first go into the bluetooth setting of your android device, and pair to the Arduino in the Smart Wiper Box. 

Next navigate to where the application is located in the Android's app drawer and launch the application. Once the application is launched, press the *Connect Bluetooth* button and wait until the dialog says *Bluetooth Opened*. In order to check that a successful bluetooth connection was made, look for a green light on the box. Once this occurs, look at the same dialog, as this is where the data will be displayed. 

To send data, type the message into the text box and press *Send Data*. 

In order to close the bluetooth connection, press the *Disconnect Bluetooth* button. This stops all bluetooth communication with the box. 

In order to view the logs that were saved, navigate to your device's Downloads folder through either a file management application (i.e. OI File Manager) or the Downloads application. The data will be stored in a file called *wiper_data.txt*

## **Installation Instruction**

In order to install Smart Wiper Box onto the wiper of your car, you firstly make a piece of sticky back band through the two slots on the lid of box, and then fix the band onto the arm of wiper, which is roughly six inches from the bottom of the wiper. The bottom of box should face down to the wiper arm and the lid should face up. Make sure the box is attached onto the wiper arm tightly enough. And now, you can drive your car with the Smart Wiper Box! 


## **Testing Condition**

The Smart Wiper Box was tested on the wiper of Ford Focus Titanium 2012 around Palo Alto, CA, at the speed range of 0-50 mph, under different road conditions: urban roads, campus roads and parking areas, and under different driving conditions: smooth style, rapid style, and extremely braking and turning. 
