package bgu.spl181.net.api.Protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A class that represents a movie in our BB service
 */
public class Movie {
    @JsonProperty ("id")
    private String id = null;

    @JsonProperty ("name")
    private String name = null;

    @JsonProperty ("price")
    private String price = null;

    @JsonProperty ("bannedCountries")
    private String[] bannedCountries = null;

    @JsonProperty ("availableAmount")
    private String availableAmount = "0";

    @JsonProperty ("totalAmount")
    private String totalAmount = "0";

    private ReentrantReadWriteLock movieRWLock = new ReentrantReadWriteLock();


    public void setInitialMovieValues(String id, String name, String amount, String price){
        movieRWLock.writeLock().lock();
        this.id = id;
        this.name = name;
        this.availableAmount = amount;
        this.totalAmount = amount;
        this.price = price;
        movieRWLock.writeLock().unlock();
    }

    public String getId() {
        movieRWLock.readLock().lock();
        String ans = id;
        movieRWLock.readLock().unlock();
        return ans;
    }

    public String getName() {
        movieRWLock.readLock().lock();
        String ans = name;
        movieRWLock.readLock().unlock();
        return ans;
    }

    public String getPrice() {
        movieRWLock.readLock().lock();
        String ans = price;
        movieRWLock.readLock().unlock();
        return ans;
    }

    public void setPrice(String price) {
        movieRWLock.writeLock().lock();
        this.price = price;
        movieRWLock.writeLock().unlock();
    }

    public String[] getBannedCountries() {
        movieRWLock.readLock().lock();
        String[] ans = bannedCountries;
        movieRWLock.readLock().unlock();
        return ans;
    }

    public void setBannedCountries(String[] bannedCountries) {
        movieRWLock.writeLock().lock();
        this.bannedCountries = bannedCountries;
        movieRWLock.writeLock().unlock();
    }

    public String getAvailableAmount() {
        movieRWLock.readLock().lock();
        String ans = availableAmount;
        movieRWLock.readLock().unlock();
        return ans;
    }

    public void setAvailableAmount(String availableAmount) {
        movieRWLock.writeLock().lock();
        this.availableAmount = availableAmount;
        movieRWLock.writeLock().unlock();
    }

    public String getTotalAmount() {
        movieRWLock.readLock().lock();
        String ans = totalAmount;
        movieRWLock.readLock().unlock();
        return ans;
    }

}
