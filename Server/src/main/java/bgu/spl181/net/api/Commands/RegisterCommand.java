package bgu.spl181.net.api.Commands;

import bgu.spl181.net.api.Protocol.User;
import bgu.spl181.net.api.Protocol.UsersDatabase;
import java.io.Serializable;


public class RegisterCommand<T> implements Command<T> {

    private UsersDatabase databases = null;

    public Serializable execute(T arg){

        String[] detailArray = (String[]) arg;
        if (detailArray.length != 4){  //there is not enough details at input
            return new ERRORCommand(this.toString());
        }
        String userName = detailArray[1];
        if(databases.isLoggedIn(userName))  //check if user is already logged
            return new ERRORCommand(this.toString());
        String userPassword = detailArray[2];
        String country = detailArray[3];

        User newUser = new User();
        newUser.setInitialUserValues(userName, userPassword, country);

        if(databases.registerUser(newUser))
            return new ACKCommand(this.toString() + " succeeded");
        else
            return new ERRORCommand(this.toString());
    }

    public void SetSharedUserData(UsersDatabase data){
        this.databases = data;
    }

    public String toString(){
        return "registration";
    }
}
