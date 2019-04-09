package sys;

import Utils.Serialize;
import leveldb.LevelDb;
import org.iq80.leveldb.DBIterator;
import org.json.JSONObject;

import java.util.Map;

public class test {
    public static LevelDb db = new LevelDb("sys/db/Blockchain");

    public static void main(String[] args) {
        try{
            DBIterator it=db.getDb().iterator();
            while (it.hasNext()) {
                Map.Entry<byte[], byte[]> item = it.next();
                String key = new String(item.getKey(), Configure.CHARSET);
                String value = new String(item.getValue(), Configure.CHARSET);//null,check.
                Block block = Serialize.deSerializeBlock(new JSONObject(value));
                block.printBlock();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
