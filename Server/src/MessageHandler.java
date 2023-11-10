import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MessageHandler extends Remote {
    void add(int i, String valueTagPair) throws RemoteException;
    String get(int index, int b) throws RemoteException;
}
