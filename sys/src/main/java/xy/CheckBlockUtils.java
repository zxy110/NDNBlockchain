package xy;

import java.util.ArrayList;
import java.util.Arrays;

public class CheckBlockUtils {
	public static boolean checkData(String s){    //验证数据函数，静态
		String strings[] = s.split("\\s+");
		ArrayList<Integer> cal = new ArrayList<Integer>();
		char opOne = 0;
		char opTwo = 0;
		int left = 0;
		for(String str:strings){
			if(str.matches("-?[0-9]+"))
				cal.add(Integer.parseInt(str));
			if("+".equals(str)|"-".equals(str)|"*".equals(str)|"/".equals(str))
				opOne = str.charAt(0);
			if("=".equals(str)|"<".equals(str)|">".equals(str))
				opTwo = str.charAt(0);
		}
		if(cal.size() == 3){
			switch(opOne){
			case '+': left = cal.get(0) + cal.get(1);break;
			case '-': left = cal.get(0) - cal.get(1);break;
			case '*': left = cal.get(0) * cal.get(1);break;
			case '/':if(cal.get(1) == 0)return false;
				else left = cal.get(0) / cal.get(1);break;
			default: return false;
		}
		switch(opTwo){
		case '=': return left == cal.get(2);
		case '<': return left < cal.get(2);
		case '>': return left > cal.get(2);
		default: return false;
		}
	}
	return false;	
	}
	
	public static boolean checkBlockSize(Block block){
		if(block.blockSize < Configure.INITBLOCKSIZE || block.blockSize > Configure.MAXBLOCKSIZE)
			return false;
		else{
			if(block.blockSize == Configure.INITBLOCKSIZE + block.s.length())
				return true;
			return false;
		}	
	}
	
	public static boolean checkPrevHash(byte[] prevHash,Block block){
		if(Arrays.equals(prevHash, block.prevBlock))	return true;
		else return false;
	}
	
	public static boolean NrawCheckProofOfWork(Block block){
		//notice that this function return FALSE after finishing the pow;
		byte[] blockHash = Utils.digestsha256(block);           //utils里面有摘要函数digestsha256
	//	System.out.print("\n");	for(int i=0;i<32;i++) System.out.print(blockHash[i]+" "); System.out.print("\n");		
	//	if(blockHash[0] == 0 && blockHash[1] == 0)	
		if(blockHash[0] == 0 && blockHash[1] == 0 && blockHash[2] == 0 && blockHash[3] < 31)   //8×3+4=28个0
				return false;
		else	return true;
	}

	public static boolean NrawCheckProofOfStack(Block block){
		//
		return true;
	}
	
}
