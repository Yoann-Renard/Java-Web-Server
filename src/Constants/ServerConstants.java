package Constants;

import java.io.File;
import java.util.*;

public class ServerConstants {

    public int PORT;

    public final File WEB_ROOT;
    public final String DEFAULT_FILE;
    public final String FILE_NOT_FOUND;
    public final String METHOD_NOT_SUPPORTED;

    // DEFAULT OPTIONS
    public boolean verbose = false;

    public List<String> SUPPORTED_METHODS;

    public final HashMap<String,String> ENDPOINTS;

    public ServerConstants(){
        PORT = 8080;

        // FILES
        WEB_ROOT = new File("src/WebRoot/");
        DEFAULT_FILE = "index.html";
        FILE_NOT_FOUND = "404.html";
        METHOD_NOT_SUPPORTED = "not_supported.html";

        // OPTIONS
        verbose = false;

        SUPPORTED_METHODS = Arrays.asList("GET", "HEAD");


        ENDPOINTS = new HashMap<>();
        ENDPOINTS.put("/", DEFAULT_FILE);
    }
}
