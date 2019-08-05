package bgu.spl181.net.api.Protocol;

import bgu.spl181.net.api.Commands.*;
import bgu.spl181.net.api.ConnectionsImpl;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserServiceProtocol<T> implements BidiMessagingProtocol<T> {

    protected int currentConnectionId = -1; //will save the Client connection Id
    protected ConnectionsImpl<T> currentConnections = null; //will save the Client Connection
    protected Serializable resultCommand; //will save the ACK/ERROR commands
    protected UsersDatabase usersData;
    protected MoviesDatabase movieData;
    private boolean shouldTerm;
    protected String userName;
    protected boolean isLogin = false;

    public UserServiceProtocol(UsersDatabase usersdata, MoviesDatabase moviedata ){
        this.usersData = usersdata;
        this.movieData = moviedata;
        shouldTerm = false;
    }

    public void start(int connectionId, Connections<T> connections){
        this.currentConnectionId = connectionId;
        this.currentConnections = (ConnectionsImpl<T>) connections;

    }

    public void process(T message) {

        String[] trimByCountry = message.toString().trim().split("country="); //will split at index 0 before the = and after at index 1
        String[] trimBySpaces = trimByCountry[0].trim().split(" ");
        String[] commandSplit = null;
        if(trimByCountry.length > 1) {
            commandSplit = new String[trimBySpaces.length+1];
            for (int i = 0;i<trimBySpaces.length;i++)
                commandSplit[i] = trimBySpaces[i];
            commandSplit[commandSplit.length-1] = trimByCountry[1].replace("\"", "");
        }
        String commandType = trimBySpaces[0].toUpperCase();


        if (commandType.equals("REGISTER")) {
            if (userName != null || commandSplit == null) { // user already logged in, can't preform registration
                resultCommand = new ERRORCommand("registration");
                return;
            }
            resultCommand = register(commandSplit);

        } else if (commandType.equals("LOGIN")) {
            resultCommand = login(trimBySpaces);
        }
        else if (commandType.equals("SIGNOUT")) {
            resultCommand = logout();
        }
        return;
    }

    private Serializable register(String[] commandSplit){
        Command newCommand = new RegisterCommand();
        newCommand.SetSharedUserData(usersData);
        return newCommand.execute(commandSplit);
    }

    private Serializable login (String[] commandSplit) {
        Command newCommand = new LoginCommand();

        newCommand.SetSharedUserData(usersData);
        if (userName != null){
            return new ERRORCommand("login");
        }
        Serializable commandStatus = newCommand.execute(commandSplit);
        if (commandSplit.length > 1 && commandStatus instanceof ACKCommand) {
            this.userName = commandSplit[1]; //save the user name at the protocol field
            usersData.addToLoggedByCHId(currentConnectionId, userName);
            isLogin = usersData.isLoggedIn(userName);
        }
        return commandStatus;
    }

    private Serializable logout(){
        Serializable commandStatus;
        if (this.userName != null && usersData.disconnectUser(this.userName)) {
            commandStatus = new ACKCommand("signout succeeded");
            shouldTerm = true;
            currentConnections.disconnect(currentConnectionId, (T) commandStatus); //disconnect the client
        } else {
            commandStatus = new ERRORCommand("signout");
        }
        return commandStatus;
    }

    public boolean shouldTerminate(){
        return shouldTerm;
    }

    public void broadcastToLogged(T msg){
        ConcurrentHashMap<String, Integer> loggedList = usersData.getLoggedUsersByCHId();
        for(Map.Entry<String, Integer> entry: loggedList.entrySet()) {
            Integer CHId = entry.getValue();
                currentConnections.send(CHId, msg );

        }
    }

}
