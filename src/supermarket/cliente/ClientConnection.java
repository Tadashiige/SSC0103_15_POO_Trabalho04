/**
 * Classe simples de uso apenas inicial para estabelecer conexão com o servidor.
 */

package supermarket.cliente;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientConnection{
	private String host = null;
	private String gate;
	private Socket sock;
	private static ClientConnection clientConnection = null;
	private static Requester requester = null;
	
	/**
	 * Como só deve haver uma conexão com o servidor, o construtor é privado - modelo Singletton
	 * @param host
	 * @param gate
	 * @param client
	 */
	private ClientConnection (String host, String gate, ClientApp client){
		this.host = host;
		this.gate = gate;
		try {
			sock = new Socket(this.host, Integer.parseInt(this.gate));
			requester = Requester.getRequester(sock, client);
		} catch (UnknownHostException e) {
			
			System.out.println("Endereço ip não encontrado.");
		} catch (IOException e) {
			
			System.out.println("Erro de IO com o socket cliente");
		} catch (NumberFormatException e){
			
			System.out.println("NumberFormatException: valor de gate inválido");
		}
	}
	
	/**
	 * Função verificadora do modelo Singletton, para instancia de apenas uma conexão com o servidor
	 * @param host
	 * @param gate
	 * @param client
	 * @return
	 */
	public static Requester getRequester (String host, String gate, ClientApp client){
		if(clientConnection == null){
			clientConnection = new ClientConnection(host, gate, client);
			if(ClientConnection.requester == null){
				clientConnection = null;
				return null;
			}
		}
		return ClientConnection.requester;
	}

}
