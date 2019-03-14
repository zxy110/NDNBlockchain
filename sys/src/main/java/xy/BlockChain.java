package xy;

import java.util.Vector;

public class BlockChain {
	private Vector<Block> blockChain;  //区块数组
	private byte[] genesisHash;   
	
	public BlockChain(){
		//Create a best chain, loading genesis block, notice that the program cannot respond folks. 
		this.blockChain = new Vector<Block>();
		Block genesisBlock = genesisBlockLoad();
		this.genesisHash = Utils.digestsha256(genesisBlock);
		this.blockChain.addElement(genesisBlock);
	}
	
	public void addBlock(Block newBlock){
		if(this.getBlockChainCapacity() > 0)
			this.blockChain.addElement(newBlock);
	}
	
	public Block getBlockFromBlockChain(int index){
		return blockChain.elementAt(index);
	}
	
	public int getBlockChainLength(){
		return this.blockChain.size();
	}
	
	public int getBlockChainCapacity(){
		return this.blockChain.capacity();
	}
	
	public void clearBlockChain(){
		this.blockChain.clear();
	}
	
	public Block getLastLocalBlock(){
		return this.blockChain.lastElement();
	}
	
	public byte[] getGenesisHash(){
		return this.genesisHash;
	}
	
	public Block genesisBlockLoad(){
		String genesisString = "BlockNDN: start now";
		long time = 20161209;
		long nonce = 0;
		String s = "Kailey";
		Block genesisBlock = new Block(Utils.digestsha256(genesisString) , time , nonce , s);
		return genesisBlock;
	}
}
