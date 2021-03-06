


 
#define DEBUG false
 

void setup()
{
  pinMode(13,OUTPUT);
  digitalWrite(13,LOW);
  
  
  Serial1.begin(115200); // your esp's baud rate might be different
  Serial.begin(115200);
  

  pinMode(2,OUTPUT);
  digitalWrite(2,LOW);
  digitalWrite(13,LOW);
  //delay(5000);//Serial.begin(9600); 
  //sendCommand("AT+RST\r\n",2000,DEBUG); // reset module
  sendCommand("AT+CWMODE=3\r\n",1000,DEBUG); // configure as access point
  sendCommand("AT+CWSAP=\"ESP2\",\"password\",1,4\r\n",3000,DEBUG);
  
  delay(10000);
  
  
  sendCommand("AT+CIPMUX=1\r\n",1000,DEBUG); // configure for multiple connections
  sendCommand("AT+CIPSERVER=1,80\r\n",1000,DEBUG); // turn on server on port 80
  //Serial.println("Server Ready");
  
}
 
void loop()
{
  if(Serial1.available()) // check if the esp is sending a message 
  {
 
    
    if(Serial1.find("+IPD,"))
    {
   //   Serial.print("yes");
     delay(300); // wait for the serial buffer to fill up (read all the serial data) //1000
     // get the connection id so that we can then disconnect
     int connectionId = Serial1.read()-48; // subtract 48 because the read() function returns 
                                           // the ASCII decimal value and 0 (the first decimal number) starts at 48
          
     Serial1.find("ctrl"); // advance cursor to "ctrl"
          
     int command = (Serial1.read()-48);
     //delay(1000);
     //String content = "OK";     
     //sendHTTPResponse(connectionId,content);
     
     // make close command
     //String closeCommand = "AT+CIPCLOSE="; 
     //closeCommand+=connectionId; // append connection id
     //closeCommand+="\r\n";
     
     //sendCommand(closeCommand,1000,DEBUG); // close connection
///Serial.print(command);
     
        if (command == 1)
        {
          digitalWrite(13,LOW);
          digitalWrite(2,HIGH);
        }
        else if (command == 2)
        {
          digitalWrite(13,HIGH);
          digitalWrite(2,LOW);
          Serial.write(command);
        }
      
      
     }
    
     
    }
  }
  
 
/*
* Name: sendData
* Description: Function used to send data to ESP8266.
* Params: command - the data/command to send; timeout - the time to wait for a response; debug - print to Serial window?(true = yes, false = no)
* Returns: The response from the esp8266 (if there is a reponse)
*/
String sendData(String command, const int timeout, boolean debug)
{
    String response = "";
    
    int dataSize = command.length();
    char data[dataSize];
    command.toCharArray(data,dataSize);
           
    Serial.write(data,dataSize); // send the read character to the esp8266
    if(debug)
    {
      Serial.println("\r\n====== HTTP Response From Arduino ======");
      Serial.write(data,dataSize);
      Serial.println("\r\n========================================");
    }
    
    long int time = millis();
    
    while( (time+timeout) > millis())
    {
      while(Serial.available())
      {
        
        // The esp has data so display its output to the serial window 
        char c = Serial.read(); // read the next character.
        response+=c;
      }  
    }
    
    if(debug)
    {
      Serial.print(response);
    }
    
    return response;
}
 
/*
* Name: sendHTTPResponse
* Description: Function that sends HTTP 200, HTML UTF-8 response
*/
void sendHTTPResponse(int connectionId, String content)
{
     
     // build HTTP response
     String httpResponse;
     String httpHeader;
     // HTTP Header
     httpHeader = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=UTF-8\r\n"; 
     httpHeader += "Content-Length: ";
     httpHeader += content.length();
     httpHeader += "\r\n";
     httpHeader +="Connection: close\r\n\r\n";
     httpResponse = httpHeader + content + " "; // There is a bug in this code: the last character of "content" is not sent, I cheated by adding this extra space
     sendCIPData(connectionId,httpResponse);
}
 
/*
* Name: sendCIPDATA
* Description: sends a CIPSEND=<connectionId>,<data> command
*
*/
void sendCIPData(int connectionId, String data)
{
   String cipSend = "AT+CIPSEND=";
   cipSend += connectionId;
   cipSend += ",";
   cipSend +=data.length();
   cipSend +="\r\n";
   sendCommand(cipSend,1000,DEBUG);
   sendData(data,1000,DEBUG);
}
 
/*
* Name: sendCommand
* Description: Function used to send data to ESP8266.
* Params: command - the data/command to send; timeout - the time to wait for a response; debug - print to Serial window?(true = yes, false = no)
* Returns: The response from the esp8266 (if there is a reponse)
*/
String sendCommand(String command, const int timeout, boolean debug)
{
    String response = "";
           
    Serial1.print(command); // send the read character to the esp8266
    
    long int time = millis();
    
    while( (time+timeout) > millis())
    {
      while(Serial1.available())
      {
        
        // The esp has data so display its output to the serial window 
        char c = Serial1.read(); // read the next character.
        response+=c;
      }  
    }
    
    if(debug)
    {
      Serial.print(response);
    }
    
    return response;
}
