package weathersystem;

import java.io.*;
import java.net.*;
import java.util.*;


public class Handler implements Runnable {
    
    final DataInputStream inFromClient;
    final DataOutputStream outToClient;  
    
    //
    static String latitude;
    static String longitude;
    static String elevation;
    static String temperature;
    static String humidity;
    static String windSpeed;
    static String area;
    static String crop;
    static String stationID;
    
    //
    static String clientType;
    static String id;
    
    public Handler(Socket _client) throws IOException{
        Socket client = _client;
        
        inFromClient = new DataInputStream(client.getInputStream());
        outToClient = new DataOutputStream(client.getOutputStream());
    }

    @Override
    public void run() {
        try {
            //Get client type and register its ID
            clientType = getClientType(inFromClient);
            registerID(outToClient,inFromClient);
            System.out.println("Client: " + id + " has connected.");
            
            if (clientType.equals("WeatherStation")){
                weatherStationHandler(inFromClient, outToClient);
            }
            else if (clientType.equals("User")){
                userLogin(inFromClient, outToClient);
                userHandler(outToClient, inFromClient);//function to handle client request 
            }
            //If weatherstation, call weatherStationHandler and wait for 1s
            //If User, call userHandler
            Thread.sleep(1000);

        }
        catch (IOException | InterruptedException e) { }
    }
    
    static String getClientType(DataInputStream _inFromClient) throws IOException {
        //Read data in from client
        return _inFromClient.readUTF();
    }
    
    static void registerID(DataOutputStream _outToClient, DataInputStream _inFromClient) throws IOException {    
        //Depending on client type, increment corresponding variable
        //Assign ID varible to both the current handler thread and its client object 
        switch(clientType){
            case "WeatherStation" -> {
                Server.weatherStations++;
                id = "WS" + Integer.toString(Server.weatherStations);
                _outToClient.writeUTF(id);
                Server.weatherStationIDs.add(id);
                Server.stationsData.add((null));
                break;
            }
            case "User" -> {
                Server.users++;
                id = "U" + Integer.toString(Server.users);
                _outToClient.writeUTF(id);
                break;
            }
        }
        id = _inFromClient.readUTF();
    }
    
    static void weatherStationHandler(DataInputStream _inFromClient, DataOutputStream _outToClient) throws IOException {
        Server.screen.addClient(id);
        
        while(true){
            try {
                //Read all the data from the weather station and print to console
                latitude = _inFromClient.readUTF();
                longitude = _inFromClient.readUTF();
                elevation = _inFromClient.readUTF();
                temperature = _inFromClient.readUTF();
                humidity = _inFromClient.readUTF();
                windSpeed = _inFromClient.readUTF();
                area = _inFromClient.readUTF();
                crop = _inFromClient.readUTF();
                stationID = _inFromClient.readUTF();
                
                ArrayList<String> stationData = new ArrayList<>();
                
                stationData.add(stationID);
                stationData.add(latitude);
                stationData.add(longitude);
                stationData.add(elevation);
                stationData.add(temperature);
                stationData.add(humidity);
                stationData.add(windSpeed);
                stationData.add(area);
                stationData.add(crop);
                
                int stationNum = Integer.parseInt(stationData.get(0).substring(stationData.get(0).length() - 1));
                Server.stationsData.set(stationNum, stationData);
                
                Thread.sleep(1000);
            } 
            catch (IOException | InterruptedException e) { }
        }        
    }
    
    static void userLogin(DataInputStream _inFromClient, DataOutputStream _outToClient) throws IOException {
        
        //COMPARE USER DATA TO USERNAME AND PASSWORD STORED ON DATABASE
        String Duser="", Dpass="";
            
        //OPEN FILE, READ FOR USER AND PASS VARIABLES
        File file = new File("src/weathersystem/Database.txt");
        try (Scanner myReader = new Scanner(file)) {
            Duser = myReader.nextLine();
            Dpass = myReader.nextLine();
            myReader.close();
        } 
        catch (FileNotFoundException e) { }

        boolean loginSuccessful = false;       
        while (!loginSuccessful){
            //READ USER DATA
            String user = _inFromClient.readUTF();
            String pass = _inFromClient.readUTF();
            
            //COMPARISON
            if (user.equals(Duser) && (pass.equals(Dpass))){
                loginSuccessful = true;
                _outToClient.writeUTF("Login Successful");
            }
            else{
                _outToClient.writeUTF("Login Unsuccessful");
            }
        }
    }
    
    static void userHandler(DataOutputStream _outToClient, DataInputStream _inFromClient) throws IOException{
        Server.screen.addClient(id);
        
        while(true){
            try{
                //Update weather station list
                _outToClient.writeUTF(Integer.toString(Server.weatherStationIDs.size()));
                for (int i = 0; i < Server.weatherStationIDs.size(); i++) {
                    _outToClient.writeUTF(Server.weatherStationIDs.get(i));
                }
            
                //Send weather station data to user
                String currentStation = _inFromClient.readUTF();          
                for (int i = 0; i < Server.stationsData.size(); i++) {
                    if(Server.stationsData.get(i).get(0).equals(currentStation)){
                        _outToClient.writeUTF(Server.stationsData.get(i).get(0)); //ID
                        _outToClient.writeUTF(Server.stationsData.get(i).get(1)); //Lat
                        _outToClient.writeUTF(Server.stationsData.get(i).get(2)); //Lon
                        _outToClient.writeUTF(Server.stationsData.get(i).get(3)); //Ele
                        _outToClient.writeUTF(Server.stationsData.get(i).get(4)); //Temp
                        _outToClient.writeUTF(Server.stationsData.get(i).get(5)); //Hum
                        _outToClient.writeUTF(Server.stationsData.get(i).get(6)); //Wind
                        _outToClient.writeUTF(Server.stationsData.get(i).get(7)); //Area
                        _outToClient.writeUTF(Server.stationsData.get(i).get(8)); //Crop
                    }
                }
            }
            catch( NullPointerException e ){}
        }  
    }
}
