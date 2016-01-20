package com.pbasolutions.android.authentication;

import com.pbasolutions.android.json.PBSLoginJSON;

/**
 * Created by pbadell on 6/25/15.
 * Interface that allows different server connection type to implement authentication process.
 */
public interface PBSIServerAuthenticate {
    /**
     * Authenticate user upon sign in process. Result will return
     * a User object with stored of username, password and authToken.
     * @param username username to be used upon sign in.
     * @param pass password of that user must match its user name.
     * @return PBS_LoginJSON object that holds needed information for user session.
     **/
    public PBSLoginJSON userSignIn(final String username, final String pass, final String serial, String ServerURI);

    /**
     * Authenticate server upon opening apps before login. Result will return
     * a string of authenticated server URI.
     * @param serverURI server URI to communicate with.
     * @return authenticated Server URI string.
     **/
    public boolean authenticateServerURI(final String serverURI);

    /**
     * Send user log out request to server.
     * @param username username used to log in.
     * @param authToken used.
     * @param serverURL server url.
     * @return authenticated Server URI string.
     **/
    public boolean userLogOut(final String username, final String authToken, final String serverURL);

    /**
     * Authenticate user token via server.
     * @param username
     * @param authToken
     * @param serverURL
     * @param deviceID
     * @return
     */
    public boolean  authenticateTokenServer(final String username, final String authToken, final String serverURL, final String deviceID);

    /**
     * @param roleID
     * @param orgID
     * @param clientID
     * @param serverURL
     * @return
     */
    public boolean submitRole(final String roleID, final String orgID, final String clientID, final String serverURL);
}
