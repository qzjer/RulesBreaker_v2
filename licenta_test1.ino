#include <AFMotor.h>

// --------------- Pin defines ---------------

// Conveyor Belt
const int Enable = 31;          // On/Off Stepper 1
const int Direction_Pin = 33;   // Used to set the direction of the rotation
const int Stepper_Pin = 35;     // Used to select coils


// RGB LED
int   redPin = 47;              // The input pin for the RED color
int greenPin = 49;              // The input pin for the GREEN color
int  bluePin = 51;              // The input pin for the BLUE color


// ...MICRO SWITCH...
int buttonPin = 21;             // Micro Switch pin

// Push System
AF_Stepper motor2(200, 2);      // Used to initialize module 2 of the shield

// --------------- Variables defines ---------------

// LED Colors
#define red   0x01 // (hexa) 0x00000001 // (dec) 1 | red color is used as END OF PROCESS, namely, when is executed 
                   //                              | the *backToInitialPos()* function
#define blue  0x02 // (hexa) 0x00000010 // (dec) 2 | blue color is used as THE PROCESS ITSELF, namely, the action of
                   //                              | the *Conveyor Belt* & the *Push* method.
#define green 0x03 // (hexa) 0x00000011 // (dec) 3 | green color is used as READY TO DRINK, so it means you can take
                   //                              | your COCKTAIL.

// Conveyor directions
#define left  0xff
#define right 0x00

// Bluetooth flag
int Power_Switch;               // Used to power ON the system by bluetooth module

// Button flag
int buttonState = 0;            // Used to memorize the button state 
boolean timePassed = false;     // Used at debouce system 


void setup() 
{
  // ------------------ Setup pins (as output/input) ---------------------
  // Conveyor stepper pins
  pinMode(Stepper_Pin,   OUTPUT); 
  pinMode(Direction_Pin, OUTPUT);
  pinMode(Enable,        OUTPUT);
  digitalWrite(Stepper_Pin, LOW);
  digitalWrite(Enable,      LOW);

  // Switch button pin
  pinMode     (buttonPin, INPUT);
  digitalWrite(buttonPin,  HIGH);                                   // Enable pull-up resistor
  attachInterrupt(digitalPinToInterrupt(21), ISRButton, FALLING);   // Setup external interrupt (INT0) at FALLING edge

  // Timer setup for debounce
  noInterrupts();                                                   // Disable global interrupts
  TCCR1A = 0;                                                       // Set the timer for overflow interrupt
  TCCR1B = 0;                                                       // Timer off (initially) and use 256 prescaler (see atmega2560 datasheet)
  TCNT1 = 64910;                                                    // Set the timer counter (TCNT1) with 64910 => generate an interrupt at 0.01sec or 59285 for 0.1 sec
  TIMSK1 |= (1 << TOIE1);                                           // Enable timer overflow interrupt flag
  interrupts();                                                     // Enable external interrupts
  
  // RGB LED pins
  pinMode(redPin,  OUTPUT);
  pinMode(greenPin,OUTPUT);
  pinMode(bluePin, OUTPUT);
  
  delay(1000);        
  Serial.begin(9600);
}


void loop() 
{
  
   while (Serial.available() > 0) //daca e mai mare ca 0, atunci citeste 'Seriala-TX and RX' (pin 14&15)
            {    
               Power_Switch = Serial.read();
            }
            
        if (Power_Switch == '1') 
            {
              Serial.println("Cuba Libre             1");
               stepperRotate (270 ,left,1);
               stepperRotate (2500,left,2);
               backToInitialPos();
               Power_Switch = 0;
             }else if (Power_Switch == '2')
              {
               Serial.println("Gin Tonic             2");
               stepperRotate (770 , left, 1);
               stepperRotate (500 , left, 2);
               backToInitialPos();
               Power_Switch = 0; 
            }else if (Power_Switch == '3')
              {
               Serial.println("Long Island           3");
               stepperRotate (270 , left, 1);
               stepperRotate (500 , left, 1);
               stepperRotate (1500, left, 1);
               stepperRotate (500 , left, 1);
               backToInitialPos();
               Power_Switch = 0; 
            }else if (Power_Switch == '4')
              {
               Serial.println("Screw Driver          4");
               stepperRotate(2270, left , 1);
               stepperRotate(500 , right, 2);
               backToInitialPos();
               Power_Switch = 0; 
            }else if (Power_Switch == '5')
              {
               Serial.println("Vodka Coke            5");
               stepperRotate(2270, left , 1);
               stepperRotate(500 , left , 2);
               backToInitialPos();
               Power_Switch = 0; 
            }else if (Power_Switch == '6')
              {
               Serial.println("Vodka Tonic           6");
               stepperRotate(2270, left , 1);
               stepperRotate(1000, right, 2);
               backToInitialPos();
               Power_Switch = 0; 
            }else if (Power_Switch == '7')
              {
                Serial.println("Back to initial position");
                backToInitialPos();
              }
            
}

