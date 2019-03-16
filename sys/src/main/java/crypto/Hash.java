package crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.security.MessageDigest;
import java.security.Security;

public class Hash {
    /**
     * SHA256消息摘要
     *
     * @param data 待处理的消息摘要数据
     * @return byte[] 消息摘要
     */
    public static byte[] encodeSHA256(byte[] data){
        try{
            MessageDigest messageDigest=MessageDigest.getInstance("SHA-256");
            messageDigest.update(data);
            return messageDigest.digest();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * SHA256消息摘要
     *
     * @param data 待处理的消息摘要数据
     * @return String 消息摘要
     **/
    public static String encodeSHA256Hex(byte[] data){
        try {
            //执行消息摘要
            byte[] b = encodeSHA256(data);
            //做十六进制的编码处理
            return new String(Hex.encode(b));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * RipeMD160消息摘要
     *
     * @param data 待处理的消息摘要数据
     * @return byte[] 消息摘要
     */
    public static byte[] encodeRipeMD160(byte[] data){
        try {
            //加入BouncyCastleProvider的支持
            Security.addProvider(new BouncyCastleProvider());
            //初始化MessageDigest
            MessageDigest md = MessageDigest.getInstance("RipeMD160");
            //执行消息摘要
            return md.digest(data);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * RipeMD160Hex消息摘要
     *
     * @param data 待处理的消息摘要数据
     * @return String 消息摘要
     **/
    public static String encodeRipeMD160Hex(byte[] data){
        try {
            //执行消息摘要
            byte[] b = encodeRipeMD160(data);
            //做十六进制的编码处理
            return new String(Hex.encode(b));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
