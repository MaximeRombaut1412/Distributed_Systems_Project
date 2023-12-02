import javax.crypto.SecretKey;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MessageHandlerImpl extends UnicastRemoteObject implements MessageHandler {
    private BulletinBoard bulletinBoard;
    public MessageHandlerImpl(int n) throws RemoteException{
       bulletinBoard = new BulletinBoard(n);
   }

    @Override
    public void add(int index, String message, String tag) throws RemoteException {
        bulletinBoard.addMessage(message,index,tag);
    }

    @Override
    public String get(int index, String tag) throws RemoteException {
        return bulletinBoard.getMessage(index,tag);
    }

    @Override
    public void sendBump(Bump bump) throws RemoteException {
        bulletinBoard.setBumb(bump);
    }

    @Override
    public Bump getBump() throws RemoteException {
        return bulletinBoard.getBump();
    }
}

