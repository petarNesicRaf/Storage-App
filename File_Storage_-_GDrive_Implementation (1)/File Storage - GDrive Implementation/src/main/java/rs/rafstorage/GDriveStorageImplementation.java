package rs.rafstorage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.gson.Gson;
import rs.rafstorage.enumeration.SortingType;
import rs.rafstorage.exceptions.*;
import rs.rafstorage.handler.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("ALL")
public class GDriveStorageImplementation implements StorageHandler {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static Drive service;
    private File root;
    private Config config;

    static {
        StorageManager.registerSpecificationInterface(new GDriveStorageImplementation());
        try {
            setService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service =  new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).setApplicationName("Google Drive Storage").build();
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = GDriveStorageImplementation.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE, DriveScopes.DRIVE_METADATA, DriveScopes.DRIVE_SCRIPTS, DriveScopes.DRIVE_APPDATA, DriveScopes.DRIVE_FILE);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        return credential;
    }

    @Override
    public boolean getStorage(String s) throws RootDirectoryException, ElementNotFoundException, IOException {
        File folder = searchForFolder(s);
        File file = searchForFileInFolder(s, "config.json");

        java.io.File configFile = java.io.File.createTempFile("config", ".json");
        FileOutputStream fileOutputStream = new FileOutputStream(configFile);
        service.files().get(file.getId()).executeMediaAndDownloadTo(fileOutputStream);
        fileOutputStream.close();

        FileInputStream fileInputStream = new FileInputStream(configFile);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        this.config = new Gson().fromJson(inputStreamReader, Config.class);
        fileInputStream.close();
        inputStreamReader.close();

        root = folder;
        return true;
    }

    @Override
    public boolean createStorage(String s) throws ElementNotCreatedException, RootDirectoryException, ElementNotFoundException, IOException {
        root = createDirectory(searchForFolder(s), "Storage");
        config = new Config();
        addConfig();
        return true;
    }

    @Override
    public boolean createStorage(String s, int i) throws ElementNotCreatedException, RootDirectoryException, ElementNotFoundException, IOException {
        root = createDirectory(searchForFolder(s), "Storage");
        config = new Config();
        config.addMaxFile(root.getId(), i);
        addConfig();
        return true;
    }

    @Override
    public boolean createStorage(String s, long l, int i) throws ElementNotCreatedException, RootDirectoryException, ElementNotFoundException, IOException {
        root = createDirectory(searchForFolder(s), "Storage");
        config = new Config(l);
        config.addMaxFile(root.getId(), i);
        addConfig();
        return true;
    }

    @Override
    public boolean createStorage(String s, long l, int i, String... strings) throws ElementNotCreatedException, RootDirectoryException, ElementNotFoundException, IOException {
        root = createDirectory(searchForFolder(s), "Storage");
        config = new Config(l, Arrays.asList(strings));
        config.addMaxFile(root.getId(), i);
        addConfig();
        return true;
    }

    @Override
    public boolean createStorage(String s, String... strings) throws RootDirectoryException, ElementNotCreatedException, ElementNotFoundException, IOException {
        root = createDirectory(searchForFolder(s), "Storage");
        config = new Config(Arrays.asList(strings));
        addConfig();
        return true;
    }

    @Override
    public boolean createStorage(String s, long l) throws RootDirectoryException, ElementNotCreatedException, ElementNotFoundException, IOException {
        root = createDirectory(searchForFolder(s), "Storage");
        config = new Config(l);
        addConfig();
        return true;
    }

    @Override
    public boolean createStorage(String s, int i, String... strings) throws RootDirectoryException, ElementNotCreatedException, ElementNotFoundException, IOException {
        root = createDirectory(searchForFolder(s), "Storage");
        config = new Config(Arrays.asList(strings));
        config.addMaxFile(root.getId(), i);
        addConfig();
        return true;
    }

    @Override
    public boolean createStorage(String s, long l, String... strings) throws RootDirectoryException, ElementNotCreatedException, ElementNotFoundException, IOException {
        root = createDirectory(searchForFolder(s), "Storage");
        config = new Config(l, Arrays.asList(strings));
        addConfig();
        return true;
    }

    private void addConfig() throws ElementNotCreatedException, IOException {
        File configFile = new File();
        configFile.setName("config.json");
        configFile.setParents(Collections.singletonList(root.getId()));

        java.io.File file = new java.io.File("config.json");
        FileWriter myWriter = new FileWriter(file);
        myWriter.write(new Gson().toJson(config));
        myWriter.close();
        FileContent mediaContent = new FileContent("text/json", file);
        if (service.files().create(configFile, mediaContent).setFields("id").execute() == null)
            throw new ElementNotCreatedException("Nije uspelo kreiranje konfiguracionog fajla.");
        file.delete();
    }

    @Override
    public boolean createDirectory(String s, String s1, int i) throws ElementNotCreatedException, ElementNotFoundException, IOException, ElementNotDeletedException {
        File folder = searchForFolder(s);
        if (s1.contains("{")){
            String string[] = s1.split("\\{");
            int number = Integer.parseInt(string[1].split("}")[0]);
            for (int j = 1; j <= number; j++) {
                File file = createDirectory(folder, string[0] + j);
                config.addMaxFile(file.getId(), i);
            }
        } else {
            File file = createDirectory(folder, s1);
            config.addMaxFile(file.getId(), i);
        }
        delete("config.json");
        addConfig();
        return true;
    }

    @Override
    public boolean createDirectory(String s, String s1) throws ElementNotCreatedException, ElementNotFoundException, IOException {
        File folder = searchForFolder(s);
        if (s1.contains("{")){
            String string[] = s1.split("\\{");
            int number = Integer.parseInt(string[1].split("}")[0]);
            for (int i = 1; i <= number; i++)
                createDirectory(folder, string[0] + i);
        } else
            createDirectory(folder, s1);
        return true;
    }

    @Override
    public boolean createDirectory(String s) throws ElementNotCreatedException, IOException {
        if (s.contains("{")){
            String string[] = s.split("\\{");
            int number = Integer.parseInt(string[1].split("}")[0]);
            for (int i = 1; i <= number; i++)
                createDirectory(root, string[0] + i);
        } else
            createDirectory(root, s);
        return true;
    }

    @Override
    public boolean createDirectory() throws ElementNotCreatedException, IOException {
        createDirectory(root, "New Folder");
        return true;
    }

    private File createDirectory(File parent, String name) throws IOException, ElementNotCreatedException {
        File child = new File();
        child.setName(name);
        child.setMimeType("application/vnd.google-apps.folder");
        child.setParents(Collections.singletonList(parent.getId()));

        File file = service.files().create(child).setFields("*").execute();
        if (file == null)
            throw new ElementNotCreatedException("Nije uspelo kreiranje direktorijuma.");
        return file;
    }

    @Override
    public boolean createFile(String s) throws ElementNotCreatedException, NotEnoughSpaceException, NumberOfFilesException, UnsupportedExtensionException, IOException {
        checkFilesLimit(s,1);
        createFile(root, s);
        return true;
    }

    @Override
    public boolean createFile(String s, String... strings) throws ElementNotCreatedException, NotEnoughSpaceException, NumberOfFilesException, UnsupportedExtensionException, ElementNotFoundException, IOException {
        File folder = searchForFolder(s);
        checkFilesLimit(s, strings.length);
        for (String string : strings)
            createFile(folder, string);
        return true;
    }

    private boolean createFile(File folder, String s) throws ElementNotCreatedException, IOException, UnsupportedExtensionException, NotEnoughSpaceException {
        checkExtension(s);
        File file = new File();
        file.setName(s);
        file.setParents(Collections.singletonList(folder.getId()));
        if (service.files().create(file).execute() == null)
            throw new ElementNotCreatedException("Fajl bezuspesno kreiran.");
        return true;
    }

    @Override
    public boolean uploadFile(String s, String s1, String... strings) throws ElementNotFoundException, NumberOfFilesException, NotEnoughSpaceException, UnsupportedExtensionException, IOException {
        searchForFolder(s);
        checkFilesLimit(s, strings.length);
        for (String string : strings) {
            Path path = Paths.get(s1 + "/" + string);
            if (Files.notExists(path))
                throw new ElementNotFoundException("Zadati fajl nije nadjen.");
            java.io.File file = new java.io.File(s1 + "/" + string);
            if (file.isFile())
                checkExtension(string);
            if (getSize(root.getId()) + file.length() > config.getMaxSize())
                throw new NotEnoughSpaceException("Dodavanjem fajla ce se prevazici ogranicena velicina skladista.");
            File uploadFile = new File();
            uploadFile.setName(string);
            uploadFile.setParents(Collections.singletonList(s));
            FileContent mediaContent = new FileContent("*/*", file);
            if (service.files().create(uploadFile, mediaContent).setFields("id").execute() == null)
                throw new ElementNotFoundException("Neuspesno dodavanje fajla");
        }
        return true;
    }

    @Override
    public boolean delete(String s, String... strings) throws ElementNotFoundException, ElementNotDeletedException, IOException {
        for (String string : strings)
            delete(s, string);
        return true;
    }

    @Override
    public boolean delete(String s) throws ElementNotFoundException, ElementNotDeletedException, IOException {
        delete(root.getId(), s);
        return true;
    }

    private void delete(String parent, String child) throws ElementNotDeletedException, ElementNotFoundException, IOException {
        searchForFolder(parent);
        File file = searchForFileInFolder(parent, child);
        service.files().delete(file.getId()).execute();
        if (!service.files().list().setQ("name ='" + child + "' and '" + parent + "' in parents").execute().getFiles().isEmpty())
            throw new ElementNotDeletedException("Fajl ili direktorijum nije uspesno uklonjen.");
    }

    @Override
    public boolean move(String s, String s1, String... strings) throws NumberOfFilesException, ElementNotMovedException, ElementNotFoundException, IOException {
        searchForFolder(s);
        searchForFolder(s1);
        checkFilesLimit(s1, strings.length);
        for (String string : strings) {
            File file = searchForFileInFolder(s, string);
            if (service.files().update(file.getId(), new File()).setAddParents(s1).execute() == null)
                throw new ElementNotMovedException("Fajl nije uspesno pomeren.");
        }
        return true;
    }

    @Override
    public boolean download(String s, String s1, String... strings) throws ElementNotFoundException, IOException {
        searchForFolder(s);
        if (Files.notExists(Paths.get(s1)))
            throw new ElementNotFoundException("Zadata lokalna putanja nije pronadjena.");
        for (String string : strings){
            File downloadFile = searchForFileInFolder(s, string);
            java.io.File file = new java.io.File(s1 +"/" + string);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            service.files().get(downloadFile.getId()).executeMediaAndDownloadTo(fileOutputStream);
            fileOutputStream.close();
        }
        return true;
    }

    @Override
    public boolean rename(String s, String s1, String s2) throws ElementNotFoundException, RenameException, IOException {
        searchForFolder(s);
        File file = searchForFileInFolder(s, s1);
        if (service.files().update(file.getId(), new File().setName(s2)).execute() == null)
            throw new RenameException("Nije uspelo preimenovanje fajla ili direktorijuma.");
        return true;
    }

    @Override
    public Map<String, List<String>> listNamesAndMeta(String s) throws ElementNotFoundException, IOException {
        searchForFolder(s);
        List<File> files = service.files().list().setQ("mimeType!='application/vnd.google-apps.folder' and '" + s + "' in parents").setFields("*").execute().getFiles();
        HashMap<String, List<String>> names = new HashMap<>();
        for (File file : files) {
            List<String> meta = new ArrayList<>();
            meta.add(file.getId());
            meta.add(file.getFileExtension());
            meta.add(file.getMimeType());
            meta.add(file.getDescription());
            meta.add(file.getSize() + "");
            meta.add(file.getCreatedTime() + "");
            meta.add(file.getModifiedTime() + "");
            names.put(file.getName(), meta);
        }
        return names;
    }

    @Override
    public List<String> listFilesFromDirectory(String s) throws IOException, ElementNotFoundException {
        searchForFolder(s);
        List<File> files = service.files().list().setQ("mimeType!='application/vnd.google-apps.folder' and '" + s + "' in parents").setFields("*").execute().getFiles();
        ArrayList<String> names = new ArrayList<>();
        for (File file : files)
            names.add(file.getName());
        return names;
    }

    @Override
    public List<String> listFilesAndSubdirectories(String s) throws ElementNotFoundException, IOException {
        searchForFolder(s);
        return listFilesInSubdirectories(s);
    }

    private List<String> listFilesInSubdirectories(String path) throws IOException, ElementNotFoundException {
        ArrayList<String> names = new ArrayList<>();
        List<File> files  = service.files().list().setQ("mimeType!='application/vnd.google-apps.folder' and '" + path + "' in parents").setFields("*").execute().getFiles();
        for (File file : files)
            names.add(file.getName());
        List<File> folders  = service.files().list().setQ("mimeType='application/vnd.google-apps.folder' and '" + path + "' in parents").execute().getFiles();
        for (File folder : folders)
            names.addAll(listFilesInSubdirectories(folder.getId()));
        return names;
    }

    @Override
    public List<String> listFilesByExtension(String s) throws IOException {
        return listFilesByExtension(root.getId(), s);
    }

    private List<String> listFilesByExtension(String path, String extension) throws IOException {
        ArrayList<String> names = new ArrayList<>();
        List<File> files  = service.files().list().setQ("mimeType!='application/vnd.google-apps.folder' and '" + path + "' in parents").setFields("*").execute().getFiles();
        for (File file : files) {
            if (file.getFileExtension().equals(extension))
                names.add(file.getName());
        }
        List<File> folders  = service.files().list().setQ("mimeType='application/vnd.google-apps.folder' and '" + path + "' in parents").execute().getFiles();
        for (File folder : folders)
            names.addAll(listFilesByExtension(folder.getId(), extension));
        return names;
    }

    @Override
    public List<String> listFilesByString(String s) throws IOException {
        return listFilesByString(root.getId(), s);
    }

    private List<String> listFilesByString(String path, String word) throws IOException {
        ArrayList<String> names = new ArrayList<>();
        List<File> files  = service.files().list().setQ("mimeType!='application/vnd.google-apps.folder' and '" + path + "' in parents").setFields("*").execute().getFiles();
        for (File file : files) {
            String name = file.getName();
            if (name.contains(word))
                names.add(file.getName());
        }
        List<File> folders  = service.files().list().setQ("mimeType='application/vnd.google-apps.folder' and '" + path + "' in parents").execute().getFiles();
        for (File folder : folders)
            names.addAll(listFilesByString(folder.getId(), word));
        return names;
    }

    @Override
    public boolean containsFile(String s, String... strings) throws ElementNotFoundException, IOException {
        searchForFolder(s);
        List<String> words = new ArrayList<String>(Arrays.asList(strings));
        List<File> files = service.files().list().setQ("mimeType!='application/vnd.google-apps.folder' and '" + s + "' in parents").setFields("*").execute().getFiles();
        for (File file : files)
            words.remove(file.getName());
        if (words.isEmpty())
            return true;
        return false;
    }

    @Override
    public String findFileDirectory(String s) throws IOException {
        return findFileDirectory(root, s);
    }

    private String findFileDirectory(File folder, String name) throws IOException {
        List<File> files  = service.files().list().setQ("name = '" + name + "' and mimeType!='application/vnd.google-apps.folder' and '" + folder.getId() + "' in parents").setFields("*").execute().getFiles();
        if (!files.isEmpty())
            return folder.getName();
        List<File> folders  = service.files().list().setQ("mimeType='application/vnd.google-apps.folder' and '" + folder.getId() + "' in parents").execute().getFiles();
        for (File childFolder : folders) {
            String folderName = findFileDirectory(childFolder, name);
            if (folderName != null)
                return folderName;
        }
        return null;
    }

    @Override
    public List listFilesByTimePeriod(String s, String s1, String s2) throws ElementNotFoundException, IOException, ParseException {
        searchForFolder(s);
        Date startDate = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").parse(s1);;
        Date endDate = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").parse(s2);;

        List<File> files = service.files().list().setQ("mimeType!='application/vnd.google-apps.folder' and '" + s + "' in parents").setFields("*").execute().getFiles();
        ArrayList<String> names = new ArrayList<>();
        for (File file : files) {
            Date fileDate = new Date(file.getModifiedTime() + "");
            if (fileDate.after(startDate) && fileDate.before(endDate))
                names.add(file.getName());
        }
        return names;
    }

    @Override
    public void setCriterium(SortingType sortingType) {
    }

    @Override
    public void filterFiles(String s) throws IOException {

    }

    private File searchForFolder(String path) throws IOException, ElementNotFoundException {
        List<File> files = service.files().list().setQ("mimeType='application/vnd.google-apps.folder'").execute().getFiles();
        for (File file : files){
            if (file.getId().equals(path))
                return file;
        }
        throw new ElementNotFoundException("Direktorijum sa zadatom putanjom nije pronadjen.");
    }

    private File searchForFileInFolder(String folder, String name) throws ElementNotFoundException, IOException {
        List<File> files = service.files().list().setQ("name ='" + name + "' and '" + folder + "' in parents").execute().getFiles();
        if (files.isEmpty())
            throw new ElementNotFoundException("Fajl ili direktorijum sa zadatim nazivom ne postoji u zadatom direkotorijumu.");
        return files.get(0);
    }

    private void checkFilesLimit(String path, int newFiles) throws IOException, NumberOfFilesException {
        int files =  service.files().list().setQ("mimeType!='application/vnd.google-apps.folder' and '" + path + "' in parents").execute().getFiles().size();
        if (files + newFiles > config.getMaxFile(path))
            throw new NumberOfFilesException("Dodavanjem fajlova u zadati direktorijum prevazici ce se njegovo ogranicenje broja fajlova.");
    }

    private void checkExtension(String name) throws UnsupportedExtensionException {
        String[] extension = name.split("\\.");
        if(extension.length <= 1)
            throw new UnsupportedExtensionException("Fajl naziv je unet bez ekstenzije.");
        if (config.getExtensions().contains(extension[1]))
            throw new UnsupportedExtensionException("Fajl sa tom ektenzijom nije dozvoljen.");
    }

    private long getSize(String path) throws IOException {
        long size = 0;
        List<File> files  = service.files().list().setQ("mimeType!='application/vnd.google-apps.folder' and '" + path + "' in parents").setFields("*").execute().getFiles();
        for (File file : files)
            size += file.getSize();
        List<File> folders  = service.files().list().setQ("mimeType='application/vnd.google-apps.folder' and '" + path + "' in parents").execute().getFiles();
        for (File folder : folders)
            size += getSize(folder.getId());
        return size;
    }
}
