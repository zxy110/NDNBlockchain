package crypto;

import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

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
    public static void saveKeypair(){
        try{
            KeyUtils.savePublicKey(publicKey,"keys/pub.der");
            KeyUtils.savePrivateKey(privateKey,"keys/pri.der");
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * These two functions are used to get the keys
     */
    public static PublicKey readPublicKey(){
        try{
            return KeyUtils.getPublicKey("keys/pub.der","EC");
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static PrivateKey readPrivateKey(){
        try{
            return KeyUtils.getPrivateKey("keys/pri.der","EC");
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String readPublicKeyBase64(){
        try{
            return KeyUtils.getPublicKeyBase64("keys/pub.der","EC");
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static String readPrivateKeyBase64(){
        try{
            return KeyUtils.getPrivateKeyBase64("keys/pri.der","EC");
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 签名算法(SHA256和椭圆曲线算法)
     *
     * @param data 要签名的数据包
     */
    public static byte[] signData(byte[] data) throws Exception{
        return Signatures.signData("SHA256withECDSA", data, privateKey);
    }


    /**
     * 验证签名算法(SHA256和椭圆曲线算法)
     *
     * @param data 验证签名的数据包
     * @param sign 签名包
     */
    public static boolean verifySign(byte[] data, byte[] sign) throws Exception{
        return Signatures.verifySign("SHA256withECDSA", data, publicKey, sign);
    }
}
