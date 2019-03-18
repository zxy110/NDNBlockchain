package src;

import Net.Producer;
import leveldb.LevelDb;
import leveldb.Serialize;
import org.iq80.leveldb.DBIterator;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Persistence {
    // Notice that the directory path is different in different systems.
    // When in Windows, it's "db/Blockchain"
    // When in linux, it's "sys/db/Blockchain"
    public static LevelDb db = new LevelDb("sys/db/Blockchain");


    //initial levelDB and blockchain
    public static BlockChain init(BlockChain blockChain){
        try{
            DBIterator it=db.getDb().iterator();
            if(!it.hasNext()){
                saveBlockLevelDB(blockChain.GenesisBlock());
            }else {
                System.out.println("LevelDB data: ");
                while (it.hasNext()) {
                    Map.Entry<byte[], byte[]> item = it.next();
                    String key = new String(item.getKey(), Configure.CHARSET);
                    String value = new String(item.getValue(), Configure.CHARSET);//null,check.
                    System.out.println(key + ":" + value);
                    //save to blockchain
                    Block block = Serialize.deSerializeBlock(new JSONObject(value));
                    blockChain.addBlock(block);
                    //produce
                    Map<String, Producer> producerMap = new HashMap<String, Producer>();
                    ExecutorService executor = Executors.newFixedThreadPool(10);
                    String prefix = String.join("/", Configure.blockNDNGetBlockPrefix, block.hash);
                    String prefixNewBlock = String.join("/", Configure.blockNDNGetBlockPrefix, block.getPrevBlock());
                    if(!producerMap.containsKey(prefix)){ //如果已经为它产生过producer对象，则不执行
                        Producer producer = new Producer(block, prefixNewBlock); //创建生产者，包含新区块数据和它的名字
                        executor.execute(producer);   //异步执行该生产者中的run方法，为了等待收到兴趣来传输最新的区块
                        producerMap.put(prefixNewBlock, producer);	//将该映射加入到这个map中，即名字-producer对象
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return blockChain;
    }

    //将Block存储在LevelDB中，其中，键为Block的哈希，值为Block对象
    public static void saveBlockLevelDB(Block block){
        try {
            String key = String.valueOf(block.getTimestamp());
            String value = Serialize.serializeBlock(block).toString();
            db.put(key, value);
        }catch(Exception  e){
            e.printStackTrace();
        }
    }

    //通过键在LevelDB中查找Block对象
    public static Block findBlockLevelDB(String key){
        try{
            String value = Utils.readBytes(db.get(key));
            return Serialize.deSerializeBlock(new JSONObject(value));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void test(){
        //public static void main(String[] args){
        BlockChain blockChain = new BlockChain();
        Block block = blockChain.get(0);
        saveBlockLevelDB(block);
        block = findBlockLevelDB(block.getHash());
        block.printBlock();
    }

}
