package crypto;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.stream.Stream;

public class IOUtils {

    /**
     * This function is used to read from InputStream to bytes
     */
    public static byte[] readBytes(InputStream inputStream)
            throws IOException {
        final int BUFFER_SIZE=1024;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int readCount;
        byte[] data = new byte[BUFFER_SIZE];
        while((readCount=inputStream.read(data,0,data.length)) != -1){
            buffer.write(data,0,readCount);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

    /**
     * 大数拼接
     *
     * @param data1 第一个大数
     * @param data2 第二个大数
     * @return
     */
    public static byte[] add(byte[] data1, byte[] data2) {

        byte[] result = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, result, 0, data1.length);
        System.arraycopy(data2, 0, result, data1.length, data2.length);

        return result;
    }


    /**
     * long 转化为 byte[]
     *
     * @param val
     * @return
     */
    public static byte[] toBytes(long val) {
        return ByteBuffer.allocate(Long.BYTES).putLong(val).array();
    }

    /**
     * 将SHA256结果转变为十六进制字符串：new String(Hex.encode(hash))
     */
    public static String SHA256toHex(byte[] hash){
        return (new String(Hex.encode(hash)));
    }

    /**
     * String.getBytes() 是将String进行ISO-8859-1编码的结果，可以通过new String(bytes, "utf-8")解码成utf-8的格式
     */
    public static String readBytes(byte[] bytes){
        try{
            return (new String(bytes, "utf-8"));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * BigInteger转String
     * @param b
     * @return
     */
    public static String bigInteger2String(BigInteger b){
        String s="";
        byte[] by=b.toByteArray();
        for(int i=0;i<by.length;i++){
            s=s+by[i];
        }
        return s;
    }

    /**
     * BigInteger转byte[]
     * @param b
     * @return
     */
    public static byte[] bigInteger2byte(BigInteger b){
        return bigInteger2String(b).getBytes();
    }

}
