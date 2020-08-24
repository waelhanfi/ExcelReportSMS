


import java.sql.*;

public class Sql {

  private Connection cnn = null;
  private String Adresse_IP;
  private String Nom_Base;
  private String Login;
  private String Psw;

  public Sql(String adresse_ip, String nom_base, String login, String psw) {
    Adresse_IP = adresse_ip;
    Nom_Base = nom_base;
    Login = login;
    Psw = psw;
  }

  public Sql() {
    Adresse_IP = "172.16.50.5";
    Nom_Base = "DBV9";
    Login = "MSPCDR";
    Psw = "WUSER";

  }

  public void Open_Connexion() {
    try {
      DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
      String url = "jdbc:oracle:thin:@" + Adresse_IP + ":1521:" + Nom_Base + "";
      cnn = DriverManager.getConnection(url, Login, Psw);
    }
    catch (Exception e) {
      System.out.println(e.toString());
    }
  }

  public void Open_Connexion_Access() {
    try {
      Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
      String url = "jdbc:odbc:ges_serv";
      cnn = DriverManager.getConnection(url, "", "");
    }
    catch (Exception e) {
      System.out.println(e.toString());
    }
  }

  public void Update(String query) {
    //System.out.println(query);
    try {
      Statement st = cnn.createStatement();
      st.executeUpdate(query);
      st.close();
    }
    catch (Exception e) {
      System.out.println(e.toString());
    }
  }

  public int Get_Int_For_This_Query(String query) {
    //System.out.println(query);
      try {
      Statement st = cnn.createStatement();
      ResultSet rs = st.executeQuery(query);
      if (rs.next()) {
        return rs.getInt(1);
      }
      else {
        return -1;
      }
    }
    catch (Exception e) {
      System.out.println(e.toString());
      return -1;
    }
  }

  public String Get_String_For_This_Query(String query) {
    try {
      Statement st = cnn.createStatement();
      ResultSet rs = st.executeQuery(query);
      if (rs.next()) {
        return rs.getString(1);
      }
      else {
        return "";
      }
    }
    catch (Exception e) {
      System.out.println(e.toString());
      return "";
    }
  }

  public void Commit() {
    Update("commit");
  }

  public Connection Get_Connection() {
    return cnn;
  }

  public void Fermer_Cnn() {
    try {
      cnn.close();
    }
    catch (Exception e) {

    }
  }

  public static void main(String[] args) {
    Sql sql = new Sql("172.16.50.5", "DBV9", "MSPCDR", "WUSER");
    //sql.Open_Connexion();
  }

}
