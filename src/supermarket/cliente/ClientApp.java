package supermarket.cliente;

import java.util.InputMismatchException;

import supermarket.usuario.*;

public class ClientApp{

	private static boolean conected = false;
	private static boolean logged = false;
	private static Requester requester;
	private static ClientApp client = null;
	private static String Message = null;
	
	public String getMessage (){
		return Message;
	}
	
	public boolean getLogged(){
		return logged;
	}
	
	public boolean getConected(){
		return conected;
	}
	
	/**
	 * Criação de usuário pelo Terminal. Sem validação de dados (tipos, formatação, etc)
	 * @return
	 */
	public boolean signupUser (String name,
								String address,
								String email,
								String tel,
								String keyword){
		boolean status = false;
		User newUser = new User();
		
		//inputs de dados para cadastro
		
		newUser.setName(name);
		newUser.setAddress(address);
		newUser.setEmail(email);
		newUser.setTel(tel);
		newUser.setPassword(keyword);			
		
		//requisição para cadastro do usuário
		requester.signupUser(newUser);
		
		//estatus local sobre cadastro
		status = true;
			
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
			Message = "** ->Cadastro do usuário no servidor com sucesso\n"+
					"      ID : "+ID+
					" senha: "+password+
					" <- **";
			
		//avisar sobre falha do cadastro
		} else{
			Message = "Falha no cadastro do usuário no servidor";
		}
		
		ClientUI.writeSign(Message);
		ClientUI.writeHome("Usuário logado");
	}
	
	/**
	 * Entrar no sistema pelo login do usuario. Retorna status do procedimento.
	 * @return
	 */
	public boolean loginUser (String id, String keyword){
		try{
			//requisição de login para o servidor
			requester.loginUser(id, keyword);
			
			return true;
		}
		catch(InputMismatchException e){
			Message = "InputMismatchException: Login -> Falha na leitura de dados";
			ClientUI.writeLogin(Message);
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
			Message = "Login efetuado com sucesso";
		}
		else
			Message = "Falha no login";
		
		ClientUI.writeLogin(Message);
		ClientUI.writeHome("Usuário logado");
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
			Message = "Logout efetuado com sucesso";
		}
		else
			Message = "Falha no logout";
		ClientUI.writeLogin(Message);
		ClientUI.writeHome(Message);
	}
	
	/**
	 * Método a ser chamado para imprimir mensagem de relatório de operações requisitadas. Aqui irá liberar
	 * o wait do cliente.
	 * @param serverMessage
	 */
	public void responseMessage (String serverMessage){
		Message = " ********* mensagem do servidor: "+serverMessage+"\n";
		ClientUI.writeHome(Message);
		switch(serverMessage.split(":")[0]){
		case "signup":
			ClientUI.writeSign(Message);
			break;
		case "login":
			ClientUI.writeLogin(Message);
			break;
		case "logout":
			ClientUI.writeLogin(Message);
			break;
		default:
			break;
		}
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
	
	public void conectServer (String host, String gate){
		
		//criar um requisitante que intermedia requisões por meio de conexão do cliente ao servidor e a resposta dele para este
		requester = ClientConnection.getRequester (host, gate, client);
		
		Message = " ...  ... ... Conectando Servidor ... ... ...\n";
		
		//caso requisitante foi criado, então a conexão foi estabelecida e o requisitante já pode ser usuado para enviar e receber msg.
		if(requester != null){
			Message += " ************ Conexão estabelecida ************ ";
			
			//Thread requester irá segurar o aplicativo para a interação com o servidor até o encerramento da run()
			new Thread(requester).start();
			
			conected = true;
		}		
		ClientUI.writeConection(Message+"\nFalha na conexão");
	}
	
	/**
	 * O sistema irá pedir de imediato o Ip-host do servidor e a porta para conexão. Caso a conexão
	 * não aconteça
	 * @param args
	 */
	public static void main(String[] args) {
		
		client = new ClientApp();
		ClientUI.iniciar(client);
		
	}//main

	public void signup(String name,
						String address,
						String email,
						String tel,
						String keyword){
			String msg = null;
			
			//permitir cadastro apenas se ainda não estiver logado
			if(logged == false){
				
				//verificar regularidade de leitura localmente
				if(this.signupUser(name,
									address,
									email,
									tel,
									keyword)){
					msg = "cadastro de usuario enviado";
					Message = msg + "\nWaiting Server response ... ...\n";
					
				//avisar caso houver erro localmente
				}else{
					msg = "falha no cadastro local";
					Message = msg;
				}
				
			//avisar que já está logado
			}else
				Message= "Operação inválida: Usuário está logado";
			
			ClientUI.writeSign(Message);
	}//signup
			
	public void login(String id, String keyword){
			String msg = null;
			
			//permitir login apenas se ainda não o tiver feito
			if(logged == false){
				
				//verificar regularidade de leitura de input
				if(this.loginUser(id, keyword)){
					msg = "login requisitado";
					
					Message = msg + "\nWaiting Server response ... ...\n";

				}//if
				
				//avisar caso falhe localmente
				else{
					msg = "falha no login local";
					Message = msg;
				}
				
			//avisar que um usuário já está logado
			}else
				Message = "Operação inválida: Usuário está logado";
			
			ClientUI.writeLogin(Message);
	}//login
	
	public void logout(){
			String msg = null;
			
			//permitir logout apenas se usuário estiver logado
			if(logged == true){
				logoutUser();
				msg = "Requisição de logout enviado ao servidor";
				
				Message = msg + "\nWaiting Server response ... ...\n";

			}else
				Message = "Operação inválida: Usuário não está logado";
			
			ClientUI.writeHome(Message);
	}//logout
			
	public void exit(){
			if(conected == true){
				//fazer o logout caso haja usuário logado antes de sair
				if(logged == true){
					requester.exitUser();
					String msg = "Requisição de logout enviado ao servidor";
					
					Message = msg + "\nWaiting Server response ... ...\n";
	
				}//if
				
				//ao confirmar que não existe usuário logado, sair
				if(logged == false)
					endApp();
				
				//avisar sobre erro ao logout
				else
					Message = "Error: não possível logout do sistema. Operação exit sem sucesso";
				ClientUI.writeHome(Message);
			}
	}//exit
				
}
