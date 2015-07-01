package supermarket.cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.InputMismatchException;
import java.util.Scanner;

import supermarket.usuario.*;

public class ClientApp implements Runnable {

	private static boolean logged = false;
	private static Requester requester;
	
	/**
	 * Criação de usuário pelo Terminal (BufferedReader)
	 * @return
	 */
	public boolean signupUser (){
		boolean status = false;
		BufferedReader input = new BufferedReader ( new InputStreamReader (System.in));
		User newUser = new User();
		
		String aux = null;
		
		try {
			System.out.print("Nome: ");
			aux = input.readLine();
			newUser.setName(aux);
			
			System.out.print("Endereço: ");
			aux = input.readLine();
			newUser.setAddress(aux);
			
			System.out.print("Email: ");
			aux = input.readLine();
			newUser.setEmail(aux);
			
			System.out.print("Telefone: ");
			aux = input.readLine();
			newUser.setTel(aux);
			
			System.out.print("Senha: ");
			aux = input.readLine();
			newUser.setPassword(aux);			
			
			requester.signupUser(newUser);
			
			status = true;
			
		} catch (IOException e) {
			System.out.println("Excessão de entrada com .readLine");
			
		}
		
		return status;
	}
	
	/**
	 * Método a ser chamada para resposta do servidor. Se ocorreu erro, o usuário local deve ser eliminado.
	 * @param status
	 */
	public void responseSignup (boolean status, int ID, String password){
		if(status){
			logged = true;
			System.out.println("** ->Cadastro do usuário no servidor com sucesso\n"+
					"ID : "+ID+
					" senha: "+password+
					" <- **");
		} else{
			System.out.println("Falha no cadastro do usuário no servidor");
		}
	}
	
	/**
	 * Entrar no sistema pelo login do usuario. Retorna status do procedimento.
	 * @return
	 */
	public boolean loginUser (){
		Scanner input = new Scanner (System.in);
		try{
			System.out.print("ID: ");
			String ID = input.nextLine();
			
			System.out.print("password: ");
			String Password = input.nextLine();
			
			requester.loginUser(ID, Password);
			
			return true;
		}
		catch(InputMismatchException e){
			System.out.println("InputMismatchException: Login -> Falha na leitura de dados");
		}
		
		return false;
	}
	
	/**
	 * Método a ser chamada para reposta do servidor. Se o usuário for encontrado aderir o usuário enviado.
	 * @param user
	 */
	public void responseLogin (boolean status){
		
		if(status == true){
			logged = true;
			System.out.println("Login efetuado com sucesso");
		}
		else
			System.out.println("Falha no login");
	} 
	
	/**
	 * A descrição do trabalho não pediu que uma sessão seja mantida para login, então o estado deste é manter
	 * o objeto usuário com o ID fornecido, e assim para sair basta desassociar tal objeto.
	 * @return
	 */
	public void logoutUser (){
	}
	
	public void responseLogout (boolean status){
		if(status == true){
			logged = false;
			System.out.println("Logout efetuado com sucesso");
		}
		else
			System.out.println("Falha no logout");
	}
	
	public synchronized void responseMessage (String serverMessage){
		System.out.println(" ********* mensagem do servidor: "+serverMessage+"\n");
		notify();
	}
	
	public void errorCommand (){
	}
	
	public void endApp (){
		requester.endRequest();
	}
	
	/**
	 * O sistema irá pedir de imediato o Ip-host do servidor e a porta para conexão. Caso a conexão
	 * não aconteça
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner input = new Scanner ( System.in );
		String cmd = null;		
		
		ClientApp client = new ClientApp();
		do{
			System.out.print("Host_IP: ");
			String host = input.nextLine();
			System.out.print("Gate_: ");
			String gate = input.nextLine();
			
			requester = ClientConnection.getRequester (host, gate, client);
			
			System.out.println(" ...  ... ... Conectando Servidor ... ... ...");
			
			if(requester != null){
				System.out.println(" ************ Conexão estabelecida ************ ");
				break;
			}
			System.out.println(" ........ Falha na conexão. Tentar de novo? [S / N] ....... ");
			cmd = input.nextLine();
		}while(!cmd.equals("N"));
		
		if(requester == null)
			return;
			
		//Thread requester irá segurar o aplicativo para a interação com o servidor até o encerramento da run()
		new Thread( client ).start(); 
		new Thread(requester).start();
	}

	/**
	 * Em Thread o aplicativo irá receber do usuário requisições para serviço e tais requisições serão 
	 * enviadas ao servidor para processamento. Uma outra thread está responsável por receber as respostas
	 * e chamar os métodos desta classe para que aqui processe o resultado.
	 */
	@Override
	public void run() {
		synchronized(this){
			Scanner input = new Scanner ( System.in );
			
			String cmd = "";
			
			System.out.println("\nBem vindo ao sistema de Supermercado Online");
			while(!cmd.equals("exit")){
				System.out.println("\n"+
						"############ > "+
						"Digite as opções a seguir:\n"+
						"cadastrar - cadastrar Novo Usuário\n"+
						"login - logar como usuario\n"+
						"logout - deslogar como usuário atual\n"+
						"exit - sair do aplicativo\n"+
						"\n");
				System.out.print(" >>>>>>> ");
				cmd = input.nextLine();
				switch(cmd){
					case "cadastrar":
						String msg = null;
						System.out.println("Menu escolhido: Cadastrar");
						if(logged == false){
							if(this.signupUser()){
								msg = "cadastro de usuario enviado";
								System.out.println(msg);
								System.out.println("Waiting Server response ... ...\n");
								try {
									wait();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}else{
								msg = "falha no cadastro local";
								System.out.println(msg);
							}
							
						}else
							System.out.println("Operação inválida: Usuário está logado");
						
						break;
						
					case "login":
						msg = null;
						System.out.println("Menu escolhido: Login");
						if(logged == false){
							if(this.loginUser()){
								msg = "login requisitado";
								System.out.println(msg);
								System.out.println("Waiting Server response ... ...\n");
								try {
									wait();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							else{
								msg = "falha no login local";
								System.out.println(msg);
							}
							
						}else
							System.out.println("Operação inválida: Usuário está logado");
						break;
						
					case "logout":
						msg = null;
						System.out.println("Menu escolhido: Logout");
						if(logged == true){
							requester.logoutUser();
							msg = "Requisição de logout enviado ao servidor";
							
							System.out.println(msg);
							System.out.println("Waiting Server response ... ...\n");
							try {
								wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else
							System.out.println("Operação inválida: Usuário não está logado");
						break;
						
					case "exit":
						System.out.println("Menu escolhido: exit");
						if(logged == true){
							requester.logoutUser();
							msg = "Requisição de logout enviado ao servidor";
							
							System.out.println(msg);
							System.out.println("Waiting Server response ... ...\n");
							try {
								wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if(logged == false)
							endApp();
						else
							System.out.println("Error: não possível logout do sistema. Operação exit sem sucesso");
						
						break; 
						
					default:
						System.out.println("Operação inválida: "+cmd);
						//requester.errorCommand();
						break;
										
				}//switch
				
				System.out.println("fim do processo");
				
			}//while
			
			System.out.println("\n >>>>>> encerrar aplicativo");
		}//synchronized
	}//run
	
}
