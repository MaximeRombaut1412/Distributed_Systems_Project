import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BulletinBoard {
    private List<HashMap<String,String>> bulletinBoard;
    private SecretKey key;

    public BulletinBoard(int n) {
        this.key = null;
        bulletinBoard = new ArrayList<>();
        for (int i = 0; i < n;i++){
            bulletinBoard.add(new HashMap<>());
        }
    }
    public void addMessage(String message, int index, String tag){
        bulletinBoard.get(index).put(tag,message);
    }
    public String getMessage(int index, String tag){
        String test = bulletinBoard.get(index).remove(tag);
        return test;
    }
    public void setKey(SecretKey key){
        this.key = key;
    }
    public SecretKey getKey(){
        return this.key;
    }


}
