package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADD = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args){
        try{
            Socket soc = new Socket(SERVER_ADD,SERVER_PORT);
            System.out.println("Connected!!");
            PrintWriter out = new PrintWriter(soc.getOutputStream(),true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(soc.getInputStream())
            );

            new Thread(() -> {
                try{
                    String serverResponse;
                    while((serverResponse = in.readLine()) != null){
                        System.out.println(serverResponse);
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }).start();

            Scanner scanner = new Scanner(System.in);
            String userInput;
            while(true){
                userInput = scanner.nextLine();
                out.println(userInput);
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
