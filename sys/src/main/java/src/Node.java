package src;

import Consensus.Pow;
import UTXO.Transaction;

public class Node {
    public BlockChain blockChain;
    //Map<String, Producer> producerMap;

    public Node(){
        blockChain = new BlockChain();
    }

    public void mining(){
        Block block = new Block(this.blockChain.prevBlock());
        //生成一笔正确的交易
        Transaction trans = Transaction.generateTransaction(this.blockChain.blockChain.get(0).transaction.get(0).getTxId());
        //验证交易合法性，只有交易合法时才打包进区块
        if(Transaction.verifyTransaction(trans, this.blockChain.utxo)){
            block.addTransaction(trans);
        }
        Pow.run(block);
        /*
        System.out.println("[***LOCAL***] A new block comes out: [SUCCESS] : {Time}:" + System.currentTimeMillis());
        System.out.println("                                               : {Transaction}:" );
        Transaction.printTransactions(block.transaction));
        System.out.println("                                               : {Nonce};" + block.getNonce());
        String prefix = String.join("/", Configure.blockNDNGetBlockPrefix, Utils.bytesToHexString(block.getPrevBlock());
        Producer producer = new Producer(block, prefix); //挖到新区块，创建生产者
        Thread thread = new Thread(producer);
        thread.start();
        producerMap.put(prefix, producer);
        */
        blockChain.addBlock(block);
    }

    public boolean verifyBlock(Block block){
        return Pow.verify(block);
    }

    public void test(){
    //public static void main(String[] args){
        Node n = new Node();
        n.mining();
        n.blockChain.printBlockChain();
        System.out.println(n.verifyBlock(n.blockChain.get(1)));
    }
}
