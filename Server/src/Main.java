import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLOutput;

public class Main {
    public static void main(String[] args) {
        startServer();
    }
    private static void startServer(){
        try{
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("MessageHandlerService", new MessageHandlerImpl(20));
            System.out.println("MessageHandler Server RMI ready");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}