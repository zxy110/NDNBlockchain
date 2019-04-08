package sys;

import consensus.ConsensusFactory;
import leveldb.Persistence;
import net.Consumer;
import net.Producer;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.Name;
import net.named_data.jndn.encoding.EncodingException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    //public void run(){
    public static void main(String[] args){
        BlockChain blockChain = new BlockChain();
        Map<String, Producer> producerMap = new HashMap<String, Producer>();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        //initial blockChain with blocks from levelDB
        Persistence levelDb = new Persistence();
        blockChain = levelDb.init(blockChain);

        Face face = new Face();
        String flag = "";

        System.out.println("System running...");
        System.out.println("[Time] :" + new Date().getTime());
        ArrayList<Block> blocks = blockChain.getBlockChain();
        System.out.println("[LOCAL] : 0th sys.Block : [sys.Block Digest] : " + blocks.get(blocks.size()-1).getPrevBlock());
        System.out.println("[LOCAL] :Latest sys.Block:[sys.Block Digest] : " + blocks.get(blocks.size()-1).getHash());


        boolean minerFlag = false;
        long timeStamp = System.currentTimeMillis();
        // TODO: into while and Miner finish creating a new block, but the minerFlag is still true ,waste a loop time
        while(true){

            // Miner hasn't create a new block yet & Nobody has created yet.
            ArrayList<Block> bChain = blockChain.getBlockChain();
            Block latestBlock = bChain.get(bChain.size()-1);
            if(flag.equals(latestBlock.getHash())) //将flag与本地区块链最新的区块头hash进行比较，如果
                minerFlag = true;
            else
                minerFlag = false;
            // Update flag value.
            flag = latestBlock.getHash();
            String prefix = "/" + Configure.blockNDNGetBlockPrefix + flag;	//构建一条兴趣名字	/ndn/blockndn/getblocks/PreviousHash
            Consumer consumer = new Consumer();
            Interest interest = new Interest(new Name(prefix)); //建立兴趣包
            interest.setInterestLifetimeMilliseconds(1000); //设定生命周期 1000s
            try {
                // TODO Retransmission after timeout: like TCP/IP windows : exponential order?
                // try scope is too great.....chaping
                while(!consumer.getNewBlock() && consumer.getTimeoutCount() > 0 ){  //如果收到新区块，直接跳出，如果没有收到且兴趣没有超时超过4次，不断来获取ondata和ontimeout
                    face.expressInterest(interest, consumer, consumer);
                    face.processEvents();
                    Thread.sleep(1000);
                }

                // Get a new block, use  BlockChainProxy Class to check it. If the block is legal, append to local blockchain.  //BlockChainProxy用于验证区块合法性
                if(consumer.getNewBlock()){  //如果获取到了新区块
                    //print block
                    //consumer.getBlock().printBlock();
                    //consensus factory
                    ConsensusFactory cFac = new ConsensusFactory();
                    //  The new block is illegal.
                    if(!cFac.getConsensus(Configure.Consensus).verify(blockChain.getLatestBlock(), consumer.getBlock())){  //用该函数判断区块是否合法，如果不合法执行如下操作，如果合法了，直接在blockchain里成功添加了新区块
                        System.out.println("**Get a new block**:[ERROR] : new block is illegal.");
                        System.out.println("               [sys.Block Name] :" + consumer.getBlock().getPrevBlock());
                        continue; //直接进去下一轮while循环等待区块
                    }else{
                        blockChain.addBlock(consumer.getBlock());
                    }
                    // The sys.Block is legal , append to the local blockchain.Create a new producer. //当区块合法加入到本地区块链，并且创建一个生产者对象
                    String prefixNewBlock = "/" + Configure.blockNDNGetBlockPrefix + consumer.getBlock().getPrevBlock();
                    if(!producerMap.containsKey(prefix)){ //如果已经为它产生过producer对象，则不执行
                        Producer producer = new Producer(consumer.getBlock(), prefixNewBlock); //创建生产者，包含新区块数据和它的名字
                        executor.execute(producer);   //异步执行该生产者中的run方法，为了等待收到兴趣来传输最新的区块
                        producerMap.put(prefixNewBlock, producer);	//将该映射加入到这个map中，即名字-producer对象
                    }

                    levelDb.saveBlockLevelDB(consumer.getBlock()); //持久化
                    System.out.println("[LOCAL] :  Store a new block: [SUCCESSFUL] : Store Successfully Into Blockchain.");
                    System.out.println("                          [Time]:" + new Date().getTime());
                    System.out.println("                        [height]:" + blockChain.getBlockChain().size());
                    System.out.println("              [sys.Block Prefix]: " + consumer.getBlock().getPrevBlock());
                    System.out.println("            [Previous sys.Block]: " + consumer.getBlock().getPrevBlock());
                    System.out.println("              [sys.Block Digest]: " + consumer.getBlock().getHash());
                    System.out.println("                [sys.Block Size]: " + consumer.getBlock().getBlockSize());
                    //System.out.println("               [Transaction]: " + consumer.getBlock().getTransaction());
                    System.out.println("                         [Nonce]: " + consumer.getBlock().getNonce());
                    if(Configure.Consensus.equals("Mptlbp")){
                        System.out.println("                         [m]:" + consumer.getBlock().getM());
                        System.out.println("                         [P]:" + consumer.getBlock().getP());
                        System.out.println("                         [vrfPk]:" + consumer.getBlock().getVrfPk());
                        System.out.println("                         [vrfHash]:" + consumer.getBlock().getVrfHash());
                        System.out.println("                         [vrfProof]:" + consumer.getBlock().getVrfProof());
                        System.out.println("                         [pkArrSize]:" + consumer.getBlock().getPks().size());
                        System.out.println("                         [sigArrSize]:" + consumer.getBlock().getSignatures().size());
                    }

                    //calculate delay
                    System.out.println("Delay: "+(System.currentTimeMillis()-timeStamp));
                    timeStamp = System.currentTimeMillis();

                    continue;
                }
                // Timeout: Network Congestion & No Such a Data packet
                // whether miner thread has been called in the latest loop.
                if(!minerFlag){  //如果没有获取新区块，且
                    if(consumer.getTimeoutCount() < 0 || consumer.getTimeoutCount() ==0){
                        // The next block doesn't exist yet.
                        // TODO If timeout, ask other nodes through modified ChronoSync?
                        Runnable miner = new Miner(blockChain,producerMap);
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
