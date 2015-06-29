package supermarket.servidor;

import java.util.Scanner;

import supermarket.cliente.User;

public class ServerApp{

	private String host = null;
	private String gate = null;
	private static ServerConnection serverConnection = null;
	private static ServerApp server = null;
	
	public String getHost(){
		return host;
	}
	
	public String getGate(){
		return gate;
	}
	
	private ServerApp (String host, String gate){
		this.host = host;
		this.gate = gate;
		
		serverConnection = ServerConnection.getServer (host, gate, this);
		if(serverConnection == null){
			System.out.println("Falha na criação de Conexão Server");
			System.exit(1);
		}
		System.out.println("Conexão Server aberta");
		 
		
	}
	
	public static ServerApp getServer(String host, String gate){
		if(server == null){
			server = new ServerApp(host, gate);
		}
		return server;
	}
	
	public static void main(String[] args) {
		Scanner input = new Scanner (System.in);
		String host = input.nextLine();
		String gate = input.nextLine();
		input.close();
		
		getServer(host, gate);		
	}

	public boolean signupUser (User user){
		//TODO registrar novo usuario
		
		return false;
	}
	
	public String loginUser (int ID, String password){
		//TODO recuperar usuario
		
		return null;
	}

}
