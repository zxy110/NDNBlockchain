package xy;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * GitHub测试
 */
public class Block {
	protected byte[] prevBlock;	//32
	protected int blockSize;	//4	
	protected long time;		//8
	protected long nonce;		//8
	protected String s;			//交易
	
	public Block(){
		this.prevBlock = new byte[Configure.SHA256SIZE];
		this.blockSize =  Configure.INITBLOCKSIZE;  //52Byte 最大是64，所以交易最多是12B
		this.time = 0;
		this.s = "";
	}
	
	public Block(byte[] prevBlock,long time,long nonce,String s){
		if(prevBlock.length == Configure.SHA256SIZE){
			this.prevBlock = Arrays.copyOf(prevBlock, prevBlock.length);
			this.time = time;
			this.nonce = nonce;
			this.s = s;
			this.blockSize = Configure.INITBLOCKSIZE + this.s.length();
		}
	}
	
	public Block(byte[] prevBlock,int blockSize, long time,long nonce,String s){
		if(prevBlock.length == Configure.SHA256SIZE){
			this.prevBlock = Arrays.copyOf(prevBlock, prevBlock.length);
			this.blockSize = blockSize;
			this.time = time;
			this.nonce = nonce;
			this.s = s;
		}
	}
	
	public int getBlockSize(){
		return this.blockSize;
	}
	
	public byte[] getBlockPrevHash(){
		return this.prevBlock;
	}
	
	public long getBlockNonce(){
		return this.nonce;
	}
	
	public String getBlockTxs(){
		return this.s;
	}
	
	public long getBlockTime(){
		return this.time;
	}
/*	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		byte[] prevBlock = new byte[src.Configure.SHA256SIZE];
		int time = 0;
		int nonce = 0;
		String s = "11 + 11 = 12";
		src.Block block = new src.Block(prevBlock,time,nonce,s);
		System.out.println(block.s);
		System.out.println(block.blockSize);
	}
*/
}
