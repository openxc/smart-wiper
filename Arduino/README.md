**Arduino firmware for the Smart Wiper**
===================================

The Arduino firmware calculates and sends the current wiper status to the Android device over Bluetooth. It measures the acceleration rates in X, Y, Z axes using a triple axis accelerometer, calculates the variance of acceleration rate X, Y, Z, and determine the state of the wiper based on the calculated value. The testing result demonstrated full functionality under regular driving condition, including highway driving and urban driving conditions. In the next version, new sensors might be added to the Wiper Box and could provide more data about wiper, and the data analysis would be more accurate by improving algorithm. 

**Algorithm Overview**

The accelerometer in the Smart-Wiper Box measures the instantaneous acceleration in three axes X, Y and Z ( refereed as Ax, Ay, Az). In order to distinguish the status of the wiper from non-wiping state, it is necessary to obtain the variance of theta, referred as Var(theta), and the variance of Az, referred as Var(Az). From our experiments (see the *Plot* below), clear pattern can be observed from the plot for Var(theta) and Var(Az) between wiping and non-wiping states. Therefore, an algorithm was developed to distinguish the status of the wiper and the raining condition can then be understood.

![Wiping Waves](https://github.com/openxc/smart-wiper/raw/master/Arduino/Docs/wipingwaves.jpg)

Mathematically the variance can be calculated in the following steps: 

1. Average value of theta and Az

theta bar, Az bar

2. Find the variance of theta

VarXY = square(atan2(AxisX, AxisY) - refAngle)

3. Find the variance of Az

VarZ = square(AxisZ - refZ) 

The algorithm is explained as the followings: 

1. If any of Var(theta) and Var(Az) are lower than the respective threshold value(Th) in the period of 5 seconds, the microcontroller will send *No Rain* signal to Android application over Bluetooth.

2. If both Var(theta) and Var(Az) are higher than the respective threshold value(Th) for up to 8 times the period of 5 seconds, the microcontroller will send *Light Rain* signal to Android application. 
 
3. if both Var(theta) and Var(Az) are higher than the respective threshold value(Th) for more than 8 times in the period of 5 seconds, the microcontroller will send *Heavy Rain* signal to Android application. 


