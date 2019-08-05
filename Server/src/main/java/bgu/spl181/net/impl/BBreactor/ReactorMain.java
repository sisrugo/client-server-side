package bgu.spl181.net.impl.BBreactor;

import bgu.spl181.net.api.BidiMessageEncoderDecoder;
import bgu.spl181.net.api.Protocol.BBProtocol;
import bgu.spl181.net.api.Protocol.MoviesDatabase;
import bgu.spl181.net.api.Protocol.UsersDatabase;
import bgu.spl181.net.srv.Server;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class ReactorMain {

    public static void main(String[] args) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        MoviesDatabase moviesJson = mapper.readValue(new File(System.getProperty("user.dir") + "/Database/Movies.json"), MoviesDatabase.class);
        UsersDatabase usersJson = mapper.readValue(new File(System.getProperty("user.dir") + "/Database/Users.json"), UsersDatabase.class);


                Server.reactor(
//                Runtime.getRuntime().availableProcessors(),
                        7,
                7777, //port
                () ->  new BBProtocol<>(usersJson, moviesJson), //protocol factory
                () -> new BidiMessageEncoderDecoder()
        ).serve();


    }

}


//    bin/BBClient '127.0.0.1' '7777' <InputOutput/in1.txt

// Server run command
// mvn exec:java -Dexec.mainClass="bgu.spl181.net.impl.BBtpc.TPCMain" -Dexec.args="7777"
// mvn exec:java -Dexec.mainClass="bgu.spl181.net.impl.BBreactor.ReactorMain" -Dexec.args="7777"
