package xy;

public interface CheckBlock {
	public boolean isBlockLegal(Block block, byte[] prevBlockHash);
}
