package src;

import UTXO.Transaction;
import UTXO.UTXO;
import crypto.Hash;

import java.util.ArrayList;

public class BlockChain {
    public ArrayList<Block> blockChain;
    public UTXO utxo;

    public BlockChain(){
        this.blockChain = new ArrayList<Block>();
        this.blockChain.add(GenesisBlock());
        utxo=new UTXO(this.blockChain);
    }

    /**
     * 创世区块
     * @return
     */
    public Block GenesisBlock(){
        Block genesisBlock = new Block(Hash.encodeSHA256Hex("Nchain starts".getBytes()));
        genesisBlock.transaction.add(Transaction.genesisTransaction());
        genesisBlock.setAll();
        return genesisBlock;
    }

    public void addBlock(Block block){
        block.setAll();
        blockChain.add(block);
        utxo.addUTXO(block);
    }

    public void addBlock(ArrayList<Transaction> trans){
        Block block = new Block(blockChain.get(blockChain.size()-1).hash, trans);
        block.setAll();
        blockChain.add(block);
        utxo.addUTXO(block);
    }

    public String prevBlock(){
        return this.blockChain.get(this.blockChain.size()-1).hash;
    }

    public void printBlockChain(){
        for(Block b : this.blockChain){
            b.printBlock();
        }
    }

    public Block get(int index){
        return this.blockChain.get(index);
    }

    public void test(){
    //public static void main(String[] args) {
        BlockChain test = new BlockChain();
        ArrayList<Transaction> arr=new ArrayList<Transaction>();
        arr.add(Transaction.generateTransaction(test.blockChain.get(0).transaction.get(0).getTxId()));
        test.addBlock(arr);
        test.printBlockChain();
    }
}
