/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.faces.application.FacesMessage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;




@ManagedBean(name="login_bean")
@SessionScoped
@Entity

public class Login implements Serializable {
    @Id
    private String login;
    private String password;
    private boolean valid;
    
    //constructor
    public Login(){
		
    }
	
    public Login(String login, String password) {
	this.login=login;	
	this.password = password;
    }
    
    //Getters and setters
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
    
    //login method that returns true if user exists   
    public boolean checkuser(){
        
        //preparing some objects for connection 
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        Statement stmt = null;    

        
        Sql sql = new Sql();
 
        String query = "select * from users where login='"
                        + login
                        + "' AND password='"
                        + password
                        + "'";
            System.out.println(query);
            try{ 
                sql.Open_Connexion();
                Statement st = sql.Get_Connection().createStatement();
                ResultSet rs = st.executeQuery(query);
            
                boolean more = rs.next();
                // if user does not exist set the isValid variable to false
            if (!more) 
            {
                System.out.println("Sorry, you are not a registered user! Please sign up first");
                FacesContext.getCurrentInstance().addMessage("myform1:loginBtn", new FacesMessage("login ou mot de passe incorrect"));
                setValid(false);
            } 
	        
            //if user exists set the isValid variable to true
            else if (more) 
            {
                String firstName = rs.getString("login");

                System.out.println("Welcome " + firstName);
                setLogin(login);
                setValid(true);
                sql.Fermer_Cnn();
                return true;
            }
            
             
            }catch(Exception e){
                e.printStackTrace();
            }
       
      return false;

          
    }
    
    public boolean logout() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return true;
    }
  

   
    

   
    
}
