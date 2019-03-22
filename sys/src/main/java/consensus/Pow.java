package consensus;

import java.math.BigInteger;
import src.Block;


public class Pow implements Consensus{

    public Pow(){}

    public void run(Block block){
        BigInteger target = block.calTarget();          //难度值
        while(true){
            BigInteger hash = new BigInteger(block.calHash(), 16);
            if(hash.compareTo(target) == -1){           //如果哈希值小于难度值，返回-1
                break;
            }else{
                block.setTimestamp();
                if(block.getNonce() < Long.MAX_VALUE){
                    block.setNonce(block.getNonce() + 1);
                }
                else{ block.initNonce(); }
            }
        }
    }

    public boolean verify(String prevBlock, Block block){
        boolean result=false;
        /**
         * verify the block
         * 1.verify prevBlock hash
         * 2.verify merkleroot: Because there is no transaction when produce, so we don't check merkleRoot here
         * 3.verify hash
         * 4.verify nonce
         */
        if(prevBlock.equals(block.getPrevBlock()) &&
                //block.calMerkleRoot().equals(block.getMerkleRoot())&&
                (block.calHash()).equals(block.getHash()) &&
                (new BigInteger(block.getHash(), 16)).compareTo(Block.calTarget()) == -1){
            result=true;
        }
        return result;
    }


}
