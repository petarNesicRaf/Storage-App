package rs.rafstorage.handler;

import rs.rafstorage.enumeration.SortingType;
import rs.rafstorage.exceptions.*;

import javax.xml.bind.Element;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Komponenta koja ima funkciju alata za rad sa skadistem fajlova i podrzava razne operacije nad fajlovima i samim skladistem. Biblioteka podrzava osnovne operacije nad skladistem, podesavanje konfiguracije skladista i pretragu skladista. Osnovne operacije ukljucuju kreiranje, brisanje, promenu naziva, premestanje, preuzimanje i smestanje direktorijuma i fajlova. Podesavanjem konfiguracijem korisnik moze da zada velicinu skladista, maksimalni broj fajlova po direktorijumu i vrste fajlova koji nisu prihvatljivi u skladistu. Pretraga skladista obuhvata listanje fajlova sortiranih po nazivu, vremenu modifikacije i kreacije na razne nacine.
 *
 * @author Ognjen Minic
 * @author Petar Nesic
 */
public interface StorageHandler {
    /**
     * Inicijalizacija korenskog direktorijuma i postavljanje podrazumevane konfiguracije.
     * @param path Putanja na kojoj se kreira korenski direktorijum.
     * @throws ElementNotFoundException Okida izuzetak ukoliko direktorijum na prosledjenoj putanji nije uspesno pronadjen.
     * @throws RootDirectoryException Okida izuzetak ukoliko direktorijum na putanji nije korensko skladiste.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @return Vraca "true" ukoliko je skladiste uspesno pronadjeno.
     */
    boolean getStorage(String path) throws ElementNotFoundException, RootDirectoryException, IOException;

    /**
     * Kreiranje korenskog direktorijuma na prosledjenoj putanji uz podrazumevanu konfiguraciju.
     * @param path Putanja na kojoj se kreira direktorijum.
     * @throws ElementNotCreatedException Okida izuzetak ukoliko skladista nije uspesno kreirano.
     * @throws RootDirectoryException Okida izuzetak ukoliko korenski direktorijum nije uspesno kreiran ili pronadjen.
     * @throws ElementNotFoundException Okida izuzetak ukoliko putanja skladista nije pronadjena.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @return Vraca "true" ukoliko je korenski direktorijum uspesno kreiran.
     */
    boolean createStorage(String path) throws ElementNotFoundException,ElementNotCreatedException,RootDirectoryException,IOException;

    /**
     * Kreiranje korenskog direktorijuma na prosledjenoj putanji sa maksimalnim brojem fajlova po direktorijumu.
     * @param path Putanja na kojoj se kreira direktorijum
     * @param numOfFiles Maksimalan broj fajlova po direktorijumu.
     * @throws ElementNotCreatedException Okida izuzetak ukoliko skladiste nije uspesno kreirano.
     * @throws RootDirectoryException Okida izuzetak ukoliko korenski direktorijum nije uspesno kreiran ili pronadjen.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @throws ElementNotFoundException Okida izuzetak ukoliko putanja skladista nije pronadjena.
     * @return Vraca "true" ukoliko je korenski direktorijum uspesno kreiran.
     */
    boolean createStorage(String path, int numOfFiles) throws ElementNotFoundException,ElementNotCreatedException,RootDirectoryException, IOException;

    /**
     * Kreiranje korenskog direktorijuma na prosledjenoj putanji sa maksimalnom velicinom skladista i brojem fajlova po direktorijumu.
     * @param path Putanja na kojoj se kreira direktorijum.
     * @param size Maksimalna velicina skladista u bajtovima.
     * @param numOfFiles Maksimalan broj fajlova po direktorijumu.
     * @throws ElementNotCreatedException Okida izuzetak ukoliko skladiste nije uspesno kreirano.
     * @throws RootDirectoryException Okida izuzetak ukoliko korenski direktorijum nije uspesno kreiran ili pronadjen.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @throws ElementNotFoundException Okida izuzetak ukoliko putanja skladista nije pronadjena.
     * @return Vraca "true" ukoliko je korenski direktorijum uspesno kreiran.
     */
    boolean createStorage(String path, long size, int numOfFiles) throws ElementNotFoundException,ElementNotCreatedException,RootDirectoryException, IOException;

