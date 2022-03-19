package Constants;

import java.io.File;

public class ServerConstants {
    public static int PORT = 8080;

    public static final File WEB_ROOT = new File("WebRoot/");
    public static final String DEFAULT_FILE = "index.html";
    public static final String FILE_NOT_FOUND = "404.html";
    public static final String METHOD_NOT_SUPPORTED = "not_supported.html";

    // DEFAULT OPTIONS
    public static boolean verbose = false;
}
