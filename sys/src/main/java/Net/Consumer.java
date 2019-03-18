package Net;

import net.named_data.jndn.Data;
import net.named_data.jndn.Interest;
import net.named_data.jndn.OnData;
import net.named_data.jndn.OnTimeout;
import src.Block;
import src.Utils;

import java.nio.ByteBuffer;

public class Consumer implements OnData , OnTimeout {
	protected Block block;
	protected int timeoutCount = 4;
	protected boolean getNewBlock = false;
	
	public Consumer(){;
		block = null;
	}

	public Block getBlock() {
		return block;
	}

	public boolean getNewBlock(){
		return getNewBlock;
	}

	public int getTimeoutCount() {
		return timeoutCount;
	}

	public void onTimeout(Interest interest) {
		 --timeoutCount;
		 System.out.println("Time Out: [Interest Name] :" + interest.getName().toUri());
	}

	//onData时，那么将数据转换到byteData数组，调用解压函数得到区块
	public void onData(Interest interest, Data data) {
		ByteBuffer content = data.getContent().buf();
		byte[] byteData = new byte[content.limit()];
		content.get(byteData);
		block = Utils.ByteArrayToblock(byteData);
		getNewBlock = true;
		
	}

}
