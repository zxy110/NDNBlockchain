package xy;

import java.util.Arrays;

public class BlockChainProxy implements CheckBlock {

	protected BlockChain bChain;
		
	public BlockChainProxy(){
		bChain  =  new BlockChain();
	}
	
	public BlockChainProxy(BlockChain bChain){
		this.bChain = bChain;
	}
	
	public byte[] getBlockChainGenesisHash(){
		return bChain.getGenesisHash();
	}
	
	public Block getBlock(byte[] PreviousBlock){
		for(int i = 0; i < bChain.getBlockChainLength(); i++)
			if(Arrays.equals(PreviousBlock, bChain.getBlockFromBlockChain(i).prevBlock))
				return bChain.getBlockFromBlockChain(i);
		return null;
	}
	
	public boolean addBlock(Block block){  
		if(isBlockLegal(block,getPreviousHash())){
			bChain.addBlock(block);  //blockchain类才是真的区块链类，
			return true;
		}
		return false;
	}
	
	public byte[] getPreviousHash(){
		return Utils.digestsha256(bChain.getLastLocalBlock());
	}
	
	public int getBlockHeight(){
		return bChain.getBlockChainLength();
	}

	//@Override
	public boolean isBlockLegal(Block block , byte[] prevBlockHash) {
		// TODO Auto-generated method stub
		boolean checkSize , checkPrevB , checkData , checkPOW;	//验证大小是否超过了最大，是否对应区块hash，数据是否合法，pow是否满足
		// if the previous block is genesis block, only need to check src.Block's Proof-of-Work
		checkPOW = !CheckBlockUtils.NrawCheckProofOfWork(block);
		if(Arrays.equals(block.prevBlock, this.bChain.getGenesisHash())){
			return checkPOW;
		}
		checkSize = CheckBlockUtils.checkBlockSize(block);
		checkPrevB = CheckBlockUtils.checkPrevHash(prevBlockHash, block);
		checkData = CheckBlockUtils.checkData(block.s);
		return checkSize && checkPrevB && checkData && checkPOW;
	}
	
}
