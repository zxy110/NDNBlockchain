package xy;

import java.nio.ByteBuffer;
import java.util.Arrays;

import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.OnData;
import net.named_data.jndn.OnTimeout;

public class Consumer implements OnData , OnTimeout {
	Block block;
	public int timeoutCount = 4;
	boolean getNewBlock = false;
	
	public Consumer(){;
		block = null;
	}
	
	public boolean getNewBlock(){
		return getNewBlock;
	}
	
	@Override
	public void onTimeout(Interest interest) {
		 --timeoutCount;
		 System.out.println("Time Out: [Interest Name] :" + interest.getName().toUri());
	}

	@Override  //onData时，那么将数据转换到byteData数组，调用解压函数得到区块
	public void onData(Interest interest, Data data) {
		ByteBuffer content = data.getContent().buf();
		byte[] byteData = new byte[content.limit()];
		content.get(byteData);
		block = unpackDataPacket(byteData);	
		getNewBlock = true;
		
	}
	
	//解压提取数据包的区块数据，得到各个字段，构建得到block，利用block（）函数，并返回
	private Block unpackDataPacket(byte[] byteData) { 
		byte[] prevHash = Arrays.copyOfRange(byteData, 0, Configure.SHA256SIZE);
		byte[] bblockSize = Arrays.copyOfRange(byteData, Configure.SHA256SIZE, 
						Configure.SHA256SIZE+Configure.SIZEOFINT);
		byte[] btime = Arrays.copyOfRange(byteData, Configure.SHA256SIZE+Configure.SIZEOFINT, 
						Configure.SHA256SIZE+Configure.SIZEOFINT+Configure.SIZEOFLONG);
		byte[] bnonce = Arrays.copyOfRange(byteData, Configure.SHA256SIZE+Configure.SIZEOFINT+Configure.SIZEOFLONG, 
						Configure.SHA256SIZE+Configure.SIZEOFINT+Configure.SIZEOFLONG+Configure.SIZEOFLONG);			
		byte[] bstring = Arrays.copyOfRange(byteData, 
						Configure.SHA256SIZE+Configure.SIZEOFINT+Configure.SIZEOFLONG+Configure.SIZEOFLONG, 
						byteData.length);
		int blockSize = Utils.bytesToInt(bblockSize, 0);
		long time = Utils.byteToLong(btime);
		long nonce = Utils.byteToLong(bnonce);
		String s = new String(bstring);
		Block block = new Block(prevHash, blockSize, time, nonce, s);
		return block;
	}

}
