import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    //HOLDS AND STORES ALL CONNECTED CLIENTS
    public static List<ClientHandler> clients = new ArrayList<>();
    public static void main(String argv[]) 
	{
        // Create 'Welcome Socket' for port # 6789
        try (ServerSocket welcomeSocket = new ServerSocket(6789)) 
		{
            while (true) 
			{
                // When a client knocks the door, open the door
                // i.e., Open connection, and create a new socket 'Connection Socket'
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("A new user has connected to the chat!");

                //CREATES A NEW INSTANCE OF CLIENTHANDLER TO ADD TO ARRAYLIST AND STARTS A THREAD FOR EACH NEW USER CONNECTED
                ClientHandler clientHandler = new ClientHandler(connectionSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
        }
    }

    //BROADCASTS MSG TO ALL CLIENTS IN A SPECIFIC CHAT ROOM
    //ITTERATES THROUGH ARRAYLIST OF CLIENTS AND SENDS MSG TO THOSE CLIENTS SELECTED IF THE CHAT ROOM INDEX MATCHES
    public static void sendMsg(String msgToSend, String username, int chatRoomIndex) 
    {
        for (ClientHandler client : clients) 
        {
            try
            {
                if(client.getRoomNumber() == chatRoomIndex)
                {
                    client.sendMessage(msgToSend, username);
                }
            } catch (IOException e){
            }
        }
    }
}
