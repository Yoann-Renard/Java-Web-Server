package Utils;

import java.util.Date;

public class HeaderBuilder {

    public static String HTTP_503(int file_length, String content_type){
        return """
                HTTP/1.1 501 Not Implemented
                Server: HTTP Server from Orus : 1.0
                Date: """ + new Date() +
                "\nContent-type: " + content_type +
                "\nContent-length: " + file_length +
                "\n\n";
    }

    public  static  String HTTP_200(int file_length, String content_type){
        return """
                HTTP/1.1 200 OK
                Server: HTTP Server from Orus : 1.0
                Date: """ + new Date() +
                "\nContent-type: " + content_type +
                "\nContent-length: " + file_length +
                "\n\n";
    }

    public  static  String HTTP_404(int file_length, String content_type){
        return """
                HTTP/1.1 404 File Not Found
                Server: HTTP Server from Orus : 1.0
                Date: """ + new Date() +
                "\nContent-type: " + content_type +
                "\nContent-length: " + file_length +
                "\n\n";
    }

    public  static  String HTTP_400(int file_length, String content_type){
        return """
                HTTP/1.1 400 Bad Request
                Server: HTTP Server from Orus : 1.0
                Date: """ + new Date() +
                "\nContent-type: " + content_type +
                "\nContent-length: " + file_length +
                "\n\n";
    }
}
