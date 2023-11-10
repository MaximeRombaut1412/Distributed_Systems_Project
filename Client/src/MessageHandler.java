import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MessageHandler extends Remote {

    void add(int index , String message) throws RemoteException;
    String get(int index, int b) throws RemoteException;
}
