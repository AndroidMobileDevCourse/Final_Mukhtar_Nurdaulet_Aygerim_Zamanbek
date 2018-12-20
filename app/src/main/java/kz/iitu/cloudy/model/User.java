package kz.iitu.cloudy.model;

/**
 * Created by 1506k on 5/17/18.
 */

public class User {

    private String mUsername;
    private String mPassword;

    public User() { }

    public User(String username, String password) {
        mUsername = username;
        mPassword = password;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }
}
