package supermarket.servidor;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

import supermarket.usuario.*;

public class ServerApp{

	private static int usersID = 0;
	
	private String host = null;
	private String gate = null;
	private static ServerConnection serverConnection = null;
	private static ServerApp server = null;
	
	private List<User> clients = null;
	
	public String getHost(){
		return host;
	}
	
	public String getGate(){
		return gate;
	}
	
	/**
	 * Construtor privado para criação de apenas um servidor.
	 * @param host
	 * @param gate
	 */
	private ServerApp (String host, String gate){
		this.host = host;
		this.gate = gate;
		
		serverConnection = ServerConnection.getServer (host, gate, this);
		
		if(serverConnection == null){
			System.out.println("Falha na criação de Conexão Server");
			System.exit(1);
		}
		
		clients = new ArrayList<User>();
		System.out.println("Conexão Server aberta");
		 
		
	}
	
	/**
	 * Método verificador do modelo Singletton, com construtor privado, pois deve haver
	 * apenas um servidor para os dados e apenas um servidor de conexões para o sistema.
	 * @param host
	 * @param gate
	 * @return
	 */
	public static ServerApp getServer(String host, String gate){
		if(server == null){
			server = new ServerApp(host, gate);
		}
		return server;
	}
	
	public static void main(String[] args) {
		Scanner input = new Scanner (System.in);
		System.out.println("Host_IP: localhost");
		String host = "localhost";
		System.out.print("Gate_: ");
		String gate = input.nextLine();
		
		getServer(host, gate);
		new Thread(serverConnection).start();
	}

	/**
	 * Cadastramento de usuário e atribuição de ID único. Como o ID deve ser único
	 * o método possui modificador Synchronized para incremento e atribuição de ID
	 * um por vez.
	 * @param user
	 * @return
	 */
	public synchronized int signupUser (User user){
		if(user.getID() < 0){
			usersID++;
			
			user.setID(usersID);
			clients.add(user);
			System.out.println("Cadastro do usuário: "+user);
			return usersID;
		}
		return -1;
	}
	
	/**
	 * Buscar na lista o usuário com o ID e senha especificado.
	 * @param ID
	 * @param password
	 * @return
	 */
	public String loginUser (int ID, String password){
		Optional<User> userOptional = clients
				.stream()
				.filter(user -> user.getID() == ID)
				.findFirst();
		User user = null;
		
		try{
			user = userOptional.get();
			if(user.getPassword().equals(password))
				return user.toString();
			else
				return "wrong";
		} catch(NoSuchElementException e){
			return null;
		}
	}

}
