package xy;

import java.util.Map;
import java.util.concurrent.ExecutorService;

public class VolumeProduction {
	
//	public BlockChainProxy bchainProxy;
//	public ExecutorService executor;
//	public Map<String, Producer> producerMap;	
	
//	public VolumeProduction(BlockChainProxy bchainProxy, ExecutorService executor, Map<String, Producer> producerMap){
//		this.bchainProxy = bchainProxy;
//		this.executor = executor;
//		this.producerMap = producerMap;
//	}
	
	public void initVolumeProduction(BlockChainProxy bchainProxy, ExecutorService executor, Map<String, Producer> producerMap){
		int height = bchainProxy.getBlockHeight();
		// Start begin genesis block;
		byte[] prevBlockHash = bchainProxy.getBlockChainGenesisHash();
		Block block = bchainProxy.getBlock(prevBlockHash);
		if(height > 1){
			while(block != null){		
				String prefix = String.join("/", Configure.blockNDNGetBlockPrefix, new String(prevBlockHash));
				if(!producerMap.containsKey(prefix)){     //  如果前缀没有被包含在历史区块中map
					Producer producer = new Producer(block, prefix);   //将该区块和前缀绑定
					executor.execute(producer);
					producerMap.put(prefix, producer);	
				}
				// No matter whether <prefix, Producer> has exist, move to next src.Block;
				prevBlockHash = Utils.digestsha256(block);
				block = bchainProxy.getBlock(prevBlockHash);
			}
		}
	}
	
}
