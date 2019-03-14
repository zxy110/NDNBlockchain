package leveldb;

import crypto.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import src.Block;
import src.BlockChain;

public class Serialize {
    public static LevelDb db = new LevelDb("db/Blockchain");

    //将Block对象序列化为JSON对象
    public static JSONObject serialize(Block s)throws Exception{
        JSONObject object = new JSONObject();
        object.put("version",s.getVersion());
        object.put("prevBlock",s.getPrevBlock());
        object.put("timestamp",s.getTimestamp());
        object.put("nonce",s.getNonce());
        object.put("hash",s.getHash());
        object.put("Target",s.getTarget());
        object.put("merkleRoot",s.getMerkleRoot());
        object.put("transaction",s.getTransaction());
        return object;
    }

    //将JSON对象反序列化为Block对象
    public static Block deSerialize(JSONObject object)throws Exception{
        Block block = new Block();
        block.setVersion(object.getString("version"));
        block.setPrevBlock(object.getString("prevBlock"));
        block.setTimestamp(object.getLong("timestamp"));
        block.setNonce(object.getLong("nonce"));
        block.setHash(object.getString("hash"));
        block.setTarget(object.getString("Target"));
        block.setMerkleRoot(object.getString("merkleRoot"));
        JSONArray arr = object.getJSONArray("transaction");
        for(int i=0;i<arr.length();i++){
            block.addTransaction(arr.get(i).toString());
        }
        return block;
    }

    //将Block存储在LevelDB中，其中，键为Block的哈希，值为Block对象
    public static void saveLevelDB(Block block){
        try {
            String key = block.getHash();
            String value = Serialize.serialize(block).toString();
            db.put(key, value);
        }catch(Exception  e){
            e.printStackTrace();
        }
    }

    //通过键在LevelDB中查找Block对象
    public static Block findLevelDB(String key){
        try{
            String value = IOUtils.readBytes(db.get(key));
            return Serialize.deSerialize(new JSONObject(value));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void test(){
    //public static void main(String[] args){
        BlockChain blockChain = new BlockChain();
        Block block = blockChain.get(0);
        Serialize.saveLevelDB(block);
        block = Serialize.findLevelDB(block.getHash());
        block.printBlock();
    }
}
