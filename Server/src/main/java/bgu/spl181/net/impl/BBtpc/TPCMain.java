package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.api.BidiMessageEncoderDecoder;
import bgu.spl181.net.api.Protocol.BBProtocol;
import bgu.spl181.net.api.Protocol.MoviesDatabase;
import bgu.spl181.net.api.Protocol.UsersDatabase;
import bgu.spl181.net.srv.Server;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class TPCMain {
    public static void main(String [] args) throws IOException{
//      Parse the JSON file
        ObjectMapper mapper = new ObjectMapper();

        MoviesDatabase moviesJson = mapper.readValue(new File(System.getProperty("user.dir")+"/Database/Movies.json"), MoviesDatabase.class);
        UsersDatabase usersJson = mapper.readValue(new File(System.getProperty("user.dir")+"/Database/Users.json"), UsersDatabase.class);

            Server.threadPerClient(
                7777, //port
                () -> new BBProtocol<>(usersJson, moviesJson),//protocol factory
                () -> new BidiMessageEncoderDecoder<>()
            ).serve();


    }


}
