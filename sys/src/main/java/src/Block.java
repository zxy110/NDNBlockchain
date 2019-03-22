package src;

import utxo.Transaction;
import crypto.*;
import org.bouncycastle.util.Arrays;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class Block {
    private String version;                  //版本号 9
    private String prevBlock;                //前一区块hash 64
    private long timestamp;                  //时间戳 8
    private long nonce;                      //随机数 8
    private String hash;                     //区块hash 64
    private String merkleRoot;               //Merkle树根 64
    private String target;                   //难度值 64
    private int blockSize;                   //区块大小 285  4
    private ArrayList<Transaction> transaction;   //交易

    public Block(){
        this.blockSize =  Configure.INITBLOCKSIZE;
        this.transaction = new ArrayList<Transaction>();
    }

    public Block(String prevBlock){
        this.blockSize =  Configure.INITBLOCKSIZE;
        this.version = Configure.VERSION;
        this.target = calTargetStr();
        this.prevBlock = prevBlock;
        this.transaction = new ArrayList<Transaction>();
        setTimestamp();
        initNonce();
    }

    public Block(String prevBlock, ArrayList<Transaction> transaction){
        this.blockSize =  Configure.INITBLOCKSIZE;
        this.version = Configure.VERSION;
        this.target = calTargetStr();
        this.prevBlock = prevBlock;
        this.transaction = new ArrayList<Transaction>();
        this.transaction = transaction;
        setTimestamp();
        initNonce();
    }

    public Block(String prevBlock, ArrayList<Transaction> transaction, long timestamp, long nonce){
        this.blockSize =  Configure.INITBLOCKSIZE;
        this.version = Configure.VERSION;
        this.target = calTargetStr();
        this.prevBlock = prevBlock;
        this.timestamp = timestamp;
        this.nonce = nonce;
        this.transaction = new ArrayList<Transaction>();
        this.transaction = transaction;
    }

    public void setBlockSize(int blockSize) { this.blockSize = blockSize; }

    public int getBlockSize() { return blockSize; }

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

    public void setTransaction(ArrayList<Transaction> trans){ this.transaction = trans; }

    public ArrayList<Transaction> getTransaction(){ return this.transaction;}

    /**
     * 区块头，包含前一区块哈希值，时间戳，merkle根，难度值，随机数
     * @return
     */
    public byte[] getHeaders(){
        if(this.merkleRoot==null){
            setMerkleRoot();
        }
        return Arrays.concatenate(Arrays.concatenate(this.prevBlock.getBytes(),Utils.longToBytes(this.timestamp),
                this.merkleRoot.getBytes(),Utils.bigIntegerToByte(calTarget())),Utils.longToBytes(this.nonce));
    }

    public void setAll(){
        setMerkleRoot();
        setHash();
    }

    public void addTransaction(Transaction s){ this.transaction.add(s); }

    /**
     * 计算Merkle根
     * @return
     */
    public String calMerkleRoot(){
        if(this.transaction.size()==0) return Utils.byteToHex(Hash.encodeSHA256("".getBytes()));
        ArrayList<byte[]> transArray=new ArrayList<byte[]>();
        for(int i=0;i<this.transaction.size();i++){
            transArray.add(this.transaction.get(i).getTxId().getBytes());
        }
        return Utils.byteToHex(calMerkle(transArray));
    }

    protected byte[] calMerkle(ArrayList<byte[]> t){
        if(t.size()==1) return (Hash.encodeSHA256(t.get(0)));
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
        return (BigInteger.valueOf(1).shiftLeft(256-(int)Configure.TARGET_BITS)).subtract(BigInteger.valueOf(1));
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
        System.out.println("Version："+this.version+"("+this.version.getBytes().length+")");
        System.out.println("prev-block："+this.prevBlock+"("+this.prevBlock.getBytes().length+")");
        System.out.println("hash："+this.hash+"("+this.hash.getBytes().length+")");
        System.out.println("Target："+this.target+"("+this.target.getBytes().length+")");
        System.out.println("merkleRoot："+this.merkleRoot+"("+this.merkleRoot.getBytes().length+")");
        System.out.println("timestamp："+this.timestamp+"("+Utils.longToBytes(this.timestamp).length+")");
        System.out.println("nonce："+this.nonce+"("+Utils.longToBytes(this.nonce).length+")");
        System.out.println("transaction：");
        for(Transaction trans : this.transaction){
            trans.printTransaction();
            System.out.println();
        }
    }

    /**
     * String.getBytes() 是将String进行ISO-8859-1编码的结果，可以通过new String(bytes, "utf-8")解码成utf-8的格式,已封装到IOUtiles.readBytes(bytes)中
     * PublicKey.getEncoded() 是将PublicKey按X509证书格式编码的结果
     * 将SHA256结果转变为十六进制字符串：new String(Hex.encode(hash))，已封装到IOUtils.SHA256toHex(hash)中
     */
    public static void test(){
    //public static void main(String[] args){
        Block block = new Block(Hash.encodeSHA256Hex("prevBlockHash".getBytes()));
        Transaction transaction = Transaction.generateTransaction("test");
        block.addTransaction(transaction);
        block.setMerkleRoot();
        block.setHash();

        block.printBlock();
    }

}

