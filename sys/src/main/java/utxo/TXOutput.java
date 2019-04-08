package utxo;

import crypto.Hash;
import Utils.KeyUtils;

import java.security.PublicKey;

public class TXOutput {
    private String txId;              //该交易的hash值，在交易打包后进行赋值
    private int value;                //金额
    private String scriptPubKey;      //锁定脚本：此处设置为公钥哈希，即RipeMD160(SHA256(PublicKey))
    private PublicKey publicKey;      //接收者的公钥

    public TXOutput(int value, PublicKey publicKey){
        this.value=value;
        this.publicKey=publicKey;
        this.scriptPubKey= Hash.encodeRipeMD160Hex(Hash.encodeSHA256(KeyUtils.PublicKey2Base64(publicKey).getBytes()));
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getTxId() {
        return txId;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setScriptPubKey(String scriptPubKey) {
        this.scriptPubKey = scriptPubKey;
    }

    public String getScriptPubKey() {
        return scriptPubKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void printTXOutput(){
        System.out.println("outputTxId：" + txId);
        System.out.println("value：" + value);
        System.out.println("publicKey：" + KeyUtils.PublicKey2Base64(publicKey));
        System.out.println("scriptPubKey：" + scriptPubKey);
    }
}
