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
	
	
	/**
	 * No construtor deve-se guardar as Stream de entrada e saída no atributo do objeto para comunicação em thread
	 * @param server
	 * @param client
	 */
	public ClientRequest (ServerApp server, Socket client){
		this.server = server;
		this.sock = client;
		try {
			//armazenar os canais de stream de entrada e saida
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
			//recebimentos de requisições
			while((cmd = sockIn.readLine()) != null){
				boolean status = false;
				String operation = "";
				String msg = null;
				
				//switch para processamento de requisições do usuario
				switch(cmd){
				
					//o usuário será recebido como forma de string e deverá ser recuperado pelo seu parser
					//após sua adição no servidor o estatus do processo deve ser mostrado.
					case "signUp":
						//receber usuário
						user = User.parseUser (sockIn.readLine());
						int newID = -1;
						//pedido ao servidor cadastrar o usuário e retornar seu ID
						if((newID = server.signupUser (user)) > 0)
							status = true;
						else
							msg = "Valor do ID do usuário enviado errado. Código alterado (deveria ser ID = -1 em hardcoded)";
						operation = "cadastro";
						
						//devolver resultados
						sockOut.println("signUp");
						sockOut.println(status);
						sockOut.println(newID);
						sockOut.println(user.getPassword());
						break;
					
					case "login":
						cmd = sockIn.readLine();
						
						operation = "login";
						String userString = null;
						
						//permitir login se não houver usuário logado no canal
						if(user == null){
							try{
								int ID = Integer.parseInt(cmd.split(";")[0]);
								String password = cmd.split(";")[1];
								
								//requisitar login ao servidor
								userString = server.loginUser(ID, password);
								
								//verificar existencia de resposta
								if(userString != null){
									
									//verificar erro de senha
									if(userString.equals("wrong")){
										msg = "Senha incorreta";
										userString = null;
									}
									
									//usuário e senha encontrado
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
						
						//enviar resposta do login
						sockOut.println("login");
						sockOut.println(status);
						
						break;
						
					case "logout":
						operation = "logout";
						
						//permitir logout apenas se existir usuário logado
						if(user != null){
							user = null;
							status = true;
						}
						else
							msg = "Nenhum usuário logado para logout";
						
						//enviar resposta do logout
						sockOut.println("logout");
						sockOut.println(status);
						break;
					
					case "errorCommand":
						//tratamento de comando indefinido
						operation = "errorCommand";
						msg = "Comando inexistente";
						sockOut.println("errorCommand");
						break;
						
					case "exit":
						//logística de encerramento da thread de requisições do cliente
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
		
	}//run

}
