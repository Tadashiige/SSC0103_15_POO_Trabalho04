package supermarket.cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ClientApp implements Runnable {

	private static User user = null;
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
			aux = input.readLine();
			newUser.setName(aux);
			
			aux = input.readLine();
			newUser.setAddress(aux);
			
			aux = input.readLine();
			newUser.setEmail(aux);
			
			aux = input.readLine();
			newUser.setTel(aux);
			
			aux = input.readLine();
			newUser.setPassword(aux);
			
			aux = input.readLine();
			newUser.setID(Integer.parseInt(aux));
			
			this.user = newUser;
			
			status = true;
		} catch (IOException e) {
			System.out.println("Excessão de entrada com .readLine");
			
		} catch (NumberFormatException e){
			System.out.println("Excessão numérico do ID: " + aux);
			
		}
		
		return status;
	}
	
	/**
	 * Entrar no sistema pelo login do usuario. Retorna status do procedimento.
	 * @return
	 */
	public boolean loginUser (){
		return false;
	}
	
	public static void main(String[] args) {
		Scanner input = new Scanner ( System.in );
		String host = input.nextLine();
		String gate = input.nextLine();
		input.close();
		
		requester = ClientConnection.getRequester (host, gate);
		
		if(requester == null)
			return;
			
		//Thread irá segurar o aplicativo para a interação com o servidor até o encerramento da run()
		new Thread( new ClientApp() ).start(); 
	}

	@Override
	public void run() {
		
		BufferedReader input = new BufferedReader ( new InputStreamReader (System.in) );
		
		String cmd = null;
		
		System.out.println("\nBem vindo ao sistema de Supermercado Online"+
				"Para continuar pressione o enter\n");
		
		try {
			while(!(cmd = input.readLine()).equals("exit")){
				System.out.println("\n\n"+
						"Digite as opções a seguir:"+
						"cadastrar - cadastrar Novo Usuário"+
						"login - logar como usuario"+
						"exit - sair do aplicativo"+
						"\n\n");
				switch(cmd){
				case "cadastrar":
					String msg = null;
					if(this.signupUser())
						msg = "cadastro de usuario bem sucedido";
					else
						msg = "falha no cadastro";
					
					System.out.println(msg);
					break;
					
				case "login":
					msg = null;
					if(this.loginUser()){
						msg = "login efetuado";
					}
					else
						msg = "falha no login";
					System.out.println(msg);
					break;
					
				default:
					break;
				}
			}
		} catch (IOException e) {
			
			System.out.println("erro na leitura de cmd do menu");
		}
	}
	
}
