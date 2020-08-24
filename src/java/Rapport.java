
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import javax.persistence.Entity;
import javax.persistence.Id;

@ManagedBean(name ="rapportBean")
@SessionScoped
@Entity
public class Rapport implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;
    
    private Date startDateCalender;
    private Date endDateCalender;
    
    private String startDate="01-07-2018";
    private String endDate="01-07-2018";
    private String operateur="tunisiana";
    private String resultat;
    
    private LinkedList<String> listShortCode;
    private String service;
    private int total;

    public String getResultat() {
        return resultat;
    }

    public void setResultat(String resultat) {
        this.resultat = resultat;
    }
    



    

    public Rapport(String service, int total) {
        this.service = service;
        this.total = total;
    }

    
    

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
    
    public Rapport() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getOperateur() {
        return operateur;
    }

    public void setOperateur(String operateur) {
        this.operateur = operateur;
    }
    
    public String switchDateToString(Date date){
        // Create an instance of SimpleDateFormat used for formatting 
        // the string representation of date (month/day/year)
        SimpleDateFormat ft = new SimpleDateFormat ("dd-MM-yyyy");
        // Get the date today using Calendar object.

        // Using DateFormat format method we can create a string 
        // representation of a date with the defined format.
        String reportDate = ft.format(date);
        return reportDate;
    }
    
    public Date getStartDateCalender() {
        return startDateCalender;
    }

    public void setStartDateCalender(Date startDateCalender) {
        startDate = switchDateToString(startDateCalender);
        this.startDateCalender = startDateCalender;
    }

    public Date getEndDateCalender() {
        return endDateCalender;
    }

    public void setEndDateCalender(Date endDateCalender) {

        endDate = switchDateToString(endDateCalender);
        this.endDateCalender = endDateCalender;
    }

    //getListShortCode() return ListShortCode for each operator between startDate and endDate
    public  LinkedList<String> getListShortCode(){
        //Sql sql = new Sql("localhost", "XE", "HR", "HR");
        Sql sql = new Sql("172.16.50.5", "dbv9", "MSPCDR", "WUSER");
        System.out.println(startDate);
        System.out.println(endDate);
        // return String that represent all short code in the DB
        //String query = "select distinct(LARGEACCOUNT) from sms_in where"
               // + " lower(Operator) in ('" + operateur.toLowerCase()+ "') and "
                //+ "to_char(timestamp,'YYYY-MM-DD') between '" + startDate + "' and '"+ endDate +"'";
        String query = "select distinct(LARGEACCOUNT) from sms_in where"
        + " lower(Operator) in ('" + operateur.toLowerCase()+ "') and "
        + "timestamp >= date '" + startDate + "' and timestamp < date '"+ endDate +"'+1";
        //System.out.println(query);
                
        
        
        listShortCode=new LinkedList<String>();
        try {
            sql.Open_Connexion();
            Statement st = sql.Get_Connection().createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                listShortCode.add(rs.getString(1)); 
            }
            //System.out.println(listShortCode);
            
            sql.Fermer_Cnn();
        }
        catch (Exception e) {
            sql.Fermer_Cnn();
            System.out.println(e.toString());
            
        }
       
       return listShortCode;
    }
    
    //getCount() return listRapport(service,total)
    public LinkedList<Rapport> getCount(){
        //Sql sql = new Sql("localhost", "XE", "HR", "HR");
        Sql sql = new Sql("172.16.50.5", "dbv9", "MSPCDR", "WUSER");
        //System.out.println(startDate);
        //System.out.println(endDate);
        // return String that represent all short code in the DB
        String query = "select distinct(serviceidentifier), count(*) from sms_in where"
                + " lower(Operator) in ('" + operateur.toLowerCase()+ "') and "
                + "timestamp >= date '" + startDate + "' and timestamp < date '"+ endDate +"'+1"
                + " and LARGEACCOUNT in ('"+resultat+"') group by  SERVICEIDENTIFIER";
        //System.out.println(query);
                
        LinkedList<Rapport> listRapport = new LinkedList<Rapport>();
        try {
            sql.Open_Connexion();
            Statement st = sql.Get_Connection().createStatement();
            ResultSet rs = st.executeQuery(query);
                while (rs.next()){
                    if (rs.getString(1)==null) {
                        System.out.println("no data");
                        Rapport r=new Rapport("Aucun service",rs.getInt(2));
                        listRapport.add(r);
                    }else{
                    Rapport r=new Rapport(rs.getString(1),rs.getInt(2));
                    listRapport.add(r);
                    }
                }
                //System.out.println(listRapport);
            
        }
        catch (Exception e) {
            sql.Fermer_Cnn();
            System.out.println(e.toString());
            
        }           
        return listRapport;
    }
    
    
    
    
    
    public static void main(String []args){
        Rapport rp=new Rapport();
        rp.getListShortCode();
        
    }

}
