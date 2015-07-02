package supermarket.cliente;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientUI extends Application implements Runnable{
	private static final double WIDTH = 900.0;
	private static final double HEIGHT = WIDTH*9/16;
	
	private static ClientApp client;
	
	private Scene Home;
	private Scene Conection;
	private Scene Signup;
	private Scene Login;
	private Scene Buy;
	private Scene Report;
	
	private static TextField homeBoard;
	private static TextField conectBoard;
	private static TextField signBoard;
	private static TextField loginBoard;
	
	
	public static void writeHome (String msg){
		homeBoard.setText(msg);
	}

	public static void writeConection (String msg){
		conectBoard.setText("Conection "+ msg);
	}

	public static void writeSign (String msg){
		signBoard.setText("Sign " + msg);
	}

	public static void writeLogin (String msg){
		loginBoard.setText("Login " + msg);
	}
	
	private Scene buildHome (Stage stg){
		HBox principalDiv = new HBox();
		
		//navegation bar
		VBox navegation = new VBox();
		navegation.setPrefSize(WIDTH/5, HEIGHT);
		
		//navegation bar -> buttons
			//trocar as cenas apenas para usuários autenticados ou qualificados para cada caso
		
		Button conection = new Button("Conection");
		conection.setPrefSize(WIDTH/5, HEIGHT/5);
		conection.setOnMouseClicked(event->{
			//só acessível para usuário não conectado ao servidor
			if(client.getConected() == false)
				stg.setScene(Conection);
		});
		
		Button signup = new Button("Signup");
		signup.setPrefSize(WIDTH/5, HEIGHT/5);
		signup.setOnMouseClicked(event->{
			//só acessível para usuário conectado e não logado
			if(client.getConected() && client.getLogged() == false)
				stg.setScene(Signup);
		});
		
		Button login = new Button ("Login");
		login.setPrefSize(WIDTH/5, HEIGHT/5);
		login.setOnMouseClicked(event->{
			//só acessível para usuário conectado e não logado
			if(client.getConected() && client.getLogged() == false)
				stg.setScene(Login);
		});

		Button logout = new Button ("Logout");
		logout.setPrefSize(WIDTH/5, HEIGHT/5);
		logout.setOnMouseClicked(event->{
			//só acessível para usuário conectado e logado
			if(client.getConected() && client.getLogged())
				client.logout();
		});
		
		Button buy = new Button ("Buy");
		buy.setPrefSize(WIDTH/5, HEIGHT/5);
		buy.setOnMouseClicked(event->{
			//só acessível para usuário conectado e logado
			if(client.getConected() && client.getLogged())
				stg.setScene(Buy);
		});
		
		//adicionar botões à barra
		navegation.getChildren().addAll(conection, signup, login, logout, buy);
		
		//adicionar barra à janela
		principalDiv.getChildren().add(navegation);
		
		//área de conteúdo
		StackPane content = new StackPane();
		content.setPrefSize(WIDTH*4/5, HEIGHT);

		TextField MessageBoard = new TextField("Bem vindo ao serviço de comprar de Supermercado online");
		homeBoard = MessageBoard;
		homeBoard.setEditable(false);
		MessageBoard.setPrefSize(WIDTH/2, HEIGHT/6);
		
		//adicionar texto a área de conteúdo
		content.getChildren().add(MessageBoard);
		
		//adicinar a área à janela
		principalDiv.getChildren().add(content);
		
		//criar cena e o retornar
		Scene home = new Scene(principalDiv);
		return home;
	}
	
	private Scene buildConection (Stage stg){

		StackPane principal = new StackPane();
		principal.setPrefSize(WIDTH, HEIGHT);
		
		TextField MessageBoard = new TextField("Enter the IP and Gate");
		conectBoard = MessageBoard;
		conectBoard.setEditable(false);
		MessageBoard.setPrefSize(WIDTH/2, HEIGHT/6);
		principal.getChildren().add(MessageBoard);
		
		//criar campo de inserção de IP
		HBox aux = new HBox();
		Label hostLabel = new Label("Host_IP: ");
		TextField host = new TextField();
		aux.getChildren().addAll(hostLabel, host);		
		principal.getChildren().add(aux);
		aux.setTranslateY(HEIGHT/6);
		aux.setTranslateX(WIDTH/2-(aux.getPrefWidth()/2));
		
		//criar campo de inserção de Gate
		HBox aux2 = new HBox();
		Label gateLabel = new Label("Gate_: ");
		TextField gate = new TextField();
		aux2.getChildren().addAll(gateLabel, gate);
		principal.getChildren().add(aux2);
		aux2.setTranslateY(HEIGHT/2-(aux2.getPrefHeight()/2));
		aux2.setTranslateX(WIDTH/2-(aux2.getPrefWidth()/2));
		
		Button conect = new Button("conectar");
		principal.getChildren().add(conect);
		conect.setTranslateY(HEIGHT/3);
		conect.setOnMouseClicked(event->{
			client.conectServer(host.getText(), gate.getText());
			if(client.getConected())
				stg.setScene(Home);
		});

		Button back = new Button("Voltar");
		principal.getChildren().add(back);
		back.setTranslateY(HEIGHT*2/6);
		back.setTranslateX(WIDTH/6);
		back.setOnMouseClicked(event->{
			stg.setScene(Home);
		});
		
		//criar cena e o retornar
		Scene conection = new Scene(principal);
		return conection;
	}
	
	private Scene buildSignup(Stage stg){
		
		VBox principal = new VBox();
		principal.setPrefSize(WIDTH, HEIGHT);
		
		TextField MessageBoard = new TextField("Preencha os campos a seguir");
		signBoard = MessageBoard;
		signBoard.setEditable(false);
		MessageBoard.setPrefSize(WIDTH/2, HEIGHT/6);
		principal.getChildren().add(signBoard);
		
		//criar campo de inserção de IP
		VBox form = new VBox();
		HBox aux = new HBox();
		Label nameLabel = new Label("Name: ");
		TextField name = new TextField();
		aux.getChildren().addAll(nameLabel, name);
		form.getChildren().add(aux);
		
		aux = new HBox();
		Label addressLabel = new Label("Endereço: ");
		TextField address = new TextField();
		aux.getChildren().addAll(addressLabel, address);
		form.getChildren().add(aux);
		
		aux = new HBox();
		Label emailLabel = new Label("Email: ");
		TextField email = new TextField();
		aux.getChildren().addAll(emailLabel, email);
		form.getChildren().add(aux);
		
		aux = new HBox();
		Label telLabel = new Label("Tel: ");
		TextField tel = new TextField();
		aux.getChildren().addAll(telLabel, tel);
		form.getChildren().add(aux);
		
		aux = new HBox();
		Label keywordLabel = new Label("Senha: ");
		TextField keyword = new TextField();
		aux.getChildren().addAll(keywordLabel, keyword);
		form.getChildren().add(aux);
		
		principal.getChildren().add(form);
		
		Button signupButton = new Button("cadastrar");
		principal.getChildren().add(signupButton);
		signupButton.setOnMouseClicked(event->{
			client.signup(name.getText(),
					address.getText(),
					email.getText(),
					tel.getText(),
					keyword.getText());
			if(client.getLogged())
				stg.setScene(Home);
		});
		
		Button back = new Button("Voltar");
		principal.getChildren().add(back);
		back.setTranslateY(HEIGHT*2/6);
		back.setTranslateX(WIDTH/6);
		back.setOnMouseClicked(event->{
			stg.setScene(Home);
		});
		
		Scene signup = new Scene (principal);
		return signup;
	}

	private Scene buildLogin(Stage stg){

		StackPane principal = new StackPane();
		principal.setPrefSize(WIDTH, HEIGHT);
		
		TextField MessageBoard = new TextField("Preenche o ID único do usuário e a senha");
		loginBoard = MessageBoard;
		loginBoard.setEditable(false);
		MessageBoard.setPrefSize(WIDTH/2, HEIGHT/6);
		principal.getChildren().add(MessageBoard);
		
		//criar campo de inserção de IP
		HBox aux = new HBox();
		Label idLabel = new Label("ID: ");
		TextField id = new TextField();
		aux.getChildren().addAll(idLabel, id);		
		principal.getChildren().add(aux);
		aux.setTranslateY(HEIGHT/6);
		aux.setTranslateX(WIDTH/2-(aux.getPrefWidth()/2));
		
		//criar campo de inserção de Gate
		HBox aux2 = new HBox();
		Label keyLabel = new Label("Senha_: ");
		TextField key = new TextField();
		aux2.getChildren().addAll(keyLabel, key);
		principal.getChildren().add(aux2);
		aux2.setTranslateY(HEIGHT/2-(aux2.getPrefHeight()/2));
		aux2.setTranslateX(WIDTH/2-(aux2.getPrefWidth()/2));
		
		Button login = new Button("login");
		principal.getChildren().add(login);
		login.setTranslateY(HEIGHT/3);
		login.setOnMouseClicked(event->{
			client.login(id.getText(), key.getText());
			if(client.getLogged())
				stg.setScene(Home);
		});

		Button back = new Button("Voltar");
		principal.getChildren().add(back);
		back.setTranslateY(HEIGHT*2/6);
		back.setTranslateX(WIDTH/6);
		back.setOnMouseClicked(event->{
			stg.setScene(Home);
		});
		
		//criar cena e o retornar
		Scene conection = new Scene(principal);
		return conection;
	}
	
	public static void iniciar(ClientApp client){
		ClientUI.client = client;
		new Thread(new ClientUI()).start();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setWidth(WIDTH);
		primaryStage.setHeight(HEIGHT);
		
		Home = buildHome(primaryStage);
		Conection = buildConection(primaryStage);
		Signup = buildSignup(primaryStage);
		Login = buildLogin(primaryStage);
	
		primaryStage.setOnCloseRequest(event->{
			client.exit();
		});
		
		primaryStage.setScene(Home);
		primaryStage.show();
	}

	
	
	@Override
	public void run() {
		launch();
	}
	
}
