package Utils;

import java.io.*;
import java.util.Date;

import static Constants.ServerConstants.WEB_ROOT;


public class Response {
    private final PrintWriter response_out;
    private final OutputStream response_data_out;
    private final int file_length;
    private final String content;
    private final byte[] fileData;

    public Response(String file_requested, PrintWriter out, OutputStream data_out) throws IOException {
        // TODO handle images/gifs
        content = getContentType(file_requested);
        File file = new File(WEB_ROOT, file_requested);
        file_length = (int) file.length();
        //System.out.println("FILE LENGTH: "+file_length);
        fileData = readFileData(file, file_length);
        response_out = out;
        response_data_out = data_out;
    }

    public void setHeader(int status_code){
        String header;
        switch (status_code) {
            case 200 -> header = HeaderBuilder.HTTP_200(file_length, content);
            case 404 -> header = HeaderBuilder.HTTP_404(file_length, content);
            case 503 -> header = HeaderBuilder.HTTP_503(file_length, content);
            case 400 -> header = HeaderBuilder.HTTP_400(file_length, content);
            default -> {
                System.out.println("SET HEADER : Unknown status code");
                header = null;
                System.exit(1);
            }
        }
        response_out.println(header);
    }

    public void setBody() throws IOException {
        response_data_out.write(fileData,0,file_length);
    }

    public void send() throws IOException {
        response_out.flush();
        response_data_out.flush();
    }

    private static class HeaderBuilder{
        public static String HTTP_503(int file_length, String content_type){
            return """
                HTTP/1.1 501 Not Implemented
                Server: HTTP Server from Orus : 1.0
                Date: """ + new Date() +
                    "\nContent-type: " + content_type +
                    "\nContent-length: " + file_length +
                    "\n\n";
        }

        public static String HTTP_200(int file_length, String content_type){
            return """
                HTTP/1.1 200 OK
                Server: HTTP Server from Orus : 1.0
                Date: """ + new Date() +
                    "\nContent-type: " + content_type +
                    "\nContent-length: " + file_length +
                    "\n\n";
        }

        public static String HTTP_404(int file_length, String content_type){
            return """
                HTTP/1.1 404 File Not Found
                Server: HTTP Server from Orus : 1.0
                Date: """ + new Date() +
                    "\nContent-type: " + content_type +
                    "\nContent-length: " + file_length +
                    "\n\n";
        }

        public static String HTTP_400(int file_length, String content_type){
            return """
                HTTP/1.1 400 Bad Request
                Server: HTTP Server from Orus : 1.0
                Date: """ + new Date() +
                    "\nContent-type: " + content_type +
                    "\nContent-length: " + file_length +
                    "\n\n";
        }
    }

    private String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm")  ||  fileRequested.endsWith(".html"))
            return "text/html";
        else if (fileRequested.endsWith(".jpg"))
            return "image/jpeg";
        else if (fileRequested.endsWith(".gif"))
            return "image/gif";
        else
            return "text/plain";
    }

    private byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null)
                fileIn.close();
        }
        return fileData;
    }

    public String getContent(){
        return content;
    }
}
