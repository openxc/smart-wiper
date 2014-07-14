#include <SoftwareSerial.h>

//Sending 8 bit reading (256) so analogue 
//reading can be sent in 1 byte
int axisX = 0; // first analog sensor
int axisY = 0; // second analog sensor
int axisZ = 0; // digital sensor
int inByte = 0; // incoming serial byte

int sleep = 7; //sleep pin

int bluetoothTx = 5; // TX-0 pin of bluetooth to Arduino D2
int bluetoothRx = 6; // RX-1 pin of bluetooth to Arduino D3

SoftwareSerial bluetooth(bluetoothTx, bluetoothRx);

void setup()
{
 // start serial port at 9600 bps:
 Serial.begin(9600);
 pinMode(sleep, OUTPUT);
 digitalWrite(sleep, HIGH);
 
 bluetooth.begin(115200);
 bluetooth.print("$");
 bluetooth.print("$");
 bluetooth.print("$");
 delay(100);
 bluetooth.println("U,9600,N");
 bluetooth.begin(9600);
}

void loop()
{
  
 // get incoming byte:
 inByte = Serial.read();
 // read first analog input, divide by 4 to make the range 0-255:
 axisX = analogRead(A0)/4;
 // delay 10ms to let the ADC recover:
 delay(10);
 // read second analog input, divide by 4 to make the range 0-255:
 axisY = analogRead(A1)/4;
 // read switch, multiply by 155 and add 100
 // so that you're sending 100 or 255:
 delay(10);
 axisZ = analogRead(A2)/4;
 delay(10);

// send sensor values:
 Serial.print("X = ");
 Serial.print(axisX);
 Serial.print(", ");
 Serial.print("Y = ");
 Serial.print(axisY);
 Serial.print(", ");
 Serial.print("Z = ");
 Serial.print(axisZ);
 Serial.println();
 
 /*
 if (bluetooth.available()) {
   Serial.print((char)bluetooth.read());
 }
 */
 
 bluetooth.print("X = ");
 bluetooth.print(axisX);
 bluetooth.print(", ");
 bluetooth.print("Y = ");
 bluetooth.print(axisY);
 bluetooth.print(", ");
 bluetooth.print("Z = ");
 bluetooth.print(axisZ);
 bluetooth.println();
}

