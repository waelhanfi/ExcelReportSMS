
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.Entity;
import javax.persistence.Id;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Dach
 */


public class UserBean {
    @Id
    private String login;
    private String password;
    public boolean valid;
	
    public UserBean(){
		
    }
	
    public UserBean(String login, String password) {
	this.login=login;	
	this.password = password;
    }

    public String getLogin() {
	return login;
    }

    public void setLogin(String login) {
	this.login=login;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String Password) {
	this.password = Password;
    }
    
     public boolean isValid() {
         return valid;
	}

      public void setValid(boolean newValid) {
         valid = newValid;
	}	
}
