package consensus;

import sys.Block;

public interface Consensus {
    void run(Block block);
    boolean verify(String prevBlock, Block block);
}
