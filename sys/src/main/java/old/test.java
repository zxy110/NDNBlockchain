package old;


import crypto.BilinearPairing;
import crypto.Mpts;
import crypto.Vrf;
import sys.Configure;
import it.unisa.dia.gas.jpbc.Element;

import java.math.BigInteger;
import java.util.ArrayList;


public class test extends BilinearPairing {
    public Element m;                           //32 --Zr
    public Element vrfHash;                     //66 --GT
    public Element vrfProof;                    //66 --G1
    public Element P;                           //66 --G1
    public ArrayList<Element> signatures;       //Configure.DELEGATES*66
    public ArrayList<Element> pks;              //Configure.DELEGATES*66
    public Mpts mpts;
    public Vrf vrf;

    public test(){
        super();
        m = Zr.newRandomElement().getImmutable();
        P = G1.newRandomElement().getImmutable();

        vrfHash = GT.newElement().getImmutable();
        vrfProof = G1.newElement().getImmutable();

        pks = new ArrayList<Element>(Configure.DELEGATES);
        signatures = new ArrayList<Element>(Configure.DELEGATES);
    }

    public boolean vrfRun(Element sk){
        vrf = new Vrf(P,m);
        vrf.run(sk);
        BigInteger target = BigInteger.valueOf(1).shiftLeft(256-(int)Configure.TARGET_BITS).subtract(BigInteger.valueOf(1));//难度值

        //此方法返回-1，0或1，分类为BigInteger在数字上小于，等于，或大于值val。
        if(vrf.getHash().toBigInteger().compareTo(target)==1){
            vrfHash = vrf.getHash().duplicate().getImmutable();
            vrfProof = vrf.getProof().duplicate().getImmutable();
            return true;
        }else{
            System.out.println("You are not the miner in this time slice.");
            return false;
        }
    }

    public boolean proxySig(Element sk){
        Element pk = P.mulZn(sk).getImmutable();              // 计算pk=sk*P,注意顺序
        if(vrf.verify(vrfHash,vrfProof,pk)){
            mpts = new Mpts(P, m);
            mpts.run(sk,pks,signatures,Configure.DELEGATES);
            return true;
        }else{
            return false;
        }
    }

    public boolean verifyProxySig() {
        Mpts mpts = new Mpts(P.duplicate(),m.duplicate());
        if (mpts.verify(pks, signatures, Configure.DELEGATES)) {
            System.out.println("Mpts Verify Sucess!");
            return true;
        }else{
            System.out.println("Mpts Verify Failed!");
            return false;
        }
    }

    public void calSize(){
        System.out.println("m:"+m.toBytes().length);
        System.out.println("vrfHash:"+vrfHash.toBytes().length);
        System.out.println("vrfProof:"+vrfProof.toBytes().length);
        System.out.println("P:"+P.toBytes().length);
        int size=0;
        for(int i=0;i<signatures.size();i++){
            size += signatures.get(i).toBytes().length;
        }
        System.out.println("sigArr:"+size);
        size=0;
        for(int i=0;i<pks.size();i++){
            size += pks.get(i).toBytes().length;
        }
        System.out.println("pkArr:"+size);
    }

    private void test(){
    //public static void main(String[] args){
        test n = new test();
        //n.calSize();
        for(int i=0;i<Configure.DELEGATES;i++){
            BilinearPairing bp = new BilinearPairing();
            Element sk = bp.getZr().newRandomElement().getImmutable();      // 随机生成主密钥ski
            if(n.vrfRun(sk))
                n.proxySig(sk);
            //n.calSize();
        }
        n.verifyProxySig();
    }
}
