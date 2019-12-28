import javax.xml.crypto.Data;

public class DataCache {
    private int begin, end;
    private long time;

    public DataCache(int b, int e, long time){
        this.begin=b;
        this.end = e;
        this.time = time;
    }
    public void changeTime(long time){
        this.time = time;
    }
}
