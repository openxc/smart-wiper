**Arduino firmware for the Smart Wiper**
===================================
 
The Arduino firmware calculates and sends the information about current state of the wiper to Android devices over bluetooth. It receives raw data of acceleration rates in three axes from a triple axis accelerometer, calculates the variances of acceleration in all three axes and decides if the wiper is moving and at which speed mode based on the values of variances. From our tests, it works well for regular driving environment. In some cases, it could be misled by rapid turning and braking (this also means potentially it can be used for judging if a driver is driving dangerously). In the next version, new sensors might be added to the Wiper Box and could provide more data about wiper, and the data analysis would be more accurate by improving algorithm. 

**Algorithm Overview**

In order to recognize different states of the wiper (wiping/non-wiping, wiping speed), the algorithm makes use of the statistic differences of acceleration data collected from accelerometer. From the plot below, the variance of XY-Theta (defined as atan2(acceleration in X axis, acceleration in Y axis)) and the variance of acceleration in Z axis perform very unique patterns. 

![Wiping Waves](https://github.com/openxc/smart-wiper/raw/master/Arduino/Docs/wipingwaves.jpg)

Mathematically the variance can be calculated in two steps: firstly calculate the differences between single data and the average value, then square these differences to make it always positive. To get the average value without storing data in an array (it slows down the program), we use reference value as the average: we calculate the average values of 50 points at the beginning of each loop and assign them to reference variables (refX, refY and refZ). 

Basically the algorithm works in the following way: if any of the characteristic variances are smaller than the critical values (currently, we set these values 0.05 for XY-Theta and 20000 for Z axis) in a set period (10 seconds right now), the microcontroller will send OFF signal to Android application over bluetooth; Otherwise, the microcontroller will send ON signal to Android application. In the next version of Arduino firmware, the micro-controller can decide at which speed the wiper is wiping based on the frequency of variance peaks  and send the corresponding signal to application over bluetooth.


