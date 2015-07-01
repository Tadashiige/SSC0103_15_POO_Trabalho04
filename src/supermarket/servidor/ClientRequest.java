package supermarket.servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;

import supermarket.usuario.*;

public class ClientRequest implements Runnable{
	private BufferedReader sockIn = null;
	private PrintWriter sockOut = null;
	private Socket sock = null;
	private User user = null;
	
	private ServerApp server = null;
	
	//No construtor deve-se guardar as Stream de entrada e saída no atributo do objeto para comunicação em thread
	public ClientRequest (ServerApp server, Socket client){
		this.server = server;
		this.sock = client;
		try {
			this.sockIn = new BufferedReader( new InputStreamReader (client.getInputStream()) );
			this.sockOut = new PrintWriter ( client.getOutputStream(), true );
		} catch (IOException e) {
			System.out.println("IOException: recuperar canal de entrada e saida na thread ClientRequest");
		}
		
	}
	
	/**
	 * A thread irá aguardar as requisições dos usuários via socket e então processar os dados enviados como
	 * forma de String e retornando os dados também como String. Tais Strings devem ser processados segundo
	 * sua relação requisição-resposta.
	 * Por padrão a requisição terá duas linhas de mensagem: 1ª tipo de requisição 2ª dados para ele
	 */
	@Override
	public void run() {
		
		String cmd = "";
		System.out.println("Novo usuário conectado. IP: "+sock.getInetAddress().getHostAddress());
		try {
			while((cmd = sockIn.readLine()) != null){
				boolean status = false;
				String operation = "";
				String msg = null;
				
				//switch para requisições do usuario
				switch(cmd){
				
					//o usuário será recebido como forma de string e deverá ser recuperado pelo seu parser
					//após sua adição no servidor o estatus do processo deve ser mostrado.
					case "signUp":
						user = User.parseUser (sockIn.readLine());
						int newID = -1;
						if((newID = server.signupUser (user)) > 0)
							status = true;
						else
							msg = "Valor do ID do usuário enviado errado. Código alterado (deveria ser ID = -1 em hardcoded)";
						operation = "cadastro";
						sockOut.println("signUp");
						sockOut.println(status);
						sockOut.println(newID);
						sockOut.println(user.getPassword());
						break;
					
					//o id e senha será passado para o servidor analizar e caso encontre a conta o usuário em
					//forma de string será devolvido.
					case "login":
						cmd = sockIn.readLine();
						
						operation = "login";
						String userString = null;
						if(user == null){
							try{
								int ID = Integer.parseInt(cmd.split(";")[0]);
								String password = cmd.split(";")[1];
								
								userString = server.loginUser(ID, password);
								if(userString != null){
									if(userString.equals("wrong")){
										msg = "Senha incorreta";
										userString = null;
									}
									else
										user = User.parseUser(userString);
								}
								else
									msg = "Usuário não encontrado";
	
							}catch(NumberFormatException e){
								System.out.println("Valor inválido para ID: "+cmd.split(";")[0]);
								msg = "Valor não numérico de ID";
							}
						}
						else
							msg = "Usuário já está logado";

						if(userString != null)
							status = true;
						
						sockOut.println("login");
						sockOut.println(status);
						
						break;
						
					case "logout":
						operation = "logout";
						if(user != null){
							user = null;
							status = true;
						}
						else
							msg = "Nenhum usuário logado para logout";
						sockOut.println("logout");
						sockOut.println(status);
						break;
					
					case "errorCommand":
						operation = "errorCommand";
						msg = "Comando inexistente";
						sockOut.println("errorCommand");
						break;
						
					case "exit":
						operation = "exit";
						sockOut.println("exit");
						status = true;
						
						break; 
						
					default:
						break;
						
				}//switch
				
				//enviar mensagem de relatório para o cliente
				if(msg != null){
					sockOut.println(msg);
					System.out.println(msg);
				}else{
					sockOut.println("Operação com Sucesso");
					System.out.println("Operação com Sucesso");
				}
				System.out.println("Operação: "+operation+" status: "+status);
			}//while
			System.out.println("Usuário desconectado. IP: "+sock.getInetAddress().getHostAddress());
			
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
	}

}
