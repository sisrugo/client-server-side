package bgu.spl181.net.api.Protocol;

import bgu.spl181.net.api.Commands.ACKCommand;
import bgu.spl181.net.api.Commands.BROADCASTCommand;
import bgu.spl181.net.api.Commands.ERRORCommand;
import bgu.spl181.net.api.bidi.Connections;
import java.io.Serializable;
import java.util.HashMap;

public class BBProtocol<T> extends UserServiceProtocol<T> {

    public BBProtocol(UsersDatabase usersdata, MoviesDatabase moviedata) {
        super(usersdata, moviedata);
    }

    public void start(int connectionId, Connections<T> connections) {
        super.start(connectionId, connections);
    }

    public void process(T message) {
        super.process(message); //first we send the message to USP
        String[] detailArray = message.toString().split(" ");
        Serializable broadcastMessage = null;

        detailArray[0] = detailArray[0].toUpperCase();
        if (this.resultCommand == null && detailArray[0].equals("REQUEST")) { // continue only if the current user log in,the command is a request command and no other command has been excute
            detailArray[1] = detailArray[1].toUpperCase();
            if (detailArray.length > 2 && detailArray[1].equals("BALANCE")) { //check the kind of request command of the user
                detailArray[2] = detailArray[2].toUpperCase();

                if (detailArray[2].equals("INFO")) {
                    resultCommand = balanceInfoReq();

                }
                else if (detailArray[2].equals("ADD")) {
                    resultCommand = addedingBalanceReq(detailArray);
                }
            }
            else if (detailArray[1].equals("INFO")) {
                resultCommand = getInfoMovieReq(message.toString());

            } else if (detailArray[1].equals("RENT")) {
                broadcastMessage = rentReq(message.toString());

            } else if (detailArray[1].equals("RETURN")) {
                broadcastMessage = returnReq(message.toString());
            ///requests for admin only
            } else if (detailArray[1].equals("ADDMOVIE")) {
                broadcastMessage = addMovieReq(message.toString());

            } else if (detailArray[1].equals("REMMOVIE")) {
                broadcastMessage = remMovieReq(message.toString());

             } else if (detailArray[1].equals("CHANGEPRICE")) {
                broadcastMessage = changePriceReq(message.toString());
                }
        }
        if (resultCommand == null) //if no command been found yet
                this.resultCommand = new ERRORCommand("Unknown Command");

        currentConnections.send(currentConnectionId, (T) this.resultCommand); // send result command to cilent ACK/ERROR

        if (broadcastMessage != null && broadcastMessage instanceof BROADCASTCommand){
            broadcastToLogged((T)broadcastMessage);
        }
        resultCommand = null;
    }

    /**
     * this method is for split and organize of the input
     * @param detail is the givin input
     * @return a hash map that holdes the movie details
     */
    private HashMap<String, String> findMovieDetails(String detail) {
        HashMap<String, String> moviedetails = new HashMap<>();
        String[] splitDetails = detail.split("\""); // split by quote mark
        if (splitDetails.length > 1) {
            moviedetails.put("moviename", splitDetails[1]);
            if (splitDetails.length > 2) {
                splitDetails[0] = splitDetails[0].trim();
                splitDetails[2] = splitDetails[2].trim();
                String[] amountNprice = splitDetails[2].split(" "); // split by spaces
                moviedetails.put("amount", amountNprice[0]);
                if (amountNprice.length > 1){
                    moviedetails.put("price", amountNprice[1]);
                }
                int j = 0;
                for (int i = 3; i < splitDetails.length; i++) { //save all the banned countries at the array
                    if (!splitDetails[i].equals(" ")) {
                        moviedetails.put("country" + j++, splitDetails[i]);
                    }
                }
            }
        }
        return moviedetails;
    }


    private Integer addBalance(String balance) {
        return Integer.parseInt(usersData.updateBalance(this.userName, balance));
    }


    private Serializable addedingBalanceReq(String[] detailArray){
        Serializable commandStatus;
        if (!isLogin)
            commandStatus = new ERRORCommand("request balance");
        else{
            Integer currentBalance = Integer.parseInt(usersData.getBalance(this.userName));
            String addedBalance = detailArray[3];
            Integer output = addBalance(addedBalance);
            if (isLogin && output == currentBalance + Integer.parseInt(addedBalance)) { // if the returned balance has changed, the adding
                commandStatus = new ACKCommand("balance " + output.toString() + " added " + addedBalance);
            } else
                commandStatus = new ERRORCommand("request balance");
        }

        return commandStatus;
    }

    private Serializable balanceInfoReq () {
        Serializable commandStatus;
        String output = usersData.getBalance(this.userName);
        if (isLogin && output != null)
            commandStatus = new ACKCommand("balance " + output);
        else
            commandStatus = new ERRORCommand("request balance"); //it means the user is not found
        return commandStatus;
    }

    private Serializable getInfoMovieReq(String message) {
        Serializable commandStatus;
        if (!isLogin) {
            commandStatus = new ERRORCommand("request info");
        }
        else{
            String info;
            HashMap <String, String> detail = findMovieDetails(message);
            if(detail.containsKey("moviename")) { //check if the user input a movie name, if so we will return the movie details
                String movieName = detail.get("moviename");
                info = movieData.movieInfo(movieName);
                if(info != null) // it will be null if the "movieinfo" method faild
                {
                    commandStatus = new ACKCommand("info " + info);
                }
                else {
                    commandStatus = new ERRORCommand("request info");
                }

            }else {
                info = movieData.moviesInfo(); //if the user didnt input a movie name, return all the movies at the BB details
                commandStatus = new ACKCommand("info " + info);
            }
        }
        return commandStatus;
    }

