/** 
 * Authors: Ford Silicon Valley Lab
 *
 * This code is used for the connectedwiper project, receives data from triple axis accelerometer
 * and tells if the wiper is wiping or not. It also recognizes the speed level of the wiper.
 */
 
#include <SoftwareSerial.h>
#include "math.h"

#define PERIOD 10000 // milliseconds for each measurement
#define THRESHOLD_XY 0.05 // threshold of varAngle value between wiping/non-wiping states
#define THRESHOLD_Z 10000 // threshold of varZ value between wiping/non-wiping states

int axisX = 0; // measures value of X axis
int axisY = 0; // measures value of Y axis
int axisZ = 0; // measures value of Z axis
long sumX = 0; // sums the value of X axis for calculating the refX
long sumY = 0; // sums the value of Y axis for calculating the refY
long sumZ = 0; // sums the value of Z axis for calculating the refZ
long refX = 0; // measures the reference value for X axis
long refY = 0; // measures the reference value for Y axis
long refZ = 0; // measures the reference value for Z axis
double angle; // measures the atan2(axisX, axisY)
double refAngle; // measures the reference value of atan2(axisX, axisY)
double varAngle; // measures the variance of angle with respect to refAngle
long varZ = 0; // measures the variance of axisZ with respect to refZ
int countVarXY = 0; // measures how many times the value of varAngle is beyond THRESHOLD value
int countVarZ = 0; // counts the number of lowest peak of Z axis which is below the MIN_Z value
long startTime = 0; // measures the start time in ms for each loop function
int bluetoothTx = 5; // TX-0 pin of bluetooth to Arduino D2
int bluetoothRx = 6; // RX-1 pin of bluetooth to Arduino D3 
SoftwareSerial bluetooth(bluetoothTx, bluetoothRx); // uses SoftwareSerial to send data via bluetooth

void setup()
{
 // start serial port and bluetooth port at 9600 bps:
 Serial.begin(9600);
 bluetooth.begin(9600);
}

void loop()
{
  // initialization
  axisY = 0;
  axisX = 0;
  axisZ = 0;
  angle = 0.0;
  varAngle = 0.0;
  varZ = 0.0;
  countVarXY = 0;
  countVarZ = 0;
  startTime = millis();
  
  // measures the reference values
  sumX = 0;
  sumY = 0;
  sumZ = 0;
  for (int i = 0; i < 50; i++) {
   axisX = analogRead(A0);
   delay(5);
   axisY = analogRead(A1);
   delay(5);
   axisZ = analogRead(A2);
   sumX = sumX + axisX;
   sumY = sumY + axisY;
   sumZ = sumZ + axisZ;
  }
  refX = sumX/100;
  refY = sumY/100;
  refZ = sumZ/100;
  refAngle = atan2(refX, refY);
  
  // measures the actual data in a set period and calculate the variance
  // counts the number of occurences that the variances are above the threshold value
  while (millis() - startTime <= PERIOD) {
    axisX = analogRead(A0);
    delay(10);
    axisY = analogRead(A1);
    delay(10);
    axisZ = analogRead(A2);
    angle = atan2(axisX, axisY); 
    varAngle = pow(angle - refAngle, 2);
    if (varAngle > THRESHOLD_XY) {
      countVarXY++;
    }
    varZ = pow(axisZ - refZ, 2); 
    if (varZ > THRESHOLD_Z) {
      countVarZ++;
    } 
  }  
  
  // outputs the wiping states based on the counted number
  if (countVarXY > 0 && countVarZ > 0) {
    bluetooth.println("The wiper is ON in last 10 seconds.");
  } else {
    bluetooth.println("The wiper is OFF in last 10 seconds.");
  }
}

