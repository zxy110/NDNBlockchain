package consensus;

import old.VRF;
import src.Block;

import java.math.BigInteger;

public class Mptlbp implements Consensus {

    VRF vrf;

    public Mptlbp(){
        vrf = new VRF();
    }

    public void run(Block block){
        //cal VRF
        BigInteger target = block.calTarget();          //难度值
        vrf.buildSystem();
        vrf.encryption();
        if(vrf.getHash().toBigInteger().compareTo(target)==1){     //此方法返回-1，0或1，分类为BigInteger在数字上小于，等于，或大于值val。

        }else{
            System.out.println("You are not the miner in this time slice.");
        }
    }

    public boolean verify(String prevBlock, Block block){
        boolean result=false;
        /**
         * verify the block
         * 1.verify prevBlock hash
         * 2.verify merkleroot: Because there is no transaction when produce, so we don't check merkleRoot here
         * 3.verify hash
         * 4.VRF verify
         */
        if(prevBlock.equals(block.getPrevBlock()) &&
                //block.calMerkleRoot().equals(block.getMerkleRoot())&&
                (block.calHash()).equals(block.getHash()) &&
                this.vrf.verify()){
            result=true;
        }
        return result;
    }

}
