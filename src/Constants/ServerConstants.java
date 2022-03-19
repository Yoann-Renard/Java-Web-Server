package Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerConstants {
    public static int PORT = 8080;

    public static final File WEB_ROOT = new File("src/WebRoot/");
    public static final String DEFAULT_FILE = "index.html";
    public static final String FILE_NOT_FOUND = "404.html";
    public static final String METHOD_NOT_SUPPORTED = "not_supported.html";

    // DEFAULT OPTIONS
    public static boolean verbose = false;

    public static List<String> SUPPORTED_METHODS = Arrays.asList("GET", "HEAD");

}
