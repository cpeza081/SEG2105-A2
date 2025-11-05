import edu.seg2105.client.common.ChatIF;
import edu.seg2105.edu.server.backend.EchoServer;

import java.util.Scanner;


public class ServerConsole implements ChatIF
{
    final public static int DEFAULT_PORT = 5555;
    EchoServer server;
    Scanner fromConsole;

    public ServerConsole(int port){
        server = new EchoServer(port, this);
        fromConsole = new Scanner(System.in);
    }

    public void accept(){
        try{
            String message;
            while(true){
                message = fromConsole.nextLine();
                server.handleMessageFromServerUI(message);
            }
        }catch(Exception e){
            System.out.println("Unexpected error occured while trying to read from the console.");
        }
    }

    public void display(String message){
        System.out.println("> "+message);
    }

    /**
     * This method is responsible for the creation of
     * the server instance.
     *
     * @param args[0] The port number to listen on.  Defaults to 5555
     *          if no argument is entered.
     */
    public static void main(String[] args)
    {
        int port = 0; //Port to listen on

        try
        {
            port = Integer.parseInt(args[0]); //Get port from command line
        }
        catch(Throwable t)
        {
            port = DEFAULT_PORT; //Set port to 5555
        }

        ServerConsole chat = new ServerConsole(port);

        try
        {
            chat.server.listen(); //Start listening for connections
        }
        catch (Exception ex)
        {
            System.out.println("ERROR - Could not listen for clients!");
        }
        chat.accept();
    }
}
