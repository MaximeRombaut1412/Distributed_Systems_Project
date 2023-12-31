import javax.crypto.SecretKey;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MessageHandler extends Remote {
    void add(int index, String valueTagPair, String tag) throws RemoteException;
    String get(int index, String tag) throws RemoteException;
    void sendBump(Bump bump) throws RemoteException;
    Bump getBump() throws RemoteException;
}
