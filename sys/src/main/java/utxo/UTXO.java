package utxo;

import src.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 该类用于初始化UTXO池，便于对交易进行验证
 */
public class UTXO {
    public Map<String, TXInput> inputs;
    public Map<String, TXOutput> outputs;

    public UTXO(ArrayList<Block> blockChain){
        inputs=new HashMap<String, TXInput>();
        outputs=new HashMap<String, TXOutput>();
        for(Block block : blockChain){
            for(Transaction transaction : block.getTransaction()){
                for(TXInput input:transaction.getInputs()){
                    inputs.put(input.getTxId(), input);
                }
                for(TXOutput output:transaction.getOutputs()){
                    outputs.put(output.getTxId(), output);
                }
            }
        }
    }
    public void addUTXO(Block block){
        for(Transaction transaction:block.getTransaction()){
            for(TXInput input:transaction.getInputs()){
                inputs.put(input.getTxId(), input);
            }
            for(TXOutput output:transaction.getOutputs()){
                outputs.put(output.getTxId(), output);
            }
        }
    }
}
