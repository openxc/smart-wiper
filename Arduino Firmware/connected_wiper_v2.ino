#include <SoftwareSerial.h>

int standardX = 0;
int standardY = 0;
int standardZ = 0;
int axisX = 0; // first analog sensor
int axisY = 0; // second analog sensor
int axisZ = 0; // digital sensor
int inByte = 0; // incoming serial byte

// int maxX = 0;
// int minX = 0;
int maxY = 0;
int minY = 0;
// int maxZ = 0;
// int minZ = 0;
int count = 0;

int regularValue = 60;
int fastValue = 180;

int bluetoothTx = 5; // TX-0 pin of bluetooth to Arduino D2
int bluetoothRx = 6; // RX-1 pin of bluetooth to Arduino D3

boolean isWiping = false;

SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);

void setup()
{
 // start serial port at 9600 bps:
 Serial.begin(9600);
 
 bluetooth.begin(115200);
 bluetooth.print("$");
 bluetooth.print("$");
 bluetooth.print("$");
 delay(100);
 bluetooth.println("U,9600,N");
 bluetooth.begin(9600);
 
 
 for (int i = 0; i < 10; i++) {
   standardX = standardX + analogRead(A0);
   delay(10);
   standardY = standardY + analogRead(A1);
   delay(10);
   standardZ = standardZ + analogRead(A2);
   delay(10);
 }
 standardX = standardX/10;
 standardY = standardY/10;
 standardZ = standardZ/10;
 
}

void loop()
{
  axisX = 0;
  axisY = 0;
  axisZ = 0;
//  maxX = standardX;
//  minX = standardX;
  maxY = standardY;
  minY = standardY;
//  maxZ = standardZ;
//  minZ = standardZ;
  for (int i = 0; i < 10; i++) {
    /*
    axisX =  analogRead(A0);
    delay(10);
    if (maxX < axisX) {
      maxX = axisX;
    }
    if (minX > axisX) {
      minX = axisX;
    }
    */
    
    // Read the Y axis data and determine the max/min value
    axisY = analogRead(A1);
    delay(10);
    if (maxY < axisY) {
      maxY = axisY;
    }
    if (minY > axisY) {
      minY = axisY;
    }
    
    /*
    axisZ = analogRead(A2);
    delay(10);
    if (maxZ < axisZ) {
      maxZ = axisZ;
    }
    if (minZ > axisZ) {
      minZ = axisZ;
    }
    */
  }
  
  // If the gap between max/min value is beyond the set value for continuous three times, outputs the wiping state.
  if (maxY - minY > regularValue) {
    if (count < 3) {
      count++;
    } else if (!isWiping && count >= 3) {
      isWiping = true;
      if (maxY - minY > fastValue) {
        Serial.println("The wiper is wiping in the fast mode!");
        bluetooth.println("The wiper is wiping in the fast mode!");
      } else {
        Serial.println("The wiper is wiping in the regular mode!");
        bluetooth.println("The wiper is wiping in the regular mode!");
      }
    }
  } else {
    count = 0;
    if (isWiping) {
      isWiping = false;
      Serial.println("The wiper is off!");
      bluetooth.println("The wiper is off!");
    }
  }
 
 // send sensor values to Serial moniter:
 /*
 Serial.print("X = ");
 Serial.print(axisX);
 Serial.print(", ");
 Serial.print("Y = ");
 Serial.print(axisY);
 Serial.print(", ");
 Serial.print("Z = ");
 Serial.print(axisZ);
 Serial.println();
 */
 
 /*
 if (bluetooth.available()) {
   Serial.print((char)bluetooth.read());
 }
 */
 
 // send data to bluetooth:
 /*
 bluetooth.print("X = ");
 bluetooth.print(axisX);
 bluetooth.print(", ");
 bluetooth.print("Y = ");
 bluetooth.print(axisY);
 bluetooth.print(", ");
 bluetooth.print("Z = ");
 bluetooth.print(axisZ);
 bluetooth.println();
 */
  
}
