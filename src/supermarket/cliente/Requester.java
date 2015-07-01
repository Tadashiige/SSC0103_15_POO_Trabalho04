package supermarket.cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import supermarket.usuario.*;

public class Requester implements Runnable{

	private BufferedReader sockIn = null;
	private PrintWriter sockOut = null;
	
	private static Requester requester = null;
	private static ClientApp client = null;

	private String serverMessage = null;
	/**
	 * Construtor privado para modelo Singletton
	 * @param sock
	 * @param client
	 */
	private Requester (Socket sock, ClientApp client){
		
		try {
			//guardar os stream de entrada e saída do cliente
			sockIn = new BufferedReader ( new InputStreamReader (sock.getInputStream()) );
			sockOut = new PrintWriter ( sock.getOutputStream(), true );
			Requester.client = client;
		} catch (IOException e) {
			System.out.println("IOException: Requester construtor");
		}
	}

	/**
	 * Método para verificação do Singletton, com construtor privado. Só pode haver 
	 * um requisitante do cliente para o servidor.
	 * @param sock
	 * @param client
	 * @return
	 */
	public static Requester getRequester (Socket sock, ClientApp client){
		if(requester == null)
			requester = new Requester (sock, client);
		
		return requester;
	}

	public String getServerMessage (){
		return serverMessage;
	}
	
	/**
	 * Requisição a ser feita pelo ClientApp - cadastro
	 * @param user
	 */
	public void signupUser (User user){
		sockOut.println("signUp");
		sockOut.println(user.toString());
	}
	
	/**
	 * Requisição a ser feita pelo ClientApp - login
	 * @param ID
	 * @param password
	 */
	public void loginUser (String ID, String password){
		sockOut.println("login");
		sockOut.println(ID+";"+password);
	}
	
	/**
	 * Método que irá chamar método de ClientApp pela requisição de cadastro para 
	 * resposta.
	 * @param status
	 */
	private void responseSignup (boolean status, int ID, String password){
		client.responseSignup(status, ID, password);
	}
	
	/**
	 * Método que irá chamar método de ClientApp pela requisição de login para
	 * resposta.
	 * @param user
	 */
	private void responseLogin (boolean status){
		client.responseLogin(status);
	}
	
	/**
	 * Requisição a ser feita pelo ClientApp - logout
	 */
	public void logoutUser (){
		sockOut.println("logout");
	}
	
	/**
	 * Método que irá chamar método de ClientApp pela requisição de logout para resposta
	 * @param status
	 */
	public void responseLogout (boolean status){
		client.responseLogout(status);
	}
	
	/**
	 * Método que irá enviar mensagem de relatório do servidor ao cliente e destravar o seu wait
	 */
	public void responseMessage (){
		client.responseMessage(serverMessage);
	}
	
	/**
	 * --
	 */
	public void errorCommand (){
		sockOut.println("errorCommand");
	}
	
	/**
	 * --
	 */
	private void responseErrorCommand (){
		client.errorCommand();
	}
	
	/**
	 * Método de logistica para encerramento da thread Requester
	 */
	public void endRequest (){
		sockOut.println("exit");
	}
	
	/**
	 * Em thread o requester irá esperar a resposta à requisição feita por ele mesmo, mas em outra instancia. Para cada resposta
	 * uma função privada será chamada que está vinculada ao método de ClientApp, ou seja, irá responder a ele pela requisição feita.
	 */
	@Override
	public void run() {
		
		String response = null;
		try {
			exitRequest://label para encerramento da thread
				
			//looping para leitura de respostas do servidor
			while((response = sockIn.readLine()) != null){
				boolean status = false;
				
				//switch para processar o comando de resposta do servidor
				switch(response){
					case "signUp":
						//receber dados
						status = Boolean.parseBoolean(sockIn.readLine());						
						int ID = Integer.parseInt(sockIn.readLine());
						String password = sockIn.readLine();
						
						//enviar respostas ao cliente
						responseSignup(status, ID, password);
						break;
						
					case "login":
						//receber dados
						status = Boolean.parseBoolean(sockIn.readLine());
						
						//enviar respostas ao cliente
						responseLogin(status);
						break;
						
					case "logout":
						//receber dados
						status = Boolean.parseBoolean(sockIn.readLine());
						
						//enviar respostas ao cliente
						responseLogout(status);
						break;
						
					case "exit":
						//sair do looping
						break exitRequest;
					
					case "errorCommand":
						//processamento de comandos não reconhecidos
						responseErrorCommand();
						break;
						
					default:
						break;
				}//switch
				
				//recebimento de mensagem de relatório de operação
				serverMessage = sockIn.readLine();
				//envio da mensagem
				responseMessage ();
				
			}//while
		} catch (IOException e) {
			System.out.println("IOException: Requester -> falha na leitura da resposta do servidor");
		}
		System.out.println("request conection shutdown");
	}//run

}
