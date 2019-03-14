package src;

import Consensus.Pow;

import java.math.BigInteger;

public class Node {
    public BlockChain blockChain;

    public Node(){
        blockChain = new BlockChain();
    }

    public void mining(){
        Block block = new Block(this.blockChain.prevBlock());
        block.addTransaction("Sam transfer 2RMB to Sarah");
        Pow.run(block);
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
