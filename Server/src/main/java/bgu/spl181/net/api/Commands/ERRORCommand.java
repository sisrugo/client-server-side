package bgu.spl181.net.api.Commands;

import java.io.Serializable;

public class ERRORCommand implements Serializable{

    private String message;

    public ERRORCommand(String str){
         this.message = commandName() + " " + str + " failed";

    }
    public String toString(){
        return message;
    }

    public String commandName(){
        return "ERROR";
    }

}
