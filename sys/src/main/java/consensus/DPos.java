package consensus;

import src.Block;

public class DPos implements Consensus {

    public DPos(){}

    public void run(Block block){
        while(System.currentTimeMillis() % 20!=0);
    }

    public boolean verify(String prevBlock, Block block){
        boolean result=false;
        /**
         * verify the block
         * 1.verify prevBlock hash
         * 2.verify merkleroot: Because there is no transaction when produce, so we don't check merkleRoot here
         * 3.verify hash
         */
        if(prevBlock.equals(block.getPrevBlock()) &&
                //block.calMerkleRoot().equals(block.getMerkleRoot())&&
                (block.calHash()).equals(block.getHash()) ){
            result=true;
        }
        return result;
    }

}