    /**
     * Kreiranje korenskog direktorijuma na prosledjenoj putanji sa maksimalnom velicinom skladista, brojem fajlova i listom nedozvoljenih ekstenzija.
     * @param path Putanja na kojoj se kreira direktorijum.
     * @param size Maksimalna velicina skladista.
     * @param numOfFiles Maksimalan broj fajlova.
     * @param extensions Lista ekstenzija.
     * @throws ElementNotCreatedException Okida izuzetak ukoliko skladiste nije uspesno kreirano.
     * @throws RootDirectoryException Okida izuzetak ukoliko korenski direktorijum nije uspesno kreiran ili pronadjen.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @throws ElementNotFoundException Okida izuzetak ukoliko putanja skladista nije pronadjena.
     * @return Vraca "true" ukoliko je korenski direktorijum uspesno kreiran
     */
    boolean createStorage(String path, long size, int numOfFiles, String ... extensions) throws ElementNotFoundException,ElementNotCreatedException,RootDirectoryException,IOException;

    /**
     * Kreiranje korenskog direktorijuma na prosledjenoj putanji sa listom nedozvoljenih ekstenzija.
     * @param path Putanja na kojoj se kreira direktorijum.
     * @param extensions Lista ekstenzija
     * @return Vraca "true" ukoliko je korenski direktorijum uspesno kreiran.
     * @throws ElementNotCreatedException Okida izuzetak ukoliko skladiste nije uspesno kreirano.
     * @throws RootDirectoryException Okida izuzetak ukoliko korenski direktorijum nije uspesno kreiran ili pronadjen.
     * @throws ElementNotFoundException Okida izuzetak ukoliko putanja skladista nije pronadjena.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     */
    boolean createStorage(String path, String ...extensions) throws ElementNotFoundException, ElementNotCreatedException,RootDirectoryException,IOException;

    /**
     * Kreiranje korenskog direktorijuma na prosledjenoj putanji sa konfiguracijom maksimalne velicine skladista
     * @param path Putanja na kojoj se kreira direktorijum.
     * @param size Maksimalna velicina skladista u bajtovima.
     * @return Vraca "true" ukoliko je korenski direktorijum uspesno kreiran.
     * @throws ElementNotCreatedException Okida izuzetak ukoliko skladiste nije uspesno kreirano.
     * @throws RootDirectoryException Okida izuzetak ukoliko korenski direktorijum nije uspesno kreiran ili pronadjen.
     * @throws ElementNotFoundException Okida izuzetak ukoliko putanja skladista nije pronadjena.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     */
    boolean createStorage(String path, long size)throws ElementNotFoundException,ElementNotCreatedException,RootDirectoryException, IOException;

    /**
     * Kreiranje korenskog direktorijuma na prosledjenoj putanji sa konfiguracijom maksimalnog broja fajlova po direktorijumu i listom nedozvoljenih ekstenzija.
     * @param path Putanja na kojoj se kreira direktorijum.
     * @param numOfFiles Maksimalan broj fajlova po direktorijumu.
     * @param extensions Lista nedozvoljenih ekstenzija.
     * @return Vraca "true" ukoliko je korenski direktorijum uspesno kreiran.
     * @throws ElementNotCreatedException Okida izuzetak ukoliko skladiste nije uspesno kreirano.
     * @throws RootDirectoryException Okida izuzetak ukoliko korenski direktorijum nije uspesno kreiran ili pronadjen.
     * @throws ElementNotFoundException Okida izuzetak ukoliko putanja skladista nije pronadjena.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     */
    boolean createStorage(String path, int numOfFiles, String ...extensions)throws ElementNotFoundException,ElementNotCreatedException,RootDirectoryException, IOException;

    /**
     * Kreiranje korenskog direktorijuma na prosledjenoj putanji sa konfiguracijom maksimalne velicine skladista i listom nedozvoljenih ekstenzija.
     * @param path Putanja na kojoj se kreira direktorijum.
     * @param size Maksimalna velicina skladista.
     * @param extensions Lista nedozvoljenih ekstenzija.
     * @return Vraca "true" ukoliko je korenski direktorijum uspesno kreiran.
     * @throws ElementNotCreatedException Okida izuzetak ukoliko skladiste nije uspesno kreirano.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @throws ElementNotFoundException Okida izuzetak ukoliko putanja skladista nije pronadjena.
     * @throws RootDirectoryException Okida izuzetak ukoliko korenski direktorijum nije uspesno kreiran ili pronadjen.
     */
    boolean createStorage(String path, long size, String ...extensions)throws ElementNotFoundException,ElementNotCreatedException,RootDirectoryException,IOException;

