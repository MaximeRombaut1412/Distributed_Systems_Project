import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BulletinBoard {
    private List<HashMap<String,String>> bulletinBoard;
    private Bump bump;

    public BulletinBoard(int n) {
        this.bump = null;
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
    public void setBumb(Bump bump){
        this.bump = bump;
    }
    public Bump getBump(){
        return this.bump;
    }


}
