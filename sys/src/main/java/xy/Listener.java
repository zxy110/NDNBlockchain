package xy;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.encoding.EncodingException;

public class Listener implements Runnable{

	public BlockChainProxy bchainProxy;
	public ExecutorService executor;
	public Map<String, Producer> producerMap;
	
	public Listener(BlockChainProxy bchainProxy, ExecutorService executor, Map<String, Producer> producerMap){
		this.bchainProxy = bchainProxy;
		this.executor = executor;
		this.producerMap = producerMap;
	}
	
	@Override
	public void run() {
		
		Face face = new Face();
		byte[] flag = null;

		while(true){
			boolean minerFlag = false;	
			byte[] prevHash = bchainProxy.getPreviousHash();
			// Miner Thread has run.
			if(Arrays.equals(flag, prevHash))
				minerFlag = true;
			// Ask for the next block.
			flag = prevHash;		
			String prefix = String.join("/", Configure.blockNDNGetBlockPrefix, prevHash.toString());			
			Consumer consumer = new Consumer();		          //建立消费者和兴趣包
			Interest interest = new Interest(new Name(prefix));
			interest.setInterestLifetimeMilliseconds(100000);   //设定兴趣生命周期
			try {
				face.expressInterest(interest, consumer, consumer);  
				
				// TODO retransmission timeout like TCP/IP windows
				while(!consumer.getNewBlock && consumer.timeoutCount > 0 ){
					face.processEvents();
				}
				
				// Get a new block, use BlockChainProxy Class to check it. If the block is legal, append to local blockchain.
				if(consumer.getNewBlock){
					//  The new block is illegal.
					if(!bchainProxy.addBlock(consumer.block)){
						System.out.println("Get A New src.Block : [ERROR] : new block is illegal.");
						System.out.println("                : [src.Block Name] :" + consumer.block.prevBlock);
						continue;
					}
					// The src.Block is legal , append to the local blockchain.
					
				}			
				// if minerFlag == false, miner hasn't run up yet.
				if(!minerFlag){
					if(consumer.timeoutCount < 0 || consumer.timeoutCount ==0){			
						// The next block doesn't exist yet.
						Runnable miner = new Miner(prevHash,bchainProxy,producerMap);
						Thread thread = new Thread(miner);
						thread.start();					
					}
				}				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Producer: IOException in sending data " + e.getMessage());
			} catch (EncodingException e) {
				// TODO Auto-generated catch block
				System.out.println("Producer: EncodingException in sending data " + e.getMessage());
			}
		}
	}
}