// Method used to command the push-up system
// nrOfRepeats - used to set the number of pushes needed for the x bottle

void turnOn_motor2(uint8_t nrOfRepeats) 
{
  uint8_t compareNumber = 0;                                    // Used in while condition to check if the number of pushed desired was reached
  Serial.println("__Push__ system is ON! --UP--, Delay(2 sec), --DOWN--");
  Serial.println("");        
  
  turn_on_RGB(blue);                                            // Turn on the LED (BLUE color) => procces was started
   
  while(compareNumber != nrOfRepeats)                           // While the pushes number is not equal with the desired number
  { 
      motor2.setSpeed(120);                                     
      motor2.step(4100,BACKWARD, INTERLEAVE);                          
      motor2.release();
      delay(2000);
      motor2.step(4100, FORWARD, INTERLEAVE); 
      motor2.release();
      delay(1000);
      compareNumber++;                                          // While one cycle was finished, increment the compareNumber
      
  }
  compareNumber = 0;                                            // When the desired number of cycled was reached reset the compare value
}


// This method is used to set a color for the RGB LED
// color - used to set the color LED (preset values)

void blinks_blue( int blink_LED_blue ){
  for (int x = 0; x <= blink_LED_blue; x++)
    {
    digitalWrite(bluePin, LOW);
    delay(200);
    digitalWrite(bluePin, HIGH);
    delay(200);
    }
}

void blinks_green( int blink_LED_green){
  for (int x = 0; x <= blink_LED_green; x++)
  {
    digitalWrite(greenPin, LOW);
    delay(200);
    digitalWrite(greenPin, HIGH);
    delay(200);
  }
}

void turn_on_RGB(uint8_t color)
{
  // RED Color
  if(color == 0x01)                                             // red = 0x01 got define at the start of the program
  {
    digitalWrite(redPin,HIGH);
  }
  // BLUE Color
  else if (color == 0x02)                                       // blue = 0x02 
  {
    digitalWrite(redPin,LOW);
    digitalWrite(greenPin,LOW);
    digitalWrite(bluePin,HIGH);
//    blinks_blue(2);
  }
  // GREEN Color
  else if (color == 0x03)                                       // green = 0x03
  {
    digitalWrite(redPin,LOW);
    digitalWrite(greenPin,HIGH);
    digitalWrite(bluePin,LOW);
    blinks_green(5);
  }
}


// This method is used to rotate the conveyor stepper at a desired position,
// in both directions and otherwise the set the number of push-up repeats
// nrOfTicks - used to set the rotations number (conveyor stepper)
// dir - used to set the conveyor stepper direction (left/right)
// nrOfRepeats - used to set the number of repeats (push-up system)

