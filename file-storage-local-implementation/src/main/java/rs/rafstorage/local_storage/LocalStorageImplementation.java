package rs.rafstorage.local_storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import rs.rafstorage.enumeration.SortingType;
import rs.rafstorage.exceptions.*;
import rs.rafstorage.handler.StorageHandler;
import rs.rafstorage.handler.StorageManager;


import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LocalStorageImplementation implements StorageHandler{

    static{
        StorageManager.registerSpecificationInterface(new LocalStorageImplementation());
    }

    private LocalStorage mainLocalStorage;
    private  String rootPath;
    private SortingType sortingType = SortingType.NAME_ASC;
    private Map<String,File> allFiles = new HashMap<>();

    @Override
    public boolean getStorage(String path) throws ElementNotFoundException, RootDirectoryException, IOException {
        File f = new File(path);
        File[] files = f.listFiles();

        if(!f.exists())
        {
            throw new ElementNotFoundException("Ne postoji direktorijum na ovoj putanji: " + path);
        }

        for(File file : files)
        {
            if(file.getName().equals("config.json"))
            {
                ObjectMapper mapper = new ObjectMapper();

                try {

                    LocalStorage ls = mapper.readValue(new File(file.getPath()), LocalStorage.class);
                    mainLocalStorage = ls;
                    rootPath = mainLocalStorage.getRootPath();
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        throw new RootDirectoryException("Izabrani direktorijum na putanji " + path + " nije skladiste, ne sadrzi config fajl");

    }

    @Override
    public boolean createStorage(String path) throws ElementNotFoundException, ElementNotCreatedException, RootDirectoryException,IOException {
        File f = new File(path);

        if(f == null)
        {
            throw new ElementNotFoundException("Skladiste neuspesno kreirano. Prosledjena putanja nije pronadjena.");
        }
        if(f.exists())
        {
            throw new ElementNotCreatedException("Skladiste neuspesno kreirano. Direktorijum sa ovom putanjom vec postoji.");
        }

        LocalStorage ls  =new LocalStorage(path);
        if(ls==null)
        {
            throw new RootDirectoryException("Skladiste neuspesno inicijalizovano.");
        }
        mainLocalStorage = ls;
        rootPath = path;

        return true;
    }

    @Override
    public boolean createStorage(String path, int numOfFiles) throws ElementNotFoundException,ElementNotCreatedException, RootDirectoryException,IOException {
        File f = new File(path);
        if(f == null)
        {
            throw new ElementNotFoundException("Skladiste neuspesno kreirano. Prosledjena putanja nije pronadjena.");
        }
        if(f.exists())
        {
            throw new ElementNotCreatedException("Skladiste neuspesno kreirano. Zadata putanja " +path+" ne postoji.");
        }

        LocalStorage ls  =new LocalStorage(path);
        if(ls==null)
        {
            throw new RootDirectoryException("Skladiste neuspesno inicijalizovano.");
        }
        mainLocalStorage = ls;
        rootPath = path;
        mainLocalStorage.setMaxFiles(numOfFiles);
        mainLocalStorage.getNumFileMap().put(path, numOfFiles);
        mainLocalStorage.rewriteConfig();
        return true;
    }

    @Override
    public boolean createStorage(String path, long size, int numOfFiles) throws ElementNotFoundException,ElementNotCreatedException, RootDirectoryException,IOException {
        File f = new File(path);
        if(f == null)
        {
            throw new ElementNotFoundException("Skladiste neuspesno kreirano. Prosledjena putanja nije pronadjena.");
        }
        if(f.exists())
        {
            throw new ElementNotCreatedException("Skladiste neuspesno kreirano. Element na putanji " +path+" vec postoji.");
        }

        LocalStorage ls  =new LocalStorage(path);
        if(ls==null)
        {
            throw new RootDirectoryException("Skladiste neuspesno inicijalizovano.");
        }
        mainLocalStorage = ls;
        rootPath = path;
        mainLocalStorage.setMaxFiles(numOfFiles);
        mainLocalStorage.getNumFileMap().put(path, numOfFiles);
        mainLocalStorage.setMaxSize(size);
        mainLocalStorage.rewriteConfig();
        return true;
    }

    @Override
    public boolean createStorage(String path, long size, int numOfFiles, String... strings) throws ElementNotFoundException,ElementNotCreatedException, RootDirectoryException {
        File f = new File(path);
        if(f == null)
        {
            throw new ElementNotFoundException("Skladiste neuspesno kreirano. Prosledjena putanja nije pronadjena.");
        }
        if(f.exists())
        {
            throw new ElementNotCreatedException("Skladiste neuspesno kreirano. Na prosledjenoj putanji vec postoji element.");
        }

        LocalStorage ls  =new LocalStorage(path);
        if(ls==null)
        {
            throw new RootDirectoryException("Skladiste neuspesno inicijalizovano.");
        }
        mainLocalStorage = ls;
        rootPath=path;
        mainLocalStorage.setMaxFiles(numOfFiles);
        mainLocalStorage.getNumFileMap().put(path, numOfFiles);
        mainLocalStorage.setMaxSize(size);
        for(String ext: strings)
            mainLocalStorage.getExtensions().add(ext);
        mainLocalStorage.rewriteConfig();
        return true;
    }

    @Override
    public boolean createStorage(String path, String... extensions) throws ElementNotFoundException,ElementNotCreatedException, RootDirectoryException,IOException {
        File f = new File(path);
        if(f == null)
        {
            throw new ElementNotFoundException("Skladiste neuspesno kreirano. Prosledjena putanja nije pronadjena.");
        }
        if(f.exists())
        {
            throw new ElementNotCreatedException("Skladiste neuspesno kreirano. Element na putanji " +path+" vec postoji.");
        }

        LocalStorage ls  =new LocalStorage(path);
        if(ls==null)
        {
            throw new RootDirectoryException("Skladiste neuspesno inicijalizovano.");
        }
        mainLocalStorage = ls;
        this.rootPath=path;
        for(String ext: extensions)
            mainLocalStorage.getExtensions().add(ext);
        mainLocalStorage.rewriteConfig();
        return true;
    }

    @Override
    public boolean createStorage(String path, long size) throws ElementNotFoundException,ElementNotCreatedException, RootDirectoryException, IOException {
        File f = new File(path);
        if(f == null)
        {
            throw new ElementNotFoundException("Skladiste neuspesno kreirano. Prosledjena putanja nije pronadjena.");
        }
        if(f.exists())
        {
            throw new ElementNotCreatedException("Skladiste neuspesno kreirano. Element na zadatoj putanji " +path+" vec postoji.");
        }

        LocalStorage ls  =new LocalStorage(path);
        if(ls==null)
        {
            throw new RootDirectoryException("Skladiste neuspesno inicijalizovano.");
        }
        mainLocalStorage = ls;
        rootPath = path;
        mainLocalStorage.setMaxSize(size);
        mainLocalStorage.rewriteConfig();
        return true;
    }

    @Override
    public boolean createStorage(String path, int numOfFiles, String... extensions) throws ElementNotFoundException,ElementNotCreatedException, RootDirectoryException,IOException {
        File f = new File(path);
        if(f == null)
        {
            throw new ElementNotFoundException("Skladiste neuspesno kreirano. Prosledjena putanja nije pronadjena.");
        }
        if(f.exists())
        {
            throw new ElementNotCreatedException("Skladiste neuspesno kreirano. Zadata putanja " +path+" ne postoji.");
        }

        LocalStorage ls  =new LocalStorage(path);
        if(ls==null)
        {
            throw new RootDirectoryException("Skladiste neuspesno inicijalizovano.");
        }
        mainLocalStorage = ls;
        rootPath=path;
        mainLocalStorage.setMaxFiles(numOfFiles);
        mainLocalStorage.getNumFileMap().put(path, numOfFiles);
        for(String ext: extensions)
            mainLocalStorage.getExtensions().add(ext);
        mainLocalStorage.rewriteConfig();
        return true;
    }

    @Override
    public boolean createStorage(String path, long size, String... extensions) throws ElementNotFoundException,ElementNotCreatedException, RootDirectoryException, IOException {
        File f = new File(path);
        if(f == null)
        {
            throw new ElementNotFoundException("Skladiste neuspesno kreirano. Prosledjena putanja nije pronadjena.");
        }
        if(f.exists())
        {
            throw new ElementNotCreatedException("Skladiste neuspesno kreirano. Element na zadatoj putanji " +path+" vec postoji.");
        }

        LocalStorage ls  =new LocalStorage(path);
        if(ls==null)
        {
            throw new RootDirectoryException("Skladiste neuspesno inicijalizovano.");
        }
        mainLocalStorage = ls;
        rootPath=path;
        mainLocalStorage.setMaxSize(size);
        for(String ext: extensions)
            mainLocalStorage.getExtensions().add(ext);
        mainLocalStorage.rewriteConfig();
        return true;
    }

    @Override
    public boolean createDirectory(String path, String dirName, int numOfFiles) throws ElementNotFoundException,ElementNotCreatedException,IOException {
        if (!mainLocalStorage.getPathMap().containsKey(path))
        {
            throw new ElementNotFoundException("Direktorijum neuspesno kreiran. Zadata putanja " + path + " nije pronadjena unutar skladista");
        }
        path = mainLocalStorage.getPathMap().get(path) + File.separator +path;
        File newFile = new File(path + File.separator + dirName);

        if(newFile.mkdir())
        {
            mainLocalStorage.getNumFileMap().put(path + File.separator + dirName, numOfFiles);
            mainLocalStorage.getPathMap().put(dirName,path);
            return true;
        }else{
            throw new ElementNotCreatedException("Direktorijum " + dirName + " neuspesno kreiran.");
        }

    }

    @Override
    public boolean createFile(String name) throws ElementNotCreatedException, NotEnoughSpaceException, NumberOfFilesException, UnsupportedExtensionException,IOException {
        String ext[] = name.split("\\.");

        String path = rootPath + File.separator + name;

        File newFile = new File(path);

        if(mainLocalStorage.getMaxSize() < newFile.length())
        {
            throw new NotEnoughSpaceException("Neuspesno kreiranje fajla. Nema dovoljno mesta u skladistu za  "+ name + ".");
        }

        if(checkFileExtensions(ext[1]))
        {
            throw new UnsupportedExtensionException("Neuspesno kreiranje fajla "+ name +". Ekstenzija " +ext[1].toUpperCase() + " nije prihvatljiva u skladistu.");
        }

        if(mainLocalStorage.getNumFileMap().get(rootPath) == 0)
        {
            throw new NumberOfFilesException("Neuspesno kreiranje fajla " +name +". Broj fajlova je popunjen u direktorijumu.");
        }
            try {
                newFile.createNewFile();

                int numOfFiles = mainLocalStorage.getNumFileMap().get(rootPath);
                numOfFiles -= 1;

                long size = mainLocalStorage.getMaxSize() - newFile.length();
                mainLocalStorage.setMaxSize(size);

                mainLocalStorage.getNumFileMap().put(rootPath, numOfFiles);
                mainLocalStorage.rewriteConfig();

                this.allFiles.put(name, newFile);

            } catch (IOException e) {
                throw new ElementNotCreatedException("Neuspesno kreiranje fajla " + name + ".");
            }


        return true;
    }

    @Override
    public boolean createFile(String path, String... names) throws ElementNotCreatedException,NotEnoughSpaceException, NumberOfFilesException, UnsupportedExtensionException, IOException{
        String p = mainLocalStorage.getPathMap().get(path);
        File newFile;
        String folderPath = p+File.separator+path;

        if(mainLocalStorage.getMaxSize() == 0)
        {
            throw new NotEnoughSpaceException("Neuspesno kreiranje fajla. Nema dovoljno mesta u skladistu.");
        }

        if(mainLocalStorage.getNumFileMap().get(folderPath)==0)
        {
            throw new NumberOfFilesException("Neuspesno kreiranje fajla. Broj fajlova je popunjen u direktorijumu.");
        }

        for(String name: names)
        {
            String[] fileNames = name.split("\\.");
            if(!checkFileExtensions(fileNames[1]))
            {

                if(mainLocalStorage.getNumFileMap().get(folderPath) > 0 )
                {
                    newFile = new File(folderPath + File.separator + name);
                    if(mainLocalStorage.getMaxSize() >= newFile.length())
                    {
                        try {
                            if(!newFile.createNewFile())
                                throw new ElementNotCreatedException("Fajl " + name + " nije uspesno kreiran");

                            int numOfFiles = mainLocalStorage.getNumFileMap().get(folderPath);
                            numOfFiles -= 1;
                            mainLocalStorage.getNumFileMap().put(folderPath, numOfFiles);

                            long size = mainLocalStorage.getMaxSize() - newFile.length();
                            mainLocalStorage.setMaxSize(size);

                            mainLocalStorage.rewriteConfig();

                            this.allFiles.put(name,newFile);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        throw new NotEnoughSpaceException("Neuspesno kreiranje fajla. Nema dovoljno mesta u skladistu za  "+ name + ".");
                    }
                }else{
                    throw new NumberOfFilesException("Neuspesno kreiranje fajla " +name +". Broj fajlova je popunjen u direktorijumu.");
                }
            }else{
                throw new UnsupportedExtensionException("Neuspesno kreiranje fajla "+ name +". Ekstenzija " +fileNames[1].toUpperCase() + " nije prihvatljiva u skladistu.");
            }
        }
        return true;
    }

    @Override
    public boolean uploadFile(String dest, String src, String... names) throws ElementNotFoundException, NumberOfFilesException, NotEnoughSpaceException, UnsupportedExtensionException,IOException {
        File sourceFile = new File(src);

        if(!sourceFile.exists())
        {
            throw new ElementNotFoundException("Destinacioni element nije pronadjen");
        }

        if(mainLocalStorage.getPathMap().get(dest) == null)
        {
            throw new ElementNotFoundException("Lokacija u skladistu ne postoji");
        }

        String destPath = mainLocalStorage.getPathMap().get(dest) + File.separator + dest;

        if(src.contains(mainLocalStorage.getRootName()))
        {
            throw new ElementNotFoundException("Fajl koji se smesta u skladiste mora da bude van istog");
        }

        for(String name : names)
        {
            File f = new File(src + File.separator + name);
            if(!f.isDirectory())
            {

                try {
                    String fullDestPath = destPath + File.separator +name;
                    File up = new File(fullDestPath);
                    String[] s = name.split("\\.");
                    String ext = s[1];
                    if(mainLocalStorage.getNumFileMap().get(destPath) > 0 )
                    {
                        if(mainLocalStorage.getMaxSize() > up.length())
                        {
                            if(!checkFileExtensions(ext))
                            {
                                Files.copy(f.toPath(), up.toPath());
                                int numFiles = mainLocalStorage.getNumFileMap().get(destPath) - 1;
                                mainLocalStorage.getNumFileMap().put(destPath, numFiles);
                                mainLocalStorage.setMaxSize(mainLocalStorage.getMaxSize() - up.length());
                                mainLocalStorage.rewriteConfig();

                                this.allFiles.put(name, f);
                            }else{
                                throw new UnsupportedExtensionException("Skladiste ne podrzava " + ext + " ekstenziju");
                            }
                        }else{
                            throw new NotEnoughSpaceException("Nema dovoljno mesta u skladistu.");
                        }
                    }else{
                        throw new NumberOfFilesException("U prosledjenom direktorijumu je popunjen maksimalan broj fajlova");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                File destFolder = new File(destPath + File.separator + name);
                copyFolder(f, destFolder);
                mainLocalStorage.getPathMap().put(name, destPath);
                mainLocalStorage.getNumFileMap().put(destPath+File.separator+name, mainLocalStorage.getMaxFiles());
                mainLocalStorage.rewriteConfig();
            }
        }
        return true;
    }
    private int nameCnt=1;
    private String defaultDirName = "New Directory ";
    @Override
    public boolean createDirectory(String path, String name) throws ElementNotFoundException,ElementNotCreatedException,IOException{

        File f = new File(path);
        if(f == null)
        {
            throw new ElementNotFoundException("Skladiste neuspesno kreirano. Prosledjena putanja nije pronadjena.");
        }
        boolean alreadyExists = false;
        File newFile;
        int br1;
        int br2;

        if(name.contains("{") && name.contains("}"))
        {
            String[] strings = name.split("\\{");
            String interval = strings[1].replaceAll("[^0-9]", " ");
            interval = interval.trim().replaceAll(" +", " ");
            String brojevi[] = interval.split(" ");

            br1= Integer.parseInt(brojevi[0]);
            br2 = Integer.parseInt(brojevi[1]);
            String ime = strings[0];

            for(int i = br1; i<=br2;i++)
            {
                String fullName = path + File.separator + ime+i;
                newFile = new File(fullName);
                mainLocalStorage.getNumFileMap().put(fullName, mainLocalStorage.getMaxFiles());

                mainLocalStorage.getPathMap().put(ime+i, path);

                if(newFile.mkdir() == false)
                {
                    throw new ElementNotCreatedException("Direktorijum "+ ime+i +" neuspesno kreiran.");
                }
            }
            return true;
        }


        alreadyExists = mainLocalStorage.getPathMap().containsKey(name);

        if(alreadyExists)
        {
            path = mainLocalStorage.getPathMap().get(path) + File.separator +path;
            String defaultDir = defaultDirName + nameCnt;
            newFile = new File(path + File.separator +defaultDir);

            if(newFile.mkdir())
            {
                mainLocalStorage.getNumFileMap().put(path + File.separator +defaultDirName +nameCnt, mainLocalStorage.getMaxFiles());

                mainLocalStorage.getPathMap().put(defaultDir,path);

                nameCnt++;
                return true;
            }else{
                throw new ElementNotCreatedException("Direktorijum "+ defaultDir+" neuspesno kreiran.");
            }
        }else{

            path = mainLocalStorage.getPathMap().get(path) + File.separator +path;
            newFile = new File(path + File.separator + name);

            if(newFile.mkdir())
            {
                mainLocalStorage.getNumFileMap().put(path + File.separator + name, mainLocalStorage.getMaxFiles());
                mainLocalStorage.getPathMap().put(name,path);
                return true;
            }else{
                throw new ElementNotCreatedException("Direktorijum " + name + " neuspesno kreiran.");
            }
        }
    }


    @Override
    public boolean createDirectory(String name) throws ElementNotCreatedException, IOException{

        boolean exists =false;
        String path = rootPath+ File.separator + name;

        exists = mainLocalStorage.getPathMap().containsKey(name);
        //ako postoji dir sa ovim imenom daj mu default ime
        if(exists)
        {
            String defaultDir = defaultDirName+nameCnt;
            String dirPath = rootPath + File.separator+ defaultDir;
            File f = new File(dirPath);
            if(f.mkdir())
            {
                mainLocalStorage.getNumFileMap().put(dirPath, mainLocalStorage.getMaxFiles());
                mainLocalStorage.getPathMap().put(defaultDir, rootPath);
                mainLocalStorage.rewriteConfig();
                nameCnt++;
                return true;
            }else{
                throw new ElementNotCreatedException("Direktorijum " + defaultDir + " neuspesno kreiran.");
            }
        //ako ne postoji dir sa ovim imenom daj mu prosledjeno
        }else{

            File f = new File(path);
            if(f.mkdir())
            {
                mainLocalStorage.getNumFileMap().put(path, mainLocalStorage.getMaxFiles());
                mainLocalStorage.getPathMap().put(name, rootPath);
                mainLocalStorage.rewriteConfig();
                return true;
            }else{
                throw new ElementNotCreatedException("Direktorijum " + name + " neuspesno kreiran.");
            }
        }
    }

    @Override
    public boolean createDirectory() throws ElementNotCreatedException,IOException {
        String defaultDir = defaultDirName+nameCnt;
        String path = rootPath + File.separator + defaultDir;
        File f = new File(path);

        if(f.mkdir())
        {
            mainLocalStorage.getNumFileMap().put(path, mainLocalStorage.getMaxFiles());
            mainLocalStorage.getPathMap().put(defaultDir, rootPath);
            nameCnt++;
            return true;
        }else {
            throw new ElementNotCreatedException("Direktorijum " + defaultDir + " neuspesno kreiran.");
        }
    }

    @Override
    public boolean delete(String path, String... names) throws ElementNotFoundException, ElementNotDeletedException, IOException
    {

        if(!mainLocalStorage.getPathMap().containsKey(path))
        {
            throw new ElementNotFoundException("Element neuspesno obrisan. Direktorijum " + path + " ne postoji.");
        }

        path = mainLocalStorage.getPathMap().get(path) + File.separator +path;

        File root = new File(rootPath);

        String fullPath;

        for(String name : names)
        {
            fullPath = path+File.separator+name;
            File f = new File(fullPath);
            if(f.isDirectory())
            {

                Path dir = Paths.get(fullPath.replace("\\", File.separator));
                try {
                    Files.walk(dir)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                    mainLocalStorage.setMaxSize(root.length());

                } catch (IOException e) {
                    throw new ElementNotDeletedException("Direktorijum " + name + " neuspesno obrisan.");
                }
            }else{
                //ako je prosledjen string fajl, brise se, povecava se broj slobodnih mesta za fa
                if(f.delete()) {
                    int numFiles = mainLocalStorage.getNumFileMap().get(path) + 1;
                    mainLocalStorage.getNumFileMap().put(path, numFiles);
                    long size = mainLocalStorage.getMaxSize() + f.length();
                    mainLocalStorage.setMaxSize(size);
                }else{
                    throw new ElementNotDeletedException("Fajl " + name + " neuspesno obrisan.");
                }
            }
        }
        mainLocalStorage.rewriteConfig();
        return true;
    }

    @Override
    public boolean delete(String name) throws ElementNotFoundException, ElementNotDeletedException, IOException{

        String path = rootPath + File.separator + name;
        File root = new File(rootPath);
        if(root == null)
        {
            throw new ElementNotFoundException("Element za brisanje nije uspesno pronadjen");
        }
        for(File f: root.listFiles())
        {
            if(f.getName().equals(name))
            {
                if(f.isDirectory())
                {
                    Path dir = Paths.get(path.replace("\\", File.separator));
                    try {
                        Files.walk(dir)
                                .sorted(Comparator.reverseOrder())
                                .map(Path::toFile)
                                .forEach(File::delete);

                        mainLocalStorage.setMaxSize(root.length());
                        mainLocalStorage.rewriteConfig();
                        return true;
                    } catch (IOException e) {
                        throw new ElementNotDeletedException("Direktorijum " + name + " neuspesno obrisan.");
                    }
                }else{
                    if(f.delete())
                    {
                        int numFiles = mainLocalStorage.getNumFileMap().get(path) + 1;
                        mainLocalStorage.getNumFileMap().put(path, numFiles);
                        long size = mainLocalStorage.getMaxSize() + f.length();
                        mainLocalStorage.setMaxSize(size);
                        mainLocalStorage.rewriteConfig();
                        return true;
                    }else{
                        throw new ElementNotDeletedException("Fajl " + name + " neuspesno obrisan.");
                    }
                }
            }
        }

        return true;
    }


    @Override
    public boolean move(String path1, String path2, String... names) throws ElementNotFoundException,NumberOfFilesException, ElementNotMovedException, IOException{
        if(mainLocalStorage.getPathMap().get(path1) == null || mainLocalStorage.getPathMap().get(path2) == null)
        {
               throw new ElementNotFoundException("Premestanje neuspesno. Prosledjana putanja nije pronadjena");
        }
        String fpath1 = mainLocalStorage.getPathMap().get(path1) + File.separator+path1;
        String fpath2 = mainLocalStorage.getPathMap().get(path2) + File.separator + path2;

        path1 = fpath1.replace("\\", File.separator);
        path2 = fpath2.replace("\\", File.separator);

        for(String name : names)
        {
            try {
                String destination = path2 + File.separator + name;
                String src = path1 + File.separator + name;
                if(mainLocalStorage.getNumFileMap().get(fpath2) >0)
                {
                    //from to
                    Path temp = Files.move(Paths.get(src), Paths.get(destination));
                    if (temp != null) {
                        int numfiles1 = mainLocalStorage.getNumFileMap().get(fpath1);
                        numfiles1 -= 1;
                        mainLocalStorage.getNumFileMap().put(fpath1, numfiles1);
                        int numfiles2 = mainLocalStorage.getNumFileMap().get(fpath2);
                        numfiles2 += 1;
                        mainLocalStorage.getNumFileMap().put(fpath2, numfiles2);
                    } else {
                        throw new ElementNotMovedException("Premestanje fajla " + name + " neuspesno.");
                    }
                }else {
                    throw new NumberOfFilesException("Premestanje fajla " + name + "neuspesno. Broj fajlova u direktorijumu je na maksimumu.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mainLocalStorage.rewriteConfig();
        return true;
    }


    @Override
    public boolean download(String src, String dest, String... names) throws ElementNotFoundException,DownloadException,IOException
    {
        if(mainLocalStorage.getPathMap().get(src) == null)
        {
            throw new ElementNotFoundException("Direktorijum " + src + " ne postoji.");
        }

        String path1 = (mainLocalStorage.getPathMap().get(src) + File.separator + src).replace("\\", File.separator);

        String path2 = dest.replace("\\", File.separator);

        String temp[] = path2.split("\\\\");


        if(Arrays.asList(temp).contains(mainLocalStorage.getRootName()))
        {
            throw new DownloadException("Preuzimanje fajla neuspesno. Putanja nije za preuzimanje je unutar skladista.");
        }



        for(String name : names)
        {
            File f = new File(path1 + File.separator + name);
            if(!f.isDirectory())
            {
                try {
                    Files.copy(f.toPath(), new File(path2 + File.separator + name).toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                File destFolder = new File(path2 + File.separator + name);
                copyFolder(f, destFolder);
            }
        }

        return false;
    }


    @Override
    public boolean rename(String folderPath, String fileName, String newName)  throws ElementNotFoundException, RenameException, IOException
    {
        String p1 = mainLocalStorage.getPathMap().get(fileName);
        String path = mainLocalStorage.getPathMap().get(folderPath) + File.separator + folderPath;
        File f = new File(path);
        String destPath = path+File.separator+fileName;


        String fullPath = path+File.separator+newName;

        if (!f.exists()) {
            throw new ElementNotFoundException("Direktorijum " + folderPath + " nije nadjen.");
        }

        File file = new File(destPath);
        if(!file.exists())
        {
            throw new ElementNotFoundException("Direktorijum " + folderPath + " nije nadjen.");
        }

        if(file.isDirectory())
        {
            File renameFile = new File(fullPath);
            if(file.renameTo(renameFile))
            {
                mainLocalStorage.getPathMap().remove(fileName);
                mainLocalStorage.getPathMap().put(newName, p1);
                int numFiles = mainLocalStorage.getNumFileMap().get(destPath);
                mainLocalStorage.getNumFileMap().remove(destPath);
                mainLocalStorage.getNumFileMap().put(fullPath, numFiles);
                mainLocalStorage.rewriteConfig();
                return true;
            }else{
                throw new RenameException("Direktorijum " + fileName + " neuspesno preimenovan");
            }
        }else{
            File renameFile = new File(fullPath);
            if(file.renameTo(renameFile))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public Map<String, List<String>> listNamesAndMeta(String path) throws ElementNotFoundException, IOException
    {
        if(mainLocalStorage.getPathMap().get(path) == null)
        {
            throw new ElementNotFoundException("Pretraga neuspesna. Direktorijum na prosledjenoj putanji nije pronadjen. ");
        }
        Map<String, List<String>> hash = new HashMap<>();
        String fullPath = mainLocalStorage.getPathMap().get(path) + File.separator + path;

        File folder = new File(fullPath);
        File[] files = folder.listFiles();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        for(File f : files)
        {
            List<String> metaList = new ArrayList<>();
            if(f.isFile()) {
                Path p = Paths.get(f.getPath());
                try {
                    BasicFileAttributes attributes = Files.readAttributes(p, BasicFileAttributes.class);
                    String creationTime = "Creation time: " + df.format(attributes.creationTime().toMillis());
                    String lastAccessTime = "Last access time: " + df.format(attributes.lastAccessTime().toMillis());
                    String lastModifiedTime = "Last modified time: " + df.format(attributes.lastModifiedTime().toMillis());
                    String size = "Size: " + attributes.size();
                    String regular = "Regular file: " +attributes.isRegularFile();

                    metaList.add(creationTime);
                    metaList.add(lastAccessTime);
                    metaList.add(lastModifiedTime);
                    metaList.add(size);
                    metaList.add(regular);

                    hash.put(f.getName(), metaList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return hash;
    }


    @Override
    public List<String> listFilesFromDirectory(String path) throws ElementNotFoundException, IOException
    {
        if(mainLocalStorage.getPathMap().get(path) == null)
        {
            throw new ElementNotFoundException("Direktorijum na putanji " + path + " nije pronadjen u skladistu.");
        }

        String fullPath = mainLocalStorage.getPathMap().get(path) + File.separator + path;

        File folder = new File(fullPath);
        File[] files = folder.listFiles();
        List<File> listOfFiles = Arrays.asList(files);
        List<File> sorted = sortFiles(listOfFiles);

        return getFileNames(sorted);

    }

    @Override
    public List<String> listFilesAndSubdirectories(String path) throws ElementNotFoundException, IOException
    {
        if(mainLocalStorage.getPathMap().get(path) == null)
        {
            throw new ElementNotFoundException("Direktorijum na putanji " + path + " nije pronadjen u skladistu.");
        }

        String fullPath = mainLocalStorage.getPathMap().get(path) + File.separator + path;
        List<String> files = new ArrayList<>();
        File folder = new File(fullPath);
        File[] arr = folder.listFiles();

        if(arr != null)
        {
            for(File f: arr)
            {
                if(f.isFile())
                {
                    files.add(f.getName());
                }else{
                    files.addAll(listFilesAndSubdirectories(f.getName()));
                }
            }
        }
        return files;
    }

    @Override
    public List<String> listFilesByExtension(String ext) throws IOException
    {
        List<File> files = new ArrayList<>();

        for(Map.Entry<String,File> set:this.allFiles.entrySet())
        {
            String name = set.getKey();
            String[] split = name.split("\\.");
            if(split[1].equals(ext))
                files.add(set.getValue());

        }
        List<File> sorted = sortFiles(files);
        return getFileNames(sorted);
    }

    @Override
    public List<String> listFilesByString(String string) throws IOException
    {
        List<File> files = new ArrayList<>();
        for(Map.Entry<String,File> set:this.allFiles.entrySet())
        {
            String name = set.getKey();
            String[] str = name.split("\\.");

            if(name.startsWith(string) || name.contains(string) || str[1].endsWith(string) || name.endsWith(string))
            {
               files.add(set.getValue());
            }
        }
        List<File> sorted = sortFiles(files);
        return getFileNames(sorted);
    }

    @Override
    public boolean containsFile(String path, String... names) throws ElementNotFoundException, IOException{
        if(mainLocalStorage.getPathMap().get(path)==null)
        {
            throw new ElementNotFoundException("Direktorijum na putanji " + path + " nije pronadjen u skladistu.");
        }
        path = mainLocalStorage.getPathMap().get(path) +File.separator+path;
        File f = new File(path);

        if(!f.isDirectory())
        {
            throw new ElementNotFoundException("Element na prosledjenoj putanji " + path + " nije direktorijum");
        }

        File[] files = f.listFiles();
        List<String> listOfNames = new ArrayList<>();
        for(File fs :files)
        {
            listOfNames.add(fs.getName());
        }

        for(String name:names)
        {
          if(!listOfNames.contains(name))
              return false;
        }

        return true;
    }

    @Override
    public String findFileDirectory(String fileName) throws ElementNotFoundException, IOException
    {
        if(this.allFiles.get(fileName)==null)
        {
            throw new ElementNotFoundException("Fajl " + fileName + " nije pronadjen u skladistu.");
        }
        File f = this.allFiles.get(fileName);
        File parent = f.getParentFile();
        return parent.getName();
    }

    @Override
    public void setCriterium(SortingType sortingType) throws IOException
    {
        this.sortingType = sortingType;
    }

    //
    @Override
    public List<String> listFilesByTimePeriod(String path, String s1, String s2) throws ElementNotFoundException, IOException,ParseException
    {

        if(mainLocalStorage.getPathMap().get(path) ==null)
        {
            throw new ElementNotFoundException("Direktorijum na putanji " + path + " nije pronadjen u skladistu.");
        }
        Date date1=null;
        Date date2=null;
        try {
             date1= new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").parse(s1);
             date2 =new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").parse(s2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        path = mainLocalStorage.getPathMap().get(path) + File.separator+path;
        File folder = new File(path);
        File[] files = folder.listFiles();
        List<File> listOfFiles = new ArrayList<>();

        for(File f : files)
        {
            DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
            String s = sdf.format(f.lastModified());

                Date dateOfFile = sdf.parse(s);
                if(date1 != null && date2!=null)
                {
                    if(dateOfFile.after(date1) && dateOfFile.before(date2))
                    {
                        listOfFiles.add(f);
                    }
                }
        }
        List<File> sorted = sortFiles(listOfFiles);
        return getFileNames(sorted);
    }

    @Override
    public void filterFiles(String s) throws IOException {

    }



    private List<File> sortFiles(List<File> files)
    {
        switch (this.sortingType)
        {
            case NAME_ASC:
                Collections.sort(files);
                return files;
            case NAME_DSC:
                Collections.sort(files, Collections.reverseOrder());
                return files;
            case MODIFICATION_ASC:
                Collections.sort(files, Comparator.comparing(File::lastModified));
                return files;
            case MODIFICATION_DSC:
                Collections.sort(files, Comparator.comparing(File::lastModified).reversed());
                return files;
            case CREATE_ASC:
                Collections.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        long l1 = getCreationDate(o1);
                        long l2 = getCreationDate(o2);
                        return Long.valueOf(l1).compareTo(l2);
                    }
                });
            case CREATE_DSC:
                Collections.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        long l1 = getCreationDate(o1);
                        long l2 = getCreationDate(o2);
                        return Long.valueOf(l1).compareTo(l2)*(-1);
                    }
                });
            default:
                return files;
        }
    }
    private long getCreationDate(File file)
    {
        try {
            BasicFileAttributes attr = Files.readAttributes(file.toPath(),
                    BasicFileAttributes.class);
            return attr.creationTime()
                    .toInstant().toEpochMilli();
        } catch (IOException e) {
            throw new RuntimeException(file.getAbsolutePath(), e);
        }
    }

    private List<String> getFileNames(List<File> files)
    {
        List<String> list = new ArrayList<>();
        for(File f:files)
        {
            list.add(f.getName());
        }
        return list;
    }

    private boolean checkFileExtensions(String extension)
    {
        for(String s : mainLocalStorage.getExtensions())
        {
            if(extension.equals(s))
            {
                return true;
            }
        }
        return false;
    }

    private  void copyFolder(File source, File destination)
    {
        if (source.isDirectory())
        {
            if (!destination.exists())
            {
                destination.mkdirs();
            }

            String files[] = source.list();

            for (String file : files)
            {
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);

                copyFolder(srcFile, destFile);
            }
        }
        else
        {
            InputStream in = null;
            OutputStream out = null;

            try
            {
                in = new FileInputStream(source);
                out = new FileOutputStream(destination);

                byte[] buffer = new byte[1024];

                int length;
                while ((length = in.read(buffer)) > 0)
                {
                    out.write(buffer, 0, length);
                }
            }
            catch (Exception e)
            {
                try
                {
                    in.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }

                try
                {
                    out.close();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
            }
        }
    }

}
