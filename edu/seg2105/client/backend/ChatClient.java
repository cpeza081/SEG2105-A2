// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
        if(message.startsWith("#")){
            handleCommand(message);
        }else {
            sendToServer(message);
        }
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }

  private void handleCommand(String message) {
      String[] tokens =   message.split(" ");
      String command = tokens[0].toLowerCase();

      switch(command){
          case "#quit":
              quit();
              break;

          case "#logoff":
              try {
                  if (isConnected()) {
                      closeConnection();
                  }
              }catch (IOException e){
                  clientUI.display("Error: Client is not currently connected.");
              }
              break;

          case "#sethost":
              if (isConnected()) {
                  clientUI.display("Error: Cannot change host while connected.");
              } else if (tokens.length < 2) {
                  clientUI.display("Usage: #sethost <host>");
              } else {
                  setHost(tokens[1]);
                  clientUI.display("Host set to " + tokens[1]);
              }
              break;

          case "#setport":
              if (isConnected()) {
                  clientUI.display("Error: Cannot change port while connected.");
              } else if (tokens.length < 2) {
                  clientUI.display("Usage: #setport <port>");
              } else {
                  try {
                      int port = Integer.parseInt(tokens[1]);
                      setPort(port);
                      clientUI.display("Port set to " + port);
                  } catch (NumberFormatException e) {
                      clientUI.display("Error: Port must be a number.");
                  }
              }
              break;

          case "#login":
              if (isConnected()) {
                  clientUI.display("Error: Already connected to the server.");
              } else {
                  try {
                      openConnection();
                      clientUI.display("Logged in to the server.");
                  }catch (IOException e){
                      clientUI.display("Error connecting to the server: " + e.getMessage());
                  }
              }
              break;

          case "#gethost":
              clientUI.display("Current host: " + getHost());
              break;

          case "#getport":
              clientUI.display("Current port: " + getPort());
              break;

          default:
              clientUI.display("Unknown command: " + command);
              break;
      }
  }
    /**
     * This method terminates the client.
     */
    public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
    /**
     * Implemented hook method called after the connection has been closed. The method may be overriden by subclasses to
     * perform special processing such as cleaning up and terminating, or
     * attempting to reconnect.
     */
    @Override
    protected void connectionClosed() {
        clientUI.display("Connection to server has been closed.");
    }

    /**
     * Implemented hook method called each time an exception is thrown by the client's
     * thread that is waiting for messages from the server.
     *
     * @param exception
     *            the exception raised.
     */
    @Override
    protected void connectionException(Exception exception) {
        clientUI.display("The connection to server has been lost.");
    }

}
//End of ChatClient class
