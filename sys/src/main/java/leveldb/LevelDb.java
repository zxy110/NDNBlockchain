package leveldb;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.impl.Iq80DBFactory;
import src.Configure;

import java.io.File;

public class LevelDb {
    private static DB db;

    public LevelDb(String filename){
        connectLeveldb(filename);
    }

    public static DB getDb() {
        return db;
    }

    /*
     * Open a connection to leveldb
     * You need to give filename where .db files saves
     */
    public static void connectLeveldb(String filename){
        File file=new File(filename);
        try{
            db = Iq80DBFactory.factory.open(new File(file,"db"),new Options().createIfMissing(true));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
     * Add an element to leveldb
     */
    public static void put(String key, String value){
        try {
            db.put(key.getBytes(Configure.CHARSET), value.getBytes(Configure.CHARSET));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
     * Add an element to leveldb
     */
    public static void put(byte[] key, byte[] value){
        try {
            db.put(key, value);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
     * Find an element
     */
    public static byte[] get(byte[] key){
        try {
            return db.get(key);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Find an element
     */
    public static byte[] get(String key){
        try {
            return db.get(key.getBytes(Configure.CHARSET));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Delete an element
     */
    public static void delete(String key){
        try {
            db.delete(key.getBytes(Configure.CHARSET));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
