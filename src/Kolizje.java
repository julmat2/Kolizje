import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.Scanner;
public class Kolizje {
    public static String[] kolumny(String input){
        int ilosc_kolumn_w_pliku=8; //Do modyfikacji ile jest kolumn w pliku wejściowym
        String[] info = input.split(" ");
        int x=0;
        int y=0;
        String[] kolumny = new String[ilosc_kolumn_w_pliku];
        while(x!=info.length)
        {
            if(info[x].length()!=0)
            {
                kolumny[y]=info[x];
                y++;
            }
            x++;
        }
        return kolumny;
    }

    public static class Dane{
        String data; //data
        double czas; //czas
        double x;
        double y;
        double z;
        double vx;
        double vy;
        double vz;
        public Dane(String data, double czas, double x, double y, double z, double vx, double vy, double vz){
            this.data = data;
            this.czas = czas;
            this.x = x;
            this.y = y;
            this.z = z;
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
        }
        public static void main(String[] args) throws FileNotFoundException {
            String wybrany_glowny="";
            //--------------------------------------------------------------------------------------------LISTA PLIKÓW
            String sciezka = "C:/Users/matys/Desktop/PROGRAMY_DR/Kolizje/dane/";
            File katalog = new File(sciezka);
            String[] pliki;
            FilenameFilter filtr = new FilenameFilter() {
                @Override
                public boolean accept(File f, String name) {
                    return name.endsWith(".txt");
                }
            };
            if(katalog.exists()) {
                pliki = katalog.list(filtr);
            }
            else{
                System.out.println("Brak folderu z danymi\nPowinien się znajdować w \""+sciezka+"\"");
                return;
            }
            if(pliki.length==0)
            {
                System.out.println("Brak plików w folderze \""+sciezka+"\"");
                return;
            }
            System.out.println("Lista plików:");
            for(String plik : pliki)
                System.out.println(plik);

            //---------------------------------------------------------------------------------WYBÓR SATELITY GŁÓWNEGO
            String plik_glowny = "";
            File plik_dane_glowny = new File(plik_glowny);
            while(!plik_dane_glowny.exists()) {
                System.out.println("Wybierz głównego satelitę");
                Scanner in = new Scanner(System.in);
                wybrany_glowny = in.nextLine();
                plik_glowny = sciezka + "/" + wybrany_glowny;
                //System.out.println("Ścieżka do pliku głównego: " + plik_glowny);
                plik_dane_glowny = new File(plik_glowny);
                if (!plik_dane_glowny.exists()) {
                    System.out.println("Nie ma takiego pliku");
                }
            }
            File orData = new File("C:/Users/matys/Desktop/PROGRAMY_DR/Kolizje/dane/");
            DataProvidersManager manager = DataContext.getDefault().getDataProvidersManager();
            manager.addProvider(new DirectoryCrawler(orData));
            //PrintWriter output = new PrintWriter("C:/Users/rafal/Desktop/output.rav");
            //---------------------------------------------------------------------------------------PORÓWNANIE PLIKÓW
            int ktory_plik = 0;
            while(pliki.length!=ktory_plik)
            {
                if(wybrany_glowny.equals(pliki[ktory_plik]))
                {
                    System.out.println("Pomijane jest sprawdzenie tego samego pliku.");
                    ktory_plik++;
                    continue;
                }
                PrintWriter plik_wynik = new PrintWriter("C:/Users/matys/Desktop/PROGRAMY_DR/Kolizje/dane/porownanie"+pliki[ktory_plik]);
                Scanner glowny = new Scanner(plik_dane_glowny);
                Scanner porownanie = new Scanner(new File(sciezka+"/"+pliki[ktory_plik]));
                int il_linii=0;
                while(glowny.hasNext()&&porownanie.hasNext())
                {
                    String[] glowny_linia = kolumny(glowny.nextLine());
                    Dane dane_glowny = new Dane(
                            glowny_linia[0],
                            Double.parseDouble(glowny_linia[1]),
                            Double.parseDouble(glowny_linia[2]),
                            Double.parseDouble(glowny_linia[3]),
                            Double.parseDouble(glowny_linia[4]),
                            Double.parseDouble(glowny_linia[5]),
                            Double.parseDouble(glowny_linia[6]),
                            Double.parseDouble(glowny_linia[7])
                    );
                    String[] porownianie_linia = kolumny(porownanie.nextLine());
                    Dane dane_por = new Dane(
                            porownianie_linia[0],
                            Double.parseDouble(porownianie_linia[1]),
                            Double.parseDouble(porownianie_linia[2]),
                            Double.parseDouble(porownianie_linia[3]),
                            Double.parseDouble(porownianie_linia[4]),
                            Double.parseDouble(porownianie_linia[5]),
                            Double.parseDouble(porownianie_linia[6]),
                            Double.parseDouble(porownianie_linia[7])
                    );
                    if(!dane_glowny.data.equals(dane_por.data))
                    {
                        System.out.println("Różna data");
                        break;
                    }
                    Vector3D pos_g = new Vector3D(dane_glowny.x,dane_glowny.y,dane_glowny.z);
                    Vector3D pos_p = new Vector3D(dane_por.x,dane_por.y,dane_por.z);
                    double dystans_m = Vector3D.distance(pos_g,pos_p);
                    double dystans_km = Vector3D.distance(pos_g,pos_p)*0.001;
                    plik_wynik.println(
                            dane_glowny.data+"\t"+
                                    dane_glowny.czas+"\t"+
                                    dystans_m+"\t"+
                                    dystans_km+"\t"+pliki[ktory_plik]
                    );
                    il_linii++;
                }
                plik_wynik.close();
                System.out.println(pliki[ktory_plik]+": "+il_linii+" obliczeń");
                ktory_plik++;
            }
        }
    }
}
