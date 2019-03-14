package xy;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
	
    public static String bytesToHexString(byte[] src){       
        StringBuilder stringBuilder = new StringBuilder();       
        if (src == null || src.length <= 0) {       
            return null;       
        }       
        for (int i = 0; i < src.length; i++) {       
            int v = src[i] & 0xFF;       
            String hv = Integer.toHexString(v);       
            if (hv.length() < 2) {       
                stringBuilder.append(0);       
            }       
            stringBuilder.append(hv);       
        }       
        return stringBuilder.toString();       
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
    
    public static ByteBuffer arrayToBuffer(int[] array)
    {
      ByteBuffer result = ByteBuffer.allocate(array.length);
      for (int i = 0; i < array.length; ++i)
        result.put((byte)(array[i] & 0xff));
      result.flip();
      return result;
    }
    
	public static byte[] blockToByteArray(Block newBlock){
		byte[] byteArray = new byte[newBlock.blockSize];
		byte[] blockSize = intToBytes(newBlock.blockSize);
		byte[] time = longToBytes(newBlock.time);
		byte[] nonce = longToBytes(newBlock.nonce);
		byte[] string = newBlock.s.getBytes();
		
		boolean prevBlockCopy = byteArrayCopy(byteArray,newBlock.prevBlock,0,newBlock.prevBlock.length);
		boolean blockSizeCopy = byteArrayCopy(byteArray,blockSize,newBlock.prevBlock.length,newBlock.prevBlock.length+blockSize.length);
		boolean timeCopy = byteArrayCopy(byteArray,time,newBlock.prevBlock.length+blockSize.length,
								newBlock.prevBlock.length+blockSize.length+time.length);
		boolean nonceCopy = byteArrayCopy(byteArray,Utils.longToBytes(newBlock.nonce),newBlock.prevBlock.length+blockSize.length+time.length,
								newBlock.prevBlock.length+blockSize.length+time.length+nonce.length);
		boolean stringCopy = byteArrayCopy(byteArray,string,newBlock.prevBlock.length+blockSize.length+time.length+nonce.length,
				newBlock.prevBlock.length+blockSize.length+time.length+nonce.length+string.length);
		
		if (prevBlockCopy && blockSizeCopy && timeCopy && nonceCopy && stringCopy){
			return byteArray;
		}
		else 
			return new byte[Configure.INITBLOCKSIZE];
	}
	
	public static boolean byteArrayCopy(byte[] array,byte[] bytea,int from,int to){
		int arraySpace = to - from;
		int byteaSpace = bytea.length;
		if (arraySpace < byteaSpace){
			System.out.println("Error: bytArrayCopy : Space is not enough");
			return false;
		}
		int i,j;
		for (i = from,j = Configure.OFFSETINIT ; i < to && j < byteaSpace ; i++,j++)
				array[i] = bytea[j];
		return true;
	}
	
	public static byte[] digestsha256(Block block){
		byte[] newBlockByte;
		byte[] blockHash = null;	
		try{
			newBlockByte = Utils.blockToByteArray(block); 
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");  
			messageDigest.update(newBlockByte, Configure.OFFSETINIT , newBlockByte.length);   //从0到最后
			blockHash = messageDigest.digest();  
		}
		catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		return blockHash;
	}

	public static byte[] digestsha256(String s){
		byte[] blockHash = null;	
		try{
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");  
			messageDigest.update(s.getBytes(), Configure.OFFSETINIT,s.length() );
			blockHash = messageDigest.digest();  
		}
		catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		return blockHash;
	}
	
/*	public static void main(String[] args){
		long a = 64;
		byte[] ba = Utils.longToBytes(a);
		long aa = Utils.byteToLong(ba);
	}
*/	
}
