import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MessageHandlerImpl extends UnicastRemoteObject implements MessageHandler {
    private BulletinBoard bulletinBoard;
    public MessageHandlerImpl(int n) throws RemoteException{
       bulletinBoard = new BulletinBoard(n);
   };

    @Override
    public void add(int i, String valueTagPair) throws RemoteException {
        String[] split = valueTagPair.split("||");
        bulletinBoard.setValueTagPairOnIndex(valueTagPair,i);
    }

    @Override
    public String get(int index, int b) throws RemoteException {
        return bulletinBoard.removeValueTagPairOnIndex(index,b);
    }

}

