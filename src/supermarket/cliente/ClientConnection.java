package supermarket.cliente;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientConnection implements Runnable{
	private String host = null;
	private String gate;
	private Socket sock;
	private static ClientConnection clientConnection = null;
	private static Requester requester = null;
	
	private ClientConnection (String host, String gate){
		this.host = host;
		this.gate = gate;
		try {
			sock = new Socket(this.host, Integer.parseInt(this.gate));
			requester = Requester.getRequester(sock);
		} catch (UnknownHostException e) {
			
			System.out.println("Endereço ip não encontrado.");
		} catch (IOException e) {
			
			System.out.println("Erro de IO com o socket cliente");
		} catch (NumberFormatException e){
			
			System.out.println("NumberFormatException: valor de gate inválido");
		}
	}
	
	public static Requester getRequester (String host, String gate){
		if(clientConnection == null)
			clientConnection = new ClientConnection(host, gate);
		
		return ClientConnection.requester;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	} 
}
