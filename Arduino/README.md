**Arduino firmware for the Smart Wiper**
===================================

The Arduino firmware calculates and sends the information about current state of the wiper to Android devices over Bluetooth. It receives raw data of acceleration rates in X, Y, Z axes from a triple axis accelerometer, calculates the variances of acceleration in all three axes and decides if the wiper is moving and at which speed mode based on the values of variances. The testing result demonstrated full functionality under regular driving condition. In the next version, new sensors might be added to the Wiper Box and could provide more data about wiper, and the data analysis would be more accurate by improving algorithm. 

**Algorithm Overview**

The accelerometer in the Smart-Wiper Box senses the acceleration in three axes X, Y and Z (Ax, Ay, Az). and sends them to Arduino micro-controller. In order to distinguish the status of the wiper from non-wiping state, we need to check two statistic numbers: the variance of theta, referred as Var(theta) and the variance of Az, referred as Var(Az). From our experiments (see the *Plot* below), Var(theta) and Var(Az) show the unique patterns in wiping and non-wiping states. Therefore, an algorithm was developed to distinguish the status of the wiper and the raining condition can be assumed.

![Wiping Waves](https://github.com/openxc/smart-wiper/raw/master/Arduino/Docs/wipingwaves.jpg)

Mathematically the variance can be calculated in the following steps: 

1. Average value of theta and Az
theta bar, Az bar

2. Find the variance of theta
VarXY = square(atan2(AxisX, AxisY) - refAngle)

3. Find the variance of Az
VarZ = square(AxisZ - refZ)

Basically the algorithm works in the following way: 

1. If any of Var(theta) and Var(Az) are lower than the respective threshold value(Th) in the period of 5 seconds, the microcontroller will send *No Rain* signal to Android application over Bluetooth.

2. If both Var(theta) and Var(Az) are higher than the respective threshold value(Th) for up to 8 times the period of 5 seconds, the microcontroller will send *Light Rain* signal to Android application. 
 
3. if both Var(theta) and Var(Az) are higher than the respective threshold value(Th) for more than 8 times in the period of 5 seconds, the microcontroller will send *Heavy Rain* signal to Android application. 