    /**
     * Kreira direktorijum na zadatoj putanji u skladistu sa maksimalnim dozvoljenim brojem fajlova.
     * @param path Putanja u skladistu na kojoj se kreira direktorijum.
     * @param name Naziv direktorijuma koji se kreira.
     * @param numOfFiles Maksimalan broj fajlova unutar novokreiranog direktorijuma
     * @throws ElementNotCreatedException Okida izuzetak ukoliko skladiste nije uspesno kreirano.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @throws ElementNotFoundException Okida izuzetak ukoliko putanja skladista nije pronadjena.
     * @throws ElementNotDeletedException Okida izuzetak ako se ne obrise stari konfiguracioni fajl.
     * @return Vraca "true" ukoliko je direktorijum uspesno kreiran
     */
    boolean createDirectory(String path, String name, int numOfFiles) throws ElementNotFoundException,ElementNotCreatedException, IOException, ElementNotDeletedException;

    /**
     * Kreira direktorijum na zadatoj putanji u skladistu.
     * @param path Putanja u skladistu na kojoj se kreira direktorijum.
     * @param name Naziv direktorijuma koji se kreira.
     * @throws ElementNotCreatedException Okida izuzetak ukoliko direktorijum na zadatoj putanji nije uspesno kreiran.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @throws ElementNotFoundException Okida izuzetak ukoliko putanja direktorijuma nije pronadjena.
     * @return Vraca "true" ukoliko je direktorijum uspesno kreiran.
     */
    boolean createDirectory(String path, String name) throws ElementNotFoundException,ElementNotCreatedException, IOException;

    /**
     * Kreira direktorijum u korenskom direktorijumu.
     * @param name Naziv direktorijuma koji se kreira.
     * @throws ElementNotCreatedException Okida izuzetak ukoliko direktorijum na zadatoj putanji nije uspesno kreiran.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @return Vraca "true" ukoliko je direktorijum uspesno kreiran.
     */
    boolean createDirectory(String name) throws ElementNotCreatedException, IOException;

    /**
     * Kreira direktorijum u korenskom direktorijumu sa podrazumevanim imenom.
     * @throws ElementNotCreatedException Okida izuzetak ukoliko direktorijum nije uspesno kreiran u korenskom direktorijumu.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @return Vraca "true" ukoliko je direktorijum uspesno kreiran.
     */
    boolean createDirectory() throws ElementNotCreatedException,IOException;

    /**
     * Kreira fajl u korenskom direktorijumu, bez zadavanje konkretne putanje.
     * @param name Naziv fajla koji se kreira.
     * @throws ElementNotCreatedException Okida izuzetak ukoliko fajl u korenskom direktorijumu nije uspesno kreiran.
     * @throws NotEnoughSpaceException Okida izuzetak ukoliko nema dovoljno mesta u skladistu.
     * @throws NumberOfFilesException Okida izuzetak ako je maksimalni broj fajlova u korenskom direktorijumu prekoracen.
     * @throws UnsupportedExtensionException Okida izuzetak ukoliko je vrsta prosledjenog fajla zabranjena u skladistu.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @return Vraca "true" ukoliko je fajl uspesno kreiran.
     */
    boolean createFile(String name) throws ElementNotCreatedException, NotEnoughSpaceException, NumberOfFilesException, UnsupportedExtensionException,IOException;

    /**
     * Kreira jedan ili vise fajlova na zadatoj putanji (direktorijumu).
     * @param path Putanja direktorijuma u skladistu u kojem se kreira fajl.
     * @param name Naziv ili nazivi fajlova koji se kreiraju.
     * @throws ElementNotCreatedException Okida izuzetak ukoliko fajl/fajlovi na zadatoj putanji nisu uspesno kreirani.
     * @throws NotEnoughSpaceException Okida izuzetak ukoliko nema dovoljno mesta u skladistu.
     * @throws NumberOfFilesException Okida izuzetak ako je maksimalni broj fajlova u zadatom direktorijumu prekoracen.
     * @throws UnsupportedExtensionException Okida izuzetak ukoliko je vrsta prosledjenog fajla zabranjena u skladistu.
     * @throws ElementNotFoundException Okida izuzetak ukoliko putanja direktorijuma nije pronadjena.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @return Vraca "true" ukoliko je fajl uspesno kreiran.
     */
    boolean createFile(String path, String ... name) throws ElementNotFoundException,ElementNotCreatedException,NotEnoughSpaceException, NumberOfFilesException, UnsupportedExtensionException, IOException;

