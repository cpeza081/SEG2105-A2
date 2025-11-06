package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

import java.io.IOException;
import java.util.Objects;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer {
    //Class variables *************************************************

    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5555;

    private ChatIF serverUI;

    //Constructors ****************************************************

    /**
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public EchoServer(int port, ChatIF serverUI) {
        super(port);
        this.serverUI = serverUI;
    }


    //Instance methods ************************************************

    /**
     * This method handles any messages received from the client.
     *
     * @param msg    The message received from the client.
     * @param client The connection from which the message originated.
     */
    public void handleMessageFromClient
    (Object msg, ConnectionToClient client) {
        String message = msg.toString().trim();
        serverUI.display("Message received: " + msg + " from " + client.getInfo("loginID"));

        if(message.startsWith("#login")){
            String[] parts = message.split(" ");

            if(parts.length < 2){
                try {
                    client.sendToClient("ERROR: No login ID provided. Disconnecting client.");
                    client.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
                return;

            }
            String loginID = parts[1];

            if(client.getInfo("loginID") != null){
                try{
                    client.sendToClient("ERROR: You are already logged in. Connection closing.");
                    client.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
                return;
            }

            client.setInfo("loginID", loginID);
            serverUI.display("Client "+loginID+" logged in");
            try{
                client.sendToClient("Login successful. Welcome " +loginID+".");
            }catch(IOException e){
                e.printStackTrace();
            }
            return;

        }

        String loginID = (String) client.getInfo("loginID");
        if(loginID == null){
            try{
                client.sendToClient("ERROR - You are not logged in. Disconnecting.");
                client.close();
            }catch(IOException e){
                e.printStackTrace();
            }
            return;
        }

        this.sendToAllClients(client.getInfo("loginID").toString() +" > " +msg.toString());
    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server starts listening for connections.
     */
    protected void serverStarted() {
        serverUI.display("Server listening for connections on port " + getPort());
    }

    /**
     * This method overrides the one in the superclass.  Called
     * when the server stops listening for connections.
     */
    protected void serverStopped() {
        serverUI.display("Server has stopped listening for connections.");
    }

    @Override
    protected void clientConnected(ConnectionToClient client) {
        serverUI.display("A new client has connected to the server.");
    }

    @Override
    synchronized protected void clientDisconnected(ConnectionToClient client) {
        serverUI.display(client.getInfo("loginID").toString() + " has disconnected.");
        super.clientDisconnected(client);  // keep OCSF’s internal cleanup
    }

    @Override
    synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
        super.clientDisconnected(client);
        serverUI.display("A client's connection was lost");
    }

    public void handleMessageFromServerUI(String message) {
        if (message.startsWith("#")) {
            handleCommand(message);
        } else {
            String serverMessage = message;
            serverUI.display("SERVER MESSAGE > "+serverMessage);
        }
    }

    private void handleCommand(String message) {
        String[] tokens = message.split(" ");
        String command = tokens[0].toLowerCase();

        switch (command) {
            case "#quit":
                try {
                    close();
                    System.exit(0);
                } catch (IOException e) {
                    serverUI.display("Error: Server could not quit.");
                }
                break;

            case "#stop":
                stopListening();
                break;

            case "#close":
                try {
                    close();
                    serverUI.display("The server has shut down.");
                } catch (IOException e) {
                    serverUI.display("Error: server failed to close.");
                }
                break;

            case "#setport":
                if (isListening()) {
                    serverUI.display("Error: Stop the server before setting the port.");
                } else if (tokens.length < 2) {
                    serverUI.display("Usage: #setport <port>");
                } else {
                    int port = Integer.parseInt(tokens[1]);
                    setPort(port);
                    serverUI.display("Port set to " + port);
                }
                break;

            case "#start":
                if (isListening()) {
                    serverUI.display("Error: Server already running.");
                } else {
                    try {
                        serverUI.display("Server started on port " + getPort());
                        listen();
                    } catch (IOException e) {
                        serverUI.display("Error: Server could not be started.");
                    }
                }
                break;

            case "#getport":
                serverUI.display("Server port: " + getPort());
                break;

            default:
                serverUI.display("Unknown command: " + command);
                break;
        }
    }
}
//End of EchoServer class
