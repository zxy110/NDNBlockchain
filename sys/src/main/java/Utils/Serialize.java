package Utils;

import org.json.JSONObject;
import sys.Block;

public class Serialize {

    //将Block对象序列化为JSON对象
    public static JSONObject serializeBlock(Block s)throws Exception{
        JSONObject object = new JSONObject();
        object.put("version",s.getVersion());
        object.put("prevBlock",s.getPrevBlock());
        object.put("timestamp",s.getTimestamp());
        object.put("nonce",s.getNonce());
        object.put("hash",s.getHash());
        object.put("Target",s.getTarget());
        object.put("merkleRoot",s.getMerkleRoot());
        //object.put("transaction",s.getTransaction());
        return object;
    }

    //将JSON对象反序列化为Block对象
    public static Block deSerializeBlock(JSONObject object)throws Exception{
        Block block = new Block();
        block.setVersion(object.getString("version"));
        block.setPrevBlock(object.getString("prevBlock"));
        block.setTimestamp(object.getLong("timestamp"));
        block.setNonce(object.getLong("nonce"));
        block.setHash(object.getString("hash"));
        block.setTarget(object.getString("Target"));
        block.setMerkleRoot(object.getString("merkleRoot"));
        /*
        JSONArray arr = object.getJSONArray("transaction");
        for(int i=0;i<arr.length();i++){
            block.addTransaction(arr.get(i));
        }*/
        return block;
    }
}
