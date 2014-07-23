**Arduino firmware for the Smart Wiper**
===================================

The Arduino firmware calculates and sends the information about current state of the wiper to Android devices over bluetooth. It receives raw data of acceleration rates in three axes from a triple axis accelerometer, calculates the variances of acceleration in all three axes and decides if the wiper is moving and at which speed mode based on the values of variances. From our tests, it works well for regular driving environment. In some cases, it could be misled by rapid turning and braking (this also means potentially it can be used for judging if a driver is driving dangerously). In the next version, new sensors might be added to the Wiper Box and could provide more data about wiper, and the data analysis would be more accurate by improving algorithm. 

**Algorithm Overview**

The accelerometer in the Smart-Wiper Box detects the acceleration data in three axes X, Y and Z (we call them &alpha;<sub>X</sub>, &alpha;<sub>Y</sub> and &alpha;<sub>Z</sub>), and sends them to micro-controller. In order to distinguish wiping state from non-wiping state, we need to check two statistic numbers: the variance of atan2(AxisX, AxisY) and the variance of AxisZ (we call them VarXY and VarZ below). 

We define &theta; below:

&theta = tan<sup>-1</sup>(&alpha;<sub>Y</sub> / &alpha;<sub>Z</sub>)

![theta](https://github.com/openxc/smart-wiper/raw/master/Arduino/Docs/theta.png)



From our experiments (see the *Plot* below), these two statistic numbers show their unique patterns in wiping and non-wiping states. Therefore, we can make use of these two statistic numbers to know if the wiper is on or off and at what kind of speed the wiper is wiping (fast or slow). Based on the different wiping states, we can tell if it rains and how it rains (light or heavy). 

![Wiping Waves](https://github.com/openxc/smart-wiper/raw/master/Arduino/Docs/wipingwaves.jpg)

Mathematically the variance can be calculated in the following steps: 

1. get average value of atan2(AxisX, AxisY) and AxisZ (we call them refAngle and refZ)

2. Var(theta)= square(atan2(AxisX, AxisY) - refAngle)

3. Var(Z) = square(AxisZ - refZ)

Basically the algorithm works in the following way: 

1. if any of VarXY and VarZ are smaller than critical values that we set (currently we set 0.05 for XY-Theta and 10000 for Z axis) in certain period (5 seconds), the micro-controller will send *No Rain* signal to Android application over bluetooth.
(plot1)

2. if both VarXY and VarZ are larger than the critical values for 8 times or below in 5 seconds, the micro-controller will send *Light Rain* signal to Android application. 
(we set 8 times because it fits our experimental result)
(plot2)

3. if both VarXY and VarZ are larger than the critical values for more than 8 times in 5 seconds, the micro-controller will send *Heavy Rain* signal to Android application. 
(plot3)

