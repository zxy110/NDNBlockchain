package xy;

import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.encoding.EncodingException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Peer {


public static void write(long l) {
	FileWriter fw = null;
	try {
		File f=new File("log.txt");
		fw = new FileWriter(f, true);
	} catch (IOException e) {
		e.printStackTrace();
	}
	PrintWriter pw = new PrintWriter(fw);
	pw.println(l);
	pw.flush();
	try {
		fw.flush();
		pw.close();
		fw.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
}

	//public static void main(String[] args) {
	public void run(){
		File file = new File("log.txt");
	        try {  
	            file.createNewFile(); // 创建文件  
	        } catch (IOException e) {  
	            // TODO Auto-generated catch block  
	            e.printStackTrace();  
	        }  
		
		
		BlockChainProxy blockChainProxy;
		ExecutorService executor;  //创建执行器
		Map<String, Producer> producerMap;
		// Allocate space for a blockchain, load the genesis block and create a blockchainproxy to manage this.
		blockChainProxy = new BlockChainProxy();
		executor = Executors.newFixedThreadPool(10); //固定的线程池个数
		producerMap = new HashMap<String, Producer>();

		VolumeProduction vp = new VolumeProduction();
		vp.initVolumeProduction(blockChainProxy, executor, producerMap);
			
		Face face = new Face();
		byte[] flag = null;
		
		System.out.println("System running...");
		System.out.println(" [Time] :" + new Date().getTime());
		System.out.println("[LOCAL] : 0th src.Block : [src.Block Digest] : " + Utils.bytesToHexString(blockChainProxy.getBlockChainGenesisHash()));
		System.out.println("[LOCAL] :Latest src.Block:[src.Block Digest] : " + Utils.bytesToHexString(blockChainProxy.getPreviousHash()));
		write(0);
		write(new Date().getTime());
		
		boolean minerFlag = false;	
		// TODO: into while and Miner finish creating a new block, but the minerFlag is still true ,waste a loop time
		while(true){
					
			// Miner hasn't create a new block yet & Nobody has created yet.
			if(Arrays.equals(flag, blockChainProxy.getPreviousHash())) //将flag与本地区块链最新的区块头hash进行比较，如果
				minerFlag = true;
			else 
				minerFlag = false;
			// Update flag value.
			flag = blockChainProxy.getPreviousHash();		
			String prefix = String.join("/", Configure.blockNDNGetBlockPrefix, Utils.bytesToHexString(flag));	//构建一条兴趣名字	/ndn/blockndn/getblocks/PreviousHash
			Consumer consumer = new Consumer();		
			Interest interest = new Interest(new Name(prefix)); //建立兴趣包
			interest.setInterestLifetimeMilliseconds(1000); //设定生命周期 1000s
			try {
				// TODO Retransmission after timeout: like TCP/IP windows : exponential order?
				// try scope is too great.....chaping

				while(!consumer.getNewBlock && consumer.timeoutCount > 0 ){  //如果收到新区块，直接跳出，如果没有收到且兴趣没有超时超过4次，不断来获取ondata和ontimeout
					face.expressInterest(interest, consumer, consumer);		
					face.processEvents();
					Thread.sleep(1000);
				}
				
				// Get a new block, use  BlockChainProxy Class to check it. If the block is legal, append to local blockchain.  //BlockChainProxy用于验证区块合法性
				if(consumer.getNewBlock){  //如果获取到了新区块
					//  The new block is illegal.
					if(!blockChainProxy.addBlock(consumer.block)){  //用该函数判断区块是否合法，如果不合法执行如下操作，如果合法了，直接在blockchain里成功添加了新区块
						System.out.println("**Get a new block**:[ERROR] : new block is illegal.");
						System.out.println("               [src.Block Name] :" + Utils.bytesToHexString(consumer.block.prevBlock));
						continue; //直接进去下一轮while循环等待区块
					}
					// The src.Block is legal , append to the local blockchain.Create a new producer. //当区块合法加入到本地区块链，并且创建一个生产者对象
					String prefixNewBlock = String.join("/", Configure.blockNDNGetBlockPrefix, Utils.bytesToHexString(consumer.block.prevBlock));
					if(!producerMap.containsKey(prefix)){ //如果已经为它产生过producer对象，则不执行
						Producer producer = new Producer(consumer.block, prefixNewBlock); //创建生产者，包含新区块数据和它的名字
						executor.execute(producer);   //异步执行该生产者中的run方法，为了等待收到兴趣来传输最新的区块
						producerMap.put(prefixNewBlock, producer);	//将该映射加入到这个map中，即名字-producer对象
					}
					
					write(blockChainProxy.getBlockHeight());
					write(new Date().getTime());
					 
					System.out.println("[LOCAL] :  Store a new block: [SUCCESSFUL] : Store Successfully Into Blockchain.");
					System.out.println("                      [Time]:" + new Date().getTime());
					System.out.println("                    [height]:" + blockChainProxy.getBlockHeight());
					System.out.println("              [src.Block Prefix]: " + Utils.bytesToHexString(consumer.block.prevBlock));
					System.out.println("            [Previous src.Block]: " + Utils.bytesToHexString(consumer.block.prevBlock));
					System.out.println("              [src.Block Digest]: " + Utils.bytesToHexString(Utils.digestsha256(consumer.block)));
					System.out.println("                [src.Block Size]: " + consumer.block.blockSize);
					System.out.println("               [Transaction]: " + consumer.block.s);
					System.out.println("                     [Nonce]: " + consumer.block.nonce);
					continue;
				}			
				// Timeout: Network Congestion & No Such a Data packet
				// whether miner thread has been called in the latest loop.
				if(!minerFlag){  //如果没有获取新区块，且
					if(consumer.timeoutCount < 0 || consumer.timeoutCount ==0){			
						// The next block doesn't exist yet.
						// TODO If timeout, ask other nodes through modified ChronoSync? 
						Runnable miner = new Miner(flag,blockChainProxy,producerMap);
						Thread thread = new Thread(miner);
						thread.start();					
					}
				}				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Producer: IOException in sending data " + e.getMessage());
				e.printStackTrace();
			} catch (EncodingException e) {
				// TODO Auto-generated catch block
				System.out.println("Producer: EncodingException in sending data " + e.getMessage());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
	
		
	}

}
