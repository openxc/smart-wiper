**Arduino firmware for the Smart Wiper**
===================================
 
The Arduino firmware calculates and sends that calculated state of the wiper to the Android devices over Bluetooth. The Arduino receives the raw acceleration (rate) data in three axes from the Triple-Axis Accelerometer and calculates the difference between the acceleration in all the three axes and determines whether the wiper is moving and at what speed.
From our tests, it works well with baseline driving conditions with limited rapid acceleration while turning and braking. 

**Algorithm Overview**

To recognize the different states of the wiper (wiping/non-wiping), the algorithm makes use of the change in acceleration  data. From the plot below, the variance of XY-Theta (defined as atan2(acceleration in X axis, acceleration in Y axis)) and the variance of acceleration in Z axis exhibit an unique pattern. 

![Wiping Waves](https://github.com/openxc/smart-wiper/raw/master/Arduino/Docs/wipingwaves.jpg)

Mathematically this variance is calculated in two steps: 

1. Calculate the differences between individual data points and the average value, then square these differences to make it always positive. To get the average value without storing data in an array (it slows down the program), we use reference value as the average: we calculate the average values of 50 points at the beginning of each loop and assign them to reference variables (refX, refY and refZ). 

1. The algorithm works in the following way: if any of the characteristic variances are smaller than the critical values (currently, we set these values 0.05 for XY-Theta and 20000 for Z axis) in a set period (10 seconds right now), the microcontroller will send OFF signal to Android application over bluetooth; Otherwise, the microcontroller will send ON signal to Android application. In the next version of Arduino firmware, the micro-controller can decide at which speed the wiper is wiping based on the frequency of variance peaks  and send the corresponding signal to application over bluetooth.


