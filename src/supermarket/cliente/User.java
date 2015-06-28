package supermarket.cliente;

/**
 * 
 * @author tadashi-nathalia
 *	Classe User para instanciamento de objeto-usuário para cadastro e verificação
 *	de identidade em login.
 *
 *	Atributo ID deve ser dado pelo sistema.
 */
public class User {

	private String Name = null;
	private String Address = null;
	private String Tel = null;
	private String Email = null;
	private int ID = -1;
	private String Password = null;
	
	public User(){
		
	}
	
	public void setName (String name){
		this.Name = name;
	}
	
	public String getName (){
		return Name;
	}
	
	public void setAddress (String address){
		this.Address = address;
	}
	
	public String getAddress (){
		return this.Address;
	}
	
	public void setTel (String tel){
		this.Tel = tel;
	}
	
	public String getTel (){
		return this.Tel;
	}
	
	public void setEmail (String email){
		this.Email = email;
	}
	
	public String getEmail (){
		return this.Email;
	}
	
	public void setID (int ID){
		this.ID = ID;
	}
	
	public int getID (){
		return this.ID;
	}
	
	public void setPassword (String password){
		this.Password = password;
	}
	
	public String getPassword (){
		return this.Password;
	}
}
