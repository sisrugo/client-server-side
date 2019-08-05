package bgu.spl181.net.api.Commands;

import bgu.spl181.net.api.Protocol.UsersDatabase;

import java.io.Serializable;

public class LoginCommand<T> implements Command<T> {

    private UsersDatabase databaseUser = null;

    public Serializable execute(T arg) {
        String[] detailArray = (String[]) arg;
        if (detailArray.length != 3) { //there is not enough details at input
            return new ERRORCommand(this.toString());
        }
        String userName = detailArray[1];
        if(databaseUser.isLoggedIn(userName)) //check if user is already logged
            return new ERRORCommand(this.toString());
        String passwordUser = detailArray[2];
        if (databaseUser.loginUser(userName, passwordUser)) {

            return new ACKCommand(this.toString() + " succeeded");
        }
        else
         return new ERRORCommand(this.toString());
    }

    public void SetSharedUserData(UsersDatabase data){
        this.databaseUser = data;
    }

    public String toString(){
        return "login";
    }
}

