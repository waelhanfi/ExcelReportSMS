

public class categorie_services {

    public String Libelle_Categorie;
    public String Liste_Services;

    public categorie_services(String Libelle_Categorie, String Liste_Services) {
        this.Libelle_Categorie = Libelle_Categorie;
        this.Liste_Services = Liste_Services;
    }
    
    @Override
    public String toString(){
        return Libelle_Categorie;
    }

    public categorie_services() {
    }

    public static void main(String[] args) {
        categorie_services categorie_services1 = new categorie_services();
    }

}
