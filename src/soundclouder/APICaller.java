package soundclouder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
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
public class APICaller {

    private static Logger logger = LoggerFactory.getLogger(APICaller.class);

    public static void main(String[] args) {
        Properties props = new Properties();

        try {
            props.loadFromXML(new FileInputStream("./soundclouder.xml"));

            // Create a wrapper.
            ApiWrapper wrapper = new ApiWrapper(props.getProperty("client_id"),
                    props.getProperty("client_secret"), null, null);

            //Obtain a token.
            wrapper.login(props.getProperty("uname"), props.getProperty("passwd"));

            HttpResponse resp0 = wrapper.get(Request.to("/me"));

            // Change some stuff in your profile.
            HttpResponse resp1 = wrapper.put(Request.to("/me")
                    .with("user[full_name]", "Vassilis Moustakas",
                            "user[website]", "http://www.gravatar.com/vsmoustakas")
                    .withFile("user[avatar_data]",
                            new File("/home/billy/Pictures/avatars/me.jpg")));

        }
        catch (InvalidPropertiesFormatException ipfe) {
            logger.error("Ooops... something terrible must have happened :-( Have you provided a properties XML file?");
            ipfe.printStackTrace();
        }
        catch (FileNotFoundException fnfe) {
            logger.error("Ooops... something terrible must have happened :-( Have you provided a properties XML file?");
            fnfe.printStackTrace();
        }
        catch (IOException ioe) {
            logger.error("Ooops... something terrible must have happened :-(");
            logger.error("Either you haven't provider a properties XML file or could not log-in to SoundCloud.");
            ioe.printStackTrace();
        }
    }

}
