Hello, thank you for dropping by! Please review the steps below. Instructions can also be found in Spring-Boot-Docker.docx!

About the program:
  This was written using SpringBoot, Java, and Maven.

Requirements:
To run this program you should have access to Docker and Postman. 

Steps to run the program:
1. Clone the repo
2. On your command line, go to the directory where you cloned the repo
3. Run the following command to build the jar file: mvn clean install
4. Run the following command to build the docker image: docker build -t fetch-receipt-processor-image . 
5. Open your Docker application and verify that your image is listed under images
6. Ensure port 8080 is available on your local. Terminate any processes running on this port.
7. On your command line, enter this command to run the container: docker-compose up
8. On Docker, verify the container is now running
9. Head over to Postman, you will need the following requests set up:
    POST:  http://localhost:8080/receipts/process
    GET: http://localhost:8080/receipts/{id}/points
10. The application should now be up and running!
