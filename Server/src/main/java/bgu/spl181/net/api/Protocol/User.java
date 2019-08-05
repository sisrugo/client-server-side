package bgu.spl181.net.api.Protocol;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A class that represent a user of our BB service.
 */
public class User {
    @JsonProperty("username")
    private String username = null;

    @JsonProperty ("type")
    private String type = "normal";

    @JsonProperty ("password")
    private String password = null;

    @JsonProperty ("country")
    private String country = null;

    @JsonProperty ("movies")
    private ArrayList<UserMovies> movies = new ArrayList<UserMovies>();;

    @JsonProperty ("balance")
    private String balance = "0";

    private ReentrantReadWriteLock userRWLock = new ReentrantReadWriteLock();

    /**
     * Sets initial values for user
     * @param name user's name
     * @param password user's password
     * @param country user's country
     */
    public void setInitialUserValues(String name, String password, String country){
        userRWLock.writeLock().lock();
        setUsername(name);
        setPassword(password);
        setCountry(country);
        userRWLock.writeLock().unlock();
    }

    public String getUsername() {
        userRWLock.readLock().lock();
        String ans = username;
        userRWLock.readLock().unlock();
        return ans;
    }

    public void setUsername(String username) {
        userRWLock.writeLock().lock();
        this.username = username;
        userRWLock.writeLock().unlock();
    }

    public String getType() {
        userRWLock.readLock().lock();
        String ans = type;
        userRWLock.readLock().unlock();
        return ans;
    }

    public String getPassword() {
        userRWLock.readLock().lock();
        String ans = password;
        userRWLock.readLock().unlock();
        return ans;
    }

    public void setPassword(String password) {
        userRWLock.writeLock().lock();
        this.password = password;
        userRWLock.writeLock().unlock();
    }

    public String getCountry() {
        userRWLock.readLock().lock();
        String ans = country;
        userRWLock.readLock().unlock();
        return ans;
    }

    public void setCountry(String country) {
        userRWLock.writeLock().lock();
        this.country = country;
        userRWLock.writeLock().unlock();
    }

    public ArrayList<UserMovies> getMovies() {
        userRWLock.readLock().lock();
        ArrayList<UserMovies> ans = movies;
        userRWLock.readLock().unlock();
        return ans;
    }

    public void addMovie(UserMovies movie) {
        userRWLock.writeLock().lock();
        movies.add(movie);
        userRWLock.writeLock().unlock();
    }

    public void removeMovie(UserMovies movie) {
        userRWLock.writeLock().lock();
        movies.remove(movie);
        userRWLock.writeLock().unlock();
    }

    public String getBalance() {
        userRWLock.readLock().lock();
        String ans = balance;
        userRWLock.readLock().unlock();
        return ans;
    }

    public void setBalance(String balance) {
        userRWLock.writeLock().lock();
        this.balance = balance;
        userRWLock.writeLock().unlock();
    }

    public boolean checkIfAdmin() {
        userRWLock.readLock().lock();
        boolean ans = type.equals("admin");
        userRWLock.readLock().unlock();
        return ans;
    }
}