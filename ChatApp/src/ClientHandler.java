import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable
{
    private Socket connectionSocket;
    private BufferedReader in;
    private BufferedWriter out;
    String username;
    int chatRoomIndex;

    //SETS UP INPUT AND OUTPUT STREAMS
    //READS WHICH CHAT ROOM USER WANTS TO ENTER AND USERNAME ENTERED
    //SERVER NOTIFIES USERS WHEN SOMEONE HAS ENTERED THEIR GROUP CHAT (1-4)
    public ClientHandler(Socket connectionSocket) throws IOException 
    {
        this.connectionSocket = connectionSocket;
        this.in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
        this.chatRoomIndex = Integer.parseInt(in.readLine());
        this.username = in.readLine();
        Server.sendMsg("SERVER: " + username + " has entered the chat", username, chatRoomIndex);
    }

    //RUNS WHEN NEW THREAD IS CREATED FOR CLIENTHANDLER(WHEN USER CONNECTS)
    //CONTINUESLY LISTENS FOR MSGS FROM THE CLIENT TO BROADCAST OUT TO THE GROUPCHAT USING THE "sendMsg" METHOD IN SERVER CLASS
    //ENDS WHEN CLIENTS SOCKET IS DISCONNECTED
    @Override
    public void run() 
    {
        try 
        {
            String msgFromClient;
            while (connectionSocket.isConnected()) 
            {
                msgFromClient = in.readLine();
                Server.sendMsg(msgFromClient, username, chatRoomIndex);
            }
        } catch (IOException e) {
        } finally {
            removeParticipant();
        }
    }


    //SENDS MSG TO ALL OF THE CLIENTS CONNECTED EXCEPT FOR THE ONE WHO SENT THE MSG
    public void sendMessage(String msgToSend, String username) throws IOException 
    {
        if (!this.username.equals(username)) 
        {
            out.write(msgToSend);
            out.newLine();
            out.flush();
        }
    }

    //REMOVES THE USER FROM THE GROUP CAHT IF THEY EXIT OR SHUTDOWN(STOPS THE APPLICATION FROM BRICKING)
    public void removeParticipant()
    {
        Server.clients.remove(this);
    }

    //RETURNS A CHAT ROOM INDEX SO THAT IT ALLOWS A CLIENT TO ENTER A CERTAIN GROUPCHAT
    public int getRoomNumber()
    {
        return chatRoomIndex;
    }
}