package leveldb;

import UTXO.TXInput;
import UTXO.TXOutput;
import UTXO.Transaction;
import crypto.IOUtils;
import crypto.KeyUtils;
import crypto.Secp256k1;
import org.json.JSONArray;
import org.json.JSONObject;
import src.Block;
import src.BlockChain;

import java.security.PublicKey;

public class Serialize {
    public static LevelDb db = new LevelDb("db/Blockchain");
    public static LevelDb utxo = new LevelDb("db/UTXO");

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

    //将Block存储在LevelDB中，其中，键为Block的哈希，值为Block对象
    public static void saveBlockLevelDB(Block block){
        try {
            String key = block.getHash();
            String value = Serialize.serializeBlock(block).toString();
            db.put(key, value);
        }catch(Exception  e){
            e.printStackTrace();
        }
    }

    //通过键在LevelDB中查找Block对象
    public static Block findBlockLevelDB(String key){
        try{
            String value = IOUtils.readBytes(db.get(key));
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
        Serialize.saveBlockLevelDB(block);
        block = Serialize.findBlockLevelDB(block.getHash());
        block.printBlock();
    }

    /*
    //将Transaction对象序列化为JSON对象
    public static JSONObject serializeTransaction(Transaction t)throws Exception{
        JSONObject object = new JSONObject();
        object.put("txId",t.getTxId());
        object.put("timeStamp",t.getTimestamp());
        for(TXInput in:t.getInputs()){
            serializeTXInput(in);
        }
        for(TXOutput out:t.getOutputs()){
            serializeTXOutput(out);
        }
        return object;
    }
    public static JSONObject serializeTXInput(TXInput in)throws Exception{
        JSONObject object = new JSONObject();
        object.put("txId",in.getTxId());
        object.put("publicKey", KeyUtils.PublicKey2Base64(in.getPublicKey()));
        //object.put("scriptSig",in.getScriptSig());
        return object;
    }
    public static JSONObject serializeTXOutput(TXOutput out)throws Exception{
        JSONObject object = new JSONObject();
        object.put("txId",out.getTxId());
        object.put("publicKey",KeyUtils.PublicKey2Base64(out.getPublicKey()));
        object.put("scriptSig",out.getScriptPubKey());
        object.put("scriptSig",out.getValue());
        return object;
    }

    //将JSON对象反序列化为Transaction对象
    public static Transaction deSerializeTransaction(JSONObject object)throws Exception{
        Transaction t = new Transaction();
        t.setTxId(object.getString("txId"));
        t.setTimestamp(object.getLong("timeStamp"));
        return t;
    }

    public static TXInput deserializeTXInput(JSONObject object)throws Exception{
        TXInput input = new TXInput();
        input.setTxId(object.getString("txId"));
        input.setPublicKey(KeyUtils.Base642PublicKey(object.getString("publicKey")));
        input.setScriptSig(object.getJSONArray("scriptSig"));
        return input;
    }

    public static TXOutput deserializeTXOutput(JSONObject object)throws Exception{}

    //将Transaction存储在LevelDB中，其中，键为Transaction的哈希，值为Transaction对象
    public static void saveTransactionLevelDB(Transaction block){
        try {
            String key = block.getHash();
            String value = Serialize.serialize(block).toString();
            db.put(key, value);
        }catch(Exception  e){
            e.printStackTrace();
        }
    }

    //通过键在LevelDB中查找Transaction对象
    public static Transaction findTransactionLevelDB(String key){
        try{
            String value = IOUtils.readBytes(db.get(key));
            return Serialize.deSerialize(new JSONObject(value));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args){
        try{
            TXInput in = new TXInput();
            in.setTxId("test");
            byte[] x = "test".getBytes();
            in.setScriptSig(x);
            PublicKey pub = Secp256k1.readPublicKey("Alice");
            in.setPublicKey(pub);
            JSONObject jo = serializeTXInput(in);
            deserializeTXInput(jo);
        }catch(Exception e){
            e.printStackTrace();
        }
    }*/
}
