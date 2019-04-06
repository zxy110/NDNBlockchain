package consensus;


import src.Configure;
import it.unisa.dia.gas.jpbc.Element;

import java.math.BigInteger;
import java.util.ArrayList;


public class test extends BilinearPairing{
    public Element vrfHash,vrfProof,pk;
    public ArrayList<Element> signatures,pks;
    public Mpts mpts;
    public Vrf vrf;

    public test(){
        super();
        pk = G1.newElement();
        vrfHash = GT.newElement();
        vrfProof = G1.newElement();
        pks = new ArrayList<Element>(Configure.DELEGATES);
        signatures = new ArrayList<Element>(Configure.DELEGATES);
    }

    public boolean vrfRun(){
        vrf = new Vrf();
        BigInteger target = BigInteger.valueOf(1).shiftLeft(256-(int)Configure.TARGET_BITS).subtract(BigInteger.valueOf(1));//难度值

        //此方法返回-1，0或1，分类为BigInteger在数字上小于，等于，或大于值val。
        if(vrf.getHash().toBigInteger().compareTo(target)==1){
            pk = vrf.getPk().duplicate().getImmutable();
            vrfHash = vrf.getHash().duplicate().getImmutable();
            vrfProof = vrf.getProof().duplicate().getImmutable();
            return true;
        }else{
            System.out.println("You are not the miner in this time slice.");
            return false;
        }
    }

    public boolean proxySig(){
        if(vrf.verify(vrfHash,vrfProof)){
            mpts = new Mpts(Configure.DELEGATES);
            mpts.encryption();

            pks = mpts.getPkArr();
            signatures = mpts.getSigArr();
            return true;
        }else{
            return false;
        }
    }

    public boolean verifyProxySig(){
        if(mpts.verify(pks,signatures)) return true;
        return false;
    }

    //private void test(){
    public static void main(String[] args){
        test n = new test();
        if(n.vrfRun()){
            if(n.proxySig())
                n.verifyProxySig();
        }
    }
}