    /**
     * Smestanje jednog ili vise fajlova u odredjeni direktorijum unutar skladista.
     * @param storagePath Putanja u skladistu na kojoj se smestaju odabrani fajlovi.
     * @param uploadPath Putanja direktorijuma na kojoj se nalaze fajlovi.
     * @param name Naziv ili nazivi fajlova za premestanje.
     * @throws ElementNotFoundException Okida izuzetak ukoliko fajl za smestanje nije uspesno pronadjen.
     * @throws NumberOfFilesException Okida izuzetak ako je maksimalni broj fajlova u zadatom direktorijumu prekoracen.
     * @throws NotEnoughSpaceException Okida izuzetak ukoliko nema dovoljno mesta u skladistu.
     * @throws UnsupportedExtensionException Okida izuzetak ukoliko je vrsta prosledjenog fajla zabranjena u skladistu.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @return Vraca "true" ukoliko su fajlovi uspesno smesteni u odabrani direktorijum.
     */
    boolean uploadFile(String storagePath, String uploadPath, String ... name) throws ElementNotFoundException, NumberOfFilesException, NotEnoughSpaceException,UnsupportedExtensionException,IOException;

    /**
     * Brisanje jednog ili vise fajlova/direktorijuma iz prosledjenog direktorijuma.
     * @param path Putanja direktorijuma u kojem se brisu elementi.
     * @param name Naziv ili nazivi elemenata koji su za brisanje.
     * @throws ElementNotFoundException Okida izuzetak ukoliko fajl/direktorijum za brisanje nije uspesno pronadjen.
     * @throws ElementNotDeletedException Okida izuzetak ukoliko fajl/direktorijum za brisanje nije uspesno obrisan.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @return Vraca "true" ukoliko su elementi uspesno obrisani.
     */
    boolean delete(String path, String ... name) throws ElementNotFoundException, ElementNotDeletedException, IOException;

    /**
     * Brisanje fajla ili direktorijuma iz korenskog direktorijuma.
     * @param name Naziv elementa koji je za brisanje.
     * @throws ElementNotFoundException Okida izuzetak ukoliko fajl/direktorijum za brisanje nije uspesno pronadjen u korenskom direktorijumu.
     * @throws ElementNotDeletedException Okida izuzetak ukoliko prosledjeni fajl/direktorijum za brisanje nije uspesno obrisan.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @return Vraca "true" ukoliko je element uspesno obrisan.
     */
    boolean delete(String name) throws ElementNotFoundException, ElementNotDeletedException, IOException;

    /**
     * Premestanje jednog ili vise elemenata u direktorijum na prosledjenoj putanji.
     * @param path1 Putanja na kojoj se nalaze elementi.
     * @param path2 Putanja na koju se elementi premestaju.
     * @param names Naziv ili nazivi elemenata za premestanje.
     * @throws NumberOfFilesException Okida izuzetak ako je maksimalni broj fajlova u zadatom direktorijumu prekoracen
     * @throws ElementNotMovedException Okida izuzetak ukoliko fajl nije uspesno premesten.
     * @throws ElementNotFoundException Okida izuzetak ukoliko putanja elementa nije pronadjena.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @return Vraca "true" ukoliko su elementi uspesno prebaceni
     */
    boolean move(String path1, String path2, String ... names) throws ElementNotFoundException,NumberOfFilesException, ElementNotMovedException, IOException;

    /**
     * Preuzimanje jednog ili vise elemenata iz skladista na zadatu putanju.
     * @param path1 Putanja na kojoj se nalaze elementi za preuzimanje.
     * @param path2 Putanja direktorijuma u kojem se preuzimaju elementi.
     * @param name Nazivi elemenata za preuzimanje.
     * @throws ElementNotFoundException Okida izuzetak ukoliko fajl/direktorijum nije uspesno pronadjen.
     * @throws DownloadException Okida izuzetak ukoliko fajl/direktorijum nije uspesno preuzet.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @return Vraca "true" ukoliko je element uspesno preuzet.
     */
    boolean download(String path1, String path2, String ... name) throws ElementNotFoundException,DownloadException, IOException;


    /**
     * Preimenovanje elementa na zadatoj putanji.
     * @param path Putanja elementa kojem se menja naziv.
     * @param newName Naziv elementa za preimenovanje.
     * @param fileName Elementa kojem se menja ime
     * @throws ElementNotFoundException Okida izuzetak ukoliko fajl/direktorijum za promenu imena nije uspesno pronadjen.
     * @throws RenameException  Okida izuzetak ukoliko fajl/direktorijum nije uspesno preimenovan.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @return Vraca "true" ukoliko je element uspesno preimenovan.
     */
    boolean rename(String path, String fileName, String newName) throws ElementNotFoundException, RenameException, IOException;


