**Smart Wiper**
===============

The Smart Wiper project provides a mean for collecting data on rain frequency and/or intensity. A 3D printed enclosure or 'box' houses an inertial measurement unit that can be attached to the windshield wiper. The device monitors the change in acceleration of the windshield wiper and use that as a surrogate for rain detection and intensity. 
The project contains:

* Electronics Layout: The circuit diagram and all necessary electronics for the box
* Arduino Firmware: The arduino firmware that converts acceleration data into a generic wiper movement data
* Android Project: That communicates with the electronics box over bluetooth and provides a simple UI to display that
* 3D printed enclosure: CAD files for the box, for 3D printing the box

![smartwiperbox](https://github.com/openxc/smart-wiper/raw/master/Docs/smartwiperbox.JPG)

## **Dependencies**

**Hardware**

* Bluetooth-compatible Android device
* Smart Wiper Box ([see 3D CAD files here](https://github.com/openxc/smart-wiper/tree/master/CAD))
* Electrical components ([see BOM here](https://github.com/openxc/smart-wiper/raw/master/BOM.xlsx))

![hardware](https://github.com/openxc/smart-wiper/raw/master/Docs/components.JPG)

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

![Circuit](https://github.com/openxc/smart-wiper/raw/master/Circuit_Design/SmartWiper_bb.png)


In order to assemble the box, follow these steps: 

1. Follow the electrical circuit design above to solder and connect the electronic components. 

2. Place the bluetooth-module to the bottom of the box, the soldered joints are right below two holes of the box.
   ![step2](https://github.com/openxc/smart-wiper/raw/master/Docs/step2.JPG)

3. Place the arduino pro mini above the bluetooth module and fix it to the slots.![step3](https://github.com/openxc/smart-wiper/raw/master/Docs/step3.JPG)

4. Next place the triple-axis accelerometer onto the two holes and fix it;   
   **[[Insert Image]]**

5. Once the accelerometer is firmly secured grab the insert plate and place it onto the arduino-pro-mini and cover it.
  **[[Insert Image]]**

6. Put the battery-USB-charger onto the insert plate and fix it, the USB interface of the charger should fit the interface hole of the box;
  **[[Insert Image]]**
  
7. Finally, put the Li-ion battery onto the internal side of lid, close the lid and fix it. Note: For a waterproof seal use the silicone adhesive around the lid.
  **[[Insert Image]]**
  
## **Arduino**
Once all the electronics are soldered and placed, we will upload the firmware on to the arduino. To upload the firmware you need a 3.3V USB FTDI cable, that has a standard male USB on its other end.

![ftdi](https://github.com/openxc/smart-wiper/raw/master/Docs/ftdi.JPG)
 
The Arduino firmware calculates and sends the information about current states of the wiper to Android devices over bluetooth. It receives raw data of acceleration rates in three axes from a triple axis accelerometer, calculates the variances of acceleration in all three axes and decides if the wiper is moving and at which speed mode based on the values of variances. More details on the algorithm and the scope is discussed in details [here](https://github.com/openxc/smart-wiper/tree/master/Arduino)


## **Android**

The primary purpose of the android application is to establish a bluetooth connection with the bluetooth-module and gather data from the box sent over bluetooth.  In order to use the application, first go into the bluetooth setting of your android device, and pair to the Arduino in the Smart Wiper Box. 

 ![smartwiperbox](https://github.com/openxc/smart-wiper/raw/master/Docs/android.png)

## **Installation Instruction**

For installation of the Smart-Wiper Box onto the wiper of your car, you firstly make a piece of sticky back band through the two slots on the lid of box, and then fix the band onto the arm of wiper, which is roughly six inches from the bottom of the wiper. The bottom of box should face down to the wiper arm and the lid should face up. Make sure the box is attached onto the wiper arm tightly enough. And now, you can drive your car with the Smart Wiper Box! 


## **Testing Condition**

The Smart Wiper Box was tested on the wiper of Ford Focus Titanium 2012 around Palo Alto, CA, at the speed range of 0-50 mph, under different road conditions: urban roads, campus roads and parking areas, and under different driving conditions: smooth style, rapid style, and extremely braking and turning. 

![test1](https://github.com/openxc/smart-wiper/raw/master/Docs/test1.JPG)

![test2](https://github.com/openxc/smart-wiper/raw/master/Docs/test2.JPG)
