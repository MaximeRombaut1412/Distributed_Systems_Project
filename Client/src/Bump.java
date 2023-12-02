import javax.crypto.SecretKey;
import java.io.Serializable;

public class Bump implements Serializable {
    private String tag;
    private int index;
    private SecretKey masterkey;

    public Bump(String tag, int index, SecretKey masterkey) {
        this.tag = tag;
        this.index = index;
        this.masterkey = masterkey;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public SecretKey getMasterkey() {
        return masterkey;
    }

    public void setMasterkey(SecretKey masterkey) {
        this.masterkey = masterkey;
    }
}
