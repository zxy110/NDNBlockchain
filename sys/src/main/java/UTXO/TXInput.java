package UTXO;

import crypto.KeyUtils;

import java.security.Key;
import java.security.PublicKey;
import java.util.ArrayList;

public class TXInput {
    protected String txId;                      //上一笔交易的hash值（即该输入对应输出的交易的哈希值）
    //解锁脚本 {sig,PublicKey}, 用于验证身份, 此处简化只有sig, 这里sig是对输出公钥哈希（锁定脚本）的签名，从而完成对输出的绑定
    protected byte[] scriptSig;                 //解锁脚本
    protected PublicKey publicKey;              //发送者的公钥

    public TXInput(){}

    public TXInput(String txId, PublicKey publicKey, byte[] sig){
        this.txId=txId;
        this.publicKey=publicKey;
        this.scriptSig=sig;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getTxId() {
        return txId;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setScriptSig(byte[] scriptSig) {
        this.scriptSig = scriptSig;
    }

    public byte[] getScriptSig() {
        return scriptSig;
    }

    public void printTXInput(){
        System.out.println("inputTxId：" + txId);
        System.out.println("publicKey：" + KeyUtils.PublicKey2Base64(publicKey));
        System.out.println("scriptSig：" + scriptSig);
    }
}