    /**
     * Listanje svih fajlova(naziv, metapodaci) unutar zadatog direktorijuma.
     * @param path Putanja direktorijuma.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @throws ElementNotFoundException Okida izuzetak ukoliko putanja direktorijuma nije pronadjena.
     * @return Vraca mapu naziva i metapodataka fajlova.
    */
    Map<String, List<String>> listNamesAndMeta(String path) throws ElementNotFoundException, IOException;

    /**
     * Listanje svih fajlova unutar zadatog direktorijuma.
     * @param path Putanja direktorijuma.
     * @throws ElementNotFoundException Okida izuzetak ukoliko direktorijum nije uspesno pronadjen.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @return Vraca listu svih fajlova iz zadatog direktorijuma.
     */
    List<String> listFilesFromDirectory(String path) throws ElementNotFoundException, IOException;

    /**
     * Lista sve fajlove u zadatom direktorijumu i poddirektorijumuma.
     * @param path Putanja direktorijuma.
     * @throws ElementNotFoundException Okida izuzetak ukoliko direktorijum nije uspesno pronadjen.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @return Vraca listu svih fajlova iz direktorijuma i poddirektorijuma.
     */
    List<String> listFilesAndSubdirectories(String path) throws ElementNotFoundException, IOException;

    /**
     * Lista fajlove iz skladista koji imaju zadatu ekstenziju.
     * @param name Naziv ekstenzije
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @return Vraca listu fajlova iz skladista sa prosledjenom ekstenzijom.
     */
    List<String> listFilesByExtension(String name) throws IOException;

    /**
     * Lista sve fajlove koji u svom imenu sadrze, pocinju ili se zavrsavaju sa prosledjenim stringom.
     * @param string Prosledjeni string.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @return Vraca listu fajlova koji zadovoljavaju uslove pretrage(sadrzi, pocinje, zavrsava).
     */
    List<String> listFilesByString(String string) throws IOException;

    /**
     * Provera postojanja fajla ili fajlova sa odredjenim imenom u zadatom direktorijumu.
     * @param path Putanja direktorijuma.
     * @param names Naziv ili nazivi fajlova.
     * @throws ElementNotFoundException Okida izuzetak ukoliko direktorijum nije uspesno pronadjen.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju ili pisanju.
     * @return Vraca "true" ukoliko su svi prosledjeni nazivi fajlova nadjeni.
     */
    boolean containsFile(String path, String ...names) throws ElementNotFoundException, IOException;

    /**
     * Pronalazi direktorijum u kom se nalazi fajl sa zadatim imenom.
     * @param name Naziv fajla.
     * @throws ElementNotFoundException Okida izuzetak ukoliko direktorijum nije uspesno pronadjen.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @return Vraca direktorijum u kojem je pronadjen fajl.
     */
    String findFileDirectory(String name) throws ElementNotFoundException, IOException;

    /**
     * Zadaje kriterijum sortiranja. Kriterijumi sortiranja su po nazivu, datumu kreiranja/modifikacije, rastuce ili opadajuce.
     * @param sortingType tip sortiranja.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     */
    void setCriterium(SortingType sortingType) throws IOException;

    /**
     * Lista sve fajlove koji su kreirani/modifikovani u zadatom periodu u odredjenom direktorijumu.
     * @param pathToDir putanja direktorijuma.
     * @param date1 prosledjeni datum pocetka perioda.
     * @param date2 prosledjeni datum zavrsetka perioda.
     * @throws ElementNotFoundException Okida izuzetak ukoliko direktorijum nije uspesno pronadjen.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     * @throws ParseException Okida izuzetak ukoliko datum nije korektno parsiran.
     * @return Vraca listu fajlova koji su kreirani/modifikovanu u zadatom periodu.
     */
    List<String> listFilesByTimePeriod(String pathToDir, String date1, String date2) throws ElementNotFoundException,IOException, ParseException;

    /**
     * Omogucava filtriranje rezultata pretrage fajlova.
     * @param tip tip filtriranja fajlova.
     * @throws IOException Okida izuzetak ukoliko nastane greska pri citanju/pisanju.
     */
    void filterFiles(String tip) throws IOException;
}
