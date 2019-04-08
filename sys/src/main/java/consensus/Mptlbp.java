package consensus;

import crypto.BilinearPairing;
import crypto.Mpts;
import crypto.Vrf;
import it.unisa.dia.gas.jpbc.Element;
import net.Producer;
import sys.Block;
import sys.Configure;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Mptlbp implements Consensus {
    private Element sk;
    BilinearPairing bp;
    Map<String, Producer> producerMap;

    public Mptlbp(){
        bp = new BilinearPairing();
        sk = bp.Zr.newRandomElement().getImmutable();      // 生成Mtplbp主密钥
    }

    /**
     * generate block
     * @param block
     */
    public void run(Block block){
        Vrf vrf = new Vrf(block.getP(),block.getM());
        vrf.run(sk.duplicate());
        BigInteger target = BigInteger.valueOf(1).shiftLeft(256-(int)Configure.TARGET_BITS).subtract(BigInteger.valueOf(1));//难度值

        //此方法返回-1，0或1，分类为BigInteger在数字上小于，等于，或大于值val。
        if(vrf.getHash().toBigInteger().compareTo(target)==1){
            Element pk = block.getP().mulZn(sk.duplicate()).getImmutable();
            block.setVrfPk(pk.toBytes());
            block.setVrfHash(vrf.getHash().toBytes());
            block.setVrfProof(vrf.getProof().toBytes());
        }else{
            System.out.println("You are not the miner in this time slice.");
        }
    }

    /**
     * Function: proxy sign block
     * 1. check vrf
     * 2. run mpts: check threshold, sign block
     * @param block
     * @return
     */
    public boolean proxySig(Block block){
        Vrf vrf = new Vrf(block.getP(),block.getM());
        if(vrf.verify(block.getVrfHash(),block.getVrfProof(),block.getVrfPk())){
            Mpts mpts = new Mpts(block.getP(), block.getM());
            mpts.run(sk.duplicate(),block.getPks(),block.getSignatures(),Configure.DELEGATES);
            return true;
        }else{
            return false;
        }
    }

    /**
     * verify block(proxy node)
     * check block, sign block, check multi proxy signatures
     * 1. check block hash, prev-block
     * 2. proxySig: check vrf, sign block
     * @param prevBlock
     * @param block
     * @return
     */
    public boolean proxyVerify(String prevBlock, Block block){
        /**
         * verify the block
         * 1.verify prevBlock hash
         * 2.verify merkleroot: Because there is no transaction when produce, so we don't check merkleRoot here
         * 3.verify hash
         * 4.VRF verify
         * 5.sign by mpts
         */
        if(prevBlock.equals(block.getPrevBlock()) &&
                //block.calMerkleRoot().equals(block.getMerkleRoot())&&
                (block.calHash()).equals(block.getHash()) &&
                proxySig(block)){

            // produce block
            producerMap = new HashMap<String, Producer>();
            String prefix = "/" + Configure.blockNDNGetBlockPrefix + block.getPrevBlock();
            Producer producer = new Producer(block, prefix); //挖到新区块，创建生产者
            Thread thread = new Thread(producer);
            thread.start();
            producerMap.put(prefix, producer);

            return true;
        }
        return false;
    }

    /**
     * verify block(any node)
     * check multi proxy signatures
     * @param prevBlock
     * @param block
     * @return
     */
    public boolean verify(String prevBlock, Block block) {
        Mpts mpts = new Mpts(block.getP(), block.getM());

        // proxy node run
        if(proxyVerify(prevBlock,block)){
            System.out.println("Sign block.");
        };

        if(mpts.verify(block.getPks(), block.getSignatures(), Configure.DELEGATES)) {
            System.out.println("Mpts Verify Sucess!");
            return true;
        }else{
            System.out.println("Mpts Verify Failed!");
            return false;
        }
    }
}
