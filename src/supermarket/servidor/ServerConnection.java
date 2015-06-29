package supermarket.servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnection implements Runnable{

	private static String host = null;
	private static String gate = null;
	private static ServerSocket serverSocket = null;
	private static ServerConnection serverConnection = null;	
	private static ServerApp server = null;
	
	public String getHost(){
		return host;
	}
	
	public String getGate(){
		return gate;
	}
	
	/**
	 * Construtor privado para o modelo Singletton, apenas acessado pelo méthodo getServer (...)
	 * @param host
	 * @param gate
	 * @param server
	 */
	private ServerConnection (String host, String gate, ServerApp server){
		ServerConnection.host = host;
		ServerConnection.gate = gate;
		ServerConnection.server = server;
		
		try {
			serverSocket = new ServerSocket (Integer.parseInt(gate));
			System.out.println("Porta " + gate + " do servidor aberto");
			
		} catch (IOException e) {
			
			System.out.println("facha na abertura do servidor");
		} catch (NumberFormatException e){
			
			System.out.println("formato numérico errado na criação do servidor");
		}
	}
	
	/**
	 * Verificação do Design Pattern - Singletton: a classe por este método fazer 
	 * instanciação do objeto privando o acesso direto ao construtor.
	 * @param host
	 * @param gate
	 * @param server
	 * @return
	 */
	public static ServerConnection getServer(String host, String gate, ServerApp server){
		if(serverConnection == null){
			serverConnection = new ServerConnection(host, gate, server);
		}
		return serverConnection;
	}

	/**
	 * Em thread irá esperar por novas requisições de conexão e ao aceitá-la, irá criar novas thread de
	 * ClientRequest que irá escutar a comunicação do cliente para o server
	 */
	@Override
	public void run() {

		Socket newConnection = null;
		try {
			
			//busca por novas requisições de conexão socket, e quando sim instanciar tratador de requisições
			//e o lançar em nova thread
			
			while((newConnection = serverSocket.accept()) != null){
				ClientRequest newClient = new ClientRequest (server, newConnection);
				System.out.println("Novo usuário conectado. IP: "+newConnection.getInetAddress().getHostAddress());
				new Thread(newClient).start();
			}
		} catch (IOException e) {
			
			System.out.println("IOException: Problema com nova conexão de usuário");
		}
	}

}
