package supermarket.servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import supermarket.cliente.User;

public class ClientRequest implements Runnable{
	private BufferedReader sockIn = null;
	private PrintStream sockOut = null;
	
	private ServerApp server = null;
	
	//No construtor deve-se guardar as Stream de entrada e saída no atributo do objeto para comunicação em thread
	public ClientRequest (ServerApp server, Socket client){
		this.server = server;
		try {
			this.sockIn = new BufferedReader( new InputStreamReader (client.getInputStream()) );
			this.sockOut = new PrintStream ( client.getOutputStream() );
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
		
		String cmd = null;
		
		try {
			while((cmd = sockIn.readLine()) != null){
				boolean status = false;
				String msg = "";
				
				//switch para requisições do usuario
				switch(cmd){
				
					//o usuário será recebido como forma de string e deverá ser recuperado pelo seu parser
					//após sua adição no servidor o estatus do processo deve ser mostrado.
					case "signUp":
						User newUser = User.parseUser (sockIn.readLine());
						if(server.signupUser (newUser))
							status = true;
						msg = "cadastro";
						sockOut.println(status);
						break;
					
					//o id e senha será passado para o servidor analizar e caso encontre a conta o usuário em
					//forma de string será devolvido.
					case "login":
						int ID = Integer.parseInt(cmd.split(";")[0]);
						String password = cmd.split(",")[1];
						
						String userString = server.loginUser(ID, password);
						sockOut.println(userString);					
						break;
						
					default:
						break;
						
				}//switch
				
				System.out.println("Operação: "+msg+" status: "+status);
			}//while
			
		} catch (IOException e) {
		
			e.printStackTrace();
		}
		
	}

}
