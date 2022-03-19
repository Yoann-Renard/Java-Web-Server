import Constants.ServerConstants;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

public class HTTPServer implements Runnable {

    // Client Connection via Socket Class
    private Socket connect;

    public HTTPServer(Socket c) {
        connect = c;
    }

    public static void main(String[] args){
        for (int i = 0; i < args.length; i++){
            switch (args[i]){
                case "-p":
                case "--port":
                    try {
                        ServerConstants.PORT = Integer.parseInt(args[i + 1]);
                    } catch (NumberFormatException e){
                        System.out.println("--port option value must be an integer: " + e.getMessage());
                        System.exit(1);
                    }
                    i++;
                    break;
                case "-v":
                case "--verbose":
                    ServerConstants.verbose = true;
                default:
                    break;
            }
        }

        try{
            ServerSocket server_connect = new ServerSocket(ServerConstants.PORT);
            System.out.println("Server started.\nListening for connections on port : " + ServerConstants.PORT + " ...\n");

            while(true){
                HTTPServer server = new HTTPServer(server_connect.accept());
                if (ServerConstants.verbose) {
                    System.out.println("Connection opened. (" + new Date() + ")");
                }

                Thread thread = new Thread(server);
                thread.start();
            }

        } catch (IOException e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }

    @Override
    public void run(){
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream data_out = null;
        String file_requested = null;

        try{
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            out = new PrintWriter(connect.getOutputStream());
            data_out = new BufferedOutputStream(connect.getOutputStream());

            String input = in.readLine();
            StringTokenizer parse = new StringTokenizer(input);
            String method = parse.nextToken().toUpperCase();
            file_requested = parse.nextToken().toLowerCase();

            if(!ServerConstants.SUPPORTED_METHODS.contains(method)){
                if (ServerConstants.verbose) {
                    System.out.println("501 Not Implemented : " + method + " method.");
                }
                File file = new File(ServerConstants.WEB_ROOT, ServerConstants.METHOD_NOT_SUPPORTED);
                int file_length = (int) file.length();
                String content_type = "text/html";
                byte[] file_data = readFileData(file, file_length);

                out.println(
                        Utils.HeaderBuilder.HTTP_503(file_length, content_type)
                );
                out.flush();

                data_out.write(file_data, 0, file_length);
                data_out.flush();
            }else {


                switch (method){
                    case "GET":
                        if (file_requested.endsWith("/")) {
                            file_requested += ServerConstants.DEFAULT_FILE;
                        }

                        File file = new File(ServerConstants.WEB_ROOT, file_requested);
                        int file_length = (int) file.length();
                        String content = getContentType(file_requested);

                        byte[] fileData = readFileData(file, file_length);

                        out.println(Utils.HeaderBuilder.HTTP_200(file_length, content));
                        out.flush();

                        data_out.write(fileData, 0, file_length);
                        data_out.flush();

                }
            }


        } catch (IOException ioe) {
            System.err.println("Server error : " + ioe);
        } finally {
            try {
                assert in != null;
                in.close();
                assert out != null;
                out.close();
                assert data_out != null;
                data_out.close();
                connect.close();
            } catch (Exception e) {
                System.err.println("Error closing stream : " + e.getMessage());
            }

            if (ServerConstants.verbose) {
                System.out.println("Connection closed.\n");
            }
        }
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

    private String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm")  ||  fileRequested.endsWith(".html"))
            return "text/html";
        else
            return "text/plain";
    }
}
