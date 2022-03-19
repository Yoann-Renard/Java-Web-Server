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

    }
}
