package bgu.spl181.net.api.Protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A class that holds the BB service database regarding users.
 * including logged in users, and registered users.
 * Also, this class is responsible for keeping the users' JSON file (also holding the BB service database) up to date.
 */
public class UsersDatabase {
    @JsonProperty("users")
    protected ArrayList<User> registeredUsersMap = null;

    @JsonIgnore
    protected ReentrantReadWriteLock usersRWLock = new ReentrantReadWriteLock();

    @JsonIgnore
    private ConcurrentHashMap<String, User> loggedInUsersMap = new ConcurrentHashMap<>();

    @JsonIgnore
    private ConcurrentHashMap<String ,Integer> loggedUsersByCHId = new ConcurrentHashMap<>();

    @JsonIgnore
    protected ObjectMapper mapper = new ObjectMapper();

    /**
     * register a user to our service. add given user to users' database
     * @param user to add
     * @return true if action succeeded or false if action failed
     */
    public boolean registerUser(User user){
        usersRWLock.writeLock().lock();
        for (User currentUser: registeredUsersMap){ // searching for user named userName
            if (currentUser.getUsername().equals(user.getUsername())){ //user is registered and the password matchs
                usersRWLock.writeLock().unlock();
                return false;
            }
        }

        registeredUsersMap.add(user);
        updateJSON();
        usersRWLock.writeLock().unlock();
        return true;
    }

    public void addToLoggedByCHId (Integer id, String userName){
        loggedUsersByCHId.put(userName, id);
    }

    public ConcurrentHashMap<String, Integer> getLoggedUsersByCHId() {
        return loggedUsersByCHId;
    }

    /**
     * add an already registered user to the logged in users
     * @param userName
     * @param password users password
     * @return true if action succeeded or false if action failed
     */
    public boolean loginUser(String userName, String password) {
        usersRWLock.writeLock().lock();
        if (loggedInUsersMap.containsKey(userName)) { // user is already logged in
            usersRWLock.writeLock().unlock();
            return false;
        }
        for (User user: registeredUsersMap){ // searching for user named userName
            if (user.getUsername().equals(userName) && user.getPassword().equals(password)){ //user is registered and the password matchs
                loggedInUsersMap.put(userName, user);
                usersRWLock.writeLock().unlock();
                return true;
            }
        }
        // there is no user registered with that name || the userName-password combination doesn't match
        usersRWLock.writeLock().unlock();
        return false;
    }

    /**
     * check if a given user is logged in
     * @param userName
     * @return true if user logged in or false if he isn't
     */
    public boolean isLoggedIn (String userName){
        usersRWLock.readLock().lock();
        User user = loggedInUsersMap.get(userName);
        if (user != null){
            usersRWLock.readLock().unlock();
            return true;
        }
        else{ // there is no registered user with that name
            usersRWLock.readLock().unlock();
            return false;
        }
    }

    /**
     * checks if a user is admin
     * @param userName
     * @return true if user is an admin or false otherwise
     */
    public boolean isAdmin (String userName){
        usersRWLock.readLock().lock();
        for (User user: registeredUsersMap){ // searching for user named userName
            if (user.getUsername().equals(userName) && user.getType().equals("admin")){ //user is registered and the password matchs
                usersRWLock.readLock().unlock();
                return true;
            }
        }
        usersRWLock.readLock().unlock();
        return false;
    }

    /**
     * removes user from logged in users list
     * @param userName
     * @return true if action succeeded or false if action failed
     */
    public boolean disconnectUser (String userName) {
        usersRWLock.writeLock().lock();
        if (!loggedInUsersMap.containsKey(userName)) { // there is no logged in user with that name
            usersRWLock.writeLock().unlock();
            return false;
        }
        loggedInUsersMap.remove(userName);
        usersRWLock.writeLock().unlock();
        return true;
    }

