package supermarket.cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Requester implements Runnable{

	private BufferedReader sockIn = null;
	private PrintWriter sockOut = null;
	
	private static Requester requester = null;
	
	private Requester (Socket sock){
		
		try {
			sockIn = new BufferedReader ( new InputStreamReader (sock.getInputStream()) );
			sockOut = new PrintWriter ( sock.getOutputStream() );
		} catch (IOException e) {
			System.out.println("IOException: Requester construtor");
		}
	}
	
	public static Requester getRequester (Socket sock){
		if(requester == null)
			requester = new Requester (sock);
		
		return requester;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
