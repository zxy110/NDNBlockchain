package UTXO;

import crypto.Hash;
import crypto.Secp256k1;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import src.Utils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
    protected String txId;                       //交易哈希：将交易内容<交易输入地址，输出地址，时间戳>打包进行SHA256哈希
    protected ArrayList<TXInput> inputs;         //交易输入
    protected ArrayList<TXOutput> outputs;       //交易输出
    protected long timestamp;                    //时间戳

    public Transaction(){
        this.inputs = new ArrayList<TXInput>();
        this.outputs = new ArrayList<TXOutput>();
        this.timestamp = System.currentTimeMillis();
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getTxId(){
        return this.txId;
    }

    public void setInputs(ArrayList<TXInput> inputs) {
        this.inputs = inputs;
    }

    public ArrayList<TXInput> getInputs() {
        return this.inputs;
    }

    public void setOutputs(ArrayList<TXOutput> outputs) {
        this.outputs = outputs;
    }

    public ArrayList<TXOutput> getOutputs(){
        return this.outputs;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestamp(){
        return this.timestamp;
    }

    public void addTXInputs(TXInput input){
        this.inputs.add(input);
    }

    public void addTXOutput(TXOutput output){
        this.outputs.add(output);
    }

    public void setTxId(){
        String txId = calTxId();
        this.txId = txId;
        //对所有交易输出对应的 txId 赋值
        for(int i=0;i<this.outputs.size();i++){
            this.outputs.get(i).txId = txId;
        }
    }

    /**
     * 计算交易哈希
     * 这里将时间戳,交易的输入地址，输出地址进行拼接，进行 SHA256 和 RipeMD160 哈希
     */
    public String calTxId(){
        byte[] tx = Utils.longToBytes(this.timestamp);
        for(TXInput input : this.inputs){
            tx = ByteUtils.concatenate(tx,input.publicKey.getEncoded());
        }
        for(TXOutput output : this.outputs){
            tx = ByteUtils.concatenate(tx, output.publicKey.getEncoded());
        }
        return Hash.encodeRipeMD160Hex(Hash.encodeSHA256(tx));
    }

    /**
     * 数据产生函数，该数据用于交易输入者进行签名时加密的消息内容，即所有输出者公钥的字符串拼接
     * @param outputs 所有交易输出数组
     * @return
     */
    public static byte[] generateData(ArrayList<TXOutput> outputs){
        byte[] data = outputs.get(0).getPublicKey().getEncoded();
        for(int i=1;i<outputs.size();i++){
            data = ByteUtils.concatenate(data, outputs.get(i).getPublicKey().getEncoded());
        }
        return data;
    }

    /**
     * 数据产生函数，同上
     * @param output 单个交易输出
     * @return
     */
    public static byte[] generateData(TXOutput output){
        return output.getPublicKey().getEncoded();
    }

    /**
     * 数据产生函数，同上
     * @param publicKey 单个交易输出公钥
     * @return
     */
    public static byte[] generateData(PublicKey publicKey){
        return publicKey.getEncoded();
    }

    /**
     * 生成交易
     * @param inputs 交易输入列表
     * @param outputs 交易输出列表
     * @return
     */
    public static Transaction generateTrans(ArrayList<TXInput> inputs, ArrayList<TXOutput> outputs){
        Transaction trans = new Transaction();
        trans.inputs = inputs;
        trans.outputs = outputs;
        trans.setTxId();
        return trans;
    }

    /**
     * 产生一个输入一个输出的交易
     * @param txId 交易输入的哈希，即对应的上一笔输出交易的哈希
     * @param priInput 输入者私钥
     * @param pubInput 输入者公钥
     * @param pubOutput 输出者公钥
     * @param value 金额
     * @return
     */
    public static Transaction generateTrans(String txId, PrivateKey priInput, PublicKey pubInput, PublicKey pubOutput, int value){
        //交易输入(签名是对输出者公钥进行签名，从而实现和输出绑定)
        byte[] sig = Secp256k1.signData(priInput, Transaction.generateData(pubOutput));
        TXInput input = new TXInput(txId, pubInput, sig);

        //交易输出
        TXOutput output = new TXOutput(value, pubOutput);

        //构建交易
        Transaction trans = new Transaction();
        trans.addTXInputs(input);
        trans.addTXOutput(output);
        trans.setTxId();

        return trans;
    }

    /**
     * 对交易进行UTXO验证
     * @param transaction
     * @param utxo
     * @return
     */
    public static boolean verifyTransaction(Transaction transaction, UTXO utxo){
        // 检查哈希值
        if(!(transaction.getTxId().equals(transaction.calTxId()))) return false;
        // 对每笔交易的输入进行UTXO检查
        // 计算输出金额总数
        int cash=0;
        for(TXOutput txOutput:transaction.getOutputs()){
            cash += txOutput.getValue();
        }
        for(TXInput txInput:transaction.getInputs()){
            String txId = txInput.getTxId();
            PublicKey publicKey = txInput.getPublicKey();
            byte[] sig = txInput.getScriptSig();
            /**
             * 1. 检查输入是否有对应的输出，即utxo.outputs中是否有当前输入的txId对应的值
             * 2. 检查该输入是否被花掉，即utxo.inputs中是否有当前输入的txId对应的值
             * 3. 检查该输入的公钥，签名是否正确，即Secp256k1.verifySign(publicKey, data, sig)，其中data是本笔交易输出的公钥组合，即pubOutput.getEncoded()
             * 4. 检查金额是否匹配
             */
            if(!utxo.outputs.containsKey(txId)){ return false; }
            else if(utxo.inputs.containsKey(txId)){ return false; }
            else{
                byte[] data = Transaction.generateData(transaction.getOutputs());
                if(!Secp256k1.verifySign(publicKey,data,sig)){ return false; }
            }
            cash -= utxo.outputs.get(txId).getValue();
        }
        if(cash!=0) return false;
        return true;
    }

    /**
     * 输出交易
     */
    public static void printTransactions(ArrayList<Transaction> transactions){
        for(Transaction transaction:transactions){
            transaction.printTransaction();
        }
    }
    public void printTransaction(){
        System.out.println("txId：" + this.txId);
        for(TXInput input : this.inputs){
            input.printTXInput();
        }
        for(TXOutput output : this.outputs){
            output.printTXOutput();
        }
    }


    public static Transaction genesisTransaction(long timestamp){
        Secp256k1 secp256k1=new Secp256k1();
        PublicKey pubKeyGenesis = Secp256k1.readPublicKey("Genesis");
        PrivateKey priKeyGenesis = Secp256k1.readPrivateKey("Genesis");
        PublicKey pubKeyAlice = Secp256k1.readPublicKey("Alice");
        Transaction transaction = generateTrans("Genesis", priKeyGenesis, pubKeyGenesis, pubKeyAlice, 10);
        transaction.setTimestamp(timestamp);
        transaction.setTxId();
        return transaction;
    }


    public static Transaction generateTransaction(String txId){
        Secp256k1 secp256k1=new Secp256k1();
        //生成Alice和Sam的公私钥对
        //secp256k1.generateKeypair();
        //secp256k1.saveKeypair("Alice");
        //secp256k1.generateKeypair();
        //secp256k1.saveKeypair("Sam");


        PublicKey pubKeyAlice = Secp256k1.readPublicKey("Alice");
        PublicKey pubKeySam = Secp256k1.readPublicKey("Sam");

        //交易输入
        byte[] data = ByteUtils.concatenate(pubKeySam.getEncoded(),pubKeyAlice.getEncoded());
        byte[] sig=secp256k1.signData(Secp256k1.readPrivateKey("Alice"), data);
        TXInput input = new TXInput(txId, pubKeyAlice, sig);

        //交易输出
        TXOutput output1 = new TXOutput(2, pubKeySam);
        TXOutput output2 = new TXOutput(8, pubKeyAlice);

        //构建交易
        Transaction transaction = new Transaction();
        transaction.addTXInputs(input);
        transaction.addTXOutput(output1);
        transaction.addTXOutput(output2);
        transaction.setTxId();

        //test
        //transaction.printTransaction();
        //System.out.println(transaction.txId);
        //System.out.println(Secp256k1.verifySign(pubKeyAlice, data, sig));
        //System.out.println(Secp256k1.verifySign(pubKeySam, data, sig));

        return transaction;
    }

    public void test(){
    //public static void main(String[] args){
        Secp256k1 secp256k1=new Secp256k1();
        //生成Genesis, Alice和Sam的公私钥对
        /*
        secp256k1.generateKeypair();
        secp256k1.saveKeypair("Alice");
        secp256k1.generateKeypair();
        secp256k1.saveKeypair("Sam");
        secp256k1.generateKeypair();
        secp256k1.saveKeypair("Genesis");
        */
        PublicKey pubKeyAlice = Secp256k1.readPublicKey("Alice");
        PublicKey pubKeySam = Secp256k1.readPublicKey("Sam");

        //交易输入
        byte[] data = ByteUtils.concatenate(pubKeySam.getEncoded(),pubKeyAlice.getEncoded());
        byte[] sig=secp256k1.signData(Secp256k1.readPrivateKey("Alice"), data);
        TXInput input1 = new TXInput(genesisTransaction(1552888105078l).txId, pubKeyAlice, sig);
        TXInput input2 = new TXInput(genesisTransaction(1552888105078l).txId, pubKeyAlice, sig);

        //交易输出
        TXOutput output1 = new TXOutput(2, pubKeySam);
        TXOutput output2 = new TXOutput(8, pubKeyAlice);

        //构建交易
        Transaction transaction = new Transaction();
        transaction.addTXInputs(input1);
        transaction.addTXInputs(input2);
        transaction.addTXOutput(output1);
        transaction.addTXOutput(output2);
        transaction.setTxId();

        //test
        //transaction.printTransaction();
        //System.out.println(transaction.txId);
        System.out.println(Secp256k1.verifySign(pubKeyAlice, data, sig));
        //System.out.println(Secp256k1.verifySign(pubKeySam, data, sig));
    }
}
