package consensus;

public class ConsensusFactory {
    public Consensus getConsensus(String name){
        if(name.equals("Pow")){
            return new Pow();
        }
        else if(name.equals("Mptlbp")){
            return new Mptlbp();
        }
        return null;
    }
}