    /**
     * update the users database JSON file
     */
    private synchronized void updateJSON(){
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(System.getProperty("user.dir") + "/Database/Users.json"), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * changes the user balance
     * - adds in case of request to add balance
     * - deduct balance in case of balance "usage" (renting movie)
     * @param userName
     * @param balanceChange amount to deduct (negative) or add (positive)
     * @return balance after change
     */
    public String updateBalance(String userName, String balanceChange){
        usersRWLock.writeLock().lock();
        // balanceChanges can be positive in the case of add balance, or negative in the case of using balance
        for (User user: registeredUsersMap) {// searching for user named userName
            if (user.getUsername().equals(userName)) {
                    Integer newBalance = Integer.parseInt(user.getBalance()) + Integer.parseInt(balanceChange);
                    user.setBalance(newBalance.toString());
                    updateJSON();
                    usersRWLock.writeLock().unlock();
                    return user.getBalance();
            }
        } // there is no registered user with that name
        usersRWLock.writeLock().unlock();
        return null;

    }

    /**
     * get the given user balance
     * @param userName
     * @return user's balance
     */
    public String getBalance (String userName){
        usersRWLock.readLock().lock();
        for (User user: registeredUsersMap) { // searching for user named userName
            if (user.getUsername().equals(userName)) {
                usersRWLock.readLock().unlock();
                return user.getBalance();
            }
        }
        // there is no registered user with that name
        usersRWLock.readLock().unlock();
        return null;
    }

    /**
     * removes given movie from user's movie list (if exists)
     * @param userName
     * @param movieName
     * @return true if action succeeded or false if action failed
     */
    public boolean removeMovieFromUser (String userName, String movieName){
        usersRWLock.writeLock().lock();
        for (User user: registeredUsersMap) {// searching for user named userName
            if (user.getUsername().equals(userName)) {
                    ArrayList<UserMovies> userMovieList = user.getMovies(); // list of movies currently rented by user
                    for (UserMovies userMovie : userMovieList) { // searching for movie named movieName
                        if (userMovie.getName().equals(movieName)) {
                            user.removeMovie(userMovie);
                            updateJSON();
                            usersRWLock.writeLock().unlock();
                            return true;
                        }
                    }
                    // there is no movie with that name in the user's currently rented movies
                    usersRWLock.writeLock().unlock();
                    return false;
            }
        }
        // there is no registered user with that name
        usersRWLock.writeLock().unlock();
        return false;
    }

    /**
     * adds a movie to the user's currently rented movies
     * @param userName
     * @param movieId
     * @param movieName
     * @param moviePrice
     * @param bannedCountries list of countries that don't allow this movie
     * @return true if action succeeded or false if action failed
     */
    public boolean addMovieToUser (String userName, String movieId, String movieName, String moviePrice, String[] bannedCountries){
        usersRWLock.writeLock().lock();
        for (User user: registeredUsersMap) {// searching for user named userName
            if (user.getUsername().equals(userName)) {
                    UserMovies movie = new UserMovies(); // creates entry for this movie
                    movie.setInitialValues(movieId, movieName);

                    for (UserMovies userMovie : user.getMovies()) { // searching for movie named movieName
                        if (userMovie.getName().equals(movieName)) {
                            usersRWLock.writeLock().unlock();
                            return false;
                        }
                    }

                    Integer newBalance = Integer.parseInt(user.getBalance()) - Integer.parseInt(moviePrice);
                    if (newBalance < 0){ // user doesn't have enough balance to rent this movie
                        usersRWLock.writeLock().unlock();
                        return false;
                    }
                    for (int i = 0; i < bannedCountries.length; i++){
                        if (user.getCountry().equals(bannedCountries[i])){ // user lives in a country that banned this movie
                            usersRWLock.writeLock().unlock();
                            return false;
                        }
                    }

                    // updates user's balance and rented movie list
                    user.setBalance(newBalance.toString());
                    user.addMovie(movie);
                    updateJSON();
                    usersRWLock.writeLock().unlock();
                    return true;
            }
        }
        // there is no registered user with that name
        usersRWLock.writeLock().unlock();
        return false;
    }

}
