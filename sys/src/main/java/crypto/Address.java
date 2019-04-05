package crypto;

import src.*;

import java.math.BigInteger;
import java.nio.ByteBuffer;

public class Address {
    /**
     * 生成地址
     */
    public static String getAddress(){
        try{
            //产生新的公私钥对
            //Secp256k1.generateKeypair();
            //Secp256k1.saveKeypair();

            //公钥
            byte[] publicKey = new BigInteger(Configure.PUBLIC_KEY, 16).toByteArray();

            //用SHA256和RipeMD160加密
            byte[] sha256Bytes = Hash.encodeSHA256(publicKey);
            byte[] ripemd160Bytes = Hash.encodeRipeMD160(sha256Bytes);

            //添加版本前缀0x00
            byte[] newworkID = new BigInteger("00",16).toByteArray();
            byte[] extendRipemd160Bytes = Utils.add(newworkID,ripemd160Bytes);

            //添加地址校验码（两次哈希，取前4位添加到地址后面）
            byte[] twiceSha256Bytes = Hash.encodeSHA256(Hash.encodeSHA256(extendRipemd160Bytes));
            byte[] checkSum = new byte[4];
            System.arraycopy(twiceSha256Bytes, 0, checkSum, 0, 4);
            byte[] binaryAddressBytes = Utils.add(extendRipemd160Bytes,checkSum);

            //用Base58生成58字节地址
            String bitcoinAddress = Base58.encode(binaryAddressBytes);

            //test
            //System.out.println(Utils.bytesToHexString(binaryAddressBytes));
            //System.out.println("bitcoinAddress: "+bitcoinAddress);
            return bitcoinAddress;
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }


    public void test(){
    //public static void main(String[] args){
        byte[] publicKey = new BigInteger(Configure.PUBLIC_KEY, 16).toByteArray();
        for(int i=0;i<publicKey.length;i++){
            System.out.print(publicKey[i]+" ");
        }
        ByteBuffer byteBuffer = Configure.getPublicKey();
        System.out.println(byteBuffer.get(0));
        byte[] pub = Secp256k1.readPublicKey("Alice").getEncoded();
        for(int i=0;i<pub.length;i++){
            System.out.print(pub[i]+" ");
        }
        System.out.println();
        BigInteger bigInteger = new BigInteger(Configure.PUBLIC_KEY, 16);
        System.out.println(bigInteger);
    }
}
