import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BulletinBoard {
    private HashMap<Integer,List<String>> bulletinBoard;

    public BulletinBoard(int n) {
        bulletinBoard = new HashMap<>();
        for (int i = 0; i < n; i++) {
            List<String> list = new ArrayList<>();
            bulletinBoard.put(i, list);
        }

    }
    public void setValueTagPairOnIndex(String v, int index){
        bulletinBoard.get(index).add(v);
    }
    public String removeValueTagPairOnIndex(int index, int b){
        return bulletinBoard.get(index).remove(b);
    }
}
