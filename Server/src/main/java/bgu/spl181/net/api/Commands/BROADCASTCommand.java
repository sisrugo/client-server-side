package bgu.spl181.net.api.Commands;

import java.io.Serializable;

public class BROADCASTCommand implements Serializable {

    private String message;

    public BROADCASTCommand (String str){
        this.message = commandName() + " " + str;

    }
    public String toString(){
        return message;
    }

    public String commandName(){
        return "BROADCAST";
    }
}
