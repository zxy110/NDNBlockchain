package xy;

import java.util.Map;

public class Miner implements Runnable{
	byte[] prevHash;
	BlockChainProxy bchainProxy;
	Map<String, Producer> producerMap;
	
	public Miner(byte[] prevHash){
		this.prevHash = prevHash;
	}
	
	public Miner(){
		prevHash = null;
	}
	
	public Miner(byte[] prevHash, BlockChainProxy bchainProxy, Map<String, Producer> producerMap){
		this.bchainProxy = bchainProxy;
		this.prevHash = prevHash;
		this.producerMap = producerMap;
	}

	@Override
	public void run() {
		
	//	while(true){				
	//		if(Arrays.equals(prevHash, bchainProxy.getPreviousHash())){
	//			continue;}
		
		Mining newBlock = new Mining(prevHash);
		//createNewBlock take a lot of time, getPreviousHash might has updated when the miner dig a new block.
		//	TODO Mining/Miner is a process ,rather than a thread
		// Different nodes have different String txs: 11+11/12+12/...
		if(newBlock.createNewBlock("10 +  7 = 17")){ //如果挖到了区块
			if(newBlock.isBlockLegal(newBlock.getMiningBlock(),bchainProxy.getPreviousHash())){				
				Block miningANewBlock = newBlock.getMiningBlock();
				System.out.println("[***LOCAL***] A new block comes out: [SUCCESS] : {Time}:" + System.currentTimeMillis());
				System.out.println("                                               : {Transaction}:" + miningANewBlock.s );
				System.out.println("                                               : {Nonce};" + miningANewBlock.nonce);
				String prefix = String.join("/", Configure.blockNDNGetBlockPrefix,Utils.bytesToHexString(miningANewBlock.prevBlock));
				Producer producer = new Producer(miningANewBlock, prefix); //挖到新区块，创建生产者
				Thread thread = new Thread(producer);
				thread.start();
				producerMap.put(prefix, producer);		
			}
		}
	}
}

