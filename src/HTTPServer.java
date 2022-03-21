import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import Constants.ServerConstants;
import Utils.Response;

public class HTTPServer implements Runnable {

    // Client Connection via Socket Class
    private final Socket connect;

    // OPTIONS
    private static boolean verbose = false;

    private static final ServerConstants server_const = new ServerConstants();

    public HTTPServer(Socket c) {
        connect = c;
    }

    public static void main(String[] args){
        for (int i = 0; i < args.length; i++){
            switch (args[i]) {
                case "-p", "--port" -> {
                    try {
                        server_const.PORT = Integer.parseInt(args[i + 1]);
                    } catch (NumberFormatException e) {
                        System.out.println("--port option value must be an integer: " + e.getMessage());
                        System.exit(1);
                    }
                    i++;
                }
                case "-v", "--verbose" -> verbose = true;
                default -> {
                } // TODO Throw Error: Unrecognized Option
            }
        }

        try{
            ServerSocket server_connect = new ServerSocket(server_const.PORT);
            System.out.println("Server started.\nListening for connections on port : " + server_const.PORT + " ...\n");

            while(true){
                HTTPServer server = new HTTPServer(server_connect.accept());
                if (verbose) {
                    System.out.println("Connection opened. (" + new Date() + ")");
                }

                Thread thread = new Thread(server);
                //Runtime.getRuntime().addShutdownHook(thread);
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
        String request;
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
                    if (verbose){
                        System.out.println("FILE REQUESTED : "+file_requested);
                        System.out.println("QUERY : "+query);
                    }
                }catch (ArrayIndexOutOfBoundsException e){
                    file_requested = request.replace("?", "");
                    if (verbose){
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
                    if (verbose){
                        System.out.println("PARAMETERS : "+params);
                    }
                }


                switch (method) {
                    case "GET" -> this.get(out, data_out, file_requested);
                    default -> System.out.println("Method " + method + " implemented but not handled.");
                }
            }


        } catch (FileNotFoundException e) {
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

            if (verbose) {
                System.out.println("Connection closed.\n");
            }
        }
    }


    private void fileNotFound(PrintWriter out, OutputStream data_out, String file_requested) throws IOException {
        Response response = new Response(server_const.FILE_NOT_FOUND, out,data_out);
        response.setHeader(404);
        response.setBody();
        response.send();

        if (verbose) {
            System.out.println("File " + file_requested + " not found");
        }
    }

    private void notImplemented(PrintWriter out, OutputStream data_out, String method) throws IOException {
        Response response = new Response(server_const.METHOD_NOT_SUPPORTED, out,data_out);

        response.setHeader(503);
        response.setBody();
        response.send();

        if (verbose) {
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

        Response response = new Response(file_requested,out,data_out);

        response.setHeader(200);
        response.setBody();
        response.send();

        if (verbose) {
            System.out.println("File " + file_requested + " returned");
        }
    }
}
