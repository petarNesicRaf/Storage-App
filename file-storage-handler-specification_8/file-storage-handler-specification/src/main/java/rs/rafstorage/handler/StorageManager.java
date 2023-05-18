package rs.rafstorage.handler;

public class StorageManager {
    /**
     * Staticki atribut za postavljanje skladista.
     */
    private static StorageHandler storageHandler;

    /**
     * Postavljanje odabrane implementacije skladista.
     * @param sh implementacija interfejsa StorageHandler.
     */
    public static void registerSpecificationInterface(StorageHandler sh)
    {
        storageHandler = sh;
    }

    /**
     * Metoda za vracanje implementacije StorageHandler Interfejsa.
     * @return vraca implementaciju StorageHandler interfejsa.
     */
    public static StorageHandler getSpecificationInterface()
    {
        return storageHandler;
    }



}
