package weathersystem;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class WeatherStationClient {
    
    static DataInputStream inFromServer;
    static DataOutputStream outToServer;
    
    static final String clientType = "WeatherStation";
    static String id = null;
    
    static boolean connected = false;
    
    static float latitude = 0;
    static float longitude = 0;
    static float elevation = 0;
    static float humidity = 0;
    static float temperature = 0;
    static float windSpeed = 0;
    
    static float area = 0;
    static String crop = "";
    
    public static void main(String[] args) throws IOException {
        connectToServer();
        generateFieldData();
        
        while(true) {
            sendData(outToServer);
        }
    }
    
    static void connectToServer() throws IOException {
        try {
            //Create socket with localhost and port 9090
            InetAddress address = InetAddress.getByName("127.0.0.3");
            Socket server = new Socket(address, 9090);
            
            inFromServer = new DataInputStream(server.getInputStream());
            outToServer = new DataOutputStream(server.getOutputStream());
            
            registerDetails(inFromServer, outToServer);

        } catch (IOException e) { }   
    }
    
    static void registerDetails(DataInputStream _inFromServer, DataOutputStream _outToServer) throws IOException {
        //Sent clienttype to server
        _outToServer.writeUTF(clientType);
        
        //Get id from server and assign to variable, then send it back
        id = _inFromServer.readUTF();
        _outToServer.writeUTF(id);
    }
    
    static void generateFieldData(){
        //Generate random data
        Random rand = new Random();
        area = rand.nextInt(100 + 100000); //Field area should be between 100 and 100,000
        
        String[] crops = {"Corn", "Carrot", "Potato", "Turnip", "Lettuce", "Beans", "Wheat"};
        crop = crops[rand.nextInt(crops.length-1)];
    }
    
    static void generateSensorData() {
        //Generate random data
        Random rand = new Random();
        
        //GPS
        latitude = rand.nextInt(90 + 90) - 90; //lat should be between -90 and 90
        longitude = rand.nextInt(180 + 180) - 180; //long should be between -180 band 180
        elevation = rand.nextInt(1000 + 1000) - 1000; //elevation should be between -1000 and 1000
        
        //Field Data
        temperature = rand.nextInt(50 + 50) - 50; //temperature should be between -50 and 50
        humidity = rand.nextInt(101); //humidity should be between 0 and 100
        windSpeed = rand.nextInt(101);
    }
    
    static void sendData(DataOutputStream _outToServer) throws IOException{ 
        generateSensorData();
        
        //Send all data to server
        _outToServer.writeUTF(Float.toString(latitude));
        _outToServer.writeUTF(Float.toString(longitude));
        _outToServer.writeUTF(Float.toString(elevation));
        _outToServer.writeUTF(Float.toString(humidity));
        _outToServer.writeUTF(Float.toString(temperature));
        _outToServer.writeUTF(Float.toString(windSpeed));
        
        _outToServer.writeUTF(Float.toString(area));
        _outToServer.writeUTF(crop);
        
        _outToServer.writeUTF(id);
    }
}
