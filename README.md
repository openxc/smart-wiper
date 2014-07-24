**Smart Wiper**
===============

The Smart Wiper project provides a simple way for collecting data on rain frequency and/or intensity. A 3D printed enclosure or 'box' houses an inertial measurement unit that can be attached to the windshield wiper. The device monitors the change in acceleration of the windshield wiper and use that as a surrogate for rain detection and intensity. 
The project contains:

* [Electronics Layout](https://github.com/openxc/smart-wiper/tree/master/Circuit_Design): The circuit diagram and all necessary electronics for the box
* [Arduino Firmware](https://github.com/openxc/smart-wiper/tree/master/Arduino): The arduino firmware that converts acceleration data into a generic wiper movement data
* [Android Project](https://github.com/openxc/smart-wiper/tree/master/Android): That communicates with the electronics box over bluetooth and provides a simple UI to display that
* [3D printed enclosure](https://github.com/openxc/smart-wiper/tree/master/CAD): CAD files for the box, for 3D printing the box

![smartwiperbox](https://github.com/openxc/smart-wiper/raw/master/Docs/smartwiperboxv2.JPG)

## **Dependencies**

**Hardware**

* Bluetooth-compatible Android device
* Smart Wiper Box ([see 3D CAD files here](https://github.com/openxc/smart-wiper/tree/master/CAD))
* Electrical components ([see BOM here](https://github.com/openxc/smart-wiper/raw/master/BOM.xlsx))

![hardware](https://github.com/openxc/smart-wiper/raw/master/Docs/componentsv2.JPG)

**Software**

* Android SDK
* Android ADT/Eclipse
* Arduino IDE


## **Smart Wiper Hardware Assembly**

The Smart wiper box includes:
  - Arduino Pro Mini board 
  - Bluetooth-Module 
  - Triple-Axis Accelerometer
  - Li-ion battery 
  - USB-Charger 
  - 3D printed enclosure

The image below shows the overall circuit layout

![Circuit](https://github.com/openxc/smart-wiper/raw/master/Circuit_Design/SmartWiper_bb.png)


In order to assemble the box, follow these steps: 

1. Take a look at the electrical circuit design shown above to solder and connect all the electronic components. The length of the wires can be determined by how they stack up inside the enclosure. It is good practice to place all the parts and see how they are stacked and placed over each other as detailed between Step 2 and Step 6. 

2. Place the Bluetooth-module at the bottom of the [3D printed enclosure](https://github.com/openxc/smart-wiper/tree/master/CAD), the orientation of the Bluetooth-Module should be such that the pins on the module are next to the elevated holes sticking out from the bottom of the box for the Triple-Axis Accelerometer.
   ![step2](https://github.com/openxc/smart-wiper/raw/master/Docs/step2v2.JPG)

3. Place the Arduino Pro Mini above the Bluetooth-Module such that it's front is facing up.
   ![step3](https://github.com/openxc/smart-wiper/raw/master/Docs/step3v2.JPG)   

4. Next place the Triple-Axis Accelerometer over the Arduino Pro Mini such that the end with holes sits right above the two pillars. Once the Accelerometer is placed, it can be held by the pillars.
   ![step4](https://github.com/openxc/smart-wiper/raw/master/Docs/step4v2.JPG)  

5. Once the Triple-Axis Accelerometer is firmly secured grab the insert plate and place it over the Arduino Pro Mini and cover it.
  ![step5](https://github.com/openxc/smart-wiper/raw/master/Docs/step5v2.JPG)

6. Put the USB-Charger and the slide switch onto the insert plate and fix them, the female Micro-USB end of the charger should fit through the hole providing an easy way to charge the battery without taking the box apart.    ![step6](https://github.com/openxc/smart-wiper/raw/master/Docs/step6v2.JPG)
  ![usb](https://github.com/openxc/smart-wiper/raw/master/Docs/usbv2.jpg)

  
7. Finally, put the Li-ion battery above the insert plate and fix the lid. Note: For a waterproof seal use the silicone adhesive around the lid. ![enclosure](https://github.com/openxc/smart-wiper/raw/master/Docs/enclosurev2.JPG)

## **Arduino**

Once all the electronics are soldered and placed, we will upload the firmware on to the Arduino. To upload the firmware you need a FTDI cable, that has a standard male USB on its other end. More information can be found on the Arduino's website on how to upload a program on the [Arduino Pro Mini](http://arduino.cc/en/Guide/ArduinoProMini).

The Arduino firmware calculates and sends the information about the current state of the wiper to an Android device over Bluetooth. The Arduino receives the raw acceleration data from the Triple-Axis Accelerometer and calculates the variances along the three axes and determines whether the wiper is turned ON/OFF. More details on the algorithm and the scope is discussed in details [here](https://github.com/openxc/smart-wiper/tree/master/Arduino)


## **Android**

The primary purpose of the android application is to establish a Bluetooth connection with the Bluetooth-module attached to the Arduino Pro Mini and gather data and display it.  In order to use the application, first go into the Bluetooth Setting of your android device, and pair it to the Arduino in the Smart-Wiper Box. 

 ![smartwiperbox](https://github.com/openxc/smart-wiper/raw/master/Docs/androidv2.png)

## **Installation Instruction**

For installation of the Smart-Wiper Box onto the wiper of your car, you can pass the velcro-straps through the slots on the side of box, and then fix the straps onto the wiper arm enclosing the wiper blade. It is important to orient the box such that the bottom of box goes face down, in contact with the wiper arm and the lid should face up. Ensure that the velcro strap has firmly secured the wiper box, and is not moving around. In our testing the box was attached roughly six inches from the bottom of the wiper, where it is hinged to the car. This location could change based on the shape and accesibility of the wiper, it was desirable to not place the wiper higher than six inches to avoid obstruction of sight while driving.


## **Testing Condition**

The Smart-Wiper Box was tested on the wiper of Ford Focus Titanium 2012 around Palo Alto, CA, at the speed range of 0-50 mph, under mixed road conditions containing highways, city roads and parking areas. It was also subjected to a veriety of different driving conditions, which varied from slow to rapid acceleration, braking and turning. In our tests we found that the Smart-Wiper Box was able to accurately estimate the wiper status. 

![test3](https://github.com/openxc/smart-wiper/raw/master/Docs/test3.JPG)

![test2](https://github.com/openxc/smart-wiper/raw/master/Docs/test2.JPG)
