**Smart Wiper**
===============

The Smart Wiper is a Ford Mobility Team App Challenge project designed to provide a means for collecting data on rain frequency and intensity during the monsoon season in Mumbai. The device monitors the change in acceleration of vehicle windshield wipers as a rain indicator. Based the motion speed of the wipers, the device can also monitor how stronglythe rain is falling.

## **Dependencies**

**Hardware**

* Bluetooth-compatible Android device
* Smart Wiper Box

**Software**

* Android SDK
* Android ADT/Eclipse

## **Android**

The application works by initially establishing a bluetooth connection with a previously paired device, then listening for data being sent from that device. The application can also send messages to the device over bluetooth. As soon as a bluetooth connection is opened with the device, all of the data received by the android device from the Ardruino. 

**Application Use**

In order to use the application, first go into the bluetooth setting of your android device, and pair to the Arduino in the Smart Wiper Box. 

Next navigate to where the application is located in the Android's app drawer and launch the application. Once the application is launched, press the *Connect Bluetooth* button and wait until the dialog says *Bluetooth Opened*. In order to check that a successful bluetooth connection was made, look for a green light on the box. Once this occurs, look at the same dialog, as this is where the data will be displayed. 

To send data, type the message into the text box and press *Send Data*. 

In order to close the bluetooth connection, press the *Disconnect Bluetooth* button. This stops all bluetooth communication with the box. 

In order to view the logs that were saved, navigate to your device's Downloads folder through either a file management application (i.e. OI File Manager) or the Downloads application. The data will be stored in a file called *wiper_data.txt*

## **Arduino**

The Arduino firmware sends the information about current states of the wiper to Android devices over bluetooth. It receives raw data of acceleration rates in three axes from a triple axis accelerometer, calculates the variances of acceleration in all three axes and decides if the wiper is moving and at which speed mode based on the values of variances. From our tests, it works well for regular driving environment. In some cases, it could be misled by rapid turning and braking (this also means potentially it can be used for judging if a driver is driving dangerously). In the next version, new sensors might be added to the Wiper Box and could provide more data about wiper, and the data analysis would be more accurate by improving algorithm. 

**Algorithm Overview**

In order to recognize different states of the wiper (wiping/non-wiping, wiping speed), the algorithm makes use of the statistic differences of acceleration data collected from accelerometer. From the plot below, the variance of XY-Theta (defined as atan2(acceleration in X axis, acceleration in Y axis)) and the variance of acceleration in Z axis perform very unique patterns. 

Mathematically the variance can be calculated in two steps: firstly calculate the differences between single data and the average value, then square these differences to make it always positive. To get the average value without storing data in an array (it slows down the program), we use reference value as the average: we calculate the average values of 50 points at the beginning of each loop and assign them to reference variables (refX, refY and refZ). 

Basically the algorithm works in the following way: if any of the characteristic variances are smaller than the critical values (currently, we set these values 0.05 for XY-Theta and 20000 for Z axis) in a set period (10 seconds right now), the microcontroller will send OFF signal to Android application over bluetooth; Otherwise, the microcontroller will decide at which speed the wiper is wiping based on the frequency of variance peaks (currently, we set 1-6 for SLOW mode, 7-13 for REGULAR mode and 14 or larger for FAST mode in a period of 10 seconds) and send the corresponding signal to application over bluetooth.

**Connect Arduino to Computer**

In order to connect Arduino Pro Mini board to computer, you need a 3.3V USB FTDI. Notice that all pins must connect to corresponding pins of the board.

## **Smart Wiper Hardware Assembly**

The Smart wiper box includes a Arduino Pro Mini board, a bluetooth module, a triple axis accelerometer, a Li-ion battery with battery USB charger and enclosures. In order to assembly the box, one should follow these steps. 

1) follow the electrical circuit design to solder and connect the electronic components, make sure that red LEDs are on (also make sure green LED of bluetooth module is on while it connects to device) ; 

2) place the bluetooth module onto the inner bottom of the box, and notice that the soldered joints are right below two holes of the box; 

3) place the Arduino board above the bluetooth module and fix it to the slots; 

4) place the triple axis accelerometer onto the two holes and fix it;   

5) place the insert plate onto the Arduino board and cover it;

6) place the battery USB charger onto the insert plate and fix it, the USB interface of the charger should fit the interface hole of the box;

7) place the Li-ion battery onto the internal side of lid, close the lid and fix it (for waterproof, use silicone adhesive around the lid);

Now you have your own Smart Wiper Box!

## **Installation Instruction**

In order to install Smart Wiper Box onto the wiper of your car, you firstly make a piece of sticky back band through the two slots on the lid of box, and then fix the band onto the arm of wiper, which is roughly six inches from the bottom of the wiper. The bottom of box should face down to the wiper arm and the lid should face up. Make sure the box is attached onto the wiper arm tightly enough. And now, you can drive your car with the Smart Wiper Box! 

## **Testing Condition**

The Smart Wiper Box was tested on the wiper of Ford Focus Titanium 2012 around Palo Alto, CA, at the speed range of 0-50 mph, under different road conditions: urban roads, campus roads and parking areas, and under different driving conditions: smooth style, rapid style, and extremely braking and turning. 
