package sys;

import Utils.Utils;
import crypto.BilinearPairing;
import crypto.Hash;
import it.unisa.dia.gas.jpbc.Element;
import utxo.Transaction;
import org.bouncycastle.util.Arrays;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class Block extends BilinearPairing {
    private String version;                      //版本号 9
    private String prevBlock;                    //前一区块hash 64
    private long timestamp;                      //时间戳 8
    private long nonce;                          //随机数 8
    private String hash;                         //区块hash 64
    private String merkleRoot;                   //Merkle树根 64
    private String target;                       //难度值 64
    private int blockSize;                       //区块大小 581+132*Configure.DELEGATES
    private ArrayList<Transaction> transaction;  //交易
    // Mtplbp
    private Element m;                           //32 --Zr
    private Element P;                           //66 --G1
    private Element vrfPk;                       //66 --G1
    private Element vrfHash;                     //66 --GT
    private Element vrfProof;                    //66 --G1
    private ArrayList<Element> signatures;       //Configure.DELEGATES*66
    private ArrayList<Element> pks;              //Configure.DELEGATES*66

    public Block(){
        //init Mtplbp
        super();
        m = Zr.newRandomElement();
        P = G1.newRandomElement();
        vrfPk = G1.newElement();
        vrfHash = GT.newElement();
        vrfProof = G1.newElement();
        pks = new ArrayList<Element>();
        signatures = new ArrayList<Element>();


        this.version = Configure.VERSION;
        this.blockSize =  Configure.INITBLOCKSIZE;
        this.transaction = new ArrayList<Transaction>();
    }

    public Block(String prevBlock){
        this();
        this.target = calTargetStr();
        this.prevBlock = prevBlock;
        setTimestamp();
        initNonce();
    }

    public Block(String prevBlock, ArrayList<Transaction> transaction){
        this();
        this.target = calTargetStr();
        this.prevBlock = prevBlock;
        this.transaction = transaction;
        setTimestamp();
        initNonce();
    }

    public Block(String prevBlock, ArrayList<Transaction> transaction, long timestamp, long nonce){
        this();
        this.target = calTargetStr();
        this.prevBlock = prevBlock;
        this.timestamp = timestamp;
        this.nonce = nonce;
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

    public Element getM() {
        return m.duplicate();
    }

    public void setM(byte[] m) {
        this.m.setFromBytes(m);
    }

    public Element getP() {
        return P.duplicate();
    }

    public void setP(byte[] p) {
        this.P.setFromBytes(p);
    }

    public Element getVrfPk() {
        return vrfPk.duplicate();
    }

    public void setVrfPk(byte[] vrfPk) {
        this.vrfPk.setFromBytes(vrfPk);
    }

    public Element getVrfHash() {
        return vrfHash.duplicate();
    }

    public void setVrfHash(byte[] vrfHash) {
        this.vrfHash.setFromBytes(vrfHash);
    }

    public Element getVrfProof() {
        return vrfProof.duplicate();
    }

    public void setVrfProof(byte[] vrfProof) {
        this.vrfProof.setFromBytes(vrfProof);
    }

    public ArrayList<Element> getPks() {
        return pks;
    }

    public void setPks(byte[] pks) {
        ArrayList<Element> pkArr = new ArrayList<Element>();
        Element pk = G1.newElement();
        for(int i=0;i<(pks.length/Configure.SIZEOFG1);i++){
            pk.setFromBytes(Arrays.copyOfRange(pks,i*Configure.SIZEOFG1,(i+1)*Configure.SIZEOFG1));
            pkArr.add(pk);
        }
        this.pks = pkArr;
    }

    public ArrayList<Element> getSignatures() {
        return signatures;
    }

    public void setSignatures(byte[] signatures) {
        ArrayList<Element> sigArr = new ArrayList<Element>();
        Element sig = GT.newElement();
        for(int i=0;i<(signatures.length/Configure.SIZEOFGT);i++){
            sig.setFromBytes(Arrays.copyOfRange(signatures,i*Configure.SIZEOFGT,(i+1)*Configure.SIZEOFGT));
            sigArr.add(sig);
        }
        this.signatures = sigArr;
    }

    /**
     * 区块头，包含前一区块哈希值，时间戳，merkle根，难度值，随机数
     * @return
     */
    public byte[] getHeaders(){
        if(this.merkleRoot==null){
            setMerkleRoot();
        }
        return Arrays.concatenate(Arrays.concatenate(this.prevBlock.getBytes(), Utils.longToBytes(this.timestamp),
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
        if(Configure.Consensus.equals("Mptlbp")){
            System.out.println("m:" + this.m);
            System.out.println("P:" + this.P);
            System.out.println("vrfPk:" + this.vrfPk);
            System.out.println("vrfHash:" + this.vrfHash);
            System.out.println("vrfProof:" + this.vrfProof);
            System.out.println("pksSize:" + this.getPks().size());
            System.out.println("sigsSize:" + this.getSignatures().size());
        }
    }

    /**
     * String.getBytes() 是将String进行ISO-8859-1编码的结果，可以通过new String(bytes, "utf-8")解码成utf-8的格式,已封装到IOUtiles.readBytes(bytes)中
     * PublicKey.getEncoded() 是将PublicKey按X509证书格式编码的结果
     * 将SHA256结果转变为十六进制字符串：new String(Hex.encode(hash))，已封装到IOUtils.SHA256toHex(hash)中
     */
    private static void test(){
        //public static void main(String[] args){
        Block block = new Block(Hash.encodeSHA256Hex("prevBlockHash".getBytes()));
        Transaction transaction = Transaction.generateTransaction("test");
        block.addTransaction(transaction);
        block.setMerkleRoot();
        block.setHash();

        block.printBlock();
    }

}

