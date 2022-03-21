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
import java.util.*;

public class HTTPServer implements Runnable {

    // Client Connection via Socket Class
    private Socket connect;

    private static final ServerConstants server_const = new ServerConstants();

    public HTTPServer(Socket c) {
        connect = c;
    }

    public static void main(String[] args){
        for (int i = 0; i < args.length; i++){
            switch (args[i]){
                case "-p":
                case "--port":
                    try {
                        server_const.PORT = Integer.parseInt(args[i + 1]);
                    } catch (NumberFormatException e){
                        System.out.println("--port option value must be an integer: " + e.getMessage());
                        System.exit(1);
                    }
                    i++;
                    break;
                case "-v":
                case "--verbose":
                    server_const.verbose = true;
                default:
                    break;
            }
        }

        try{
            ServerSocket server_connect = new ServerSocket(server_const.PORT);
            System.out.println("Server started.\nListening for connections on port : " + server_const.PORT + " ...\n");

            while(true){
                HTTPServer server = new HTTPServer(server_connect.accept());
                if (server_const.verbose) {
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
        String request = null;
        String file_requested = null;
        String query = null;
        HashMap<String, String> params = new HashMap<>();

        try{
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            out = new PrintWriter(connect.getOutputStream());
            data_out = new BufferedOutputStream(connect.getOutputStream());

            String input = in.readLine();
            StringTokenizer parse = new StringTokenizer(input);
            String method = parse.nextToken().toUpperCase();

            if(!server_const.SUPPORTED_METHODS.contains(method)){
                this.notImplemented(out, data_out, method);
            } else {
                request = parse.nextToken().toLowerCase();
                try {
                    file_requested = request.split("\\?")[0];
                    query = request.split("\\?")[1];
                    if (server_const.verbose){
                        System.out.println("FILE REQUESTED : "+file_requested);
                        System.out.println("QUERY : "+query);
                    }
                }catch (ArrayIndexOutOfBoundsException e){
                    file_requested = request.replace("?", "");
                    if (server_const.verbose){
                        System.out.println("FILE REQUESTED : "+file_requested);
                        System.out.println("NO QUERY");
                    }
                }
                if (query != null){
                    String[] raw_params = query.split("&");
                    for(String p : raw_params){
                        try{
                            String key = p.split("=")[0];
                            String value = p.split("=")[1];
                            params.put(key,value);
                        }catch (ArrayIndexOutOfBoundsException e){
                            // TODO Return "400 Bad Request"
                        }
                    }
                    if (server_const.verbose){
                        System.out.println("PARAMETERS : "+params);
                    }
                }


                switch (method){
                    case "GET":
                        this.get(out, data_out, file_requested);
                        break;
                    default:
                        System.out.println("Method "+method+" implemented but not handled.");
                        break;
                }
            }


        } catch (FileNotFoundException fnfe) {
            try {
                fileNotFound(out, data_out, file_requested);
            } catch (IOException ioe) {
                System.err.println("Error with file not found exception : " + ioe.getMessage());
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

            if (server_const.verbose) {
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

    private void fileNotFound(PrintWriter out, OutputStream dataOut, String file_requested) throws IOException {
        File file = new File(server_const.WEB_ROOT, server_const.FILE_NOT_FOUND);
        int file_length = (int) file.length();
        String content = "text/html";
        byte[] fileData = readFileData(file, file_length);

        out.println(Utils.HeaderBuilder.HTTP_404(file_length, content));

        out.flush(); // flush character output stream buffer

        dataOut.write(fileData, 0, file_length);
        dataOut.flush();

        if (server_const.verbose) {
            System.out.println("File " + file_requested + " not found");
        }
    }

    private void notImplemented(PrintWriter out, OutputStream data_out, String method) throws IOException {
        File file = new File(server_const.WEB_ROOT, server_const.METHOD_NOT_SUPPORTED);
        int file_length = (int) file.length();
        String content_type = "text/html";
        byte[] file_data = readFileData(file, file_length);

        out.println(
                Utils.HeaderBuilder.HTTP_503(file_length, content_type)
        );
        out.flush();

        data_out.write(file_data, 0, file_length);
        data_out.flush();
        if (server_const.verbose) {
            System.out.println("501 Not Implemented : " + method + " method.");
        }
    }

    private void get(PrintWriter out, OutputStream data_out, String file_requested) throws IOException {
        for(Map.Entry<String, String> entry : server_const.ENDPOINTS.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (file_requested.equals(key)){
                file_requested = value;
                break;
            }
        }


        File file = new File(server_const.WEB_ROOT, file_requested);
        int file_length = (int) file.length();
        String content = getContentType(file_requested);

        byte[] fileData = readFileData(file, file_length);

        out.println(Utils.HeaderBuilder.HTTP_200(file_length, content));
        out.flush();

        data_out.write(fileData, 0, file_length);
        data_out.flush();

        if (server_const.verbose) {
            System.out.println("File " + file_requested + " of type " + content + " returned");
        }
    }
}
