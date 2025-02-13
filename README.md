# Network Monitoring and Automated Response System  

This project implements a **network monitoring and automated response system** using **Java (Client) and Python (Server)**. The system continuously monitors network activity, detects potential issues, and triggers predefined actions.  

## **Key Features**  

- **Real-time network monitoring**  
- **Automated alerts & responses** (e.g., log analysis, service restarts)  
- **Client-server communication over TCP**  
- **Generates a connection status report in HTML**  
- **Cross-platform compatibility**  

## **Technologies Used**  

- **Java (Client):** Socket programming, multithreading, logging  
- **Python (Server):** Socket programming, threading, file handling  

## **Project Structure**  
/network-monitoring-system │── mainScripts/ │ ├── Client.java # Java client to connect to multiple servers │── server.py # Python server for network monitoring │── README.md # Project documentation


## **Setup & Usage**  

### **1. Run the Python Server**  

1. Install dependencies (if required):  
   ```bash
   pip install -r requirements.txt
2. Start the server:
   python server.py
3. The server will listen on PORT 1050 for incoming client connections.

### **2. Configure & Run the Java Client**
1. Modify Client.java to specify target server IPs:
  String[] servers = { "192.168.1.10", "192.168.1.20" };
2. Compile and run the client:
  javac mainScripts/Client.java
  java mainScripts.Client
3. The client will attempt to connect to the servers and generate an HTML report (connections.html) listing successful and failed connections.

### **3. 3. View Connection Status Report**
   After running the client, an HTML report (connections.html) is generated. It can be opened manually or automatically in Google Chrome.

   ***Additional Notes***
The system checks for log file patterns (configurable in server.py).
Connection status is updated every 3 hours.
The system may shut down the machine if a critical condition is met (see shutdown_pc() in server.py).
⚠️ Disclaimer: This project is for educational purposes only. Use responsibly.
