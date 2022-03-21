package Constants;

import java.io.File;
import java.util.*;

public class ServerConstants {

    public int PORT;

    public static final File WEB_ROOT = new File("src/WebRoot/");;
    public final String DEFAULT_FILE;
    public final String FILE_NOT_FOUND;
    public final String METHOD_NOT_SUPPORTED;


    public List<String> SUPPORTED_METHODS;

    public final HashMap<String,String> ENDPOINTS;

    public ServerConstants(){
        PORT = 8080;

        // FILES

        DEFAULT_FILE = "index.html";
        FILE_NOT_FOUND = "404.html";
        METHOD_NOT_SUPPORTED = "not_supported.html";


        SUPPORTED_METHODS = Arrays.asList("GET", "HEAD");


        ENDPOINTS = new HashMap<>();
        ENDPOINTS.put("/", DEFAULT_FILE);
        ENDPOINTS.put("/res/404.jpg", "res/http_cats/404.jpg");
        ENDPOINTS.put("/res/running_fox.gif", "res/index/running_fox.gif");
    }
}
