package src;

import crypto.*;
import org.bouncycastle.util.Arrays;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class Block {
    protected String version;                  //版本号
    protected String prevBlock;                //前一区块hash
    protected long timestamp;                  //时间戳
    protected long nonce;                      //随机数
    protected String hash;                     //区块hash
    protected String merkleRoot;               //Merkle树根
    protected ArrayList<String> transaction;   //交易
    protected String target;                   //难度值
    //protected long blockSize;                //区块大小

    public Block(){
        this.transaction = new ArrayList<String>();
    }

    public Block(String prevBlock){
        this.version = Configure.VERSION;
        this.target = calTargetStr();
        this.prevBlock = prevBlock;
        this.transaction = new ArrayList<String>();
        setTimestamp();
        initNonce();
    }

    public Block(String prevBlock, ArrayList<String> transaction){
        this.version = Configure.VERSION;
        this.target = calTargetStr();
        this.prevBlock = prevBlock;
        this.transaction = new ArrayList<String>();
        this.transaction = transaction;
        setTimestamp();
        initNonce();
    }

    public Block(String prevBlock, ArrayList<String> transaction, long timestamp, long nonce){
        this.version = Configure.VERSION;
        this.target = calTargetStr();
        this.prevBlock = prevBlock;
        this.timestamp = timestamp;
        this.nonce = nonce;
        this.transaction = new ArrayList<String>();
        this.transaction = transaction;
    }

    public void setVersion(String v){ this.version = v; }

    public String getVersion(){ return this.version; }

    public void setPrevBlock(String prevBlock){ this.prevBlock = prevBlock; }

    public String getPrevBlock(){ return this.prevBlock; }

    public void setTimestamp(){ this.timestamp = System.currentTimeMillis(); }

    public void setTimestamp(long timestamp){ this.timestamp = timestamp; }

    public long getTimestamp(){ return this.timestamp; }

    public void initNonce(){
        Random randomNonce = new Random(System.currentTimeMillis()); //time is seed
        this.nonce = randomNonce.nextLong();
    }

    public void setNonce(long nonce){ this.nonce = nonce; }

    public long getNonce(){ return this.nonce; }

    public void setHash(){ this.hash = calHash(); }

    public void setHash(String hash){ this.hash = hash; }

    public String getHash(){ return this.hash;}

    public void setTarget(String s){ this.target = s;}

    public String getTarget(){ return this.target;}

    public void setMerkleRoot(){ this.merkleRoot = calMerkleRoot(); }

    public void setMerkleRoot(String merkleRoot){ this.merkleRoot = merkleRoot; }

    public String getMerkleRoot(){ return this.merkleRoot; }

    public void setTransaction(ArrayList<String> trans){ this.transaction = trans; }

    public ArrayList<String> getTransaction(){ return this.transaction;}

    /**
     * 区块头，包含前一区块哈希值，时间戳，merkle根，难度值，随机数
     * @return
     */
    public byte[] getHeaders(){
        if(this.merkleRoot==null){
            setMerkleRoot();
        }
        return Arrays.concatenate(Arrays.concatenate(this.prevBlock.getBytes(),IOUtils.toBytes(this.timestamp),
                this.merkleRoot.getBytes(),IOUtils.bigInteger2byte(calTarget())),IOUtils.toBytes(this.nonce));
    }

    public void setAll(){
        setMerkleRoot();
        setHash();
    }

    public void addTransaction(String s){ this.transaction.add(s); }

    /**
     * 计算Merkle根
     * @return
     */
    public String calMerkleRoot(){
        if(this.transaction.size()==0) return IOUtils.SHA256toHex("NULL".getBytes());
        ArrayList<byte[]> transArray=new ArrayList<byte[]>();
        for(int i=0;i<this.transaction.size();i++){
            transArray.add(this.transaction.get(i).toString().getBytes());
        }
        return IOUtils.SHA256toHex(calMerkle(transArray));
    }

    protected byte[] calMerkle(ArrayList<byte[]> t){
        if(t.size()==1) return (t.get(0));
        ArrayList<byte[]> nextt=new ArrayList<byte[]>((t.size()+1)/2);
        for(int i=0;i<t.size()-1;i+=2){
            nextt.add(Hash.encodeSHA256(Arrays.concatenate(t.get(i),t.get(i+1))));
        }
        if(t.size()%2==1){
            int tail = nextt.size()-1;
            nextt.set(tail, Hash.encodeSHA256(Arrays.concatenate(nextt.get(tail),t.get(t.size()-1))));
        }
        return calMerkle(nextt);
    }

    /**
     * 计算hash：对区块头进行二次SHA256哈希计算
     * @return
     */
    public String calHash(){ return Hash.encodeSHA256Hex(Hash.encodeSHA256(getHeaders())); }

    /**
     * 计算难度值，即1左移TARGET_BITS位
     */
    public static BigInteger calTarget(){
        return BigInteger.valueOf(1).shiftLeft(256-(int)Configure.TARGET_BITS);
    }

    public static String calTargetStr(){
        String tar = calTarget().toString(16);
        int zeroNum = Configure.TARGET_BITS/4;
        for(int i=0;i<zeroNum;i++){
            tar = "0" + tar;
        }
        return tar;
    }

    public void printBlock(){
        System.out.println("Version："+this.version);
        System.out.println("prev-block："+this.prevBlock);
        System.out.println("hash："+this.hash);
        System.out.println("Target："+this.target);
        System.out.println("merkleRoot："+this.merkleRoot);
        System.out.println("timestamp："+this.timestamp);
        System.out.println("nonce："+this.nonce);
        System.out.println("transaction："+this.transaction);
        System.out.println();
    }

    /**
     * String.getBytes() 是将String进行ISO-8859-1编码的结果，可以通过new String(bytes, "utf-8")解码成utf-8的格式,已封装到IOUtiles.readBytes(bytes)中
     * PublicKey.getEncoded() 是将PublicKey按X509证书格式编码的结果
     * 将SHA256结果转变为十六进制字符串：new String(Hex.encode(hash))，已封装到IOUtils.SHA256toHex(hash)中
     */
    public static void test(){
    //public static void main(String[] args){
        Block block = new Block(Hash.encodeSHA256Hex("prevBlockHash".getBytes()));
        block.addTransaction("Sam transfer 2RMB to Alice");
        block.addTransaction("Alice transfer 2RMB to Bob");
        block.addTransaction("Bob transfer 2RMB to Sarah");
        block.setMerkleRoot();
        block.setHash();

        block.printBlock();
    }

}

