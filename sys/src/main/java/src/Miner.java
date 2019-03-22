package src;

import consensus.*;
import net.Producer;
import utxo.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Miner implements Runnable{
    private BlockChain blockChain;
    Map<String, Producer> producerMap;

    public Miner(){
        blockChain = new BlockChain();
        producerMap = new HashMap<String, Producer>();
    }

    public Miner(BlockChain bchain, Map<String, Producer> producerMap){
        this.blockChain = bchain;
        this.producerMap = producerMap;
    }

    //mining
    public void run(){
        Block block = new Block(this.blockChain.getLatestBlock());
        //生成一笔正确的交易
        Transaction trans = Transaction.generateTransaction(this.blockChain.getBlockChain().get(0).getTransaction().get(0).getTxId());
        //验证交易合法性，只有交易合法时才打包进区块
        if(Transaction.verifyTransaction(trans, this.blockChain.getUtxo())){
            block.addTransaction(trans);
        }
        //consensus factory
        ConsensusFactory cFac = new ConsensusFactory();
        cFac.getConsensus(Configure.Consensus).run(block);
        block.setAll();
        // test
        //this.blockChain.addBlock(block);

        // print new block
        System.out.println("[***LOCAL***] A new block comes out");
        System.out.println("             {Hash}:" + block.getHash());
        System.out.println("             {Time}:" + block.getTimestamp());
        System.out.println("             {Nonce}:" + block.getNonce());
        System.out.println("             {MerkleRoot}:" + block.getMerkleRoot());
        System.out.println("             {Transaction}:" + block.getTransaction().size());
        //System.out.println("              {Transaction}:" );
        //Transaction.printTransactions(block.transaction);
        // produce block
        String prefix = "/" + Configure.blockNDNGetBlockPrefix + block.getPrevBlock();
        Producer producer = new Producer(block, prefix); //挖到新区块，创建生产者
        Thread thread = new Thread(producer);
        thread.start();
        producerMap.put(prefix, producer);
    }


    public void test(){
    //public static void main(String[] args){
        Miner n = new Miner();
        n.run();
        ArrayList<Block> bChain = n.blockChain.getBlockChain();
        //consensus factory
        ConsensusFactory cFac = new ConsensusFactory();
        System.out.println(cFac.getConsensus(Configure.Consensus).verify(bChain.get(bChain.size()-2).getHash(),bChain.get(bChain.size()-1)));
        //n.blockChain.printBlockChain();
        n.run();
        System.out.println(cFac.getConsensus(Configure.Consensus).verify(bChain.get(bChain.size()-2).getHash(),bChain.get(bChain.size()-1)));
    }
}
