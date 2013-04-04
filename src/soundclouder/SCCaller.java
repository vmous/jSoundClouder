package soundclouder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;

/**
 * The simplest caller class possible for the SoundCloud API.
 *
 * @author billy
 */
public class SCCaller {

    public static final String CONF_FILE = "./soundclouder.xml";

    private static Logger logger = LoggerFactory.getLogger(SCCaller.class);

    private final Properties props;

    private final ApiWrapper caller;

    private boolean loggedIn;

    /**
     * Constructor.
     */
    public SCCaller() {
        props = new Properties();

        try {
            // Load properties
            props.loadFromXML(new FileInputStream(CONF_FILE));
        }
        catch (IOException ioe) {
            logger.error("Ooops... Could not load properties. The appliaction will misbehave!");
            ioe.printStackTrace();
        }

        // Create a wrapper.
        caller = new ApiWrapper(props.getProperty("client_id"),
                props.getProperty("client_secret"), null, null);

        loggedIn = false;
    }


    public static void main(String[] args) {
        SCCaller soundclouder = new SCCaller();

        soundclouder.login(
                soundclouder.getProps().getProperty("uname"),
                soundclouder.getProps().getProperty("passwd"));

        soundclouder.updateProfile();
    }


    /**
     * Change the SoundCloud user profile.
     */
    public boolean updateProfile() {
        logger.info("Requesting to update user profile...");
        boolean success = false;

        if (loggedIn) {
            try {
                HttpResponse resp = caller.put(Request.to("/me")
                        .with("user[full_name]", "Vassilis Moustakas",
                                "user[website]", "http://www.gravatar.com/vsmoustakas")
                        .withFile("user[avatar_data]",
                                new File("/home/billy/Pictures/avatars/light_bulb.jpg")));
            } catch (IOException ioe) {
                logger.error("Unable to PUT the request.");
                ioe.printStackTrace();
            }
        }
        else {
            logger.error("Cannot update a profile without being logged-in first.");
        }

        return success;
    }


    /**
     * Log-in the caller with the given username and password.
     *
     * @param uname
     *     The username (typically an e-mail).
     * @param passwd
     *     The password.
     *
     * @return
     *     {@code true} if the log-in was successful; {@code false} otherwise.
     */
    public boolean login(String uname, String passwd) {
        boolean success = false;

        if (loggedIn) {
            success = true;
            logger.info("We are already logged-in!");
        }
        else {

            try {
                //Try to obtain a token.
                caller.login(uname, passwd);
                success = true;
            }
            catch (IOException ioe) {
                logger.error("Ooops... Unable to acquire log-in token :-(");
                ioe.printStackTrace();
            }
        }

        return (this.loggedIn = success);
    }


    // -- Getters/Setters


    /**
     * Getter for the caller properties set by the user.
     *
     * @return
     *     The properties of the caller.
     */
    public Properties getProps() {
        return props;
    }


    /**
     * Getter to query if the caller is logged-in or not.
     *
     * @return
     *     {@code true} if the user is logged-in; {@code false} otherwise.
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

}
