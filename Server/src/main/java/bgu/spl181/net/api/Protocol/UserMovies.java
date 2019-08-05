package bgu.spl181.net.api.Protocol;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A class that will hold (basic) info regarding the movie's the user had rented
 */
public class UserMovies {

    @JsonProperty("id")
    private String id = null;

    @JsonProperty("name")
    private String name = null;

    public void setInitialValues(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
