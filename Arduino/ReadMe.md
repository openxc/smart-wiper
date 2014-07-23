**Arduino firmware for the Smart Wiper**
===================================

The Arduino firmware calculates and sends the current wiper status to the Android device over Bluetooth. It measures the acceleration rates in X, Y, Z axes using a triple axis accelerometer, calculates the variance of acceleration rate X, Y, Z, and determine the state of the wiper based on the calculated value. The testing result demonstrated full functionality under regular driving condition, including highway driving and urban driving conditions. In the next version, new sensors might be added to the Wiper Box and could provide more data about wiper, and the data analysis would be more accurate by improving algorithm. 

**Algorithm Overview**

The accelerometer in the Smart-Wiper Box measures the instantaneous acceleration in three axes X, Y and Z ( refereed as a<sub>X</sub>, a<sub>Y</sub> and a<sub>Z</sub>). The directions of X, Y and Z are defined in the following picture:

![car_orientation](https://github.com/openxc/smart-wiper/raw/master/Arduino/Docs/car_orientation.JPG)


Now we define &theta; below:

&theta; = tan<sup>-1</sup>(a<sub>Y</sub> / a<sub>Z</sub>)

![theta](https://github.com/openxc/smart-wiper/raw/master/Arduino/Docs/theta.png)

In order to distinguish the status of the wiper from non-wiping state, it is necessary to obtain the variance of &theta;, referred as Var(&theta;), and the variance of a<sub>Z</sub>, referred as Var(a<sub>Z</sub>). From our experiments (see the *Plot* below), clear pattern can be observed from the plot for Var(&theta;) and Var(a<sub>Z</sub>) between wiping and non-wiping states. Therefore, an algorithm was developed to distinguish the status of the wiper and the raining condition can then be understood.

![Wiping Waves](https://github.com/openxc/smart-wiper/raw/master/Arduino/Docs/wipingwaves.png)

Mathematically the variance can be calculated in the following steps: 

1. Find average values of &theta; and a<sub>Z</sub> (In order to get the average values, the microcontroller sums up &theta in the first 50 data points (1 sec) when receiving data and calculates the average values)

  ![equ4](https://github.com/openxc/smart-wiper/raw/master/Arduino/Docs/equ4.png)       
  ![equ5](https://github.com/openxc/smart-wiper/raw/master/Arduino/Docs/equ5.png)

2. Find the variance of &theta;

  ![equ2](https://github.com/openxc/smart-wiper/raw/master/Arduino/Docs/equ2.png)

3. Find the variance of a<sub>Z</sub>

  ![equ3](https://github.com/openxc/smart-wiper/raw/master/Arduino/Docs/equ3.png) 



The algorithm is explained as the followings: 


1. If any of Var(&theta;) and Var(a<sub>Z</sub>) are *lower* than the respective threshold value(Th) in the period of 5 seconds, the microcontroller will send **No Rain** signal to Android application over Bluetooth.


  ![no rain](https://github.com/openxc/smart-wiper/raw/master/Arduino/Docs/no_rain.png)


2. If both Var(&theta;) and Var(a<sub>Z</sub>) are *higher* than the respective threshold value(Th) for *1-8 points* (1-2 peaks) the period of 5 seconds, the microcontroller will send **Light Rain** signal to Android application. 


  ![light rain](https://github.com/openxc/smart-wiper/raw/master/Arduino/Docs/light_rain.png)
 
 
3. if both Var(&theta;) and Var(a<sub>Z</sub>) are *higher* than the respective threshold value(Th) for *more than 8 points* (3 peaks or more) in the period of 5 seconds, the microcontroller will send **Heavy Rain** signal to Android application. 


  ![heavy rain](https://github.com/openxc/smart-wiper/raw/master/Arduino/Docs/heavy_rain.png)


