import java.io.Serializable;

import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


import java.util.Date;
import java.util.LinkedList;
import java.util.Vector;
@ManagedBean(name ="pieChartBean")
@SessionScoped
@Entity
public class PieChartData implements Serializable {
    private static final long serialVersionUID = 1L;
     @Id
    private Long id;
    
    private  List<KeyValue> pieDataListService;
    private  List<KeyValue> pieDataListOperateur;
    private  List<KeyValue> pieDataListSc;

    private Date startDateCalender;
    private Date endDateCalender;
    private String startDate="01-07-2018";
    private String endDate="12-07-2018";

    
    
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

    public Date getStartDateCalender() {
        return startDateCalender;
    }

    public void setStartDateCalender(Date startDateCalender) {
        System.out.println(startDateCalender);
                System.out.println("\n");

        
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

    
   

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    

    public PieChartData() {
    }
    


    
    // getPieDataListService() return KeyValue(service,total) as pieDataListService
    public  List<KeyValue> getPieDataListService() {
        
        //Sql sql = new Sql("localhost", "XE", "HR", "HR");
        Sql sql = new Sql();
        System.out.println(startDate);
        System.out.println(endDate);
        // return String that represent all short code in the DB
        String query = "select distinct(serviceidentifier), count(*) from sms_in where "
                        + "timestamp >= date '" + startDate + "' and timestamp < date '"+ endDate +"'+1"
                        + "group by  SERVICEIDENTIFIER"; 
        System.out.println(query);
        pieDataListService = new ArrayList<KeyValue>();
        try {
            sql.Open_Connexion();
            Statement st = sql.Get_Connection().createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                if (rs.getString(1)==null) {
                        System.out.println("no data");
                         pieDataListService.add(new KeyValue("Aucun service",String.valueOf(rs.getInt(2))));
                    }else{
                     pieDataListService.add(new KeyValue(rs.getString(1),String.valueOf(rs.getInt(2))));
                    }
                

            }
        sql.Fermer_Cnn();
        }
        
        catch (Exception e) {
            sql.Fermer_Cnn();
            System.out.println(e.toString());
            
        }   
        
        
        return pieDataListService;
    }

    
    public void setPieDataListService(List<KeyValue> pieDataListService) {
        this.pieDataListService = pieDataListService;
    }

    
    public LinkedList<categorie_services> Get_Operateur() {
        LinkedList<categorie_services> les_operateurs = new LinkedList<categorie_services>();
        les_operateurs.add(new categorie_services("Tunisiana", "'OTT','Ooredoo','Tunisiana'"));
        les_operateurs.add(new categorie_services("TunisieTelecom", "'TT','TunisieTelecom','Elissa'"));
        les_operateurs.add(new categorie_services("Orange", "'Orange'"));
        return les_operateurs;
    }
    
    // getPieDataListOperatur() return KeyValue(operateur,total) as pieDataListOperateur
    public  List<KeyValue> getPieDataListOperateur() {
        
        //Sql sql = new Sql("localhost", "XE", "HR", "HR");
        Sql sql = new Sql();
        System.out.println(startDate);
        System.out.println(endDate);
        
        LinkedList<categorie_services> listOperateur=Get_Operateur();
        pieDataListOperateur = new ArrayList<KeyValue>();
        for(categorie_services operateur : listOperateur){
            String listop = operateur.Libelle_Categorie;
           
            // return String that represent all operator code in the DB
            String query = "select distinct(OPERATOR), count(*) from sms_in where "
                            + "timestamp >= date '" + startDate + "' and timestamp < date '"+ endDate +"'+1 "
                            + "and lower(operator) in ('" + listop.toLowerCase()+"') "
                            + " group by  OPERATOR";
            System.out.println(query);
            
            try {
                sql.Open_Connexion();
                Statement st = sql.Get_Connection().createStatement();
                ResultSet rs = st.executeQuery(query);
                while (rs.next()){

                    pieDataListOperateur.add(new KeyValue(rs.getString(1),String.valueOf(rs.getInt(2))));
                    
                }
                
                System.out.println(pieDataListOperateur);
                sql.Fermer_Cnn();
            }

            catch (Exception e) {
                e.printStackTrace();
                sql.Fermer_Cnn();
                System.out.println(e.toString());

            }   
        }
        
        return pieDataListOperateur;
    }
    
    
    public void setPieDataListOperateur(List<KeyValue> pieDataListOperateur) {
        this.pieDataListOperateur = pieDataListOperateur;
    }

    // getPieDataListSc() return KeyValue(shortcode,total) as pieDataListSC
    public  List<KeyValue> getPieDataListSc() {

        //Sql sql = new Sql("localhost", "XE", "HR", "HR");
        Sql sql = new Sql();
        System.out.println(startDate);
        System.out.println(endDate);
        // return String that represent all short code in the DB
        String query = "select distinct(LARGEACCOUNT), count(*) from sms_in where "
                        + "timestamp >= date '" + startDate + "' and timestamp < date '"+ endDate +"'+1 "
                        + "group by  LARGEACCOUNT";
        System.out.println(query);
        pieDataListSc = new ArrayList<KeyValue>();
        try {
            sql.Open_Connexion();
            Statement st = sql.Get_Connection().createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){

                pieDataListSc.add(new KeyValue(rs.getString(1),String.valueOf(rs.getInt(2))));
            }
        sql.Fermer_Cnn();
        }

        catch (Exception e) {
            sql.Fermer_Cnn();
            System.out.println(e.toString());

    }   


    return pieDataListSc;
}
    public void setPieDataListSc(List<KeyValue> pieDataListSc) {
        this.pieDataListSc = pieDataListSc;
    }

    

}