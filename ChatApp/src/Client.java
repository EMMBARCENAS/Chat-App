import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client
{

		private Socket connectionSocket;
		private BufferedReader in;
		private BufferedWriter out;
		private String username;	
		private int chatRoomIndex;			
		
		//SETS UP INPUT AND OUTPUT STREAMS, USERNAME, AND INDEX
		public Client(Socket connectionSocket, String username, int chatRoomIndex) 
		{
			try 
			{
				this.connectionSocket = connectionSocket;
				this.out = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));
				this.in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				this.username = username;
				this.chatRoomIndex = chatRoomIndex;
			} catch (IOException e) {
			}
		}

//OUTPUTS THE CHAT ROOM INDEX AND USERNAME BACK TO THE SERVER
//CONTINUES TO READ INPUT FROM USER TO SEND BACK TO SERVER SO SERVER CAN USE BROADCAST METHOD TO SEND TO EVERYBODY CONNECTED
public void sendMessage() throws IOException 
{
	try 
	{
		out.write(Integer.toString(chatRoomIndex));
		out.newLine();
		out.write(username);
		out.newLine();
		out.flush();

		try (Scanner keyboard = new Scanner(System.in)) 
		{
			while(connectionSocket.isConnected())
			{
				String msgToSend = keyboard.nextLine();
				out.write(username + ": " + msgToSend);
				out.newLine();
				out.flush();
			}
		}
	} catch (IOException e) {
	}
}

//STARTS THREAD & LISTENS FOR INCOMING MSGS FROM THE CLIENT TO SEND TO SERVER TO BROADCAST
public void lookForMsg() 
{
    new Thread(new Runnable() 
	{
        @Override
        public void run() 
		{
            try 
			{
                String msgFromGC;
                while ((msgFromGC = in.readLine()) != null) 
				{
                    System.out.println(msgFromGC);
                }
            } catch (IOException e) {
            }
        }
    }).start();
}

public static void main(String[] args)throws IOException {
	/*
	ASKS USER WHICH GROUPCHAT THEY WANT TO ENTER AND FOR THEIR NAME
	CONNECTS USER TO GROUPCHAT (SERVER) TO SEND MSGS TO OTHER USERS IN SAME GROUPCHAT
	TAKES IN USERS MSG AND SENDS TO ALL OTHER USERS
	CONNECTS CLIENT TO SERVER AND USES THE "lookForMsg()" TO START A NEW THREAD FOR EACH CLIENT CONNECTED AND "sendMessage()" TO RELAY USER INPUT TO SERVER
	*/
	try (Scanner keyboard = new Scanner(System.in)) 
	{
		System.out.println("Type which group chat you want to enter! (1-4)");
		int chatRoomIndex = keyboard.nextInt();
		keyboard.nextLine();
		System.out.println("Enter your username for the groupchat: ");
		String username = keyboard.nextLine();
		Socket connectionSocket = new Socket("localhost", 6789);
		Client client = new Client(connectionSocket, username, chatRoomIndex);
		client.lookForMsg();
		client.sendMessage();
		}
	}
}