    private Serializable rentReq(String message) {
        Serializable broadcastMessage = null;
        if (!isLogin)
            resultCommand = new ERRORCommand("request rent");
        else{

            boolean ifsucceed = false;
            String moviename = findMovieDetails(message).get("moviename");
            if(moviename != null) {
                ifsucceed = movieData.rentMovie(userName, moviename, usersData); //use the 'rentMovie' method at moviedata
            }

            if (ifsucceed) {
                resultCommand = new ACKCommand("rent \"" + moviename + "\" success");
//                currentConnections.send(currentConnectionId, (T)commandStatus); // sends the ACK message
                broadcastMessage = new BROADCASTCommand("movie \"" + moviename + "\" " + movieData.getPriceAndCopies(moviename));


            } else
                resultCommand = new ERRORCommand("request rent");

        }
        return broadcastMessage;
    }

    private Serializable returnReq(String message) {
        Serializable broadcastMessage = null;
        if (!isLogin)
            resultCommand = new ERRORCommand("request return");
        else{

            boolean ifsucceed = false;
            String moviename = findMovieDetails(message).get("moviename");
            if(moviename != null) {
                ifsucceed= movieData.returnMovie(userName, moviename, usersData); //use the 'returnMovie' method at moviedata
            }

            if (ifsucceed) {
                resultCommand = new ACKCommand("return \"" + moviename + "\" success");
//                currentConnections.send(currentConnectionId, (T)commandStatus); // sends the ACK message
                broadcastMessage = new BROADCASTCommand("movie \"" + moviename + "\" " + movieData.getPriceAndCopies(moviename));
            } else
                resultCommand = new ERRORCommand("request return");

        }
        return broadcastMessage;
    }

    private Serializable addMovieReq(String message) {
        Serializable broadcastMessage = null;
        if (!isLogin)
            resultCommand = new ERRORCommand("request addmovie");
        else{
            boolean ifSucceeded;
            HashMap<String, String> movieDet = findMovieDetails(message);

            if ((!usersData.isAdmin(userName)) || (movieDet.get("amount") == null || Integer.parseInt(movieDet.get("amount")) < 1 ) || ((movieDet.get("price")==null) || Integer.parseInt(movieDet.get("price")) < 1)) // check if input price and amount of copies is valid
            {
                ifSucceeded = false;
            }
            else {
                String[] countries = new String[movieDet.size() - 3]; //array for the banned countries
                for (int i = 0; i < movieDet.size() - 3; i++) {
                    countries[i] = movieDet.get("country" + i);
                }
                ifSucceeded = movieData.addMovie(movieDet.get("moviename"), movieDet.get("amount"), movieDet.get("price"), countries); //use the 'addMovie' method at moviedata
            }
            if (ifSucceeded){
                resultCommand = new ACKCommand("addmovie \"" + movieDet.get("moviename") + "\" success");
//                currentConnections.send(currentConnectionId, (T)commandStatus); // sends the ACK message
                broadcastMessage = new BROADCASTCommand("movie \"" + movieDet.get("moviename") + "\" " + movieDet.get("amount") + " " + movieDet.get("price"));
            }
            else
                resultCommand = new ERRORCommand("request addmovie");
        }
        return broadcastMessage;

    }

    private Serializable remMovieReq (String message){
        Serializable broadcastMessage = null;
        if (!isLogin)
            resultCommand = new ERRORCommand("request remmovie");
        else{

            boolean ifsucceed = false;
            String movieName = findMovieDetails(message).get("moviename");
            if(usersData.isAdmin(userName)) {
                ifsucceed = movieData.removeMovie(movieName); //use the 'removeMovie' method at moviedata
            }

            if (ifsucceed) {
                resultCommand = new ACKCommand("remove \"" + movieName + "\" success");
//                currentConnections.send(currentConnectionId, (T)commandStatus); // sends the ACK message
                broadcastMessage = new BROADCASTCommand("movie \"" + movieName + "\" removed");
            } else
                resultCommand = new ERRORCommand("request remmovie");
        }
        return broadcastMessage;
    }

    private Serializable changePriceReq (String message){
        Serializable broadcastMessage = null;
        if (!isLogin)
            resultCommand = new ERRORCommand("request changeprice");
        else{
            boolean ifsucceed = false;
            HashMap<String, String> details = findMovieDetails(message);
            String moviename = details.get("moviename");
            String price = details.get("amount");
            //check if input price is valid and if user is admin
            if(usersData.isAdmin(userName) && price != null && Integer.parseInt(price) > 0) {
                ifsucceed = movieData.changePrice(moviename, price); //use the 'removeMovie' method at moviedata
            }

            if (ifsucceed) {
                resultCommand = new ACKCommand("changeprice \"" + moviename + "\" success");
//                currentConnections.send(currentConnectionId, (T)commandStatus); // sends the ACK message
                broadcastMessage = new BROADCASTCommand("movie \"" + moviename + "\" " + movieData.getPriceAndCopies(moviename));
            } else
                resultCommand = new ERRORCommand("request changeprice");
        }
        return broadcastMessage;

    }
}

