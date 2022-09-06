package weathersystem;

import java.net.*;
import java.io.*;

public class UserClient {
    
    static LoginGUI screenLogin = new LoginGUI();
    static ClientGUI screenClient = new ClientGUI();
    
    static DataInputStream inFromServer;
    static DataOutputStream outToServer;
    
    static final String clientType = "User";
    static String id = null;
    
    static boolean loggedIn = false;
    static String selectedStation = "";
    
    public static void main(String[] args) throws IOException {
        //Initialise user GUI
        screenLogin.setVisible(true);
        
        connectToServer();
        while(true){
            System.out.print("");
            if(loggedIn){
                updateWeatherStationList(inFromServer);
                receiveData(inFromServer, outToServer);
            }
        }
    }
    
    static void connectToServer() throws IOException {
        
        try {
            //Create socket with localhost and port 9090
            InetAddress address = InetAddress.getByName("127.0.0.2");
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
    
    static void getCredentials(String username, String password) throws IOException{
        sendCredentials(outToServer, inFromServer, username, password);
    }
    
    static void sendCredentials(DataOutputStream _outToServer, DataInputStream _inFromServer, String username, String password) throws IOException{
        _outToServer.writeUTF(username);
        _outToServer.writeUTF(password);
        
        String success = _inFromServer.readUTF();
        if(success.equals("Login Successful")){
            loggedIn = true;
            screenLogin.setVisible(false);
            screenClient.setVisible(true);
        }
    }
    
    static void logout(){
        loggedIn = false;
        screenClient.setVisible(false);
        screenLogin.setVisible(true);
    }
    
    static void updateWeatherStationList(DataInputStream _inFromServer) throws IOException {     
        int loops = Integer.parseInt(_inFromServer.readUTF());
        for (int i = 0; i < loops; i++) {
            String station = _inFromServer.readUTF();
            screenClient.addWeatherStation(station);
        }
    }
    
    static void receiveData(DataInputStream _inFromServer, DataOutputStream _outToServer)throws IOException {
        selectedStation = screenClient.getStation();
        _outToServer.writeUTF(selectedStation);
        
        String stationID = _inFromServer.readUTF();
        String lat = _inFromServer.readUTF();
        String lon = _inFromServer.readUTF();
        String ele = _inFromServer.readUTF();
        String temp = _inFromServer.readUTF();
        String hum = _inFromServer.readUTF();
        String wind = _inFromServer.readUTF();
        String area = _inFromServer.readUTF();
        String crop = _inFromServer.readUTF();
        
        screenClient.updateFields(stationID, lat, lon, ele, temp, hum, wind, area, crop);
    }
}
