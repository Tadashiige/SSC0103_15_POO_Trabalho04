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
	 * Criação de usuário pelo Terminal. Sem validação de dados (tipos, formatação, etc)
	 * @return
	 */
	public boolean signupUser (){
		boolean status = false;
		BufferedReader input = new BufferedReader ( new InputStreamReader (System.in));
		User newUser = new User();
		
		String aux = null;
		
		try {
			//inputs de dados para cadastro
			
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
			
			//requisição para cadastro do usuário
			requester.signupUser(newUser);
			
			//estatus local sobre cadastro
			status = true;
			
		} catch (IOException e) {
			System.out.println("Excessão de entrada com .readLine");
			
		}
		
		return status;
	}
	
	/**
	 * Método a ser chamada para resposta do servidor. Se ocorreu erro, estado de login deve se manter falso.
	 * @param status
	 */
	public void responseSignup (boolean status, int ID, String password){
		//se o estatus de resposta for true, então o servidor cadastrou com sucesso o usuário e retorna o ID para futuros logins
		if(status){
			logged = true;
			System.out.println("** ->Cadastro do usuário no servidor com sucesso\n"+
					"ID : "+ID+
					" senha: "+password+
					" <- **");
			
		//avisar sobre falha do cadastro
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
			//inputs do ID e senha para login
			
			System.out.print("ID: ");
			String ID = input.nextLine();
			
			System.out.print("password: ");
			String Password = input.nextLine();
			
			//requisição de login para o servidor
			requester.loginUser(ID, Password);
			
			return true;
		}
		catch(InputMismatchException e){
			System.out.println("InputMismatchException: Login -> Falha na leitura de dados");
		}
		
		return false;
	}
	
	/**
	 * Método a ser chamada para reposta do servidor.
	 * @param user
	 */
	public void responseLogin (boolean status){
		//se a resposta do servidor for true, então o login foi efetuado com sucesso
		if(status == true){
			//guardar estado de login
			logged = true;
			System.out.println("Login efetuado com sucesso");
		}
		else
			System.out.println("Falha no login");
	} 
	
	/**
	 * Função não tem parametros de entrada, basta pedido ao servidor.
	 * @return
	 */
	public void logoutUser (){
		//requisição de logout para servidor
		requester.logoutUser();
	}
	
	/**
	 * Método a ser chamado para resposta do servidor.
	 * @param status
	 */
	public void responseLogout (boolean status){
		//se estado for true, então o logout foi efetivado no servidor.
		if(status == true){
			//guardar estado de logout
			logged = false;
			System.out.println("Logout efetuado com sucesso");
		}
		else
			System.out.println("Falha no logout");
	}
	
	/**
	 * Método a ser chamado para imprimir mensagem de relatório de operações requisitadas. Aqui irá liberar
	 * o wait do cliente.
	 * @param serverMessage
	 */
	public synchronized void responseMessage (String serverMessage){
		System.out.println(" ********* mensagem do servidor: "+serverMessage+"\n");
		notify();
	}
	
	/**
	 * --
	 */
	public void errorCommand (){
	}
	
	/**
	 * Método para logistica de encerramento da thread requester
	 */
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
		
		//looping para tentativas de conexões
		do{
			//inputs de IP e GATE
			System.out.print("Host_IP: ");
			String host = input.nextLine();
			System.out.print("Gate_: ");
			String gate = input.nextLine();
			
			//criar um requisitante que intermedia requisões por meio de conexão do cliente ao servidor e a resposta dele para este
			requester = ClientConnection.getRequester (host, gate, client);
			
			System.out.println(" ...  ... ... Conectando Servidor ... ... ...");
			
			//caso requisitante foi criado, então a conexão foi estabelecida e o requisitante já pode ser usuado para enviar e receber msg.
			if(requester != null){
				System.out.println(" ************ Conexão estabelecida ************ ");
				break;
			}
			
			//quando o requisitante não for criado oferecer nova tentativa ao usuário
			System.out.println(" ........ Falha na conexão. Tentar de novo? [S / N] ....... ");
			cmd = input.nextLine();
		}while(!cmd.equals("N"));
		
		//se não existir requisitante, então o usuário desistiu da conexão ou houve algum erro inesperado
		if(requester == null)
			return;
			
		//Thread requester irá segurar o aplicativo para a interação com o servidor até o encerramento da run()
		new Thread( client ).start(); 
		new Thread(requester).start();
	}//main

	/**
	 * Em Thread o aplicativo irá receber do usuário requisições para serviço e tais requisições serão 
	 * enviadas ao servidor para processamento. Uma outra thread está responsável por receber as respostas
	 * e chamar os métodos desta classe para que aqui processe o resultado.
	 */
	@Override
	public void run() {
		
		//execução em modo synchronized para uso de wait()
		synchronized(this){
			Scanner input = new Scanner ( System.in );
			String cmd = "";

			//interface de menu de opções do cliente
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
				
				//switch para processar comando do usuário
				switch(cmd){
					case "cadastrar":
						String msg = null;
						System.out.println("Menu escolhido: Cadastrar");
						
						//permitir cadastro apenas se ainda não estiver logado
						if(logged == false){
							
							//verificar regularidade de leitura localmente
							if(this.signupUser()){
								msg = "cadastro de usuario enviado";
								System.out.println(msg);
								System.out.println("Waiting Server response ... ...\n");
								try {
									wait();//entrar em modo de espera até o servidor enviar resposta
								} catch (InterruptedException e) {
									System.out.println("InterruptedException: Exceção do wait() em signup");
									e.printStackTrace();
								}
								
							//avisar caso houver erro localmente
							}else{
								msg = "falha no cadastro local";
								System.out.println(msg);
							}
							
						//avisar que já está logado
						}else
							System.out.println("Operação inválida: Usuário está logado");
						
						break;
						
					case "login":
						msg = null;
						System.out.println("Menu escolhido: Login");
						
						//permitir login apenas se ainda não o tiver feito
						if(logged == false){
							
							//verificar regularidade de leitura de input
							if(this.loginUser()){
								msg = "login requisitado";
								System.out.println(msg);
								System.out.println("Waiting Server response ... ...\n");
								try {
									wait();//esperar resposta do servidor
								} catch (InterruptedException e) {
									System.out.println("InterruptedException: Exceção do wait() em login");
									e.printStackTrace();
								}
							}//if
							
							//avisar caso falhe localmente
							else{
								msg = "falha no login local";
								System.out.println(msg);
							}
							
						//avisar que um usuário já está logado
						}else
							System.out.println("Operação inválida: Usuário está logado");
						break;
						
					case "logout":
						msg = null;
						System.out.println("Menu escolhido: Logout");
						
						//permitir logout apenas se usuário estiver logado
						if(logged == true){
							logoutUser();
							msg = "Requisição de logout enviado ao servidor";
							
							System.out.println(msg);
							System.out.println("Waiting Server response ... ...\n");
							try {
								wait();//esperar resposta do servidor
							} catch (InterruptedException e) {
								System.out.println("InterruptedException: Exceção do wait() em logout");
								e.printStackTrace();
							}
						}else
							System.out.println("Operação inválida: Usuário não está logado");
						break;
						
					case "exit":
						System.out.println("Menu escolhido: exit");
						//fazer o logout caso haja usuário logado antes de sair
						if(logged == true){
							requester.logoutUser();
							msg = "Requisição de logout enviado ao servidor";
							
							System.out.println(msg);
							System.out.println("Waiting Server response ... ...\n");
							try {
								wait();//esperar resposta do servidor
							} catch (InterruptedException e) {
								System.out.println("InterruptedException: Exceção do wait() em logout antes de exit");
								e.printStackTrace();
							}
						}//if
						
						//ao confirmar que não existe usuário logado, sair
						if(logged == false)
							endApp();
						
						//avisar sobre erro ao logout
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
