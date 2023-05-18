package rs.rafstorage;

import rs.rafstorage.exceptions.*;
import rs.rafstorage.handler.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class App {

    private static StorageHandler sh;
    private static Scanner in;

    public static void main( String[] args ) {
        try {
            //Class.forName("rs.rafstorage.LocalStorageImplementation");
            Class.forName("rs.rafstorage.GDriveStorageImplementation");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        sh = StorageManager.getSpecificationInterface();
        in = new Scanner(System.in);

        initStorage(args);

        boolean active = true;
        System.out.println(
                "0.Izlaz\n" +
                "1.Kreiranje fajla\n" +
                "2.Kreiranje direktorijuma\n" +
                "3.Pomeranje fajla ili direktorijuma\n" +
                "4.Uklanjanje fajla ili direktorijuma\n" +
                "5.Preimenovanje fajla ili direktorijuma\n" +
                "6.Preuzimanje fajla\n" +
                "7.Dodavanje fajla\n" +
                "8.Pretrazivanje fajlova u direktorijumu\n" +
                "9.Pretrazivanje fajlova u direktorijumu i poddirektorijima\n" +
                "10.Pretrazivanje fajlova po ekstenziji\n" +
                "11.Pretrazivanje fajlova po reci\n" +
                "12.Pretrazivanje da li direktorijum sadrzi fajlove\n" +
                "13.Pretraga u kom direktorijumu se nalazi fajl\n" +
                "14.Pretrazivanje fajlova po periodu poslednje promene\n" +
                "15.Zadavanje kriterijuma sortiranja\n" +
                "16.Zadavanje kriterijuma filtriranja\n" +
                "17.Pretrazivanje direktorijuma za metapodatke fajlova");

        while(active){
            System.out.println("Izaberite akciju unosom odgovarajuceg broja. 0/1/2/3/4/5/6/7/8/9/10/11/12/13/14/15/16/17");
            int option = in.nextInt();
            in.nextLine();

            try{
                switch (option){
                    case 0:
                        active = false;
                        break;
                    case 1:
                        createFile();
                        break;
                    case 2:
                        createDirectory();
                        break;
                    case 3:
                        moveFile();
                        break;
                    case 4:
                        deleteFile();
                        break;
                    case 5:
                        renameFile();
                        break;
                    case 6:
                        downloadFile();
                        break;
                    case 7:
                        uploadFile();
                        break;
                    case 8:
                        listFilesFromDirectory();
                        break;
                    case 9:
                        listFilesAndSubdirectories();
                        break;
                    case 10:
                        listFilesByExtension();
                        break;
                    case 11:
                        listFilesByString();
                        break;
                    case 12:
                        containsFile();
                        break;
                    case 13:
                        findFileDirectory();
                        break;
                    case 14:
                        listFilesByTimePeriod();
                        break;
                    case 15:
                        setCriterium();
                        break;
                    case 16:
                        filterFiles();
                        break;
                    case 17:
                        listNamesAndMeta();
                        break;

                }
            } catch (NumberOfFilesException | UnsupportedExtensionException | ElementNotCreatedException | NotEnoughSpaceException | ElementNotFoundException | IOException | ElementNotDeletedException | ElementNotMovedException | RenameException | DownloadException | ParseException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    //Local:
    //Testiranje Folder GDrive: 1MdIybs10rjdF5JgutqK8PKz-8nHLm3eA
    //Skladiste GDrive: 1QwTq6TS-BCgGjqF5VtLIZ1Fonj7Ko0Xd
    private static void initStorage(String[] path){
        if (path.length >= 1){
            try {
                if (sh.getStorage(path[0]))
                    return;
            } catch (ElementNotFoundException | RootDirectoryException | IOException e) {
                System.out.println(e.getMessage());
            }
        }

        while (true){
            try {
                createStorage();
                break;
            } catch (RootDirectoryException | ElementNotCreatedException | ElementNotFoundException | IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void createStorage() throws RootDirectoryException, ElementNotCreatedException, ElementNotFoundException, IOException {
        System.out.println("Da li zelite da kreirate skladiste? da/ne");
        System.out.print("");
        if(in.nextLine().equals("ne"))
            System.exit(0);
        System.out.println("Unesite putanju gde da se kreira skladiste:");
        String path = in.nextLine();
        System.out.println("Ukoliko zelite da skladiste ne predje ogranicenu velicinu (u bajtovima) unesite broj ili 0 da nema ogranicenja:");
        long maxSize = in.nextInt();
        in.nextLine();
        System.out.println("Ukoliko zelite da skladiste ne prima odredjenje ekstenzije unesite razdvojene zarezom:");
        String ext = in.nextLine();
        System.out.println("Ukoliko zelite da korenski direktorijum sadrzi ogranicen broj fajlova unesite broj ili -1 da nema ogranicenja:");
        int maxFiles = in.nextInt();
        in.nextLine();

        if (maxSize > 0) {
            if (maxFiles >= 0){
                if (!ext.isEmpty())
                    sh.createStorage(path, maxSize, maxFiles, ext.split(","));
                else
                    sh.createStorage(path, maxSize, maxFiles);
                return;
            }
            if (!ext.isEmpty())
                sh.createStorage(path, maxSize, ext.split(","));
            else
                sh.createStorage(path, maxSize);
            return;
        }
        if (maxFiles >= 0){
            if (!ext.isEmpty())
                sh.createStorage(path, maxFiles, ext.split(","));
            else
                sh.createStorage(path, maxFiles);
            return;
        }
        if (!ext.isEmpty())
            sh.createStorage(path, ext.split(","));
        else
            sh.createStorage(path);
    }
    private static void createFile() throws NumberOfFilesException, UnsupportedExtensionException, ElementNotCreatedException, NotEnoughSpaceException, ElementNotFoundException, IOException {
        System.out.println("Unesite putanju gde da se kreira fajl:");
        String path = in.nextLine();
        System.out.println("Unesite naziv fajla sa ekstenzijom, ukoliko vise razdvojite zarezom:");
        String name = in.nextLine();

        sh.createFile(path,name.split(","));
    }

    private static void createDirectory() throws ElementNotCreatedException, ElementNotFoundException, IOException, ElementNotDeletedException {
        System.out.println("Unesite putanju gde da se kreira direktorijum:");
        String path = in.nextLine();
        System.out.println("Unesite naziv direktorijuma, ukoliko zelite vise unesite u formi [naziv {broj}]:");
        String name = in.nextLine();
        System.out.println("Ukoliko zelite da direktorijum/i sadrzi ogranicen broj fajlova unesite broj ili -1 da nema ogranicenja:");
        int maxFiles = in.nextInt();

        if (maxFiles >= 0)
            sh.createDirectory(path, name, maxFiles);
        else
            sh.createDirectory(path, name);
    }

    private static void moveFile() throws NumberOfFilesException, ElementNotMovedException, ElementNotFoundException, IOException {
        System.out.println("Unesite putanju gde da se nalazi fajl ili direktorijum:");
        String path = in.nextLine();
        System.out.println("Unesite putanju direktorijuma gde zelite da se premesti:");
        String newPath = in.nextLine();
        System.out.println("Unesite naziv fajla ili direktorijuma, ukoliko vise razdvojite zarezom:");
        String name = in.nextLine();

        sh.move(path, newPath, name.split(","));
    }

    private static void deleteFile() throws ElementNotFoundException, IOException, ElementNotDeletedException {
        System.out.println("Unesite putanju gde da se nalazi fajl ili direktorijum:");
        String path = in.nextLine();
        System.out.println("Unesite naziv fajla ili direktorijuma, ukoliko vise razdvojite zarezom");
        String name = in.nextLine();

        sh.delete(path,name.split(","));
    }

    private static void renameFile() throws RenameException, ElementNotFoundException, IOException {
        System.out.println("Unesite putanju gde se nalazi fajl ili direktorijum:");
        String path = in.nextLine();
        System.out.println("Unesite trenutni naziv fajla ili direktorijuma");
        String name = in.nextLine();
        System.out.println("Unesite novi naziv fajla ili direktorijuma:");
        String newName = in.nextLine();

        sh.rename(path, name, newName);
    }

    private static void downloadFile() throws DownloadException, ElementNotFoundException, IOException {
        System.out.println("Unesite putanju gde se nalazi fajl ili direktorijum:");
        String sourcePath = in.nextLine();
        System.out.println("Unesite putanju gde da se smesti  fajl ili direktorijum:");
        String targetPath = in.nextLine();
        System.out.println("Unesite naziv fajla ili direktorijuma, ukoliko vise razdvojite zarezom:");
        String name = in.nextLine();

        sh.download(sourcePath, targetPath, name.split(","));
    }

    private static void uploadFile() throws NumberOfFilesException, UnsupportedExtensionException, NotEnoughSpaceException, ElementNotFoundException, IOException {
        System.out.println("Unesite putanju gde se nalazi fajl:");
        String sourcePath = in.nextLine();
        System.out.println("Unesite putanju gde da se smesti  fajl:");
        String targetPath = in.nextLine();
        System.out.println("Unesite naziv fajla, ukoliko vise razdvojite zarezom:");
        String name = in.nextLine();

        sh.uploadFile(targetPath, sourcePath, name.split(","));
    }

    private static void listNamesAndMeta() throws ElementNotFoundException, IOException {
        System.out.println("Unesite putanju direktorijuma koji da se pretrazuje:");
        String path = in.nextLine();

        Map<String, List<String>> files = sh.listNamesAndMeta(path);
        Set<String> names = files.keySet();
        for(String name : names) {
            System.out.println(name);
            List<String> metaData = files.get(name);
            for(String meta : metaData)
                System.out.print(meta + ",");
            System.out.println();
        }
    }

    private static void listFilesFromDirectory() throws ElementNotFoundException, IOException {
        System.out.println("Unesite putanju direktorijuma koji da se pretrazuje:");
        String path = in.nextLine();

        List<String> files = sh.listFilesFromDirectory(path);
        for (String file: files)
            System.out.println(file);
    }

    private static void listFilesAndSubdirectories() throws ElementNotFoundException, IOException {
        System.out.println("Unesite putanju direktorijuma koji da se pretrazuje:");
        String path = in.nextLine();

        List<String> files = sh.listFilesAndSubdirectories(path);
        for (String file: files)
            System.out.println(file);
    }

    private static void listFilesByExtension() throws IOException {
        System.out.println("Unesite za koju ekstenzijju da se traze fajlovi:");
        String extension = in.nextLine();

        List<String> files = sh.listFilesByExtension(extension);
        for (String file: files)
            System.out.println(file);
    }

    private static void listFilesByString() throws IOException {
        System.out.println("Unesite za koju rec da se traze fajlovi:");
        String string = in.nextLine();

        List<String> files = sh.listFilesByString(string);
        for (String file: files)
            System.out.println(file);
    }

    private static void containsFile() throws IOException, ElementNotFoundException {
        System.out.println("Unesite putanju direktorijuma koji da se pretrazuje:");
        String path = in.nextLine();
        System.out.println("Unesite nazive fajlova koji se proveravaju razdvojene zarezom:");
        String names = in.nextLine();

        if (sh.containsFile(path, names.split(",")))
            System.out.println("Sadrzi.");
        else
            System.out.println("Ne sadrzi.");
    }

    private static void findFileDirectory() throws IOException, ElementNotFoundException {
        System.out.println("Unesite naziv fajla koji se trazi:");
        String name = in.nextLine();

        System.out.println(sh.findFileDirectory(name));
    }

    private static void listFilesByTimePeriod() throws IOException, ElementNotFoundException, ParseException {
        System.out.println("Unesite putanju direktorijuma koji da se pretrazuje:");
        String path = in.nextLine();
        System.out.println("Unesite datum od kog se pretrazuje:");
        String startDate = in.nextLine();
        System.out.println("Unesite datum do kog se pretrazuje:");
        String endDate = in.nextLine();

        System.out.println(sh.listFilesByTimePeriod(path, startDate, endDate));
    }

    private static void setCriterium() throws IOException, ElementNotFoundException {
    }

    private static void filterFiles() throws IOException, ElementNotFoundException {
    }
}
