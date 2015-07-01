package supermarket.usuario;

/**
 * 
 * @author tadashi-nathalia
 *	Classe User para instanciamento de objeto-usuário para cadastro e verificação
 *	de identidade em login.
 *
 *	Atributo ID deve ser dado pelo sistema.
 */
public class User {

	private int ID = -1;
	private String Name = null;
	private String Address = null;
	private String Tel = null;
	private String Email = null;
	private String Password = null;
	
	public User(){
		this.ID = -1;
	}
	
	/**
	 * Sobreposição para armazenamento em String do User
	 * @return
	 */
	@Override
	public String toString (){
		return this.ID + ";"+
				this.Name + ";"+
				this.Address + ";"+
				this.Tel + ";"+
				this.Email + ";"+
				this.Password;
	}
	
	/**
	 * Parser para recuperar User de string
	 * @param data
	 * @return
	 */
	public static User parseUser (String data){
		User recoverUser = new User();
		String [] dataSplit = data.split(";");
		try {
			recoverUser.setID(Integer.parseInt(dataSplit[0]));
			recoverUser.setName(dataSplit[1]);
			recoverUser.setAddress(dataSplit[2]);
			recoverUser.setTel(dataSplit[3]);
			recoverUser.setEmail(dataSplit[4]);
			recoverUser.setPassword(dataSplit[5]);
			
			return recoverUser;
		}
		catch (NumberFormatException e){
			System.out.println("NumberFormatException: Exception de formatação no User Parse");
			e.printStackTrace();
		}
		catch (ArrayIndexOutOfBoundsException e){
			System.out.println("Array Out of Bounds: Erro na gravação ou escrita da String User");
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * Sets e Gets dos atributos
	 */
	
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
