package weathersystem;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server {
    
    static ServerGUI screen = new ServerGUI();
    
    //Stores amount of weather stations and users
    static int weatherStations = -1;
    static int users = -1;
    
    static ArrayList<String> weatherStationIDs = new ArrayList<>();
    static ArrayList<ArrayList<String>> stationsData = new ArrayList<ArrayList<String>>();
    
    public static void main(String[] args) throws IOException{
        //Initialise GUI
        screen.setVisible(true);
        
        //Initialise ServerSocket with port 9090
        ServerSocket server = new ServerSocket(9090);

        while(true) {
            establishConnection(server);
        }
    }
    
    static void establishConnection(ServerSocket _server) throws IOException {
        
        Socket client = null;
        
        try{
            //Wait for client to connect
            client = _server.accept();
            
            //Instantiate Handler class and start it in a new Thread
            Handler t = new Handler(client);
            Thread th = new Thread(t);
            th.start();
            
        } catch(IOException e) {
            client.close();
        }
    }
}
