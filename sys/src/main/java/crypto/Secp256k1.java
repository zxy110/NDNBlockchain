package crypto;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;


public class Secp256k1 {
    public static KeyPair keyPair;
    public static PublicKey publicKey;
    public static PrivateKey privateKey;

    /**
     * This function is used to generate ECC keys, which uses secp256k1 to generate.
     */
    public static void generateKeypair(){
        try{
            keyPair=KeyUtils.generateKeypair("secp256k1");
            publicKey=keyPair.getPublic();
            privateKey=keyPair.getPrivate();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * This function is used to save the keys, it's stored in the directory of /keys.
     * You can run the follow command in terminal to print the pub.der and pri.der:
     * pub.der: openssl pkey -inform DER -pubin -in pub.der -text
     * pri.der: openssl pkey -inform DER -in pri.der -text
     */
    public static void saveKeypair(String filename){
        try{
            // Notice that the directory path is different in different systems.
            // When in Windows, it's "key/filename/pub.der"
            // When in linux, it's "sys/key/filename/pub.der"
            KeyUtils.savePublicKey(publicKey,"sys/key/" + filename + "/pub.der");
            KeyUtils.savePrivateKey(privateKey,"sys/key/" + filename + "/pri.der");
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * These two functions are used to get the keys
     */
    public static PublicKey readPublicKey(String filename){
        try{
            // Notice that the directory path is different in different systems.
            // When in Windows, it's "key/filename/pub.der"
            // When in linux, it's "sys/key/filename/pub.der"
            return KeyUtils.getPublicKey("sys/key/" + filename + "/pub.der","EC");
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static PrivateKey readPrivateKey(String filename){
        try{
            // Notice that the directory path is different in different systems.
            // When in Windows, it's "key/filename/pub.der"
            // When in linux, it's "sys/key/filename/pub.der"
            return KeyUtils.getPrivateKey("sys/key/" + filename + "/pri.der","EC");
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String readPublicKeyBase64(String filename){
        try{
            // Notice that the directory path is different in different systems.
            // When in Windows, it's "key/filename/pub.der"
            // When in linux, it's "sys/key/filename/pub.der"
            return KeyUtils.getPublicKeyBase64("sys/key/" + filename + "/pub.der","EC");
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static String readPrivateKeyBase64(String filename){
        try{
            // Notice that the directory path is different in different systems.
            // When in Windows, it's "key/filename/pub.der"
            // When in linux, it's "sys/key/filename/pub.der"
            return KeyUtils.getPrivateKeyBase64("sys/key/" + filename + "/pri.der","EC");
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 签名算法(SHA256和椭圆曲线算法)
     * 使用私钥签名data数据包
     * 这里主要是为了UTXO中验证sig和公钥的关联，因此data设置成固定值，不作为参数传入
     *
     * @param data 数据包
     * @param privateKey 私钥
     */
    public static byte[] signData(PrivateKey privateKey, byte[] data){
        try{
            return signData("SHA256withECDSA", data, privateKey);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /*
     * This function is used to sign data with the algorithm and key
     */
    public static byte[] signData(String algorithm, byte[] data, PrivateKey key)
            throws Exception{
        Signature signer = Signature.getInstance(algorithm);
        signer.initSign(key);
        signer.update(data);
        return (signer.sign());
    }

    /**
     * 验证签名算法
     * 用于UTXO中sig和publickey的关联验证，data设置为固定值
     *
     * @param publicKey 公钥
     * @param sign 签名包
     */
    public static boolean verifySign(PublicKey publicKey, byte[] data, byte[] sign){
        try{
            return verifySign("SHA256withECDSA", data, publicKey, sign);
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


    /*
     * This function is used to verify signature
     */
    public static boolean verifySign(String algorithm, byte[] data, PublicKey key, byte[] sig)
            throws Exception{
        Signature signer = Signature.getInstance(algorithm);
        signer.initVerify(key);
        signer.update(data);
        return (signer.verify(sig));
    }
}
