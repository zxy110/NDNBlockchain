package crypto;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class Signatures {
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
