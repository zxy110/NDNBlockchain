package src;

import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;
import javafx.util.Pair;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Utils {
    /**
     * Block to byte[]
     */
    public static byte[] blockToByteArray(Block block){
        byte[] blockByte=new byte[block.getBlockSize()];
        byte[] version = block.getVersion().getBytes();             //9
        byte[] prevBlock = block.getPrevBlock().getBytes();         //64
        byte[] hash = block.getHash().getBytes();                   //64
        byte[] target = block.getTarget().getBytes();               //64
        byte[] merkleRoot = block.getMerkleRoot().getBytes();       //64
        byte[] timestamp = Utils.longToBytes(block.getTimestamp()); //8
        byte[] nonce = Utils.longToBytes(block.getNonce());         //8
        byte[] blockSize = Utils.intToBytes(block.getBlockSize());  //4

        ArrayList<byte[]> byteArray = new ArrayList<byte[]>();
        byteArray.add(version);
        byteArray.add(prevBlock);
        byteArray.add(hash);
        byteArray.add(target);
        byteArray.add(merkleRoot);
        byteArray.add(timestamp);
        byteArray.add(nonce);
        byteArray.add(blockSize);

        byteArrayCopy(blockByte, byteArray);
        return blockByte;
    }

    /**
     * Add all byte[] from byteArray to r
     * @return
     */
    public static void byteArrayCopy(byte[] r, ArrayList<byte[]> byteArray){
        int i=0,j=0;
        for(byte[] b : byteArray){
            for(j=0;j<b.length;i++,j++){
                r[i] = b[j];
            }
        }
    }

    /**
     * byte[] to Block
     */
    public static Block ByteArrayToblock(byte[] blockByte){
        Block block = new Block();

        byte[] version = Arrays.copyOfRange(blockByte, 0, Configure.VERSIONSIZE);       //9
        byte[] prevBlock = Arrays.copyOfRange(blockByte, Configure.VERSIONSIZE, Configure.VERSIONSIZE + Configure.SHA256SIZE);      //64
        byte[] hash = Arrays.copyOfRange(blockByte, Configure.VERSIONSIZE + Configure.SHA256SIZE,
                Configure.VERSIONSIZE + Configure.SHA256SIZE + Configure.SHA256SIZE);           //64
        byte[] target = Arrays.copyOfRange(blockByte, Configure.VERSIONSIZE + Configure.SHA256SIZE + Configure.SHA256SIZE,
                Configure.VERSIONSIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SHA256SIZE);         //64
        byte[] merkleRoot = Arrays.copyOfRange(blockByte, Configure.VERSIONSIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SHA256SIZE,
                Configure.VERSIONSIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SHA256SIZE);     //64
        byte[] timestamp = Arrays.copyOfRange(blockByte, Configure.VERSIONSIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SHA256SIZE,
                Configure.VERSIONSIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SIZEOFLONG);      //8
        byte[] nonce = Arrays.copyOfRange(blockByte, Configure.VERSIONSIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SIZEOFLONG,
                Configure.VERSIONSIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SIZEOFLONG + Configure.SIZEOFLONG);          //8
        byte[] blockSize = Arrays.copyOfRange(blockByte, Configure.VERSIONSIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SIZEOFLONG + Configure.SIZEOFLONG,
                Configure.VERSIONSIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SHA256SIZE + Configure.SIZEOFLONG + Configure.SIZEOFLONG + Configure.SIZEOFINT);       //4

        block.setVersion(Utils.readBytes(version));
        block.setPrevBlock(Utils.readBytes(prevBlock));
        block.setHash(Utils.readBytes(hash));
        block.setTarget(Utils.readBytes(target));
        block.setMerkleRoot(Utils.readBytes(merkleRoot));
        block.setTimestamp(Utils.byteToLong(timestamp));
        block.setNonce(Utils.byteToLong(nonce));
        block.setBlockSize(Utils.bytesToInt(blockSize,0));

        return block;
    }

    public static byte[] longToBytes(long data)
    {
        byte[] bytes = new byte[8];
        bytes[7] = (byte) (data & 0xff);
        bytes[6] = (byte) ((data >> 8) & 0xff);
        bytes[5] = (byte) ((data >> 16) & 0xff);
        bytes[4] = (byte) ((data >> 24) & 0xff);
        bytes[3] = (byte) ((data >> 32) & 0xff);
        bytes[2] = (byte) ((data >> 40) & 0xff);
        bytes[1] = (byte) ((data >> 48) & 0xff);
        bytes[0] = (byte) ((data >> 56) & 0xff);
        return bytes;
    }

    public static long byteToLong(byte[] b) {
        long s = 0;
        long s0 = b[0] & 0xff;
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
        long s4 = b[4] & 0xff;
        long s5 = b[5] & 0xff;
        long s6 = b[6] & 0xff;
        long s7 = b[7] & 0xff;
        s6 <<= 8;
        s5 <<= 16;
        s4 <<= 24;
        s3 <<= 8 * 4;
        s2 <<= 8 * 5;
        s1 <<= 8 * 6;
        s0 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }

    public static byte[] intToBytes(int data)
    {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data & 0xff00) >> 8);
        bytes[2] = (byte) ((data & 0xff0000) >> 16);
        bytes[3] = (byte) ((data & 0xff000000) >> 24);
        return bytes;
    }

    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ( ((src[offset+3] & 0xFF)<<24)
                |((src[offset+2] & 0xFF)<<16)
                |((src[offset+1] & 0xFF)<<8)
                |(src[offset] & 0xFF));
        return value;
    }

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
     */
    public static byte[] add(byte[] data1, byte[] data2) {

        byte[] result = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, result, 0, data1.length);
        System.arraycopy(data2, 0, result, data1.length, data2.length);

        return result;
    }

    /**
     * 将哈希结果转变为十六进制字符串：new String(Hex.encode(hash))
     */
    public static String byteToHex(byte[] hash){
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
     * byte[]转String
     */
    public static String byteToString(byte[] b){
        String s="";
        for(int i=0;i<b.length;i++){
            s=s+b[i];
        }
        return s;
    }

    /**
     * BigInteger转String
     */
    public static String bigIntegerToString(BigInteger b){
        String s="";
        byte[] by=b.toByteArray();
        for(int i=0;i<by.length;i++){
            s=s+by[i];
        }
        return s;
    }

    /**
     * BigInteger转byte[]
     */
    public static byte[] bigIntegerToByte(BigInteger b){
        return bigIntegerToString(b).getBytes();
    }

    public static ByteBuffer arrayToBuffer(int[] array)
    {
        ByteBuffer result = ByteBuffer.allocate(array.length);
        for (int i = 0; i < array.length; ++i)
            result.put((byte)(array[i] & 0xff));
        result.flip();
        return result;
    }
}
