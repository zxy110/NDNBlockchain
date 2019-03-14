package crypto;

import org.bouncycastle.util.encoders.Hex;
import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyUtils {
    /**
     * This function is used to generate ECC keys
     */
    public static KeyPair generateKeypair(String curveName)throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec(curveName);
        keyPairGenerator.initialize(ecGenParameterSpec, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }


    /**
     * The following two functions are used to save the keys.
     * You can run the follow command in terminal to print the pub.der and pri.der:
     * pub.der: openssl pkey -inform DER -pubin -in publickey.der -text
     * pri.der: openssl pkey -inform DER -in privatekey.der -text
     */
    public static void savePublicKey(PublicKey publicKey, String fileName)throws Exception{
        byte[] pub = publicKey.getEncoded();
        OutputStream pubfs = new FileOutputStream(fileName);
        for (int i = 0; i < pub.length; i++) {
            pubfs.write(pub[i]);
        }
    }
    public static void savePrivateKey(PrivateKey privateKey, String fileName)throws Exception{
        byte[] pri = privateKey.getEncoded();
        OutputStream prifs=new FileOutputStream(fileName);
        for(int i=0;i<pri.length;i++){
            prifs.write(pri[i]);
        }
    }


    /**
     * The following two functions are used to read the keys from files.
     */
    public static PublicKey getPublicKey(String filename, String algorithm) throws Exception {
        byte[] encodedKey=IOUtils.readBytes(new FileInputStream(filename));
        X509EncodedKeySpec keySpec=new X509EncodedKeySpec(encodedKey);
        KeyFactory keyFactory=KeyFactory.getInstance(algorithm);
        return keyFactory.generatePublic(keySpec);
    }
    public static PrivateKey getPrivateKey(String filename, String algorithm) throws Exception{
        byte[] encodedKey=IOUtils.readBytes(new FileInputStream(filename));
        PKCS8EncodedKeySpec keySpec=new PKCS8EncodedKeySpec(encodedKey);
        KeyFactory keyFactory=KeyFactory.getInstance(algorithm);
        return keyFactory.generatePrivate(keySpec);
    }

    public static String getPublicKeyBase64(String filename, String algorithm) throws Exception{
        PublicKey pub=getPublicKey(filename,algorithm);
        BASE64Encoder bse=new BASE64Encoder();
        return (bse.encode(pub.getEncoded()));
    }
    public static String getPrivateKeyBase64(String filename, String algorithm) throws Exception{
        PrivateKey pri=getPrivateKey(filename,algorithm);
        BASE64Encoder bse=new BASE64Encoder();
        return (bse.encode(pri.getEncoded()));
    }


    public static String getPublicKeyHex(String filename, String algorithm) throws Exception{
        PublicKey pub=getPublicKey(filename,algorithm);
        return new String(Hex.encode(pub.getEncoded()));
    }
    public static String getPrivateKeyHex(String filename, String algorithm) throws Exception{
        PrivateKey pri=getPrivateKey(filename,algorithm);
        return new String(Hex.encode(pri.getEncoded()));
    }

}
