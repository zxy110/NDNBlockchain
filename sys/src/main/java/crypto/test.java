package crypto;

import xy.Utils;

import java.math.BigInteger;

public class test {
    /**
     * test Hash
     */
    public static void testHash() {
        try{
            Hash test = new Hash();
            String t1 = test.encodeSHA256Hex("Hello".getBytes());
            System.out.println(t1);
            System.out.println(t1.length());
            String t2=test.encodeRipeMD160Hex("Hello".getBytes());
            System.out.println(t2);
            System.out.println(t2.length());
            byte[] b1=test.encodeSHA256("Hello".getBytes());
            byte[] b2=test.encodeRipeMD160("Hello".getBytes());
            for(int i=0;i<b1.length;i++){
                System.out.print(b1[i]);
            }
            System.out.println();
            for(int i=0;i<b2.length;i++){
                System.out.print(b2[i]);
            }
            System.out.println();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * test secp256k1
     */
    public void testSecp256k1(){
        try {
            // generate keys
            Secp256k1 test1 = new Secp256k1();
            test1.generateKeypair();

            Secp256k1 test2 = new Secp256k1();
            test2.generateKeypair();

            // data
            byte[] data = new byte[1000];
            for (int i = 0; i < data.length; i++) {
                data[i] = 0xa;
            }

            // signature with privateKey
            byte[] sign1 = test1.signData(data);
            byte[] sign2 = test1.signData(data);

            // verify when data is changed
            boolean v1 = test1.verifySign(data, sign1);
            data[1] = 0xb;
            boolean v2 = test1.verifySign(data, sign1);
            data[1] = 0xa;
            System.out.println(v1);
            System.out.println(v2);

            // verify when signature is changed
            boolean v3 = test1.verifySign(data, sign2);
            sign2[20] = (byte)~sign1[20];
            boolean v4 = test1.verifySign(data, sign2);
            System.out.println(v3);
            System.out.println(v4);

            // verify using other public keys
            boolean v5 = test2.verifySign(data, sign1);
            System.out.println(v5);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成地址
     */
    public String generateAddress(){
       try{
           //产生新的公私钥对
           //Secp256k1.generateKeypair();
           //Secp256k1.saveKeypair();

           //公钥
           byte[] publicKey = new BigInteger("044dd258cc3e050b570299ef45de5d96e524051096a2a9ae52d22ba8927b167fcef297f35a0de8b7c5789264d2de858dc8582c39368c399fd91dc5a92c33d85aa1", 16).toByteArray();

           //用SHA256和RipeMD160加密
           byte[] sha256Bytes = Hash.encodeSHA256(publicKey);
           byte[] ripemd160Bytes = Hash.encodeRipeMD160(sha256Bytes);

           //添加版本前缀0x00
           byte[] newworkID = new BigInteger("00",16).toByteArray();
           byte[] extendRipemd160Bytes = IOUtils.add(newworkID,ripemd160Bytes);

           //添加地址校验码（两次哈希，取前4位添加到地址后面）
           byte[] twiceSha256Bytes = Hash.encodeSHA256(Hash.encodeSHA256(extendRipemd160Bytes));
           byte[] checkSum = new byte[4];
           System.arraycopy(twiceSha256Bytes, 0, checkSum, 0, 4);
           byte[] binaryAddressBytes = IOUtils.add(extendRipemd160Bytes,checkSum);

           //用Base58生成58字节地址
           String bitcoinAddress = Base58.encode(binaryAddressBytes);

           //test
           //System.out.println(Utils.bytesToHexString(binaryAddressBytes));
           System.out.println("bitcoinAddress: "+bitcoinAddress);

           return bitcoinAddress;
       }catch(Exception e){
           e.printStackTrace();
           return "";
       }
    }
}