import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLOutput;

public class Main {
    public static void main(String[] args) {
        startServer();
        //BulletinBoard bulletinBoard = new BulletinBoard(5);
        //bulletinBoard.setValueTagPairOnIndex(new ValueTagPair("Test", 5), 2);
        //bulletinBoard.setValueTagPairOnIndex(new ValueTagPair("Test2", 4), 2);
        //bulletinBoard.setValueTagPairOnIndex(new ValueTagPair("Test3", 5), 1);


        //System.out.println();

    }
    private static void startServer(){
        try{
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("MessageHandlerService", new MessageHandlerImpl(5));
            System.out.println("MessageHandler Server RMI ready");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}