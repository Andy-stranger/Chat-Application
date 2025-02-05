package org.example;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static final int port = 12346;
    private static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server is running. Waiting for connections..");

            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while(true){
                    String serverMsg = scanner.nextLine();
                    broadcast("[Server]: "+serverMsg, null );
                }
            }).start();

            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: "+clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void broadcast(String msg, ClientHandler sender){
        for(var client : clients){
            if(client != sender){
                client.sendMessage(msg);
            }
        }
    }

    private static class ClientHandler implements Runnable{
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String userName;

        public ClientHandler(Socket socket){
            this.clientSocket = socket;
            try{
                out = new PrintWriter(clientSocket.getOutputStream(),true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            }
            catch (IOException e){
                 e.printStackTrace();
            }
        }

        @Override
        public void run(){
            try{
                out.println("Enter your name: ");
                userName = in.readLine();
                System.out.println("User " + userName + " connected...");
                out.println("Welcome to the chat, " + userName + "!!");
                out.println("Type your message here...");

                String inputLine;
                while((inputLine = in.readLine()) != null){
                    System.out.println("["+userName+"]"+inputLine);
                    broadcast("["+userName+"]"+inputLine, this);
                }
                clients.remove(this);
                System.out.println("User "+userName+" disconnected...");
            }
            catch (IOException e){
                e.printStackTrace();
            }
            finally {
                try{
                    in.close();
                    out.close();
                    clientSocket.close();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(String msg){
            out.println(msg);
        }
    }
}
