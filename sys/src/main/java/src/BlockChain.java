package src;

import utxo.Transaction;
import utxo.UTXO;
import crypto.Hash;

import java.util.ArrayList;

public class BlockChain {
    private ArrayList<Block> blockChain;
    private UTXO utxo;

    public BlockChain(){
        this.blockChain = new ArrayList<Block>();
        this.blockChain.add(GenesisBlock());
        utxo=new UTXO(this.blockChain);
    }

    public ArrayList<Block> getBlockChain() {
        return blockChain;
    }

    public void setBlockChain(ArrayList<Block> blockChain) {
        this.blockChain = blockChain;
    }

    public UTXO getUtxo() {
        return utxo;
    }

    public void setUtxo(UTXO utxo) {
        this.utxo = utxo;
    }

    /**
     * 创世区块
     * @return
     */
    public Block GenesisBlock(){
        Block genesisBlock = new Block(Hash.encodeSHA256Hex("Nchain starts".getBytes()));
        genesisBlock.getTransaction().add(Transaction.genesisTransaction(1552888105078l));
        genesisBlock.setTimestamp(1552888105078l);
        genesisBlock.setNonce(12345l);
        genesisBlock.setAll();
        return genesisBlock;
    }

    public void addBlock(Block block){
        blockChain.add(block);
        utxo.addUTXO(block);
    }

    public void addBlock(ArrayList<Transaction> trans){
        Block block = new Block(blockChain.get(blockChain.size()-1).getHash(), trans);
        block.setAll();
        blockChain.add(block);
        utxo.addUTXO(block);
    }

    public String getLatestBlock(){
        return this.blockChain.get(this.blockChain.size()-1).getHash();
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
        arr.add(Transaction.generateTransaction(test.blockChain.get(0).getTransaction().get(0).getTxId()));
        test.addBlock(arr);
        test.printBlockChain();
    }
}