void stepperRotate(int nrOfTicks, uint8_t dir, uint8_t nrOfRepeats ){
// Left Direction
   if(dir == 0xFF)
     {
       digitalWrite(Direction_Pin, LOW);               // Direction_Pin on LOW, is LEFT rotation for a Clockwise rotation
       digitalWrite(Enable,         LOW);
       Serial.println("<<--------------");
       Serial.println("Conveyor Belt action");

       for (int x = 0; x <=nrOfTicks; x++)              // While the desired number of ticks is less than x
           {                                            // roatate the conveyor stepper (*x will be incremented 
              digitalWrite(Stepper_Pin, HIGH);          // with one after the stepper made one step
              delayMicroseconds(1100); 
              digitalWrite(Stepper_Pin, LOW ); 
              delayMicroseconds(1100);
              
                 if (x == nrOfTicks)                      
                 {                                                      // Stop the conveyor stepper and if the desired number of steps is equal 
                    digitalWrite(Enable,HIGH);                          // with the number that stepper made, stop the stepper and power the push-up system
                    turnOn_motor2(nrOfRepeats);         
                 }
            }        
     }
// Right Direction
   else if (dir == 0x00)                                                // See the defined constants
     {
       digitalWrite(Direction_Pin, HIGH);                               // Direction_Pin on HIGH, is RIGHT rotation
       digitalWrite(Enable,        LOW);
       Serial.println("-------------->>");
       Serial.println("Conveyor Belt action");

       for (int x = 0; x <=nrOfTicks; x++)                              // It is the same method used for *Left Direction*
           {                                                            // The only difference is that we set "Direction_Pin" on HIGH
              digitalWrite(Stepper_Pin, HIGH);                          // for a Counter Clockwise rotation
              delayMicroseconds(1100); 
              digitalWrite(Stepper_Pin, LOW ); 
              delayMicroseconds(1100);
              
                 if (x == nrOfTicks)
                 { 
                    digitalWrite(Enable,HIGH);
                    turnOn_motor2(nrOfRepeats);
                 }
           }        
     }
}

// This method is used to bring back the 
// conveyor stepper in the initial position

void backToInitialPos()
{
  Serial.println("Back to initial position ...  \\||");
  digitalWrite(Direction_Pin , HIGH);
  digitalWrite(Enable, LOW);                                            // Turn on the conveyor stepper 
  digitalWrite(bluePin, LOW);                                           // Cutting power for the Blue Light | procces has ended.
  turn_on_RGB(red);
  for (int x = 0; x <=100000; x++)                                      // Set an astronomic number of steps because we want that the conveyor stepper
      {                                                                 // to rotate until the "HOME" switch will be pressed
        digitalWrite(Stepper_Pin, HIGH); 
        delayMicroseconds(1100); 
        digitalWrite(Stepper_Pin, LOW ); 
        delayMicroseconds(1100);
            
        if((timePassed == true) && ((PIND & (1<<PIND0)) == 0))          // If timePassed flag is true => 0.01 sec was passed (spikes interval)    
          {                                                             // and the switch is pressed, stop the conveyor stepper (the "home" position  
           digitalWrite(Enable,HIGH);                                   // was reached), and put the timePassed flag back to false   
           timePassed = false;
              {
              Serial.println("Move a bit left, so the Power_Switch is set free");
              digitalWrite(Direction_Pin, LOW);
              digitalWrite(Enable, LOW);
            for (int x = 0; x <=100; x++)
               {
               digitalWrite(Stepper_Pin, HIGH); 
               delayMicroseconds(900); 
               digitalWrite(Stepper_Pin, LOW ); 
               delayMicroseconds(900);
               }
              }
               digitalWrite(Enable, HIGH);                              // Turn off the Stepper_1
               turn_on_RGB(green);                                      // Turn on GreenLight
               Power_Switch= 0;
               digitalWrite(greenPin, LOW);
           }       
       }
}

void ISRButton()                                                        // Interrupt service routine (ISR) asociated to the external interrupt (INT0)
{                                                                       // When the button was pressed, set the timer counter (TCNT) with the correspondent value (0.01s)
  TCNT1 = 64910;                                                        // and start the timer (initially he is stopped) 
  TCCR1B |= (1 << CS12); //pornim timerul
  
}

ISR(TIMER1_OVF_vect)                                                    // Interrupt Service Routine (ISR) asociated to the timer overflow 
{                                                                       // When the ISR routine was called => 0.01 sec was passed => put the timePassed flag on true
  timePassed = true;                                                    // and reset the timer counter value to 0. 
  TCCR1B = 0;
  Serial.println("                                                   There you are . . .  ||");
}