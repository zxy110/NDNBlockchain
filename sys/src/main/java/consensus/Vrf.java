package consensus;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;


public class Vrf extends BilinearPairing{

    private Element sk, skm, pk, Hash, Proof, VerifyHash, VerifyProof;

    public Vrf(){
        super();
        buildSystem();
        encryption();
    }

    public Element getPk() {
        return pk;
    }

    public Element getHash() {
        return Hash;
    }

    public Element getProof() {
        return Proof;
    }

    public void buildSystem() {
        //初始化Zr中的元素
        sk = Zr.newRandomElement().getImmutable();          //随机生成主密钥sk
        skm = Zr.newElement().getImmutable();
        skm = m.add(sk);

        //初始化为G1中的元素，G1是加法群
        pk = G1.newElement().getImmutable();
        pk = P.mulZn(sk);                                   // 计算pk=sk*P,注意顺序
        Proof = G1.newElement().getImmutable();
        VerifyProof = G1.newElement().getImmutable();

        //初始化GT中的元素，GT是乘法群
        Hash = GT.newElement().getImmutable();
        VerifyHash = GT.newElement().getImmutable();
    }

    public void encryption(){
        System.out.println("-------------------VRF Run----------------------");
        calHash();
        calProof();
    }

    protected void calHash() {
        Hash = pairing.pairing(P, P).getImmutable();        // 计算e（P,P）
        Hash = Hash.powZn(skm);
        System.out.println("VRF_Hash=" + Hash);
    }

    protected void calProof() {
        Proof = P.mulZn(skm);
        System.out.println("VRF_Proof=" + Proof);
    }


    public boolean verify(Element vrfHash, Element vrfProof) {
        System.out.println("-------------------VRF Verify----------------------");
        VerifyHash = pairing.pairing(P, vrfProof);
        VerifyProof = P.mulZn(m);
        VerifyProof = pk.add(VerifyProof);
        System.out.println("V1=" + VerifyHash);
        System.out.println("V2=" + VerifyProof);

        if (VerifyHash.isEqual(vrfHash) && VerifyProof.isEqual(vrfProof)){
            System.out.println("VRF Verify Success!");
            return true;
        }else{
            System.out.println("VRF Verify Fail!");
            return false;
        }
    }
}
