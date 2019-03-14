package src;

import crypto.Hash;

import java.util.ArrayList;

public class BlockChain {
    public ArrayList<Block> blockChain;

    public BlockChain(){
        blockChain = new ArrayList<Block>();
        blockChain.add(GenesisBlock());
    }

    /**
     * 创世区块
     * @return
     */
    public Block GenesisBlock(){
        Block genesisBlock = new Block(Hash.encodeSHA256Hex("Nchain starts".getBytes()));
        genesisBlock.transaction.add("Genesis tranfer 2RMB to Sam");
        genesisBlock.setAll();
        return genesisBlock;
    }

    public void addBlock(Block block){
        block.setAll();
        blockChain.add(block);
    }

    public void addBlock(ArrayList<String> trans){
        Block block = new Block(blockChain.get(blockChain.size()-1).hash, trans);
        block.setAll();
        blockChain.add(block);
    }

    public String prevBlock(){
        return this.blockChain.get(this.blockChain.size()-1).hash;
    }

    public void printBlockChain(){
        for(Block b : blockChain){
            b.printBlock();
        }
    }

    public Block get(int index){
        return this.blockChain.get(index);
    }

    public void test(){
    //public static void main(String[] args) {
        BlockChain test = new BlockChain();
        ArrayList<String> trans=new ArrayList<String>();
        trans.add("Sarah tranfer 2RMB to Jam");
        test.addBlock(trans);
        test.printBlockChain();
    }
}
