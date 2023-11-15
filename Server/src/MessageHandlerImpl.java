import javax.crypto.SecretKey;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MessageHandlerImpl extends UnicastRemoteObject implements MessageHandler {
    private BulletinBoard bulletinBoard;
    public MessageHandlerImpl(int n) throws RemoteException{
       bulletinBoard = new BulletinBoard(n);
   };

    @Override
    public void add(int index, String message, String tag) throws RemoteException {
        //String[] split = valueTagPair.split("||");
        bulletinBoard.addMessage(message,index,tag);
    }

    @Override
    public String get(int index, String tag) throws RemoteException {
        return bulletinBoard.getMessage(index,tag);
    }

    @Override
    public void sendKey(SecretKey masterkey) throws RemoteException {
        bulletinBoard.setKey(masterkey);
    }

    @Override
    public SecretKey getKey() throws RemoteException {
        return bulletinBoard.getKey();
    }


}

