/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.FileOutputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;

/**
 *
 * @author Dach
 */
@ManagedBean(name ="extractBean")
@SessionScoped
@Entity
public class WeGotThis implements Serializable {
    @Id
    private Long id;
    private String annee;
    private String mois;
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAnnee() {
        return annee;
    }

    public void setAnnee(String annee) {
        this.annee = annee;
    }

    public String getMois() {
        return mois;
    }

    public void setMois(String mois) {
        this.mois = mois;
    }
    
    public  HashMap<String,HashMap<String,LinkedList<String>>> getListOperateur() {
        Sql sql = new Sql();
        System.out.println(mois);
        System.out.println(annee);
        LinkedList<categorie_services> listOp=Get_Operateur();
        LinkedList<String> listOperateur = new LinkedList<String>();
        // return String that represent all operator in the DB
        try {
        for(categorie_services operateur_service : listOp){
            String listop = operateur_service.Libelle_Categorie;
            
            String query = "select distinct(Operator) from sms_in where"
                    + " lower(operator) in ('" + listop.toLowerCase()+"') and  "
                    + "to_char(timestamp,'MM-YYYY') = '" + mois + "-"
                    + annee + 
                    "'";
        
        
            sql.Open_Connexion();
            Statement st = sql.Get_Connection().createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()){
                listOperateur.add(rs.getString(1));
                
            }

        }
            HashMap<String,HashMap<String,LinkedList<String>>> accountMap = new HashMap<String,HashMap<String,LinkedList<String>>>();
            
            
            for (String operateur : listOperateur){
                
                String key = returnKey(listOp,operateur);
                
                LinkedList<String> listAccount = new LinkedList<String>();
                //System.out.println(operateur);
                String query0 = "select distinct(LARGEACCOUNT) from sms_in "
                        + "where lower(Operator) in (" + key+") and "
                        + "to_char(timestamp,'MM-YYYY') = '" + mois + "-"+ annee +  "'";


                //System.out.println(query0);
                Statement st2 = sql.Get_Connection().createStatement();
                ResultSet rs2 = st2.executeQuery(query0);
                HashMap<String,LinkedList<String>> serviceMap = new HashMap<String,LinkedList<String>>();
                while (rs2.next()) {
                    String largeAccount = rs2.getString(1);
                    String querySidentifier = "select distinct(SERVICEIDENTIFIER) from sms_in "
                        + "where lower(Operator) in (" + key+") and  " +
                            " LARGEACCOUNT = '"+largeAccount+"' and "
                        + "to_char(timestamp,'MM-YYYY') = '" + mois + "-"+ annee +  "'";
                    LinkedList<String> listService =  new LinkedList<>();
                    Statement st3 = sql.Get_Connection().createStatement();
                    ResultSet rs3 = st3.executeQuery(querySidentifier);
                    while(rs3.next()){
                        if (rs3.getString(1)==null) {
                            listService.add("NotFound");
                        }
                        else
                        listService.add(rs3.getString(1).toLowerCase());
                    }
                    
                    
                    
                    serviceMap.put(largeAccount,listService);
                    System.out.println(largeAccount);
                    System.out.println(listService);
                }
                accountMap.put(operateur,serviceMap);
            }//end for each operateur
            System.out.println(accountMap);
            
            sql.Fermer_Cnn();
            return accountMap;
            


        } catch (Exception e) {
            sql.Fermer_Cnn();
            System.out.println(e.toString());
            return null;
        }  
        
    }//end getListOperateur
    
    public boolean getAllInformation() {
        // list jour/mois/année -> operateur -> code -> service -> count
        // key = operateur , valuer = obejctA -> Map
        //obejA : Key = code , valeur objB ->Map
        //Dossier - > pour chaque operateur, page XL pour chaque code -> AccountMap, 
        // ObjB - > key : Date pour tous les services valeur :Count (mawjoud -> valeur, null -> 0)
    try{    
        HashMap<String,HashMap<String,LinkedList<String>>> accountMap = getListOperateur();
        Sql sql = new Sql();
        sql.Open_Connexion();
        
        //Creation d'une matrice !!! ligne -> Date, Colone -> service -> valuer = count
        for (String key : accountMap.keySet()){

            HashMap<String,LinkedList<String>> insideMap = accountMap.get(key);
            HashMap<String,Integer> lastList = new HashMap<String,Integer>();
            LinkedList<String> listcode = new LinkedList<String>();
            HSSFWorkbook workbook = new HSSFWorkbook();
            Sheet sheet1 ;
            Sheet Bordereau = workbook.createSheet("Bordereau");
                HSSFRow row = (HSSFRow) Bordereau.createRow( 1 );
                HSSFCell cellSc = row.createCell( 1 );
                cellSc.setCellValue("Numéro court");
                HSSFCell celTotal = row.createCell( 2 );
                celTotal.setCellValue("Total");   
                
                

            //for(String keyCode : insideMap.keySet()){
            
            for(String keyCode : insideMap.keySet()){    

                LinkedList<Integer> listSumTotal = new LinkedList<Integer>();
                int _totalIndex = 0;
                LinkedList<String> listServices = insideMap.get(keyCode);
                LinkedList<LinkedList<Integer>> list = new LinkedList<LinkedList<Integer>>();
                
                
                
                //HSSFWorkbook workbook = new HSSFWorkbook();
                int pos =0;
                
                sheet1 = workbook.createSheet(keyCode);
                workbook.setSheetOrder(keyCode, pos);
      
                try{
                        
                    // Row with index 1 is the second row in Excel sheet
                    HSSFRow row1 = (HSSFRow) sheet1.createRow( 1 );
                    HSSFCell cell;
                    
                    
                    //creation of the first row with the services
                    //for(categorie_services service : listServices){
                    for(int cellIndex=0;cellIndex<listServices.size();cellIndex++){    
                        cell = row1.createCell( cellIndex +1 );
                        if(listServices.get(cellIndex).equals("NotFound"))
                            cell.setCellValue( "Not Found");
                        
                        else
                            cell.setCellValue( listServices.get(cellIndex));
                    }

                    cell = row1.createCell( listServices.size() +1 );
                    cell.setCellValue("Total");
                    LinkedList<categorie_services> listOp=Get_Operateur();
                    String OperateurKey = returnKey(listOp, key);
                    Vector dates = Les_Jours_Du_Mois(sql,mois,annee);
                    for (int i=0;i<dates.size();i++){
                        LinkedList<Integer> listInterne = new LinkedList<Integer>();
                        int Total = 0;
                        
                        for(String service : listServices){
                            

                            String jour = (String)(dates.elementAt(i));
                            jour = jour.split("-")[0];
                            
                            String query;
                            
                            if(service.equals("NotFound")){
                                //recherche du count pour l'intersection du chaque jour et service(->listSe)
                            query = "select count(*) from sms_in where lower(Operator) in (" + OperateurKey.toLowerCase()
                            + ") and timestamp >= date '"+annee+"-"+mois+"-" +jour+ "'" 
                            +" and timestamp < date '"+annee+"-"+mois+"-"+jour+"'+1"        
                            + " and serviceidentifier is null  and LARGEACCOUNT = '" + keyCode + "'";
                                
                            }
                            else{
                            //recherche du count pour l'intersection du chaque jour et service(->listSe)
                             query = "select count(*) from sms_in where lower(Operator) in (" + OperateurKey.toLowerCase()
                            + ") and timestamp >= date '"+annee+"-"+mois+"-" +jour+ "'" 
                            +" and timestamp < date '"+annee+"-"+mois+"-"+jour+"'+1"        
                            + " and lower(serviceidentifier) in ('" + service.toLowerCase()
                            + "') and LARGEACCOUNT = '" + keyCode + "'";
                            }

                            Statement st = sql.Get_Connection().createStatement();
                            ResultSet rs = st.executeQuery(query);
                            while (rs.next()){
                                listInterne.add(rs.getInt(1));
                                Total += rs.getInt(1);

                                try{
                                    listSumTotal.set(_totalIndex, listSumTotal.get(_totalIndex) + rs.getInt(1));
                                    _totalIndex++;

                                }catch(Exception e){
                                    listSumTotal.add(rs.getInt(1));
                                    _totalIndex++;
                                }

                            }
                                                             
                        }// end for each services
                    
                        try{
                            listSumTotal.set(_totalIndex, listSumTotal.get(_totalIndex) +Total);
                 
                        }catch(Exception e){
                            listSumTotal.add(Total);
                                        
                        }

                        _totalIndex = 0;
                        listInterne.add(Total);
                        list.add(listInterne);
                    

                    }//end for each date
                    int _index = 0;
                    int rowNumber = 2;
                    for(LinkedList<Integer> lis : list){
                        HSSFRow writeRow = (HSSFRow) sheet1.createRow( rowNumber );
                        rowNumber++;
                        int colNumber = 0;
                        HSSFCell writeCell;
                        writeCell = writeRow.createCell(colNumber);
                        colNumber++;
                        writeCell.setCellValue((String)dates.elementAt(_index));
                        _index++;
                       
                        
                        for (int number : lis){
                            writeCell = writeRow.createCell(colNumber);
                            colNumber++;
                            writeCell.setCellValue(number);

                        }
                        //fileOut.println(line);
                    }
                    //fileOut.close();
                    HSSFRow totalRow = (HSSFRow) sheet1.createRow( rowNumber );

                    int colNumber = 0;
                    HSSFCell totalCell;
                    totalCell = totalRow.createCell(colNumber);
                    colNumber++;
                    totalCell.setCellValue("Total");
                        
                    for (Integer sum:listSumTotal){
                        totalCell = totalRow.createCell(colNumber);
                        colNumber++;
                        totalCell.setCellValue(sum);

                    }

                    lastList.put(keyCode, listSumTotal.getLast());
                    
                    
                    int _indRow = 2;
                    
                    for(String code : lastList.keySet()){ 
                        
                        HSSFRow writeRowBordereau = (HSSFRow) Bordereau.createRow( _indRow );
                        _indRow++;

                        HSSFCell writeCellBordereau1;
                        
                        writeCellBordereau1 = writeRowBordereau.createCell(1);
                        writeCellBordereau1.setCellValue(code);
                        
                        writeCellBordereau1 = writeRowBordereau.createCell(2);
                        writeCellBordereau1.setCellValue(lastList.get(code));
                        
                        
                        
                      
                    }
                          

                    
                    // Write the output to a file
                            
                    
                    //String filePath = "C:\\Users\\Dach\\Documents\\NetBeansProjects\\Data2Excel\\"+key+"-"+annee+"-"+mois+".xls";
                    String filePath = path+"\\"+key+"-"+annee+"-"+mois+".xls"; 
                    System.out.println(path);
                    
                    
                    FileOutputStream fileOut = new FileOutputStream(filePath);

                    workbook.write(fileOut);
                    fileOut.close();

                    // Closing the workbook
                    workbook.close();
                }
                catch(Exception e){
                    e.printStackTrace();
                    sql.Fermer_Cnn();
                }
              
            pos++;
            
            }//end for each sc
            
            
            
            System.out.println(lastList.size());
            System.out.print(lastList);
            
            
            
            
        }//end for each operateur
            return true;
    }catch(Exception e){
        e.printStackTrace();
    }
    return false;
    }//end getAllInformation
        
    
    
    
   public Vector Les_Jours_Du_Mois(Sql sql,String _mois,String _annee) {
        Vector les_dates = new Vector();
        String Date_consultation;
        String query;
        String mois = _mois;
        String annee = _annee;
        try {sql.Open_Connexion();
            Statement st_date = sql.Get_Connection().createStatement();
            ResultSet rs_date;
            //SELECTIONNER LES DATES CONSULTATION DANS CETTE PERIODE
            query = "select distinct(to_char(TIMESTAMP,'DD-MM-YYYY')) from sms_in where to_char(TIMESTAMP,'YYYY-MM') in ('"
                    + annee + "-" + mois + "') order by to_char(TIMESTAMP,'DD-MM-YYYY')";
            
            rs_date = st_date.executeQuery(query);
            while (rs_date.next()) {
                //RECPTION DES DATES CONSULTATIONS
                Date_consultation = rs_date.getString(1);
                les_dates.addElement(Date_consultation);
            }
        } catch (SQLException sQLException) {
            System.out.println(sQLException.toString());
            sql.Fermer_Cnn();
        }
        return les_dates;
    }

    public String returnKey(LinkedList<categorie_services> listOp,String key){

        for(categorie_services categorieService : listOp ){

            if(categorieService.Libelle_Categorie.toLowerCase().equals(key.toLowerCase())){
                String Key = categorieService.Liste_Services.toLowerCase();

                return Key;
            }
        }
     
         
     return null;   
    }
   
    public LinkedList<categorie_services> Get_Operateur() {
        LinkedList<categorie_services> les_operateurs = new LinkedList<categorie_services>();
        les_operateurs.add(new categorie_services("Tunisiana", "'OTT','Tunisiana','Ooredoo'"));
        les_operateurs.add(new categorie_services("TunisieTelecom", "'TT','TunisieTelecom','Elissa'"));
        les_operateurs.add(new categorie_services("Orange", "'Orange'"));
        return les_operateurs;
    }


    //public static void main(String[] args) {
       // WeGotThis stat=new WeGotThis();
        //Sql sql = new Sql("localhost", "XE", "HR", "HR");
      //  System.out.println("hi");
        //sql.Fermer_Cnn();
        //sql.Open_Connexion();
        //String mois="07";
        //String annee="2018";
        //stat.getListOperateur();
       // stat.getAllInformation();
        
        //try{
         //   stat.getAllInformation();
        //}catch(Exception e){
            //e.printStackTrace();}
        //sql.Fermer_Cnn();
        
        
        
                                        
       
   // }//end main 

    
}//end WeGotTThis
