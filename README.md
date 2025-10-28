BUS BOOKING SYSTEM - SERVER DEPLOYMENT

1. PREREQUISITES
   - Java 17
   - Apache Tomcat 10.x
   - Port 8080 available

2. DEPLOYMENT
   1) Navigate to Tomcat installation directory
   2) find webapps folder inside Tomact , delete all the files and folder inside webapps and make sure webapps directory is empty
   a) Copy ROOT.war to Tomcat's webapps/ folder
   b) Start Tomcat:
      - Linux/Mac: ./bin/startup.sh
      - Windows: bin\startup.bat
3. VERIFY
   Open browser: http://localhost:8080/ROOT/api/seats/availability?origin=B&destination=D&passengerCount=3
   Should return JSON with seat availability


BUS BOOKING SYSTEM - CLIENT USAGE

1. PREREQUISITES
   - Java 17
   - Server must be running on 8080 port

2. RUNNING THE CLIENT
   
   Open terminal/command prompt:
   java -jar busbooking-client.jar

3. USAGE
   
   Menu Options:
   1. Check Seat Availability
      - Enter origin (A/B/C/D/E)
      - Enter destination (A/B/C/D/E)
      - Enter passenger count
      
   2. Reserve Tickets
      - Enter origin (A/B/C/D/E)
      - Enter destination (A/B/C/D/E)
      - Enter passenger count
      - Enter total price
      
   3. Exit


Improvements:

1. API must be secured
2. API validation
3. API to cancel reservation
4. Logging
5. Race condition issue must be addressed
