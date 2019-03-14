package xy;

import java.util.Arrays;
import java.util.Random;

public class Mining implements CheckBlock{

	private Block block;
	private boolean result;
	
	public Mining(){	
		block = new Block();
	}
	
	public Mining(byte[] prevHash){
		block = new Block();
		block.prevBlock = Arrays.copyOf(prevHash, prevHash.length);
	}
	
	public boolean createNewBlock(String txs){		
		//TODO: File I/O, fetch txs from file. 
		block.s = txs;
		block.blockSize = Configure.INITBLOCKSIZE + block.s.length();
		if (!CheckBlockUtils.checkData(txs)) return false;
		
		boolean flag = false;
		while(!flag){
			block.time = System.currentTimeMillis();
			Random randomNonce = new Random(block.time); //block.time is seed
			block.nonce = randomNonce.nextLong();
			flag = miningBlock();
		}
		return true;
	}
	
	protected boolean miningBlock() {
		long max = Long.MAX_VALUE;
		long min = Long.MIN_VALUE;
		while(CheckBlockUtils.NrawCheckProofOfWork(block)){   //use function POW to check whether the block is legal. if legal, return false
			if(block.nonce < max-2 && block.nonce > min)
				block.nonce++;
			else
				return false;
		}
		return true;
	}
	
	public Block getMiningBlock(){
		return this.block;
	}
	
	public boolean getResult(){
		return result;
	}
	
	@Override //验证区块是否合法
	public boolean isBlockLegal(Block block, byte[]prevBlockHash) {
		// TODO Auto-generated method stub
		boolean checkSize,checkPrevB = false,checkData = false;
		//blockSize should be valued between INITBLOCKSIZE and MAXBLOCKSIZE; 
		checkSize = CheckBlockUtils.checkBlockSize(block);
		//previous block hash = block.prevBlock;
		checkPrevB = CheckBlockUtils.checkPrevHash(prevBlockHash, block);
		//check data
		checkData = CheckBlockUtils.checkData(block.s);
		return checkSize && checkPrevB && checkData;
	}

/*	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
*/	
}
