package bgu.spl181.net.api.Protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A class that holds the BB service database regarding movies.
 * Also, this class is responsible for keeping the movies' JSON file (also holding the BB service database) up to date.
 */
public class MoviesDatabase {

    @JsonProperty("movies")
    private ArrayList<Movie> movies;

    @JsonIgnore
    protected ReentrantReadWriteLock moviesRWLock = new ReentrantReadWriteLock();

    @JsonIgnore
    protected ObjectMapper mapper = new ObjectMapper();


    /**
     * request for info regarding all movies
     * @return a String with all movies' names in the database
     */
    public String moviesInfo(){
        moviesRWLock.readLock().lock();
        String moviesList = "";
        for (Movie movie: movies){ // for each movie in tha database
            moviesList = moviesList + "\"" + movie.getName() + "\" ";
        }
        moviesRWLock.readLock().unlock();
        return moviesList.trim();
    }

    /**
     * request info on a specific movie
     * @param movieName the name of the movie
     * @return String which holds the requested movie name, available amount, price and banned countries (if there are any).
     */
    public String movieInfo(String movieName){
        moviesRWLock.readLock().lock();
        String toReturn = null;
        for (Movie movie: movies){
            if(movie.getName().equals(movieName)){ // searches for such movie in the database
                    toReturn = "\"" + movie.getName() + "\" " + movie.getAvailableAmount() + " " + movie.getPrice();
                    String[] bannedCountries = movie.getBannedCountries();
                    for (int i = 0 ; i < bannedCountries.length ; i++)
                        toReturn = toReturn + " \"" + bannedCountries[i] + "\"";
            }
        }
        // if no such movie exist- - returning null.
        moviesRWLock.readLock().unlock();
        return toReturn;
    }

    /**
     * returns movie from specific user rented movies
     * @param userName
     * @param movieName
     * @param usersData holds the user data, required to access user data
     * @return true if action succeeded or false otherwise
     */
    public boolean returnMovie(String userName, String movieName, UsersDatabase usersData){
        moviesRWLock.writeLock().lock();
        boolean isCompleted = false;
        for (Movie movie : movies){
            if (movie.getName().equals(movieName)){ // searches if movieName exist in the database
                synchronized (movie){
                    if (usersData.removeMovieFromUser(userName,movieName)){ // returns true if request to remove movie from user had succeeded
                        // user had this movie in his rented movies list
                        Integer newAvailable = Integer.parseInt(movie.getAvailableAmount())+ 1; //increment available copies of the movies
                        movie.setAvailableAmount(newAvailable.toString());
                        updateJSON();
                        isCompleted = true;
                    }
                    else{ // couldn't remove movie from user. (user didn't exist | user didn't have this movie)
                        moviesRWLock.writeLock().unlock();
                        return isCompleted;
                    }
                }
            }
        }
        //couldn't find movie in the database
        moviesRWLock.writeLock().unlock();
        return isCompleted;
    }

    /**
     * request from userName to rent movieName
     * @param userName
     * @param movieName
     * @param usersData holds the user data, required to access user data
     * @return true if action succeeded or false otherwise
     */
    public boolean rentMovie(String userName, String movieName, UsersDatabase usersData){
        moviesRWLock.writeLock().lock();
        boolean isCompleted = false;
        for (Movie movie : movies){ // searches for movieName in the movie database
            if (movie.getName().equals(movieName)){
                synchronized (movie){
                    if (Integer.parseInt(movie.getAvailableAmount()) < 1){ // checks that there is at least 1 copy of this movie available
                        moviesRWLock.writeLock().unlock();
                        return isCompleted; // false
                    }
                    // tries to add movie to user's rented movies list. will succeed if user exist, he has enough balance and if this movie is not rented by him.
                    if (usersData.addMovieToUser(userName,movie.getId(),movieName, movie.getPrice(), movie.getBannedCountries())){ // succeeded
                        Integer newAvailable = Integer.parseInt(movie.getAvailableAmount())- 1;
                        movie.setAvailableAmount(newAvailable.toString()); //decrement 1 from available amount
                        updateJSON();
                        isCompleted = true;
                    }
                    else{ // couldn't add movie to user
                        moviesRWLock.writeLock().unlock();
                        return isCompleted;
                    }
                }
            }
        }
        // there is no movie with that name in the database
        moviesRWLock.writeLock().unlock();
        return isCompleted;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Admin Methods. will arrive here AFTER checking if the user is admin
    //////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * adds new movie to the database
     * @param movieName
     * @param amount
     * @param price
     * @param bannedContries
     * @return true if action succeeded or false otherwise
     */
    public boolean addMovie(String movieName, String amount, String price, String[] bannedContries){
        moviesRWLock.writeLock().lock();
        Integer maxId = 0; // will hold the max movie id from the movie database
        for (Movie movie: movies){
            maxId = Math.max(maxId, Integer.parseInt(movie.getId()));
            if (movie.getName().equals(movieName)){ // checks if there is a movie in the database with that name
                moviesRWLock.writeLock().unlock();
                return false;
            }
        }
        Movie toAdd = new Movie();
        maxId++; // maxId now holds the max movieId + 1;
        toAdd.setInitialMovieValues(maxId.toString(),movieName,amount,price);
        toAdd.setBannedCountries(bannedContries);
        movies.add(toAdd); // adds movie to database
        updateJSON();
        moviesRWLock.writeLock().unlock();
        return true;
    }

    /**
     * removes a movie from the database. will fail if there is no such movie or some user possess a copy of that movie
     * @param movieName
     * @return true if action succeeded or false otherwise
     */
    public boolean removeMovie(String movieName){
        moviesRWLock.writeLock().lock();
        for (Movie movie: movies){
            if (movie.getName().equals(movieName)){ // checks if the movie is in the database
                synchronized (movie){
                    if (movie.getTotalAmount().equals(movie.getAvailableAmount())){ // there are no rented copies of this movie
                        movies.remove(movie); // removes movie
                        updateJSON();
                        moviesRWLock.writeLock().unlock();
                        return true;
                    }
                    // there are rented copies of this movie
                    moviesRWLock.writeLock().unlock();
                    return false;
                }
            }
        }
        // the movie is not in the database
        moviesRWLock.writeLock().unlock();
        return false;
    }

    /**
     * change the price for a movie
     * @param movieName
     * @param newPrice
     * @return true if action succeeded or false otherwise
     */
    public boolean changePrice(String movieName, String newPrice){
        moviesRWLock.writeLock().lock();
        for (Movie movie: movies){// checks if the movie is in the database
            if (movie.getName().equals(movieName)){
                synchronized (movie){
                    movie.setPrice(newPrice); // sets new price
                    updateJSON();
                    moviesRWLock.writeLock().unlock();
                    return true;
                }
            }
        }
        // the movie in not in the database
        moviesRWLock.writeLock().unlock();
        return false;
    }

    /**
     * update the users database JSON file
     */
    ////////////////////////////////////// NEED TO CHANGE THE LOCATION IN WHICH WE SAVE THE NEW JSON FILE ////////////////////////////////////////////////////////////
    private synchronized void updateJSON(){
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(System.getProperty("user.dir") + "/Database/Movies.json"), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getPriceAndCopies(String movieName){
        moviesRWLock.readLock().lock();
        for (Movie movie: movies){
            if (movie.getName().equals(movieName)){
                moviesRWLock.readLock().unlock();
                return movie.getAvailableAmount() + " " + movie.getPrice();
            }
        }
        moviesRWLock.readLock().unlock();
        return null;
    }

}
