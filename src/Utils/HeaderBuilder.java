package Utils;

import java.util.Date;

public class HeaderBuilder {

    public static String HTTP_503(int file_length, String content_type){
        return """
                HTTP/1.1 501 Not Implemented
                Server: HTTP Server from Orus : 1.0
                Data: """ + new Date() +
                "\nContent-type: " + content_type +
                "\nContent-length: " + file_length + "\n";
    }

    public  static  String HTTP_200(int file_length, String content_type){
        return """
                HTTP/1.1 200 Not Implemented
                Server: HTTP Server from Orus : 1.0
                Data: """ + new Date() +
                "\nContent-type: " + content_type +
                "\nContent-length: " + file_length + "\n";
    }
}
